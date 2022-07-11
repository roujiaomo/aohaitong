package com.aohaitong.utils.offshore.message;


/*
 * B被A拉入群组语句
 */
public class OffshoreCommunicationMessage16 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 5513551198088893798L;

    /*
     *发起拉人请求APP账号
     */

    private Long friendAccount;

    /*
     *群组ID
     */

    private Long groupId;

    /*
     *群组名称
     */

    private String groupName;

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
    public OffshoreCommunicationMessage16() {
        super(16);
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
