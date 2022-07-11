package com.aohaitong.utils.offshore.message;


/*
 * 修改群名语句
 */
public class OffshoreCommunicationMessage31 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -3827992608888302479L;

    /*
     *发起修改app群名的账号
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
    public OffshoreCommunicationMessage31() {
        super(31);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public Long getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(Long friendAccount) {
        this.friendAccount = friendAccount;
    }
}
