package com.aohaitong.business.transmit;

import android.os.Handler;
import android.os.Looper;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.ChatMsgBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 进行聊天管理
 * 相互确认通信都在这里,只有最终结果是成功失败调用接口通知
 */
public class TransmitManager {

    public static TransmitManager instance;

    static {
        instance = new TransmitManager();
    }

    private TransmitManager() {
    }

    /**
     * 发送文本消息
     *
     * @param bean 消息实体类
     */
    public void sendText(ChatMsgBean bean) {
        //上传服务器
        if (bean.getIsGroup()) {
            sendGroupMsg(bean);
        } else {
            sendMsg(bean);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> sendFail(bean), NumConstant.getFailTime() * 1000L);
    }

    /**
     * 发送文本消息
     *
     * @param bean 消息实体类
     */
    public void sendVideo(ChatMsgBean bean, List<String> photoDataList) {
        //上传服务器
        sendVideoMsg(bean, photoDataList);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> sendFail(bean), NumConstant.getFailTime() * 1000L);
    }

    //十秒后如果未收到回信那就发送失败
    private void sendFail(ChatMsgBean bean) {
        ChatMsgBean da = DBManager.getInstance(MyApplication.getContext()).queryChatById(bean.getId());
        if (da != null && da.getStatus() == StatusConstant.SEND_LOADING) {
            bean.setStatus(StatusConstant.SEND_FAIL);
            if (bean.getMessageType() != 0) {
                bean.setMsg("");
            }
            DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
            EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_SERVICE_REFRESH));
            EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
        }
    }

    /**
     * 发送私聊消息
     */
    private void sendMsg(ChatMsgBean bean) {
        BusinessController.sendMessage(bean, new ISendListener() {
            @Override
            public void sendSuccess() {
                bean.setStatus(StatusConstant.SEND_SUCCESS);
                if (bean.getMessageType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                    bean.setMsg("");
                }
                DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_SERVICE_REFRESH, bean));
                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
            }

            @Override
            public void sendFail(String reason) {
                bean.setStatus(StatusConstant.SEND_FAIL);
                if (bean.getMessageType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                    bean.setMsg("");
                }
                DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.EVENT_CHAT_SERVICE_REFRESH, bean));
                EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.TYPE_CHAT_REFRESH));

            }
        });
    }

    private void sendVideoMsg(ChatMsgBean bean, List<String> photoDataList) {
        BusinessController.sendVideoMessage(bean, photoDataList, new ISendListener() {
            @Override
            public void sendSuccess() {
                bean.setStatus(StatusConstant.SEND_SUCCESS);
                if (bean.getMessageType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                    bean.setMsg("");
                }
                DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_SERVICE_REFRESH));
                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
            }

            @Override
            public void sendFail(String reason) {
                bean.setStatus(StatusConstant.SEND_FAIL);
                if (bean.getMessageType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                    bean.setMsg("");
                }
                DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.EVENT_CHAT_SERVICE_REFRESH));
                EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.TYPE_CHAT_REFRESH));

            }
        });
    }

    /**
     * 发送群聊消息
     */
    private void sendGroupMsg(ChatMsgBean bean) {
        BusinessController.sendGroupMessage(bean, new ISendListener() {
            @Override
            public void sendSuccess() {
                bean.setStatus(StatusConstant.SEND_SUCCESS);
                if (bean.getMessageType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                    bean.setMsg("");
                }
                DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                EventBus.getDefault().post(new MsgEntity("", StatusConstant.EVENT_CHAT_SERVICE_REFRESH));
                EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
            }

            @Override
            public void sendFail(String reason) {
                bean.setStatus(StatusConstant.SEND_FAIL);
                if (bean.getMessageType() != StatusConstant.TYPE_TEXT_MESSAGE) {
                    bean.setMsg("");
                }
                DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.EVENT_CHAT_SERVICE_REFRESH));
                EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.TYPE_CHAT_REFRESH));

            }
        });
    }
}
