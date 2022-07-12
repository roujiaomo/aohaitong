package com.aohaitong.constant;

/**
 * 各种状态
 */
public class StatusConstant {
    //消息发送状态
    public static int SEND_FAIL = 0;
    public static int SEND_SUCCESS = 1;
    public static int SEND_LOADING = 2;

    //收到信息阅读状态
    public static int READ_UNREAD = 0;
    public static int READ_READED = 1;

    //发送还是接收
    public static int SEND_TYPE_SENDER = 0;
    public static int SEND_TYPE_RECEIVER = 1;

    //连接方式是socket还是mq
    public static int CONNECT_SOCKET = 0;
    public static int CONNECT_MQ = 1;
    public static int CONNECT_DISCONNECTED = 2;

    //接收的语句类型
    public static int TYPE_HEART = 0; //心跳消息单拎出来
    public static int TYPE_CHAT_REFRESH = 1; //通知消息页面刷新
    public static int TYPE_FRIEND_REFRESH = 2; //通知好友页面刷新
    public static int TYPE_FRIEND_APPLY_REFRESH = 3; //通知好友申请页面刷新
    public static int TYPE_NO_NET = 6;//断网通知
    public static int TYPE_NET_CONNECTED = 7;//连上网的通知
    public static int TYPE_BROADCAST = 8;//接收到广播
    public static int TYPE_DISMISS_DIALOG = 9;//接收到广播

    //刷新聊天界面
    public static int EVENT_CHAT_SERVICE_REFRESH = 1001; //通知消息页面刷新
    public static int EVENT_CHAT_RECEIVE_MESSAGE = 1002; //通知消息页面刷新
    public static int EVENT_CHAT_RECEIVE_USER_LOGIN_STATUS = 1003; //通知消息页面登录状态更新


    //消息类型,由遨海制定
    public static int MSG_LOGIN = 1;//登录
    public static int MSG_LOGOUT = 2;//退出登录
    public static int MSG_UPDATE_FRIEND_REQUEST = 3;
    public static int MSG_UPDATE_FRIEND = 4;
    public static int MSG_UPDATE_MY_NICKNAME = 5;
    public static int MSG_UPDATE_MY_PASSWORD = 6;
    public static int MSG_SEND_FRIEND_APPLY = 7;
    public static int MSG_RECEIVE_FRIEND_APPLY = 8;
    public static int MSG_SEND_FRIEND_APPLY_RESULT = 9;
    public static int MSG_RECEIVE_FRIEND_APPLY_RESULT = 10;
    public static int MSG_DELETE_FRIEND = 11;
    public static int MSG_RECEIVE_DELETE_FRIEND = 12;
    public static int MSG_EDIT_FRIEND_NICKNAME = 13;
    public static int MSG_SEND_TEXT = 24;
    public static int MSG_RECEIVE_TEXT = 25;
    public static int MSG_FRIEND_NICKNAME_CHANGED = 29;

    public static int MSG_RECEIVE_BROADCAST = 28;
    public static int MSG_SEND_CREATE_GROUP_APPLY = 14;
    public static int MSG_SEND_ADD_GROUP_MEMBER_APPLY = 15;
    public static int MSG_RECEIVE_JOIN_GROUP = 16;
    public static int MSG_RECEIVE_OTHER_JOIN_GROUP = 17;
    public static int MSG_SEND_REMOVE_GROUP_MEMBER_APPLY = 18;
    public static int MSG_RECEIVE_REMOVE_GROUP = 19;
    public static int MSG_SEND_EXIT_GROUP_APPLY = 20;
    public static int MSG_RECEIVE_OTHER_REMOVE_GROUP = 21;
    public static int MSG_SEND_DISSOLVE_GROUP_APPLY = 22;
    public static int MSG_RECEIVE_DISSOLVE_GROUP = 23;

    public static int MSG_SEND_GROUP_MESSAGE_APPLY = 26;
    public static int MSG_RECEIVE_GROUP_MESSAGE = 27;

    public static int MSG_SEND_MODIFY_GROUP_NAME_APPLY = 30;
    public static int MSG_RECEIVE_MODIFY_GROUP_NAME = 31;

    public static int MSG_SEND_GET_GROUP_LIST_APPLY = 32;
    public static int MSG_RECEIVE_GET_GROUP_LIST = 33;
    public static int MSG_SEND_GET_USER_LOGIN_STATUS = 34;
    public static int MSG_RECEIVE_USER_LOGIN_STATUS = 35;

    public static String SUCCESS = "0";
    public static String FAIL = "1";
    public static String NOT_REGISTER = "2";
    public static String SUCCESS_REISSUE = "1/0";

    //分组列表标识标题还是内容item
    public static int ITEM_TYPE_HEADER = 0;
    public static int ITEM_TYPE_CONTENT = 1;
    //好友申请状态
    public static int TYPE_PASS = 0;
    public static int TYPE_UN_PASS = 1;
    public static int TYPE_FORBIDDEN = 2;
    //是否是好友
    public static int TYPE_IS_FRIEND = 0;
    public static int TYPE_IS_NOT_FRIEND = 1;
    //是否有好友申请
    public static int TYPE_HAVE_APPLY = 0;
    public static int TYPE_NOT_HAVE_APPLY = 1;

    //消息类型
    public static int TYPE_TEXT_MESSAGE = 0; //文字消息
    public static int TYPE_RECORD_MESSAGE = 1;//语音消息
    public static int TYPE_PHOTO_MESSAGE = 2;//图片消息
    public static int TYPE_VIDEO_MESSAGE = 3;//视频消息
    public static int TYPE_GROUP_NOTIFY_MESSAGE = 4; //群组内通知消息(创建群聊, 拉人进群, 踢人进群, 解散群聊, 后续可能会有主动进群)

    public static int TAKE_PHOTO_CAMERA = 101;
    public static int TAKE_PHOTO_ALBUM = 102;

    /**
     * BUILD方式
     */
    public static String BUILD_REVIEW = "review";
    public static String BUILD_IN_TEST = "intest";
    public static String BUILD_OFFICIAL = "official";

    //分组列表标识标题还是内容item
    public static int ITEM_FRIEND_LIST_HEADER = 0;
    public static int ITEM_FRIEND_LIST_CONTENT = 1;
    //连接失败
    public static String CONNECTION_SOCKET_BROKEN_PIPE = "Broken pipe";
}
