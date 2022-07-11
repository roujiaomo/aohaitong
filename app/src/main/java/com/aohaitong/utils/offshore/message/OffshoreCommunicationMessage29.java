package com.aohaitong.utils.offshore.message;


/*
 * 接收好友昵称修改
 */
public class OffshoreCommunicationMessage29 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 7367995607218334358L;

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
    public OffshoreCommunicationMessage29() {
        super(29);
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
