package com.aohaitong.utils.offshore.message;


/*
 * B接群组退出语句
 */
public class OffshoreCommunicationMessage20 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 6360266278048521165L;

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
    public OffshoreCommunicationMessage20() {
        super(20);
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
