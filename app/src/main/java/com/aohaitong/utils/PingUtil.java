package com.aohaitong.utils;

import android.util.Log;

import com.aohaitong.constant.CommonConstant;

import java.io.IOException;
import java.net.InetAddress;

public class PingUtil {
    public static boolean pingHost(String str) {  //str  为要ping的IP地址
        boolean result = false;
        Log.d(CommonConstant.LOGCAT_TAG, "开始ping");
        int timeOut = 3000;   // 超时应该在3钞以上
        try {
            result = InetAddress.getByName(str).isReachable(timeOut);
        } catch (IOException e) {
            Log.e(CommonConstant.LOGCAT_TAG, "ping异常: " + str + result);
        }
        Log.d(CommonConstant.LOGCAT_TAG, "结束ping: " + str + ":" + result);
        return result;
    }
}
