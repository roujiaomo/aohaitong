package com.aohaitong.utils.offshore.message;


/*
 * 群成员接收B被A移除群组/B从群组退出语句
 */
public class OffshoreCommunicationMessage21 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -1956030232657943228L;


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
    public OffshoreCommunicationMessage21() {
        super(21);
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
