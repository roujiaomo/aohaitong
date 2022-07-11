package com.aohaitong.utils.offshore.message;


/*
 * A删除B好友语句
 */
public class OffshoreCommunicationMessage11 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 7107045020382850081L;

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
    public OffshoreCommunicationMessage11() {
        super(11);
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
