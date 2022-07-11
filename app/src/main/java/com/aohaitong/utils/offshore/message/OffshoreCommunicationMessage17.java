package com.aohaitong.utils.offshore.message;


/*
 * 群成员接收B被A拉入群语句
 */
public class OffshoreCommunicationMessage17 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 6475177036076847320L;

    /*
     *发起拉人请求APP账号
     */

    private Long friendAccount;

    /*
     *群组ID
     */

    private Long groupId;

    /*
     *备用
     */

    private Integer spare;

    /*
     *二进制数据
     */

    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage17() {
        super(17);
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getSpare() {
        return spare;
    }

    public void setSpare(Integer spare) {
        this.spare = spare;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
