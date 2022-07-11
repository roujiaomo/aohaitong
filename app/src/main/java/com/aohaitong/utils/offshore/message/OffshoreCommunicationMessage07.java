package com.aohaitong.utils.offshore.message;


/*
 *A发送添加B为好友请求语句
 */
public class OffshoreCommunicationMessage07 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -8533739244222966867L;

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
    public OffshoreCommunicationMessage07() {
        super(7);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
