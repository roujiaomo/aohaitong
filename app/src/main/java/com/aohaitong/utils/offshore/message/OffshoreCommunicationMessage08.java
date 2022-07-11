package com.aohaitong.utils.offshore.message;


/*
 * B接收A添加好友申请的处理语句
 */
public class OffshoreCommunicationMessage08 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 2863623597501240449L;

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
    public OffshoreCommunicationMessage08() {
        super(8);
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
