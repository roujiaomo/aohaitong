package com.aohaitong.business.transmit;

import android.util.Log;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.ChatMsgBean;
import com.aohaitong.business.IPController;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationAccountMessage;
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
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage29;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage30;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage31;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage32;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage33;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage34;
import com.aohaitong.utils.offshore.sentence.BHMSentence;
import com.aohaitong.utils.offshore.sentence.JHDSentence;
import com.aohaitong.utils.offshore.util.CommVdesMessageUtil;
import com.aohaitong.utils.offshore.util.JhdAnalysisGroupUtil;
import com.aohaitong.utils.offshore.util.JhdAnalysisUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

//用于各种业务类,由于协议改变,用这个新的类
public class BusinessController {


    private static void setDefaultMsg(OffshoreCommunicationAccountMessage o) {
        o.setYear(DateUtil.getInstance().getYear());
        o.setMonth(DateUtil.getInstance().getMouth());
        o.setDay(DateUtil.getInstance().getDay());
        o.setHour(DateUtil.getInstance().getHour());
        o.setMinute(DateUtil.getInstance().getMinute());
        o.setSecond(DateUtil.getInstance().getSecond());
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
    }

    private static void setDefault24Msg(OffshoreCommunicationAccountMessage o, ChatMsgBean chatMsgBean) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(new Date(Long.parseLong(chatMsgBean.getTime())));
        o.setYear(Integer.parseInt(date.split("-")[0]));
        o.setMonth(Integer.parseInt(date.split("-")[1]));
        o.setDay(Integer.parseInt(date.split("-")[2].split(" ")[0]));
        o.setHour(Integer.parseInt(date.split(" ")[1].split(":")[0]));
        o.setMinute(Integer.parseInt(date.split(" ")[1].split(":")[1]));
        o.setSecond(Integer.parseInt(date.split(" ")[1].split(":")[2]));
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
    }

    /*===================================发送消息====================================*/

    /**
     * 发送私聊消息
     */
    public static void sendMessage(ChatMsgBean chatMsgBean, ISendListener iSendListener) {
        Log.e(CommonConstant.LOGCAT_TAG, "发送了24号消息: " + new Gson().toJson(chatMsgBean));
        int num = NumConstant.getJHDNum();
        OffshoreCommunicationMessage24 msg24 = new OffshoreCommunicationMessage24();
        setDefault24Msg(msg24, chatMsgBean);
        msg24.setMsgId(StatusConstant.MSG_SEND_TEXT);
        msg24.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        msg24.setData(chatMsgBean.getMsg());
//        msg24.setSpare(0);
        msg24.setMsgType(chatMsgBean.getMessageType());
        msg24.setSourceAccount(MyApplication.TEL);
        msg24.setDestinationAccount(Long.parseLong(chatMsgBean.getTelephone()));
        msg24.setSequenceNumber(num);
        msg24.setTimeLong(chatMsgBean.getRecordTime());
        //船端的非文字消息,需要拆包
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            String msgKey = JhdAnalysisUtil.messageSplit(msg24);
            Map<Integer, JHDSentence> map = JhdAnalysisUtil.getSplitDataMap().get(msgKey);
            if (map != null) {
                Log.e(CommonConstant.LOGCAT_TAG, "发送船端消息, 总包数: " + map.size());
                for (int key : map.keySet()) {
                    try {
                        JHDSentence jhdSentence = map.get(key);
                        if (jhdSentence != null) {
                            Log.d(CommonConstant.LOGCAT_TAG, "发送了第" +
                                    ((OffshoreCommunicationMessage24) jhdSentence.getOffshoreCommunicationMessage()).getCurrent()
                                    + "包 : " + new Gson().toJson(jhdSentence));
                            SendController.getInstance().sendMsg(iSendListener,
                                    CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        iSendListener.sendFail(e.getMessage());
                    }
                }
            }
        } else {
            msg24.setTotal(1);
            msg24.setCurrent(1);
            JHDSentence jhdSentence = new JHDSentence();
            jhdSentence.setOffshoreCommunicationMessage(msg24);
            try {
                SendController.getInstance().sendMsg(iSendListener,
                        CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                iSendListener.sendFail(e.getMessage());
            }

        }
    }

    /**
     * 发送私聊消息
     */
    public static void sendVideoMessage(ChatMsgBean chatMsgBean, List<String> photoDataList, ISendListener iSendListener) {
        Log.e(CommonConstant.LOGCAT_TAG, "发送了24号消息: " + new Gson().toJson(chatMsgBean));
        int num = NumConstant.getJHDNum();
        OffshoreCommunicationMessage24 msg24 = new OffshoreCommunicationMessage24();
        setDefaultMsg(msg24);
        msg24.setMsgId(StatusConstant.MSG_SEND_TEXT);
        msg24.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        msg24.setData(chatMsgBean.getMsg());
        msg24.setMsgType(chatMsgBean.getMessageType());
        msg24.setSourceAccount(MyApplication.TEL);
        msg24.setDestinationAccount(Long.parseLong(chatMsgBean.getTelephone()));
        msg24.setSequenceNumber(num);
        msg24.setTimeLong(chatMsgBean.getRecordTime());
        //船端的非文字消息,需要拆包
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            String msgKey = JhdAnalysisUtil.messageSplit(msg24);
            Map<Integer, JHDSentence> map = JhdAnalysisUtil.getSplitDataMap().get(msgKey);
            if (map != null) {
                Log.e(CommonConstant.LOGCAT_TAG, "发送船端消息, 总包数: " + map.size());
                for (int key : map.keySet()) {
                    try {
                        JHDSentence jhdSentence = map.get(key);
                        if (jhdSentence != null) {
                            Log.d(CommonConstant.LOGCAT_TAG, "发送了第" +
                                    ((OffshoreCommunicationMessage24) jhdSentence.getOffshoreCommunicationMessage()).getCurrent()
                                    + "包 : " + new Gson().toJson(jhdSentence));
                            SendController.getInstance().sendMsg(iSendListener,
                                    CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        iSendListener.sendFail(e.getMessage());
                    }
                }
            }
        } else {
            if (msg24.getMsgType() == 3) {
                String msgKey = JhdAnalysisUtil.videoMessageSplit(msg24, photoDataList);
                Map<Integer, JHDSentence> map = JhdAnalysisUtil.getSplitDataMap().get(msgKey);
                if (map != null) {
                    Log.e(CommonConstant.LOGCAT_TAG, "发送岸端视频消息, 总包数: " + map.size());
                    for (int key : map.keySet()) {
                        try {
                            JHDSentence jhdSentence = map.get(key);
                            if (jhdSentence != null) {
                                Log.d(CommonConstant.LOGCAT_TAG, "发送了第" +
                                        ((OffshoreCommunicationMessage24) jhdSentence.getOffshoreCommunicationMessage()).getCurrent()
                                        + "包 : " + new Gson().toJson(jhdSentence));
                                SendController.getInstance().sendMsg(iSendListener,
                                        CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            iSendListener.sendFail(e.getMessage());
                        }
                    }
                }
            } else {
                msg24.setTotal(1);
                msg24.setCurrent(1);
                JHDSentence jhdSentence = new JHDSentence();
                jhdSentence.setOffshoreCommunicationMessage(msg24);
                try {
                    SendController.getInstance().sendMsg(iSendListener,
                            CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                    iSendListener.sendFail(e.getMessage());
                }
            }


        }
    }

    /*-------------------用户相关---------------------*/

    /**
     * 发送登录消息
     */
    public static void sendLogin(String tel, String password, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage01 o = new OffshoreCommunicationMessage01();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_LOGIN);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(password);
        o.setSourceAccount(Long.valueOf(tel));
        o.setDestinationAccount(Long.valueOf(tel));
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.LOGIN_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送登出消息
     */
    public static void sendLogOut(ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage02 o = new OffshoreCommunicationMessage02();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_LOGOUT);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData("0");
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送修改昵称消息
     */
    public static void sendRename(String name, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage05 o = new OffshoreCommunicationMessage05();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_UPDATE_MY_NICKNAME);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(name);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送修改密码消息
     */
    public static void sendResetPassword(String newPassword, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage06 o = new OffshoreCommunicationMessage06();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_UPDATE_MY_PASSWORD);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(newPassword);
        o.setOldPassword(MyApplication.PASSWORD);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }


    /*-------------------好友相关---------------------*/

    /**
     * 发送好友申请
     */
    public static void sendFriendApply(String friendTel, String verificationMsg, ISendListener iSendListener, int num) {
        String relTel = friendTel.replaceAll(" ", "");
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage07 o = new OffshoreCommunicationMessage07();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_FRIEND_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(verificationMsg);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(Long.parseLong(relTel));
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送他人申请好友的申请结果,同意或者拒绝
     */
    public static void sendFriendApplyResult(String friendTel, boolean agree, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage09 o = new OffshoreCommunicationMessage09();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_FRIEND_APPLY_RESULT);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(agree ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(Long.parseLong(friendTel));
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送删除好友
     */
    public static void sendDelFriend(String friendTel, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage11 o = new OffshoreCommunicationMessage11();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_DELETE_FRIEND);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData("0");
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(Long.parseLong(friendTel));
        o.setSpare(0);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送修改好友备注
     */
    public static void sendEditFriendNickName(String friendTel, String nickName, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage13 o = new OffshoreCommunicationMessage13();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_EDIT_FRIEND_NICKNAME);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(nickName);
        o.setFriendAccount(Long.valueOf(friendTel));
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 更新好友群组请求语句
     * 获取上次更新后的所有新的好友信息
     */
    public static void sendGetFriendList(String lastTime, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage03 o = new OffshoreCommunicationMessage03();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_UPDATE_FRIEND_REQUEST);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(lastTime == null ? "" : lastTime);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 更新所有群组请求语句
     * 获取上次更新后的所有新的好友信息
     */
    public static void sendGetGroupList(ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage32 o = new OffshoreCommunicationMessage32();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_GET_GROUP_LIST_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData("");
        o.setSpare(4);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 创建群组语句
     */
    public static void sendCreateGroup(String groupName, String groupMember, int num, ISendListener iSendListener) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage14 o = new OffshoreCommunicationMessage14();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_CREATE_GROUP_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(groupMember);
        o.setSpare(0);
        o.setGroupId(0L);
        o.setGroupName(groupName);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 群主拉人进群(创建完群组后)
     */

    public static void sendInviteJoinGroup(Long groupId, String groupMember, int num, ISendListener iSendListener) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage15 o = new OffshoreCommunicationMessage15();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_ADD_GROUP_MEMBER_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setGroupId(groupId);
        o.setData(groupMember);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }


    public static void sendRemoveGroupMember(Long groupId, String groupMember, int num, ISendListener iSendListener) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage18 o = new OffshoreCommunicationMessage18();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_REMOVE_GROUP_MEMBER_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setGroupId(groupId);
        o.setData(groupMember);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 群主解散群聊
     */
    public static void sendDissolveGroup(Long groupId, int num, ISendListener iSendListener) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage22 o = new OffshoreCommunicationMessage22();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_DISSOLVE_GROUP_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setGroupId(groupId);
        o.setData("0");
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 群成员退出群聊
     */
    public static void sendExitGroup(Long groupId, int num, ISendListener iSendListener) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage20 o = new OffshoreCommunicationMessage20();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_EXIT_GROUP_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setGroupId(groupId);
        o.setData("0");
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送修改群名
     */
    public static void sendModifyGroupName(Long groupId, String groupName, int num, ISendListener iSendListener) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage30 o = new OffshoreCommunicationMessage30();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_MODIFY_GROUP_NAME_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setGroupId(groupId);
        o.setData(groupName);
        o.setGroupName(groupName);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送群聊消息
     */


    /**
     * 发送文本消息
     */
    public static void sendGroupMessage(ChatMsgBean chatMsgBean, ISendListener iSendListener) {
        int num = NumConstant.getJHDNum();
        OffshoreCommunicationMessage26 msg26 = new OffshoreCommunicationMessage26();
        setDefaultMsg(msg26);
        msg26.setMsgId(StatusConstant.MSG_SEND_GROUP_MESSAGE_APPLY);
        msg26.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        msg26.setData(chatMsgBean.getMsg());
        msg26.setSpare(6);
        msg26.setGroupId(Long.parseLong(chatMsgBean.getGroupId()));
        msg26.setMsgType(chatMsgBean.getMessageType());
        msg26.setSourceAccount(MyApplication.TEL);
        msg26.setDestinationAccount(MyApplication.TEL);
        msg26.setSequenceNumber(num);
        msg26.setTimeLong(chatMsgBean.getRecordTime());
        Log.d(CommonConstant.LOGCAT_TAG, "发送了26号消息: " + new Gson().toJson(chatMsgBean));
        //船端的非文字消息,需要拆包
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            String msgKey = JhdAnalysisGroupUtil.messageSplit(msg26);
            Map<Integer, JHDSentence> map = JhdAnalysisGroupUtil.getSplitDataMap().get(msgKey);
            if (map != null) {
                Log.e(CommonConstant.LOGCAT_TAG, "发送船端消息, 总包数: " + map.size());
                for (int key : map.keySet()) {
                    Log.e(CommonConstant.LOGCAT_TAG, "发送船端消息第" + key + "包");
                    try {
                        JHDSentence jhdSentence = map.get(key);
                        if (jhdSentence != null) {
                            SendController.getInstance().sendMsg(iSendListener,
                                    CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        iSendListener.sendFail(e.getMessage());
                    }
                }
            }
        } else {
            msg26.setTotal(1);
            msg26.setCurrent(1);
            JHDSentence jhdSentence = new JHDSentence();
            jhdSentence.setOffshoreCommunicationMessage(msg26);
            Log.e(CommonConstant.LOGCAT_TAG, "发送26号消息语句: " + new Gson().toJson(jhdSentence));
            try {
                SendController.getInstance().sendMsg(iSendListener,
                        CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                iSendListener.sendFail(e.getMessage());
            }

        }
    }

    /**
     * 获取聊天对象登录状态
     */
    public static void sendGetUserLoginStatusList(String userTel, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage34 o = new OffshoreCommunicationMessage34();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_SEND_GET_USER_LOGIN_STATUS);
        o.setResFlag(StatusConstant.SEND_TYPE_SENDER);
        o.setData(userTel);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /*----------------应答信息--------------------*/

    /**
     * 发送更新好友群组信息收到后的应答
     */
    public static void sendFriendListAnswer(boolean success, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage04 o = new OffshoreCommunicationMessage04();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_UPDATE_FRIEND);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送收到添加好友的请求的应答
     */
    public static void sendReceiveFriendApplyAnswer(boolean success, long tel, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage08 o = new OffshoreCommunicationMessage08();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_FRIEND_APPLY);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(tel);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送收到自己的好友申请添加结果的应答
     */
    public static void sendMyFriendApplyResultAnswer(boolean success, long tel, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage10 o = new OffshoreCommunicationMessage10();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_FRIEND_APPLY_RESULT);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(tel);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送收到自己被删除好友了的应答
     */
    public static void sendFriendDeletedAnswer(boolean success, long tel, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage12 o = new OffshoreCommunicationMessage12();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_DELETE_FRIEND);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(tel);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送自己收到他人发送的消息的应答
     * 应答时也需要拼新增字段
     */
    public static void sendMsgReceiveAnswer(boolean success, OffshoreCommunicationMessage25 msg25, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage25 offshoreCommunicationMessage25 = new OffshoreCommunicationMessage25();
        setDefaultMsg(offshoreCommunicationMessage25);
        offshoreCommunicationMessage25.setMsgId(StatusConstant.MSG_RECEIVE_TEXT);
        offshoreCommunicationMessage25.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        offshoreCommunicationMessage25.setData(success ? StatusConstant.SUCCESS : msg25.getData());
//        offshoreCommunicationMessage25.setSpare(0);
        offshoreCommunicationMessage25.setSourceAccount(MyApplication.TEL);
        offshoreCommunicationMessage25.setDestinationAccount(msg25.getSourceAccount());
        offshoreCommunicationMessage25.setSequenceNumber(num);
        //判断自己是否是船端,是船端回复
        offshoreCommunicationMessage25.setTotal(1);
        offshoreCommunicationMessage25.setCurrent(1);
        offshoreCommunicationMessage25.setTimeLong(msg25.getTimeLong());
        offshoreCommunicationMessage25.setMsgType(msg25.getMsgType());
        jhdSentence.setOffshoreCommunicationMessage(offshoreCommunicationMessage25);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            Log.e(CommonConstant.LOGCAT_TAG, "应答25号消息异常：" + e.getMessage());
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送自己收到他人发送的消息的应答
     * 应答时也需要拼新增字段
     */
    public static void sendGroupMsgReceiveAnswer(boolean success, OffshoreCommunicationMessage27 msg27, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage27 offshoreCommunicationMessage27 = new OffshoreCommunicationMessage27();
        setDefaultMsg(offshoreCommunicationMessage27);
        offshoreCommunicationMessage27.setMsgId(StatusConstant.MSG_RECEIVE_GROUP_MESSAGE);
        offshoreCommunicationMessage27.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        offshoreCommunicationMessage27.setData(success ? StatusConstant.SUCCESS : msg27.getData());
        offshoreCommunicationMessage27.setSpare(6);
        offshoreCommunicationMessage27.setSourceAccount(MyApplication.TEL);
        offshoreCommunicationMessage27.setDestinationAccount(msg27.getSourceAccount());
        offshoreCommunicationMessage27.setSequenceNumber(num);
        //判断自己是否是船端,是船端回复
        offshoreCommunicationMessage27.setTotal(1);
        offshoreCommunicationMessage27.setCurrent(1);
        offshoreCommunicationMessage27.setTimeLong(msg27.getTimeLong());
        offshoreCommunicationMessage27.setMsgType(msg27.getMsgType());
        offshoreCommunicationMessage27.setGroupId(msg27.getGroupId());
        jhdSentence.setOffshoreCommunicationMessage(offshoreCommunicationMessage27);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            Log.e(CommonConstant.LOGCAT_TAG, "应答27号消息异常：" + e.getMessage());
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送自己收到他人修改昵称的消息的应答
     */
    public static void sendFriendNickNameChangesReceiveAnswer(boolean success, long tel, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage29 o = new OffshoreCommunicationMessage29();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_FRIEND_NICKNAME_CHANGED);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(tel);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送自己被人拉入群聊的应答
     */
    public static void sendJoinGroupAnswer(boolean success, ISendListener iSendListener, OffshoreCommunicationMessage16 msg16, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage16 o = new OffshoreCommunicationMessage16();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_JOIN_GROUP);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setFriendAccount(msg16.getFriendAccount());
        o.setGroupId(msg16.getGroupId());
        o.setGroupName(msg16.getGroupName());
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送自己知道新群员入群的应答
     */
    public static void sendOtherJoinGroupAnswer(boolean success, ISendListener iSendListener, OffshoreCommunicationMessage17 msg17, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage17 o = new OffshoreCommunicationMessage17();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_OTHER_JOIN_GROUP);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setGroupId(msg17.getGroupId());
        o.setFriendAccount(msg17.getFriendAccount());
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送自己被移除群聊的应答
     */
    public static void sendRemoveGroupAnswer(boolean success, ISendListener iSendListener, OffshoreCommunicationMessage19 msg19, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage19 o = new OffshoreCommunicationMessage19();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_REMOVE_GROUP);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setGroupId(msg19.getGroupId());
        o.setFriendAccount(msg19.getFriendAccount());
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送群组内其他人收到有人被移除群聊的应答
     */
    public static void sendOtherRemoveGroupAnswer(boolean success, ISendListener iSendListener, OffshoreCommunicationMessage21 msg21, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage21 o = new OffshoreCommunicationMessage21();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_OTHER_REMOVE_GROUP);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setGroupId(msg21.getGroupId());
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }


    /**
     * 发送群组成员收到群聊解散的应答
     */
    public static void sendDeleteGroupAnswer(boolean success, ISendListener iSendListener, OffshoreCommunicationMessage23 msg23, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage23 o = new OffshoreCommunicationMessage23();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_DISSOLVE_GROUP);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(0);
        o.setGroupId(msg23.getGroupId());
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }


    /**
     * 发送接收到修改群聊名称的应答
     */
    public static void sendModifyGroupNameAnswer(boolean success, ISendListener iSendListener, OffshoreCommunicationMessage31 msg31, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage31 o = new OffshoreCommunicationMessage31();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_MODIFY_GROUP_NAME);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(2);
        o.setFriendAccount(0L);
        o.setGroupId(msg31.getGroupId());
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /**
     * 发送 更新群组信息 收到后的应答
     */
    public static void sendGroupListAnswer(boolean success, ISendListener iSendListener, int num) {
        JHDSentence jhdSentence = new JHDSentence();
        OffshoreCommunicationMessage33 o = new OffshoreCommunicationMessage33();
        setDefaultMsg(o);
        o.setMsgId(StatusConstant.MSG_RECEIVE_GET_GROUP_LIST);
        o.setResFlag(StatusConstant.SEND_TYPE_RECEIVER);
        o.setData(success ? StatusConstant.SUCCESS : StatusConstant.FAIL);
        o.setSpare(4);
        o.setSourceAccount(MyApplication.TEL);
        o.setDestinationAccount(MyApplication.TEL);
        o.setSequenceNumber(num);
        jhdSentence.setOffshoreCommunicationMessage(o);
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    /*----------------心跳消息--------------------*/
    public static void sendHeartMsg(ISendListener iSendListener, int num) {
        BHMSentence bhmSentence = new BHMSentence();
        bhmSentence.setConfirmIdentifier("0");
        bhmSentence.setMmsi(Long.valueOf(MyApplication.TEL));
        bhmSentence.setSentencesNumber(num);
        bhmSentence.setData(DateUtil.getInstance().getTime() + "000");
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageBHM(bhmSentence), num, NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }

    public static void sendHeartMsgAnswer(ISendListener iSendListener, String heartMsg) {
        try {
            SendController.getInstance().sendMsg(iSendListener, CommVdesMessageUtil.messageBHMRes(heartMsg), CommVdesMessageUtil.reslvToBHM(heartMsg).getSentencesNumber(), NumConstant.COMMON_FAIL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            iSendListener.sendFail(e.getMessage());
        }
    }
}
