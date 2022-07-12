package com.aohaitong.business.transmit;

import android.text.TextUtils;
import android.util.Log;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.ChatMsgBean;
import com.aohaitong.bean.FriendApplyBean;
import com.aohaitong.bean.FriendBean;
import com.aohaitong.bean.GroupBean;
import com.aohaitong.bean.GroupEventEntity;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.IPController;
import com.aohaitong.business.request.HeartQueueManager;
import com.aohaitong.business.request.MsgRequest;
import com.aohaitong.business.request.RequestQueueManager;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.utils.BitmapUtils;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.FileUtils;
import com.aohaitong.utils.NotificationUtil;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.utils.StringUtil;
import com.aohaitong.utils.ThreadPoolManager;
import com.aohaitong.utils.audio.RecordManager;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationAccountMessage;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage01;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage02;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage03;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage04;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage05;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage06;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage07;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage08;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage09;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage10;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage11;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage12;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage13;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage14;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage15;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage16;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage17;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage18;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage19;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage20;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage21;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage22;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage23;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage24;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage25;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage26;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage27;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage28;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage29;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage30;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage31;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage32;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage33;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage34;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage35;
import com.aohaitong.utils.offshore.sentence.JHDSentence;
import com.aohaitong.utils.offshore.util.CommVdesMessageUtil;
import com.aohaitong.utils.offshore.util.JhdAnalysisGroupUtil;
import com.aohaitong.utils.offshore.util.JhdAnalysisUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendController {
    private final Map sendListeners = new HashMap();
    private final List<SendCheck> list = new ArrayList<>();
    private static SendController sendController;
    private int CONNECT_TYPE = StatusConstant.CONNECT_SOCKET;
    public static List<String> groupIdList = new ArrayList<>();

    private static class SendCheck {
        private final int num;
        private final long sendTime;
        private final long failTime;

        public SendCheck(int num, long sendTime, long failTime) {
            this.num = num;
            this.sendTime = sendTime;
            this.failTime = failTime;
        }
    }

    /**
     * 设置连接的方式,由初始化ip来确定
     *
     * @param TYPE 连接的方式,mq或者socket或者无网
     */
    public void setTYPE(int TYPE) {
        this.CONNECT_TYPE = TYPE;
    }

    public static SendController getInstance() {
        if (sendController == null) {
            sendController = new SendController();
        }
        return sendController;
    }

    private SendController() {
        EventBus.getDefault().register(this);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkFail();
            }
        }).start();
    }

    /**
     * 发送消息
     *
     * @param listener 发送消息成功失败的回调
     * @param s        发送的消息字符串
     * @param num      发送消息的数字
     */
    public void sendMsg(ISendListener listener, String s, int num, long failTime) {
        Log.d(CommonConstant.LOGCAT_TAG, "发送请求语句: " + s);
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_DISCONNECTED) {
            if (listener != null) {
                listener.sendFail("当前无服务，请稍后再试");
                return;
            }
        }
        if (listener != null) {
            sendListeners.put(num, listener);
            list.add(new SendCheck(num, DateUtil.getInstance().getTime(), failTime));
        }
        if (s.contains("BHM")) {
            HeartQueueManager.getInstance().push(new MsgRequest(s, new MsgRequest.SendCallback() {
                @Override
                public void onFinish() {
                }

                @Override
                public void onError(String message) {
                    if (listener != null) {
                        //发送失败后 remove掉这条的失败检查
                        sendListeners.remove(num);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).num == num) {
                                list.remove(i);
                            }
                        }
                        listener.sendFail("网络异常");
                    }
                }
            }));
        } else {
            RequestQueueManager.getInstance().push(new MsgRequest(s, new MsgRequest.SendCallback() {
                @Override
                public void onFinish() {
                }

                @Override
                public void onError(String message) {
                    if (listener != null) {
                        //发送失败后 remove掉这条的失败检查
                        sendListeners.remove(num);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).num == num) {
                                list.remove(i);
                            }
                        }
                        listener.sendFail("网络异常");
                    }
                }
            }));
        }

    }

    /**
     * 接收消息
     * 有部分是应答消息,有部分是新消息,有部分是心跳消息
     * 这里只处理应答消息以及新消息,心跳消息由心跳管理类处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        if (message.contains("BHM")) {
            Log.d(CommonConstant.LOGCAT_TAG, "接收心跳: " + message);
            try {
                MsgEntity msgEntity = new MsgEntity();
                msgEntity.setType(StatusConstant.TYPE_HEART);
                msgEntity.setMsg(message);
                EventBus.getDefault().post(msgEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JHDSentence jhdSentence = CommVdesMessageUtil.reslvToJHD(message);
                OffshoreCommunicationMessage communicationMessage = jhdSentence.getOffshoreCommunicationMessage();
                Log.d(CommonConstant.LOGCAT_TAG, "接收语句: " + new Gson().toJson(jhdSentence));
                int msgID = communicationMessage.getMsgId();
                if (msgID == StatusConstant.MSG_LOGIN) {//登录
                    OffshoreCommunicationMessage01 o = (OffshoreCommunicationMessage01) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "登录失败");
                    }
                } else if (msgID == StatusConstant.MSG_LOGOUT) {//登出
                    OffshoreCommunicationMessage02 o = (OffshoreCommunicationMessage02) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "退出失败");
                    }
                } else if (msgID == StatusConstant.MSG_UPDATE_FRIEND_REQUEST) {//拉取好友信息
                    OffshoreCommunicationMessage03 o = (OffshoreCommunicationMessage03) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "拉取失败");
                    }
                } else if (msgID == StatusConstant.MSG_UPDATE_FRIEND) {//接收所有好友信息,接收近期的所有好友信息,并发送应答消息,
                    OffshoreCommunicationMessage04 o = (OffshoreCommunicationMessage04) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    SPUtil.instance.putValues(new SPUtil.ContentValue(MyApplication.TEL + CommonConstant.LAST_UPDATE_TIME, DateUtil.getInstance().getTime()));
                    DBManager.getInstance(MyApplication.getContext()).updateFriendList(getFriends(o));
                    BusinessController.sendFriendListAnswer(true, null, communicationMessage.getSequenceNumber());
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));
                } else if (msgID == StatusConstant.MSG_UPDATE_MY_NICKNAME) {//更新自己的昵称
                    OffshoreCommunicationMessage05 o = (OffshoreCommunicationMessage05) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "更新失败");
                    }
                } else if (msgID == StatusConstant.MSG_UPDATE_MY_PASSWORD) {//更新自己的密码
                    OffshoreCommunicationMessage06 o = (OffshoreCommunicationMessage06) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "更新失败");
                    }
                } else if (msgID == StatusConstant.MSG_SEND_FRIEND_APPLY) {//发送好友请求
                    OffshoreCommunicationMessage07 o = (OffshoreCommunicationMessage07) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
//                        insertCreateGroupChatMsg();
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(o.getData()) ? "目标对象未注册" : "发送失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_FRIEND_APPLY) {//接收到好友请求
                    OffshoreCommunicationMessage08 o = (OffshoreCommunicationMessage08) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    MyApplication.msgCount++;
                    NotificationUtil.getInstance().newFriendApplyNotification(o.getSourceAccount(), o.getData());
                    DBManager.getInstance(MyApplication.getContext()).insertFriendApply(
                            new FriendApplyBean("", "", o.getData(), o.getSourceAccount() + "",
                                    DateUtil.getInstance().getTime() + "",
                                    StatusConstant.TYPE_UN_PASS, MyApplication.TEL + "", StatusConstant.SEND_TYPE_RECEIVER, StatusConstant.READ_UNREAD));
                    BusinessController.sendReceiveFriendApplyAnswer(true, o.getSourceAccount(), null, communicationMessage.getSequenceNumber());
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_APPLY_REFRESH));
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));

                } else if (msgID == StatusConstant.MSG_SEND_FRIEND_APPLY_RESULT) {//处理接收到的好友申请
                    OffshoreCommunicationMessage09 o = (OffshoreCommunicationMessage09) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "处理失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_FRIEND_APPLY_RESULT) {//接收自己发送的好友请求的结果
                    // 接收到好友申请结果,更新申请列表以及好友列表,并发送应答消息
                    OffshoreCommunicationMessage10 o = (OffshoreCommunicationMessage10) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        DBManager.getInstance(MyApplication.getContext()).insertFriend(new FriendBean(MyApplication.TEL + "", "", DBManager.getInstance(MyApplication.getContext()).selectFriendApplyNickName(o.getSourceAccount() + ""), o.getSourceAccount() + ""));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));
                    } else {
                        DBManager.getInstance(MyApplication.getContext()).updateFriendApply(communicationMessage.getSequenceNumber() + "", StatusConstant.TYPE_FORBIDDEN);
                    }
                    BusinessController.sendMyFriendApplyResultAnswer(true, o.getSourceAccount(), null, communicationMessage.getSequenceNumber());
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_APPLY_REFRESH));
                } else if (msgID == StatusConstant.MSG_DELETE_FRIEND) {//删除好友信息
                    OffshoreCommunicationMessage11 o = (OffshoreCommunicationMessage11) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(o.getData()) ? "非好友关系" : "删除失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_DELETE_FRIEND) {//接收被删除好友信息的消息
                    // 接收到被删除好友的通知,更新好友列表,并发送应答消息
                    OffshoreCommunicationMessage12 o = (OffshoreCommunicationMessage12) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    DBManager.getInstance(MyApplication.getContext()).deleteFriend(o.getSourceAccount() + "");
                    BusinessController.sendFriendDeletedAnswer(true, o.getSourceAccount(), null, communicationMessage.getSequenceNumber());
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));
                } else if (msgID == StatusConstant.MSG_EDIT_FRIEND_NICKNAME) {//修改好友昵称
                    OffshoreCommunicationMessage13 o = (OffshoreCommunicationMessage13) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(o.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(o.getData()) ? "非好友关系或对方未注册" : "修改失败");
                    }
                } else if (msgID == StatusConstant.MSG_SEND_TEXT) {//收到发送消息的应答
                    OffshoreCommunicationMessage24 msg24 = (OffshoreCommunicationMessage24) communicationMessage;
                    if (msg24.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(msg24.getData())) {//成功
                        Log.e(CommonConstant.LOGCAT_TAG, "收到24号消息的成功应答 : " + new Gson().toJson(msg24));
                        JhdAnalysisUtil.removeSplitMessage(msg24);
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else { //失败
                        //船端语音消息失败只可能是包未收全 1/未接收包1，2,3.... ，岸端失败只有状态1
                        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET
                                && msg24.getMsgType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                            //重发需要补的包
                            if (msg24.getData().equals(StatusConstant.SUCCESS_REISSUE)) { //补包失败
                                Log.e(CommonConstant.LOGCAT_TAG, "24号消息补包3次失败：" + msg24.getData());
                                doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(msg24.getData()) ? "目标对象未注册" : "发送失败");
                                return;
                            }
                            JhdAnalysisUtil.messageReissue(msg24);
                        } else {
                            Log.e(CommonConstant.LOGCAT_TAG, "收到24号消息的失败应答" + new Gson().toJson(msg24));
                            doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(msg24.getData()) ? "目标对象未注册" : "发送失败");
                        }
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_TEXT) {//接收到好友消息
                    OffshoreCommunicationMessage25 msg25 = (OffshoreCommunicationMessage25) communicationMessage;
                    if (msg25.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    Log.e(CommonConstant.LOGCAT_TAG, "收到25号消息" + new Gson().toJson(msg25));
                    //如果是船端接收到语音消息,是逐包接收
                    if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET
                            && msg25.getMsgType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                        JhdAnalysisUtil.addOffshoreCommunicationMessage(msg25);
                    } else {
//                        if (msg25.getMsgType() == 3) {
//                            JhdAnalysisUtil.addOffshoreCommunicationMessage(msg25);
//                        } else {
                        handleReceiveChatMessage(msg25);
//                        }
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_BROADCAST) {
                    // 接收到广播消息,更新数据,并且在主页面显示滚动
                    OffshoreCommunicationMessage28 o = (OffshoreCommunicationMessage28) communicationMessage;
                    MsgEntity msgEntity = new MsgEntity();
                    msgEntity.setType(StatusConstant.TYPE_BROADCAST);
                    DBManager.getInstance(MyApplication.getContext()).addBroad(o.getData(), o.getBusinessType(), DateUtil.getInstance().getTime());
                    EventBus.getDefault().post(msgEntity);
                } else if (msgID == StatusConstant.MSG_FRIEND_NICKNAME_CHANGED) {
                    // 接收到好友昵称修改消息
                    OffshoreCommunicationMessage29 o = (OffshoreCommunicationMessage29) communicationMessage;
                    if (o.getDestinationAccount() != MyApplication.TEL)
                        return;
                    DBManager.getInstance(MyApplication.getContext()).updateFriendName(o.getSourceAccount() + "", o.getData());
                    BusinessController.sendFriendNickNameChangesReceiveAnswer(true, o.getSourceAccount(), null, o.getSequenceNumber());
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                } else if (msgID == StatusConstant.MSG_SEND_CREATE_GROUP_APPLY) {//创建群聊的应答
                    OffshoreCommunicationMessage14 msg14 = (OffshoreCommunicationMessage14) communicationMessage;
                    if (msg14.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    if (!msg14.getData().equals("0")) {
                        DBManager.getInstance(MyApplication.getContext())
                                .createGroup(msg14.getGroupId().toString(), msg14.getGroupName(),
                                        msg14.getData(), MyApplication.TEL + "");
                        //插入创建群聊的聊天消息
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                        EventBus.getDefault().post(new GroupEventEntity(14, msg14.getGroupId().toString()));
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(14, msg14, msg14.getGroupId());
                                groupIdList.add(msg14.getGroupId().toString());
                            }
                        }.start();
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "创建群组失败");
                    }
                } else if (msgID == StatusConstant.MSG_SEND_ADD_GROUP_MEMBER_APPLY) {//群主拉人入群后的应答
                    OffshoreCommunicationMessage15 msg15 = (OffshoreCommunicationMessage15) communicationMessage;
                    if (msg15.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    String responseStatus = msg15.getData().split(",")[0];
                    if (responseStatus.equals("0")) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(15, msg15, msg15.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "添加群组成员失败");
                    }

                } else if (msgID == StatusConstant.MSG_RECEIVE_JOIN_GROUP) { //被拉入群聊
                    //接收到被拉入群聊的消息
                    OffshoreCommunicationMessage16 msg16 = (OffshoreCommunicationMessage16)
                            communicationMessage;
                    if (isMqLogin() && msg16.getDestinationAccount() == MyApplication.TEL) {
                        //获取群主信息(data第一位)
                        String[] groupMembers = msg16.getData().split("/");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < groupMembers.length; i++) {
                            if (i == 0) {
                                continue;
                            }
                            sb.append(groupMembers[i]).append("/");
                        }
                        DBManager.getInstance(MyApplication.getContext()).createGroup(
                                msg16.getGroupId().toString(),
                                msg16.getGroupName(),
                                sb.toString(),
                                groupMembers[0]
                        );
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(16, msg16, msg16.getGroupId());
                                if (!groupIdList.contains(msg16.getGroupId().toString())) {
                                    groupIdList.add(msg16.getGroupId().toString());
                                }
                                EventBus.getDefault().post(new GroupEventEntity(16, ""));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                            }
                        }.start();
                        BusinessController.sendJoinGroupAnswer(true, null, msg16, communicationMessage.getSequenceNumber());
                    }

                    if (!isMqLogin() && msg16.getFriendAccount() != MyApplication.TEL) { //拉人的人不是我
                        if (msg16.getData().contains(",")) {
                            if (msg16.getData().split(",")[1].contains(MyApplication.TEL + "")) {
                                //被拉的人
                                String[] groupMembers = msg16.getData().split(",")[0].split("/");
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < groupMembers.length; i++) {
                                    if (i == 0) {
                                        continue;
                                    }
                                    sb.append(groupMembers[i]).append("/");
                                }
                                DBManager.getInstance(MyApplication.getContext()).createGroup(
                                        msg16.getGroupId().toString(),
                                        msg16.getGroupName(),
                                        sb.toString(),
                                        groupMembers[0]
                                );
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        insertCreateGroupChatMsg(16, msg16, msg16.getGroupId());
                                        if (!groupIdList.contains(msg16.getGroupId().toString())) {
                                            groupIdList.add(msg16.getGroupId().toString());
                                        }
                                        EventBus.getDefault().post(new GroupEventEntity(16, ""));
                                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                    }
                                }.start();
                            }
                        } else {
                            if (msg16.getData().contains(MyApplication.TEL + "")) {
                                String[] groupMembers = msg16.getData().split("/");
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < groupMembers.length; i++) {
                                    if (i == 0) {
                                        continue;
                                    }
                                    sb.append(groupMembers[i]).append("/");
                                }
                                DBManager.getInstance(MyApplication.getContext()).createGroup(
                                        msg16.getGroupId().toString(),
                                        msg16.getGroupName(),
                                        sb.toString(),
                                        groupMembers[0]
                                );
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        insertCreateGroupChatMsg(16, msg16, msg16.getGroupId());
                                        if (!groupIdList.contains(msg16.getGroupId().toString())) {
                                            groupIdList.add(msg16.getGroupId().toString());
                                        }
                                        EventBus.getDefault().post(new GroupEventEntity(16, ""));
                                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                    }
                                }.start();
                            }
                        }
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_OTHER_JOIN_GROUP) { //其他群组成员接收到新人入群
                    OffshoreCommunicationMessage17 msg17 = (OffshoreCommunicationMessage17) communicationMessage;
                    if (isMqLogin() && msg17.getDestinationAccount() == MyApplication.TEL) {
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).updateGroupMember(msg17.getGroupId().toString(), msg17.getData());
                        BusinessController.sendOtherJoinGroupAnswer(true, null, msg17, communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(17, msg17, msg17.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    }

                    if (!isMqLogin() && groupIdList.contains(msg17.getGroupId().toString()) && msg17.getFriendAccount() != MyApplication.TEL) {
                        if (!msg17.getData().contains(MyApplication.TEL + "")) {
                            //更新本地群聊表信息
                            DBManager.getInstance(MyApplication.getContext()).updateGroupMember(msg17.getGroupId().toString(), msg17.getData());
                            BusinessController.sendOtherJoinGroupAnswer(true, null, msg17, communicationMessage.getSequenceNumber());
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    insertCreateGroupChatMsg(17, msg17, msg17.getGroupId());
                                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                                }
                            }.start();
                        }
                    }

                } else if (msgID == StatusConstant.MSG_SEND_REMOVE_GROUP_MEMBER_APPLY) { //群主移除群成员的应答
                    OffshoreCommunicationMessage18 msg18 = (OffshoreCommunicationMessage18) communicationMessage;
                    if (msg18.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    String responseStatus = msg18.getData().split(",")[0];
                    if (responseStatus.equals("0")) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertRemoveGroupChatMsg(18, msg18, msg18.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "移除群成员失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_REMOVE_GROUP) { //接收到自己被移除群聊
                    OffshoreCommunicationMessage19 msg19 = (OffshoreCommunicationMessage19) communicationMessage;
                    if (isMqLogin() && msg19.getDestinationAccount() == MyApplication.TEL) {
                        groupIdList.remove(msg19.getGroupId().toString());
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).deleteGroup(msg19.getGroupId().toString());
                        //我的群聊界面刷新
                        EventBus.getDefault().post(new GroupEventEntity(19, msg19.getGroupId().toString()));
                        DBManager.getInstance(MyApplication.getContext()).delMsgByTel(msg19.getGroupId().toString(), true);
                        //首页消息刷新(正常应该弹对话框)
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                        //暂时删除对应聊天记录
                        BusinessController.sendRemoveGroupAnswer(true, null, msg19, communicationMessage.getSequenceNumber());
                    }

                    if (!isMqLogin() && msg19.getData().contains(MyApplication.TEL + "") && groupIdList.contains(msg19.getGroupId().toString())) {
                        groupIdList.remove(msg19.getGroupId().toString());
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).deleteGroup(msg19.getGroupId().toString());
                        //我的群聊界面刷新
                        EventBus.getDefault().post(new GroupEventEntity(19, msg19.getGroupId().toString()));
                        DBManager.getInstance(MyApplication.getContext()).delMsgByTel(msg19.getGroupId().toString(), true);
                        //首页消息刷新(正常应该弹对话框)
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                        //暂时删除对应聊天记录
                        BusinessController.sendRemoveGroupAnswer(true, null, msg19, communicationMessage.getSequenceNumber());
                    }
                } else if (msgID == StatusConstant.MSG_SEND_EXIT_GROUP_APPLY) { //群成员退群的应答
                    OffshoreCommunicationMessage20 msg20 = (OffshoreCommunicationMessage20) communicationMessage;
                    if (msg20.getData().equals("0")) {
                        groupIdList.remove(msg20.getGroupId().toString());
                        DBManager.getInstance(MyApplication.getContext()).deleteGroup(msg20.getGroupId().toString());
                        DBManager.getInstance(MyApplication.getContext()).delMsgByTel(msg20.getGroupId().toString(), true);
                        EventBus.getDefault().post(new GroupEventEntity(20, msg20.getGroupId().toString()));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                        //也应删除对应聊天表及聊天记录
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "退出群聊失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_OTHER_REMOVE_GROUP) { //群组内其他人接收到有人被移除群聊
                    OffshoreCommunicationMessage21 msg21 = (OffshoreCommunicationMessage21) communicationMessage;
                    if (isMqLogin() && msg21.getDestinationAccount() == MyApplication.TEL) {
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).removeGroupMember(msg21.getGroupId().toString(), msg21.getData().split(",")[0]);
                        BusinessController.sendOtherRemoveGroupAnswer(true, null, msg21, communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertRemoveGroupChatMsg(21, msg21, msg21.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    }

                    if (!isMqLogin() && groupIdList.contains(msg21.getGroupId().toString())) {
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).removeGroupMember(msg21.getGroupId().toString(), msg21.getData().split(",")[0]);
                        BusinessController.sendOtherRemoveGroupAnswer(true, null, msg21, communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertRemoveGroupChatMsg(21, msg21, msg21.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    }
                } else if (msgID == StatusConstant.MSG_SEND_DISSOLVE_GROUP_APPLY) { //群主解散群聊的应答
                    OffshoreCommunicationMessage22 msg22 = (OffshoreCommunicationMessage22) communicationMessage;
                    if (msg22.getData().equals("0")) {
                        groupIdList.remove(msg22.getGroupId().toString());
                        DBManager.getInstance(MyApplication.getContext()).deleteGroup(msg22.getGroupId().toString());
                        EventBus.getDefault().post(new GroupEventEntity(22, msg22.getGroupId().toString()));
                        //也应删除对应聊天表及聊天记录
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "解散群聊失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_DISSOLVE_GROUP) { //群内其他人收到解散群聊的消息
                    OffshoreCommunicationMessage23 msg23 = (OffshoreCommunicationMessage23) communicationMessage;
                    if (isMqLogin() && msg23.getDestinationAccount() == MyApplication.TEL) {
                        groupIdList.remove(msg23.getGroupId().toString());
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).deleteGroup(msg23.getGroupId().toString());
                        DBManager.getInstance(MyApplication.getContext()).delMsgByTel(msg23.getGroupId().toString(), true);
                        EventBus.getDefault().post(new GroupEventEntity(23, msg23.getGroupId().toString()));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                        BusinessController.sendDeleteGroupAnswer(true, null, msg23, communicationMessage.getSequenceNumber());
                    }
                    if (!isMqLogin() && groupIdList.contains(msg23.getGroupId().toString())) {
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).deleteGroup(msg23.getGroupId().toString());
                        DBManager.getInstance(MyApplication.getContext()).delMsgByTel(msg23.getGroupId().toString(), true);
                        EventBus.getDefault().post(new GroupEventEntity(23, msg23.getGroupId().toString()));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                        BusinessController.sendDeleteGroupAnswer(true, null, msg23, communicationMessage.getSequenceNumber());
                    }
                } else if (msgID == StatusConstant.MSG_SEND_MODIFY_GROUP_NAME_APPLY) { //群成员修改群名的应答
                    OffshoreCommunicationMessage30 msg30 = (OffshoreCommunicationMessage30) communicationMessage;
                    if (msg30.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    if (msg30.getData().equals("0")) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(30, msg30, msg30.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "修改群名失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_MODIFY_GROUP_NAME) { //群内其他人收到修改群名的消息
                    OffshoreCommunicationMessage31 msg31 = (OffshoreCommunicationMessage31) communicationMessage;
                    if (isMqLogin() && msg31.getDestinationAccount() == MyApplication.TEL) {
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).updateGroupName(msg31.getGroupId().toString(), msg31.getData());
                        EventBus.getDefault().post(new GroupEventEntity(31, msg31.getGroupId().toString()));
                        BusinessController.sendModifyGroupNameAnswer(true, null, msg31, communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(31, msg31, msg31.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    }

                    if (!isMqLogin() && groupIdList.contains(msg31.getGroupId().toString())) {
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext()).updateGroupName(msg31.getGroupId().toString(), msg31.getData());
                        EventBus.getDefault().post(new GroupEventEntity(31, msg31.getGroupId().toString()));
                        BusinessController.sendModifyGroupNameAnswer(true, null, msg31, communicationMessage.getSequenceNumber());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                insertCreateGroupChatMsg(31, msg31, msg31.getGroupId());
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));
                            }
                        }.start();
                    }
                } else if (msgID == StatusConstant.MSG_SEND_GROUP_MESSAGE_APPLY) { //发送群聊消息
                    OffshoreCommunicationMessage26 msg26 = (OffshoreCommunicationMessage26) communicationMessage;
                    if (msg26.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(msg26.getData())) {//成功
                        Log.e(CommonConstant.LOGCAT_TAG, "收到26号消息的成功应答 : " + new Gson().toJson(msg26));
                        JhdAnalysisGroupUtil.removeSplitMessage(msg26);
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else { //失败
                        //船端语音消息失败只可能是包未收全 1/未接收包1，2,3.... ，岸端失败只有状态1
                        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET
                                && msg26.getMsgType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                            //重发需要补的包
                            if (msg26.getData().equals(StatusConstant.SUCCESS_REISSUE)) { //补包失败
                                Log.e(CommonConstant.LOGCAT_TAG, "26号消息补包3次失败：" + msg26.getData());
                                doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(msg26.getData()) ? "目标对象未注册" : "发送失败");
                                return;
                            }
                            JhdAnalysisGroupUtil.messageReissue(msg26);
                        } else {
                            Log.e(CommonConstant.LOGCAT_TAG, "收到26号消息的失败应答" + new Gson().toJson(msg26));
                            doListenerError(communicationMessage.getSequenceNumber(), StatusConstant.NOT_REGISTER.equals(msg26.getData()) ? "目标对象未注册" : "发送失败");
                        }
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_GROUP_MESSAGE) {//接收到群聊消息
                    OffshoreCommunicationMessage27 msg27 = (OffshoreCommunicationMessage27) communicationMessage;
                    Log.e(CommonConstant.LOGCAT_TAG, "收到27号消息" + new Gson().toJson(msg27));
                    if (msg27.getSourceAccount() == MyApplication.TEL) {
                        return;
                    }
                    if (isMqLogin() && msg27.getDestinationAccount() == MyApplication.TEL) {
//                        //如果是船端接收到语音消息,是逐包接收
//                        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET
//                                && msg27.getMsgType() != StatusConstant.TYPE_TEXT_MESSAGE) {
//                            JhdAnalysisGroupUtil.addOffshoreCommunicationMessage(msg27);
//                        } else {
//                            handleReceiveGroupChatMessage(msg27);
//                        }
                        handleReceiveGroupChatMessage(msg27);
                    }   //船端收到群组消息 本地包含该群组
                    if (!isMqLogin() && groupIdList.contains(msg27.getGroupId().toString())) {
                        if (msg27.getDestinationAccount() != MyApplication.TEL) {
                            if (msg27.getMsgType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                                JhdAnalysisGroupUtil.addOffshoreCommunicationMessage(msg27, false);
                            } else {
                                handleReceiveGroupChatMessage(msg27);
                            }
                            //其他人发的消息,设定自己为补包者
                        } else {
                            if (msg27.getMsgType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                                JhdAnalysisGroupUtil.addOffshoreCommunicationMessage(msg27, true);
                            } else {
                                handleReceiveGroupChatMessage(msg27);
                            }
                        }
                    }
                } else if (msgID == StatusConstant.MSG_SEND_GET_GROUP_LIST_APPLY) {//拉取群组信息
                    OffshoreCommunicationMessage32 msg32 = (OffshoreCommunicationMessage32) communicationMessage;
                    Log.e(CommonConstant.LOGCAT_TAG, "收到32号消息" + new Gson().toJson(msg32));
                    if (msg32.getDestinationAccount() != MyApplication.TEL)
                        return;
                    if (StatusConstant.SUCCESS.equals(msg32.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "拉取失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_GET_GROUP_LIST) {//更新群组信息
                    OffshoreCommunicationMessage33 msg33 = (OffshoreCommunicationMessage33) communicationMessage;
                    if (msg33.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    Log.e(CommonConstant.LOGCAT_TAG, "收到33号消息" + new Gson().toJson(msg33));
                    if (msg33.getDestinationAccount() != MyApplication.TEL)
                        return;
                    //更新本地库 回复应答
                    handleUpdateGroupInfo(msg33.getData());
                } else if (msgID == StatusConstant.MSG_SEND_GET_USER_LOGIN_STATUS) {//更新群组信息
                    OffshoreCommunicationMessage34 msg34 = (OffshoreCommunicationMessage34) communicationMessage;
                    if (msg34.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    Log.e(CommonConstant.LOGCAT_TAG, "收到34号消息" + new Gson().toJson(msg34));
                    if (StatusConstant.SUCCESS.equals(msg34.getData())) {
                        doListenerSuccess(communicationMessage.getSequenceNumber());
                    } else {
                        doListenerError(communicationMessage.getSequenceNumber(), "获取失败");
                    }
                } else if (msgID == StatusConstant.MSG_RECEIVE_USER_LOGIN_STATUS) {//更新群组信息
                    OffshoreCommunicationMessage35 msg35 = (OffshoreCommunicationMessage35) communicationMessage;
                    if (msg35.getDestinationAccount() != MyApplication.TEL) {
                        return;
                    }
                    Log.e(CommonConstant.LOGCAT_TAG, "收到35号消息" + new Gson().toJson(msg35));
                    EventBus.getDefault().post(new MsgEntity(msg35.getData(), StatusConstant.EVENT_CHAT_RECEIVE_USER_LOGIN_STATUS));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleReceiveChatMessage(OffshoreCommunicationMessage25 msg25) {
        //本地聊天表存储数据,不需要包数相关
        ChatMsgBean bean = new ChatMsgBean();
        bean.setSendType(StatusConstant.SEND_TYPE_RECEIVER);
        bean.setNowLoginTel(MyApplication.TEL + "");
        bean.setTime(DateUtil.getInstance().getTime(msg25.getYear(), msg25.getMonth(),
                msg25.getDay(), msg25.getHour(), msg25.getMinute(), msg25.getSecond()) + "");
        bean.setStatus(StatusConstant.READ_UNREAD);
        bean.setTelephone(msg25.getSourceAccount() + "");
        if (msg25.getMsgType() == 0) {
            bean.setMsg(msg25.getData());
        } else {
            bean.setMsg("");
        }
        bean.setMessageType(msg25.getMsgType());
        bean.setIsGroup(false);
        bean.setGroupId("");
        bean.setInsertTime(new Date());
        if (bean.getMessageType() == StatusConstant.TYPE_RECORD_MESSAGE) { // 1 语音消息
            bean.setRecordTime(msg25.getTimeLong());
            //存储音频文件到本地
            String filePath = RecordManager.getInstance().RECORD_FILE_PATH + FileUtils.generateFileName("amr");
            bean.setFilePath(filePath);
            ThreadPoolManager.getInstance().execute(() -> {
                FileUtils.stringToFile(msg25.getData(), filePath);
            });
        } else if (bean.getMessageType() == StatusConstant.TYPE_PHOTO_MESSAGE) {
            String filePath = BitmapUtils.DEFAULT_FILE_PATH + FileUtils.generateFileName("jpg");
            bean.setFilePath(filePath);
            ThreadPoolManager.getInstance().execute(() -> {
                FileUtils.stringToFile(msg25.getData(), filePath);
            });
        } else if (bean.getMessageType() == StatusConstant.TYPE_VIDEO_MESSAGE) {
            String filePath = BitmapUtils.DEFAULT_FILE_PATH + FileUtils.generateFileName("mp4");
            bean.setFilePath(filePath);
            ThreadPoolManager.getInstance().execute(() -> {
                FileUtils.stringToFile(msg25.getData(), filePath);
            });
        }
        List<ChatMsgBean> chatMsgBeanList = DBManager.getInstance(MyApplication.getContext()).
                getNewsMsgLimit(msg25.getSourceAccount().toString());
        boolean isExistMessage = false;
        for (ChatMsgBean chatMsgBean : chatMsgBeanList) {
            if (chatMsgBean.getTime().equals(bean.getTime()) && chatMsgBean.getMsg().equals(bean.getMsg())) {
                isExistMessage = true;
            }
        }
        if (isExistMessage) {
            return;
        }
        DBManager.getInstance(MyApplication.getContext()).createMsg(bean);
        // 接收新消息,更新db以及页面,并且在后台的时候通知显示未读消息数量角标,并发送应答消息
        if (MyApplication.isBackGround) {
            FriendBean friendBean = DBManager.getInstance(MyApplication.getContext()).selectFriend(msg25.getSourceAccount() + "");
            MyApplication.msgCount++;
            //文字消息显示通知内容
            String noticeContent;
            if (msg25.getMsgType() == StatusConstant.TYPE_VIDEO_MESSAGE) {
                noticeContent = "[视频]";
            } else if (msg25.getMsgType() == StatusConstant.TYPE_PHOTO_MESSAGE) {
                noticeContent = "[图片]";
            } else if (msg25.getMsgType() == StatusConstant.TYPE_RECORD_MESSAGE) {
                noticeContent = "[语音]";
            } else {
                noticeContent = msg25.getData();
            }
            NotificationUtil.getInstance().newMsgNotification(noticeContent, msg25.getSourceAccount(), friendBean == null ? "" : friendBean.getName(), friendBean == null ? "" : friendBean.getNickName());
            NotificationUtil.getInstance().playVoice();
            NotificationUtil.getInstance().playVibrator();
        }
        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
        EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));

        Log.e(CommonConstant.LOGCAT_TAG, "25号消息接收成功，应答：" + new Gson().toJson(msg25));
        BusinessController.sendMsgReceiveAnswer(true, msg25,
                null, msg25.getSequenceNumber());
    }

    public void handleReceiveGroupChatMessage(OffshoreCommunicationMessage27 msg27) {
        //本地聊天表存储数据,不需要包数相关
        ChatMsgBean bean = new ChatMsgBean();
        bean.setSendType(StatusConstant.SEND_TYPE_RECEIVER);
        bean.setNowLoginTel(MyApplication.TEL + "");
        bean.setTelephone(msg27.getSourceAccount().toString());
        bean.setTime(DateUtil.getInstance().getTime(msg27.getYear(), msg27.getMonth(),
                msg27.getDay(), msg27.getHour(), msg27.getMinute(), msg27.getSecond()) + "");
        bean.setStatus(StatusConstant.READ_UNREAD);
        bean.setGroupId(msg27.getGroupId().toString());
        if (!TextUtils.isEmpty(msg27.getGroupId().toString())) {
            bean.setIsGroup(true);
        }
        bean.setTelephone(msg27.getSourceAccount() + "");
        if (msg27.getMsgType() == 0) {
            bean.setMsg(msg27.getData());
        } else {
            bean.setMsg("");
        }
        bean.setMessageType(msg27.getMsgType());
        bean.setInsertTime(new Date());
        bean.setIsGroup(true);
        if (bean.getMessageType() == StatusConstant.TYPE_RECORD_MESSAGE) { // 1 语音消息
            bean.setRecordTime(msg27.getTimeLong());
            //存储音频文件到本地
            String filePath = RecordManager.getInstance().RECORD_FILE_PATH + FileUtils.generateFileName("amr");
            bean.setFilePath(filePath);
            ThreadPoolManager.getInstance().execute(() -> {
                FileUtils.stringToFile(msg27.getData(), filePath);
            });
        } else if (bean.getMessageType() == StatusConstant.TYPE_PHOTO_MESSAGE) {
            String filePath = BitmapUtils.DEFAULT_FILE_PATH + FileUtils.generateFileName("jpg");
            bean.setFilePath(filePath);
            ThreadPoolManager.getInstance().execute(() -> {
                FileUtils.stringToFile(msg27.getData(), filePath);
            });
        } else if (bean.getMessageType() == StatusConstant.TYPE_VIDEO_MESSAGE) {
            String filePath = BitmapUtils.DEFAULT_FILE_PATH + FileUtils.generateFileName("mp4");
            bean.setFilePath(filePath);
            ThreadPoolManager.getInstance().execute(() -> {
                FileUtils.stringToFile(msg27.getData(), filePath);
            });
        }
        List<ChatMsgBean> chatMsgBeanList = DBManager.getInstance(MyApplication.getContext()).
                getGroupMessageLimitByGroupId(msg27.getGroupId());
        boolean isExistMessage = false;
        for (ChatMsgBean chatMsgBean : chatMsgBeanList) {
            if (chatMsgBean.getTime().equals(bean.getTime()) && chatMsgBean.getMsg().equals(bean.getMsg())) {
                isExistMessage = true;
            }
        }
        if (isExistMessage) {
            return;
        }
        DBManager.getInstance(MyApplication.getContext()).createMsg(bean);
        // 接收新消息,更新db以及页面,并且在后台的时候通知显示未读消息数量角标,并发送应答消息
        if (MyApplication.isBackGround) {
            //显示在通知里的群组内容 -------------------
            FriendBean friendBean = DBManager.getInstance(MyApplication.getContext()).selectFriend(msg27.getSourceAccount() + "");
            MyApplication.msgCount++;
            //文字消息显示通知内容
            String noticeContent;
            if (msg27.getMsgType() == StatusConstant.TYPE_VIDEO_MESSAGE) {
                noticeContent = "[视频]";
            } else if (msg27.getMsgType() == StatusConstant.TYPE_PHOTO_MESSAGE) {
                noticeContent = "[图片]";
            } else if (msg27.getMsgType() == StatusConstant.TYPE_RECORD_MESSAGE) {
                noticeContent = "[语音]";
            } else {
                noticeContent = msg27.getData();
            }
            NotificationUtil.getInstance().newGroupMsgNotification(noticeContent, msg27.getGroupId().toString(),
                    DBManager.getInstance(MyApplication.getContext()).getGroupNameById(msg27.getGroupId().toString()));
            NotificationUtil.getInstance().playVoice();
            NotificationUtil.getInstance().playVibrator();
        }
        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
        EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE));

        Log.e(CommonConstant.LOGCAT_TAG, "27号消息接收成功" + new Gson().toJson(msg27));
    }

    private void handleUpdateGroupInfo(String groupInfoData) {
        List<GroupBean> groupBeanList = new ArrayList<>();
        String[] groupStringList = groupInfoData.split(";");
        for (int i = 0; i < groupStringList.length; i++) {
            String[] groupString = groupStringList[i].split("/");
            StringBuilder stringBuilder = new StringBuilder();
            for (int r = 3; r < groupString.length; r++) {
                stringBuilder.append(groupString[r]).append("/");
            }
            GroupBean groupBean = new GroupBean(
                    null, groupString[0], groupString[1], stringBuilder.toString(), groupString[2]
            );
            groupBeanList.add(groupBean);
            groupIdList.add(groupBean.getGroupId());
        }
        DBManager.getInstance(MyApplication.getContext()).updateGroupList(groupBeanList);
    }

    /**
     * 创建完群聊后, 在聊天表里加入 "xx 拉入 xx,xx入群" 的消息
     * (包含创建群聊/被拉入群聊/群组内成员接收被拉入新人/修改群名)
     */
    private void insertCreateGroupChatMsg(int msgId, OffshoreCommunicationAccountMessage msg, Long groupId) {
        ChatMsgBean bean = new ChatMsgBean();
        bean.setSendType(StatusConstant.SEND_TYPE_RECEIVER);
        bean.setNowLoginTel(MyApplication.TEL + "");
        bean.setTime(DateUtil.getInstance().getTime(msg.getYear(), msg.getMonth(),
                msg.getDay(), msg.getHour(), msg.getMinute(), msg.getSecond()) + "");
        bean.setStatus(StatusConstant.READ_READED);
        bean.setIsGroup(true);
        bean.setMessageType(StatusConstant.TYPE_GROUP_NOTIFY_MESSAGE);
        bean.setInsertTime(new Date());
        switch (msgId) {
            case 14: //创建群聊
                OffshoreCommunicationMessage14 msg14 = (OffshoreCommunicationMessage14) msg;
                bean.setGroupId(msg14.getGroupId().toString());
                bean.setTelephone(msg.getDestinationAccount().toString() + "/" + msg14.getData());
                String[] groupMembers14 = msg14.getData().split("/");
                bean.setMsg("您邀请了" + getGroupMember(groupMembers14) + "进入群聊");
                break;
            case 15: //邀请xx,xx进入群聊
                OffshoreCommunicationMessage15 msg15 = (OffshoreCommunicationMessage15) msg;
                bean.setGroupId(msg15.getGroupId().toString());
                bean.setTelephone(msg.getDestinationAccount().toString() + "/" + msg15.getData());
                String[] groupMembers15 = msg15.getData().split(",")[1].split("/");
                bean.setMsg("您邀请了" + getGroupMember(groupMembers15) + "进入群聊");
                break;
            case 16: //被邀请进入群聊
                OffshoreCommunicationMessage16 msg16 = (OffshoreCommunicationMessage16) msg;
                bean.setGroupId(msg16.getGroupId().toString());
                bean.setTelephone(msg16.getFriendAccount().toString() + "/" + msg16.getData());
                FriendBean groupManager16 = DBManager.getInstance(MyApplication.getContext()).selectFriend(msg16.getFriendAccount().toString());
                String groupManagerName16 = msg16.getFriendAccount().toString();//拉人手机号
                if (groupManager16 != null) {
                    groupManagerName16 = StringUtil.getFirstNotNullString(
                            new String[]{
                                    groupManager16.getNickName(), groupManager16.getName(), groupManager16.getTelephone()
                            }
                    );
                }
                String[] groupMembers16;
                if (msg16.getData().contains(",")) {
                    groupMembers16 = msg16.getData().split(",")[1].split("/");
                } else {
                    groupMembers16 = msg16.getData().split("/");
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < groupMembers16.length; i++) {
                    if (i == 0) {
                        continue;
                    }
                    sb.append(groupMembers16[i]).append("/");
                }
                bean.setMsg(groupManagerName16 + "邀请了您进入群聊");
                break;
            case 17: //群内成员接收到 xx被邀请入群
                OffshoreCommunicationMessage17 msg17 = (OffshoreCommunicationMessage17) msg;
                bean.setGroupId(msg17.getGroupId().toString());
                bean.setTelephone(msg17.getFriendAccount().toString() + "/" + msg17.getData());
                FriendBean groupManager17 = DBManager.getInstance(MyApplication.getContext()).selectFriend(msg17.getFriendAccount().toString());
                String groupManagerName17 = msg17.getFriendAccount().toString();
                if (groupManager17 != null) {
                    groupManagerName17 = StringUtil.getFirstNotNullString(
                            new String[]{
                                    groupManager17.getNickName(), groupManager17.getName(), groupManager17.getTelephone()
                            }
                    );
                }
                String[] groupMembers17 = msg17.getData().split("/");
                bean.setMsg(groupManagerName17 + "邀请了" + getGroupMember(groupMembers17) + "进入群聊");
                break;
            case 30: //修改群名
                OffshoreCommunicationMessage30 msg30 = (OffshoreCommunicationMessage30) msg;
                bean.setGroupId(msg30.getGroupId().toString());
                bean.setTelephone(msg30.getDestinationAccount().toString());
                bean.setMsg("您修改群名为" + "\"" + msg30.getGroupName() + "\"");
                break;
            case 31://群成员接收修改群名
                OffshoreCommunicationMessage31 msg31 = (OffshoreCommunicationMessage31) msg;
                bean.setGroupId(msg31.getGroupId().toString());
                bean.setTelephone(msg31.getFriendAccount().toString());
                String groupMemberShowName =
                        DBManager.getInstance(MyApplication.getContext()).getTelephoneShowName(msg31.getFriendAccount().toString());
                bean.setMsg(groupMemberShowName + "修改群名为" + "\"" + msg31.getData() + "\"");
                break;
        }
        List<ChatMsgBean> chatMsgBeanList = DBManager.getInstance(MyApplication.getContext()).getGroupMessageLimitByGroupId(groupId);
        boolean isExistMessage = false;
        for (ChatMsgBean chatMsgBean : chatMsgBeanList) {
            if (chatMsgBean.getTime().equals(bean.getTime()) && chatMsgBean.getMsg().equals(bean.getMsg())) {
                isExistMessage = true;
            }
        }
        if (!isExistMessage) {
            DBManager.getInstance(MyApplication.getContext()).createMsg(bean);
        }
    }

    private void insertAddFriendChatMsg(int msgId, OffshoreCommunicationAccountMessage msg, Long groupId) {
        ChatMsgBean bean = new ChatMsgBean();
        bean.setSendType(StatusConstant.SEND_TYPE_RECEIVER);
        bean.setNowLoginTel(MyApplication.TEL + "");
        bean.setTime(DateUtil.getInstance().getTime(msg.getYear(), msg.getMonth(),
                msg.getDay(), msg.getHour(), msg.getMinute(), msg.getSecond()) + "");
        bean.setStatus(StatusConstant.READ_READED);
        bean.setIsGroup(true);
        bean.setMessageType(StatusConstant.TYPE_GROUP_NOTIFY_MESSAGE);
        bean.setInsertTime(new Date());
        switch (msgId) {
            case 7: //添加好友
                OffshoreCommunicationMessage14 msg14 = (OffshoreCommunicationMessage14) msg;
                bean.setGroupId(msg14.getGroupId().toString());
                bean.setTelephone(msg.getDestinationAccount().toString() + "/" + msg14.getData());
                String[] groupMembers14 = msg14.getData().split("/");
                bean.setMsg("您邀请了" + getGroupMember(groupMembers14) + "进入群聊");
                break;
        }
        List<ChatMsgBean> chatMsgBeanList = DBManager.getInstance(MyApplication.getContext()).getGroupMessageLimitByGroupId(groupId);
        boolean isExistMessage = false;
        for (ChatMsgBean chatMsgBean : chatMsgBeanList) {
            if (chatMsgBean.getTime().equals(bean.getTime()) && chatMsgBean.getMsg().equals(bean.getMsg())) {
                isExistMessage = true;
            }
        }
        if (!isExistMessage) {
            DBManager.getInstance(MyApplication.getContext()).createMsg(bean);
        }
    }

    /**
     * 群成员离开的提示消息聊天记录
     * (包含群主移除群成员/群成员从群聊退出/群组内成员接收其他群成员被移除或退出)
     */
    private void insertRemoveGroupChatMsg(int msgId, OffshoreCommunicationAccountMessage msg, Long groupId) {
        ChatMsgBean bean = new ChatMsgBean();
        bean.setSendType(StatusConstant.SEND_TYPE_RECEIVER);
        bean.setNowLoginTel(MyApplication.TEL + "");
        bean.setTime(DateUtil.getInstance().getTime(msg.getYear(), msg.getMonth(),
                msg.getDay(), msg.getHour(), msg.getMinute(), msg.getSecond()) + "");
        bean.setStatus(StatusConstant.READ_READED);
        bean.setIsGroup(true);
        bean.setMessageType(StatusConstant.TYPE_GROUP_NOTIFY_MESSAGE);
        bean.setInsertTime(new Date());
        switch (msgId) {
            case 18: //群主移除xx离开群聊
                OffshoreCommunicationMessage18 msg18 = (OffshoreCommunicationMessage18) msg;
                bean.setGroupId(msg18.getGroupId().toString());
                bean.setTelephone(msg.getDestinationAccount().toString() + "/" + msg18.getData());
                String[] groupMembers18 = msg18.getData().split(",")[1].split("/");
                bean.setMsg("您将" + getGroupMember(groupMembers18) + "移出群聊");
                break;
            case 21: //群成员接收有人离开群或者接收群主移除xx离开群聊
                OffshoreCommunicationMessage21 msg21 = (OffshoreCommunicationMessage21) msg;
                String groupId21 = msg21.getGroupId().toString();
                bean.setGroupId(groupId21);
                bean.setTelephone(msg.getDestinationAccount().toString() + "/" + msg21.getData());
                String[] data = msg21.getData().split(",");
                //判断是离开群聊还是被移除群聊 0 被移除 1 离开群聊
                String exitGroupFlag = data[1];
                String[] groupMembers21 = data[0].split("/");
                if (exitGroupFlag.equals("0")) {
                    String groupManagerTel = DBManager.getInstance(MyApplication.getContext()).getGroupManagerById(groupId21);
                    if (!groupManagerTel.equals(MyApplication.TEL + "")) {
                        String groupManagerShowName = DBManager.getInstance(MyApplication.getContext()).getTelephoneShowName(groupManagerTel);
                        bean.setMsg(groupManagerShowName + "移除了" + getGroupMember(groupMembers21) + "离开群聊");
                    } else {
                        bean.setMsg("");
                    }
                } else {
                    bean.setMsg(getGroupMember(groupMembers21) + "已退出群聊");
                }
                break;
        }
        List<ChatMsgBean> chatMsgBeanList = DBManager.getInstance(MyApplication.getContext()).getGroupMessageLimitByGroupId(groupId);
        boolean isExistMessage = false;
        for (ChatMsgBean chatMsgBean : chatMsgBeanList) {
            if (chatMsgBean.getTime().equals(bean.getTime()) && chatMsgBean.getMsg().equals(bean.getMsg())) {
                isExistMessage = true;
            }
        }
        if (!isExistMessage && !bean.getMsg().isEmpty()) {
            DBManager.getInstance(MyApplication.getContext()).createMsg(bean);
        }
    }

    /**
     * 获取这段时间增加的好友信息
     */
    private List<FriendBean> getFriends(OffshoreCommunicationMessage04 communicationMessage) {
        String data = communicationMessage.getData();
        Log.d(CommonConstant.LOGCAT_TAG, "获取好友信息04号消息: " + data);
        List<FriendBean> list = new ArrayList<>();
        FriendBean bean = new FriendBean();
        for (int i = 0; i < data.split("/").length; i++) {
            if (i == 1 && !"null".equals(data.split("/")[i])) {
                SPUtil.instance.putValues(new SPUtil.ContentValue(MyApplication.TEL + "", data.split("/")[i]));
            }
            if (i < 3) {
                continue;
            }
            if (i % 3 == 0) {
                bean = new FriendBean();
                bean.setNowLoginTel(MyApplication.TEL + "");
                bean.setTelephone(data.split("/")[i]);
            }
            if (i % 3 == 1)
                bean.setName("null".equals(data.split("/")[i]) ? "" : data.split("/")[i]);
            if (i % 3 == 2) {
                bean.setNickName("null".equals(data.split("/")[i]) ? "" : data.split("/")[i]);
                list.add(bean);
            }
        }
        Log.d(CommonConstant.LOGCAT_TAG, "获取好友信息集合长度: " + list.size());
        return list;
    }

    private String getGroupMember(String[] groupMembers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < groupMembers.length; i++) {
            if (TextUtils.isEmpty(groupMembers[i])) {
                continue;
            }
            String groupMemberName;
            if (groupMembers[i].equals(MyApplication.TEL + "")) {
                groupMemberName = "您";
                stringBuilder.append(groupMemberName).append("、");
                continue;
            }
            FriendBean friendBean = DBManager.getInstance(MyApplication.getContext()).selectFriend(groupMembers[i]);
            if (friendBean == null) { //非好友
                groupMemberName = groupMembers[i];
            } else {
                groupMemberName = StringUtil.getFirstNotNullString(
                        new String[]{
                                friendBean.getNickName(), friendBean.getName(), friendBean.getTelephone() == null ? "" : friendBean.getTelephone()
                        }
                );
            }
            stringBuilder.append(groupMemberName).append("、");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }


    private Boolean isMqLogin() {
        return IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ;
    }

    /**
     * 根据num去回调错误
     */
    private void doListenerError(Integer sequenceNumber, String reason) {
        ISendListener iSendListener = (ISendListener) sendListeners.get(sequenceNumber);
        if (iSendListener != null) {
            iSendListener.sendFail(reason);
        }
        sendListeners.remove(sequenceNumber);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).num == sequenceNumber) {
                list.remove(i);
            }
        }
    }

    /**
     * 根据num去回调成功
     */
    private void doListenerSuccess(Integer sequenceNumber) {
        ISendListener iSendListener = (ISendListener) sendListeners.get(sequenceNumber);
        if (iSendListener != null) {
            iSendListener.sendSuccess();
        }
        sendListeners.remove(sequenceNumber);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).num == sequenceNumber) {
                list.remove(i);
            }
        }
    }

    /**
     * 检验是否发送失败
     */
    private void checkFail() {
        //每隔一秒检测一次 消息是否发送失败
        try {
            for (int i = 0; i < list.size(); i++) {
                if ((DateUtil.getInstance().getTime() - list.get(i).sendTime) > list.get(i).failTime * 1000) {
                    if (sendListeners.get(list.get(i).num) != null)
                        ((ISendListener) sendListeners.get(list.get(i).num)).sendFail("超时失败");
                    sendListeners.remove(list.get(i).num);
                    list.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
