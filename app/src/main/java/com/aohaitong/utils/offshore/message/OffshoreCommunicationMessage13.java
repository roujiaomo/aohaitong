package com.aohaitong.utils.offshore.message;


/*
 * A修改B好友备注语句
 */
public class OffshoreCommunicationMessage13 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -8978184748248312553L;

    /*
     *被修改备注APP账号
     */

    private Long friendAccount;

    /*
     *二进制数据
     */

    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage13() {
        super(13);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(Long friendAccount) {
        this.friendAccount = friendAccount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
