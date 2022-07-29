package com.aohaitong.business.heart;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.BaseController;
import com.aohaitong.business.IPController;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.ThreadPoolManager;
import com.aohaitong.utils.offshore.util.CommVdesMessageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MQHeartController {
    private static MQHeartController instance = null;
    private final List<String> unReceiveHeart;
    private int failTime;
    private boolean isHeartBeatRunning = true;

    private MQHeartController() {
        unReceiveHeart = new ArrayList<>();
        failTime = 0;
    }

    public static MQHeartController getInstance() {
        if (instance == null) {
            instance = new MQHeartController();
        }
        return instance;
    }


    public void stop() {
        Log.e(CommonConstant.LOGCAT_TAG, "MQ心跳失败，准备重连，停止线程");
        //清空心跳线程
        failTime = 0;
        unReceiveHeart.clear();
        isHeartBeatRunning = false;
        ThreadPoolManager.getInstance().remove(heartBeatRunnable);
        //停止MQ连接
        BaseController.stopMQConnect(false);
        EventBus.getDefault().unregister(this);
        Log.d(CommonConstant.LOGCAT_TAG, "MQ心跳线程结束");
    }

    public void start() {
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            try {
                EventBus.getDefault().register(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isHeartBeatRunning = true;
            ThreadPoolManager.getInstance().execute(heartBeatRunnable);
        }
    }

    private final Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(CommonConstant.LOGCAT_TAG, "MQ心跳线程开始");
            failTime = 0;
            unReceiveHeart.clear();
            while (isHeartBeatRunning) {
                int num = NumConstant.getHeartNum();
                if (unReceiveHeart.size() >= 3) {
                    isHeartBeatRunning = false;
                    Log.i(CommonConstant.LOGCAT_TAG, "MQ心跳三次未收到---------------------------------");
                    reconnected();
                } else {
                    unReceiveHeart.add(num + "");
                    Log.i(CommonConstant.LOGCAT_TAG, "MQ发送次数: ---------------------------------" + unReceiveHeart.size());
                    BusinessController.sendHeartMsg(new ISendListener() {
                        @Override
                        public void sendSuccess() {

                        }

                        @Override
                        public void sendFail(String reason) {
//                            failTime++;
//                            if (failTime > 3 && isHeartBeatRunning) {
//                                Log.i(CommonConstant.LOGCAT_TAG, "MQ发送心跳三次失败---------------------------------");
//                                isHeartBeatRunning = false;
//                                reconnected();
//                            }
                        }
                    }, num);
                }
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //IP重连
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void reconnected() {
        stop();
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_NO_NET));
            BaseController.onConnectStop();
        }
    }

    /**
     * 接收消息事件
     *
     * @param entity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEntity entity) {
        if (entity == null
                || entity.getType() != StatusConstant.TYPE_HEART) {
            return;
        }
        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_NET_CONNECTED));
        unReceiveHeart.clear();
        failTime = 0;
        try {
            long time = Long.parseLong(CommVdesMessageUtil.reslvToBHM(entity.getMsg()).getData().substring(0, 13));
            if (time > NumConstant.HEARTBEAT_COMPARE_TIME) {
                DateUtil.getInstance().setCurrentTime(time);
            } else {
                DateUtil.getInstance().setCurrentTime(System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
