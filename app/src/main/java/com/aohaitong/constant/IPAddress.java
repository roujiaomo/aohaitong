package com.aohaitong.constant;

public class IPAddress {
    //暂定外网一个内网两个

    //    public static String MQ_ADDRESS = "122.9.11.240:8007";

//        public static String NETIP = "http://192.168.1.67:8343";


    public static String socketAddress = "192.168.16.232:8080";
    //本地
//    public static String NET_IP = "http://29d11159e3.zicp.vip:28596";
//    public static String mqAddress = "29d11159e3.zi8cp.vip:30410";
//    public static String registerUrl = NET_IP + "/offshoreCommunicationData/registerAccount";//Long phoneNumber, String password
//    public static String forgetPassword = NET_IP + "/offshoreCommunicationData/accountForgetPassword";//Long phoneNumber, String password
    //数据湖
//    public static String NET_IP = "http://59.46.138.229:52208";
//    public static String NET_IP = "http://59.46.138.229:9900";
//    public static String mqAddress = "59.46.138.229:61616";
//    public static String registerUrl = NET_IP + "/offshoredata-api/offshoreCommunicationData/registerAccount";//Long phoneNumber, String password
//    public static String forgetPassword = NET_IP + "/offshoredata-api/offshoreCommunicationData/accountForgetPassword";//Long phoneNumber, String password

    public static String NET_IP = "http://59.46.138.121:85/api";
    //public static String NET_IP = "http://192.168.1.11:9900";
//    public static String mqAddress = "59.46.138.229:61616";
    public static String mqAddress = "59.46.138.121:92";

    public static String registerUrl = NET_IP + "/offshoredata-api/offshoreCommunicationData/registerAccount";//Long phoneNumber, String password
    public static String forgetPassword = NET_IP + "/offshoredata-api/offshoreCommunicationData/accountForgetPassword";//Long phoneNumber, String password

    public static String getSocketAddress() {
        return socketAddress;
    }

    public static void setSocketAddress(String socketAddressValue) {
        socketAddress = socketAddressValue;
    }


    public static String getMqAddress() {
        return mqAddress;
    }

    public static void setMqAddress(String mqAddressValue) {
        mqAddress = mqAddressValue;
    }

    /**
     * 设置网络的常量
     */

    public static final String MQ_ADDRESS_VALUE = "59.46.138.229:61616";
    public static final String SOCKET_ADDRESS_VALUE = "192.168.16.232:8080";

}
