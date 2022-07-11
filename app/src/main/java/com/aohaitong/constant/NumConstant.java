package com.aohaitong.constant;

/**
 * 控制所有语句的序列号
 */
public class NumConstant {
    private static int HEART_NUM = 0;
    private static int JHD_NUM = 0;
    public static int LOGIN_FAIL_TIME = 10;
    public static int COMMON_FAIL_TIME = 60;
    public static long HEARTBEAT_COMPARE_TIME = 1609430400000L;//小于这个时间,则取系统时间
    public static int SOCKET_CONNECT_CHECK_COUNT = 10;

    public static int getFailTime() {
        return 60;
    }

    public static int getHeartNum() {
        HEART_NUM++;
        return HEART_NUM % 99;
    }

    public static int getJHDNum() {
        JHD_NUM++;
        return JHD_NUM % 199;
    }

}
