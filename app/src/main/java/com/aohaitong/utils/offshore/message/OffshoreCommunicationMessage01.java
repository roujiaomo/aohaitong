package com.aohaitong.utils.offshore.message;


/*
 *登录语句
 */
public class OffshoreCommunicationMessage01 extends OffshoreCommunicationAccountMessage {

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

    private static final long serialVersionUID = -3491369502323200995L;

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
    public OffshoreCommunicationMessage01() {
        super(1);
    }
}
