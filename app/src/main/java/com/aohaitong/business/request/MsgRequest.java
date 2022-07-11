package com.aohaitong.business.request;

/**
 * 消息发送请求
 */
public class MsgRequest {

    private String mParam;//发送的语句
    private SendCallback mSendCallBack;

    public MsgRequest(String param, SendCallback callBack) {
        this.mParam = param;
        this.mSendCallBack = callBack;
    }

    public SendCallback getSendCallBack() {
        return mSendCallBack;
    }

    public void setSendCallBack(SendCallback mSendCallBack) {
        this.mSendCallBack = mSendCallBack;
    }

    public void setMsgParam(String param) {
        this.mParam = param;
    }

    public String getMsgParam() {
        return mParam;
    }

    public interface SendCallback {
        void onFinish();

        void onError(String message);
    }

}
