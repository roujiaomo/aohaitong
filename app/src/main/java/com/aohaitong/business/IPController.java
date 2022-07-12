package com.aohaitong.business;

import android.text.TextUtils;

import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.IPAddress;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.kt.util.VersionUtil;
import com.aohaitong.utils.PingUtil;
import com.aohaitong.utils.SPUtil;

/**
 * 用于判断当前应该连接哪个IP
 * 调用时机：app初始化，心跳断连重连时
 */
public class IPController {
    public static String IP = "";
    public static String PORT = "";
    public static int CONNECT_TYPE = StatusConstant.CONNECT_DISCONNECTED;

    public static String getIp() {
        if (TextUtils.isEmpty(IP)) {
            if (VersionUtil.INSTANCE.isTestVersion()) {
                IPController.loadTestIp();
            } else {
                IPController.loadIp();
            }
        }
        return IP;
    }

    /**
     * 先ping外网IP,ping不通ping内网IP
     */
    public static void loadIp() {
        CONNECT_TYPE = StatusConstant.CONNECT_DISCONNECTED;
        if (PingUtil.pingHost(IPAddress.getMqAddress().split(":")[0])) {
            IP = IPAddress.getMqAddress().split(":")[0];
            PORT = IPAddress.getMqAddress().split(":")[1];
            CONNECT_TYPE = StatusConstant.CONNECT_MQ;
        } else if (PingUtil.pingHost(IPAddress.getSocketAddress().split(":")[0])) {
            IP = IPAddress.getSocketAddress().split(":")[0];
            PORT = IPAddress.getSocketAddress().split(":")[1];
            CONNECT_TYPE = StatusConstant.CONNECT_SOCKET;
        }
    }

    public static void loadTestIp() {
        if (SPUtil.instance.getInt(CommonConstant.SP_LOGIN_NETWORK_TYPE) == StatusConstant.CONNECT_MQ) {
            if (PingUtil.pingHost(IPAddress.getMqAddress().split(":")[0])) {
                IP = IPAddress.getMqAddress().split(":")[0];
                PORT = IPAddress.getMqAddress().split(":")[1];
                CONNECT_TYPE = StatusConstant.CONNECT_MQ;
            } else {
                CONNECT_TYPE = StatusConstant.CONNECT_DISCONNECTED;
            }
        } else if (SPUtil.instance.getInt(CommonConstant.SP_LOGIN_NETWORK_TYPE) == StatusConstant.CONNECT_SOCKET) {
            if (PingUtil.pingHost(IPAddress.getSocketAddress().split(":")[0])) {
                IP = IPAddress.getSocketAddress().split(":")[0];
                PORT = IPAddress.getSocketAddress().split(":")[1];
                CONNECT_TYPE = StatusConstant.CONNECT_SOCKET;
            } else {
                CONNECT_TYPE = StatusConstant.CONNECT_DISCONNECTED;
            }
        }
    }
}
