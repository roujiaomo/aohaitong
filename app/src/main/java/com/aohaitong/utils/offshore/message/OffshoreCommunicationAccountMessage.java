package com.aohaitong.utils.offshore.message;


/**
 * 近海通信 消息基类。
 */
public class OffshoreCommunicationAccountMessage extends OffshoreCommunicationMessage {
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Long sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Long getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Long destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    private static final long serialVersionUID = 5435528168361809778L;

    /*
     *源APP账号
     */

    private Long sourceAccount;

    /*
     *目的APP账号
     */

    private Long destinationAccount;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationAccountMessage() {
    }

    /**
     * 构造方法。
     *
     * @param msgId 消息 ID
     */
    protected OffshoreCommunicationAccountMessage(Integer msgId) {
        setMsgId(msgId);
    }
}
