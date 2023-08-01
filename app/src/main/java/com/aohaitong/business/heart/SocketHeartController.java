package com.aohaitong.business.heart;

import android.util.Log;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.BaseController;
import com.aohaitong.business.IPController;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.ThreadPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SocketHeartController {
    private static SocketHeartController instance = null;
    private long latestTime = System.currentTimeMillis(); //最后一次收到心跳消息的时间  30s*3时间后收不到就断开重连
    private boolean isHeartBeatRunning = true;

    public static SocketHeartController getInstance() {
        if (instance == null) {
            instance = new SocketHeartController();
        }
        return instance;
    }

    public void stop() {
        Log.e(CommonConstant.LOGCAT_TAG, "socket心跳失败，停止线程");
        EventBus.getDefault().unregister(this);
        isHeartBeatRunning = false;
        latestTime = System.currentTimeMillis();
        ThreadPoolManager.getInstance().remove(heartBeatRunnable);
        //停止Socket连接
        BaseController.stopSocketConnect();
        Log.d(CommonConstant.LOGCAT_TAG, "Socket心跳线程结束");
    }

    public void start() {
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            latestTime = System.currentTimeMillis();
            try {
                EventBus.getDefault().register(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isHeartBeatRunning = true;
            ThreadPoolManager.getInstance().execute(heartBeatRunnable);
        }
    }

    private final Runnable heartBeatRunnable = () -> {
        Log.d(CommonConstant.LOGCAT_TAG, "Socket心跳线程开始");
        while (isHeartBeatRunning) {
            Log.i(CommonConstant.LOGCAT_TAG, "Socket心跳线程进行中---------------------------------");
            if (System.currentTimeMillis() - latestTime > 30 * 1000) {
                reconnected();
            }
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    //IP重连
    private void reconnected() {
        latestTime = 0;
        stop();
        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_NO_NET));
        if (MyApplication.isHaveLogin) {
            BaseController.onConnectStop();
        }
    }

    /**
     * 接收消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEntity entity) {
//        if (entity == null
//                || entity.getType() != StatusConstant.TYPE_HEART) {
//            return;
//        }
//        latestTime = System.currentTimeMillis();
//        try {
//            long time = Long.parseLong(CommVdesMessageUtil.reslvToBHM(entity.getMsg()).getData().substring(0, 13));
//            if (time > NumConstant.HEARTBEAT_COMPARE_TIME) {
//                DateUtil.getInstance().setCurrentTime(time);
//            } else {
//                DateUtil.getInstance().setCurrentTime(latestTime);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BusinessController.sendHeartMsgAnswer(null, entity.getMsg());

        //        if (entity == null
//                || entity.getType() != StatusConstant.TYPE_HEART) {
//            return;
//        }
        latestTime = System.currentTimeMillis();
        DateUtil.getInstance().setCurrentTime(latestTime);
//        try {
//            long time = Long.parseLong(CommVdesMessageUtil.reslvToBHM(entity.getMsg()).getData().substring(0, 13));
//            if (time > NumConstant.HEARTBEAT_COMPARE_TIME) {
//                DateUtil.getInstance().setCurrentTime(time);
//            } else {
//                DateUtil.getInstance().setCurrentTime(latestTime);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BusinessController.sendHeartMsgAnswer(null, entity.getMsg());
    }

}

