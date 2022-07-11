package com.aohaitong.utils.offshore.message;


/*
 * B接收被A删除好友语句
 */
public class OffshoreCommunicationMessage12 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -8978184748248312553L;

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
    public OffshoreCommunicationMessage12() {
        super(12);
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
