package com.aohaitong.business;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.GroupBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.heart.MQHeartController;
import com.aohaitong.business.heart.SocketHeartController;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.business.transmit.MqController;
import com.aohaitong.business.transmit.SendController;
import com.aohaitong.business.transmit.SocketController;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.utils.ConnectThreadPoolManager;
import com.aohaitong.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 连接和心跳总体处理
 */
public class BaseController {
    //心跳: socket心跳是由船端下发,我回复应答,mq是由我发送,中心回复应答
    private static MQHeartController mqHeartController;
    private static SocketHeartController socketHeartController;
    public static boolean isNetWorkEnable = true;

    /**
     * 断线重连的触发机制：
     * 1、当触发过一次断线重连，且没船和岸都ping通，过10秒继续重连
     * 2、当船端30秒都没有收到心跳，check30次都失败，就重连
     * 3、MQ心跳/MQ传输失败
     * 4、Socket心跳失败
     */
    public static void onConnectStop() {
        Log.e(CommonConstant.LOGCAT_TAG, "onConnectStop: 断线重连开始");
        if (isMainThread()) {
            new Thread(() -> {
                IPController.loadIp();
                doOnConnectStop();
            }
            ).start();
        } else {
            IPController.loadIp();
            doOnConnectStop();
        }
    }

    /**
     * 开启长连接 -> 重新登录 -> 开启心跳
     */
    private static void doOnConnectStop() {
        SendController.getInstance().setTYPE(IPController.CONNECT_TYPE);
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            stopMQConnect(true);
            restartMQConnect();
        } else if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            startSocketConnect();
            checkIsSocketConnect();
        } else {
            //如果没ping通 则不进行操作
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                BaseController.onConnectStop();
                Log.e(CommonConstant.LOGCAT_TAG, "船岸都没ping通,进行重连");
            }, 10000);
        }
    }

    private static void doMQLogin() {
        try {
            Thread.sleep(1000);
            if (MqController.getInstance().getIsStart()) {
                BusinessController.sendLogin(MyApplication.TEL + "", MyApplication.PASSWORD, new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        Log.e(CommonConstant.LOGCAT_TAG, "MQ重连登录成功");
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_NET_CONNECTED));
                        startHeartBeat();
                        BusinessController.sendGetFriendList(SPUtil.instance.getLong(MyApplication.TEL + CommonConstant.LAST_UPDATE_TIME) + "", null, NumConstant.getJHDNum());
                        BusinessController.sendGetGroupList(null, NumConstant.getJHDNum());
                    }

                    @Override
                    public void sendFail(String reason) {
                        Log.e(CommonConstant.LOGCAT_TAG, "MQ重连登录失败: " + reason);
                        doMQLogin();
//                        if (reason.equals("超时失败")) {
//
//                        }
                    }
                }, NumConstant.getJHDNum());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static void checkIsSocketConnect() {
        Log.d(CommonConstant.LOGCAT_TAG, "socket连接状态" + SocketController.getInstance().getIsSocketStart());
        try {
            Thread.sleep(1000);
            if (SocketController.getInstance().getIsSocketStart()) {
                socketFailCount = 0;
                BusinessController.sendLogin(SPUtil.instance.getString(CommonConstant.LOGIN_TEL), SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD), new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        Log.e(CommonConstant.LOGCAT_TAG, "Socket重连登录成功");
                        MyApplication.TEL = Long.parseLong(SPUtil.instance.getString(CommonConstant.LOGIN_TEL));
                        MyApplication.PASSWORD = SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD);
                        List<GroupBean> groupList = DBManager.getInstance(MyApplication.getContext())
                                .getGroupListByTel(MyApplication.TEL + "");
                        for (GroupBean groupBean : groupList) {
                            SendController.groupIdList.add(groupBean.getGroupId());

                        }
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_NET_CONNECTED));
                        startHeartBeat();
                    }

                    @Override
                    public void sendFail(String reason) {
                        Log.e(CommonConstant.LOGCAT_TAG, "Socket重连登录失败：" + reason);
                        checkIsSocketConnect();
                    }
                }, NumConstant.getJHDNum());
            } else {
                socketFailCount++;
                if (socketFailCount < NumConstant.SOCKET_CONNECT_CHECK_COUNT) {
                    checkIsSocketConnect();
                } else {
                    //重连之前关闭现有连接和心跳
                    stopHeartBeat();
                    stopSocketConnect();
                    BaseController.onConnectStop();
                    socketFailCount = 0;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int socketFailCount = 0;

    /**
     * 退出登录
     * 关闭所有连接和心跳
     */
    public static void logOut() {
        stopHeartBeat();
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            stopMQConnect(true);
        } else if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            stopSocketConnect();
        }
    }

    /**
     * 开启长连接
     */
    public static void startConnect() {
        SendController.getInstance().setTYPE(IPController.CONNECT_TYPE);
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            startMQConnect();
        } else if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            startSocketConnect();
        }
    }

    /**
     * 关闭Socket连接
     */
    public static void stopMQConnect(boolean isLogout) {
        Log.e(CommonConstant.LOGCAT_TAG, "关闭MQ长连接");
        if (isLogout) {
            MqController.getInstance().stopConnect();
        } else {
            MqController.getInstance().stopMQWorkThread();
        }
    }

    /**
     * 关闭Socket连接
     */
    public static void stopSocketConnect() {
        Log.e(CommonConstant.LOGCAT_TAG, "关闭Socket长连接");
        SocketController.getInstance().stopConnect();
    }

    /**
     * 登录成功后开启心跳
     */
    public static void startHeartBeat() {
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            mqHeartController = MQHeartController.getInstance();
            mqHeartController.start();
        } else if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            socketHeartController = SocketHeartController.getInstance();
            socketHeartController.start();
        }
    }

    //主要用于船端 长连接通了 但是登录失败的情况
    public static void stopHeartBeat() {
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
            if (mqHeartController != null) {
                mqHeartController.stop();
            }
            mqHeartController = null;
        } else if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            if (socketHeartController != null) {
                socketHeartController.stop();
            }
            socketHeartController = null;
        }
    }

    private static final Runnable startMQConnectTask = () -> MqController.getInstance().startConnect();
    private static final Runnable startSocketConnectTask = () -> SocketController.getInstance().startConnect();

    private static final Runnable restartMQConnectTask = () -> {
        MqController.getInstance().startReConnect(
                new MqController.MQConnectListener() {
                    @Override
                    public void success() {
                        doMQLogin();
                    }

                    @Override
                    public void failed() {
                        stopMQConnect(true);
                        stopHeartBeat();
                        onConnectStop();
                    }
                }
        );

    };

    public static void startMQConnect() {
        Log.e(CommonConstant.LOGCAT_TAG, "开启MQ长连接");
        ConnectThreadPoolManager.getInstance().execute(startMQConnectTask);
    }

    public static void restartMQConnect() {
        Log.e(CommonConstant.LOGCAT_TAG, "重新开启MQ长连接");
        ConnectThreadPoolManager.getInstance().execute(restartMQConnectTask);
    }

    public static void startSocketConnect() {
        Log.e(CommonConstant.LOGCAT_TAG, "开启Socket长连接");
        ConnectThreadPoolManager.getInstance().execute(startSocketConnectTask);
    }

}
