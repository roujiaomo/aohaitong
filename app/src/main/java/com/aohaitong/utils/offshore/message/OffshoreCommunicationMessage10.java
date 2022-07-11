package com.aohaitong.utils.offshore.message;


/*
 * A接收添加B好友结果语句
 */
public class OffshoreCommunicationMessage10 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 8704937159039679466L;

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
    public OffshoreCommunicationMessage10() {
        super(10);
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
