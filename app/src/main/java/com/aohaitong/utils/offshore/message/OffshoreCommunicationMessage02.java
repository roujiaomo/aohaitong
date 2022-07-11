package com.aohaitong.utils.offshore.message;


/*
 *登出语句
 */
public class OffshoreCommunicationMessage02 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -6547982162607154037L;

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
    public OffshoreCommunicationMessage02() {
        super(2);
    }
}
