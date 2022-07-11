package com.aohaitong.utils.offshore.message;


/*
 *更新好友群组请求语句
 */
public class OffshoreCommunicationMessage03 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 5640891113191198629L;

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
    public OffshoreCommunicationMessage03() {
        super(3);
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
