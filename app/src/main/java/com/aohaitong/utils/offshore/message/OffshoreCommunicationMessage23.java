package com.aohaitong.utils.offshore.message;


/*
 * 群成员接收群组解散语句
 */
public class OffshoreCommunicationMessage23 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -1631839624243339844L;

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
    public OffshoreCommunicationMessage23() {
        super(23);
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
}
