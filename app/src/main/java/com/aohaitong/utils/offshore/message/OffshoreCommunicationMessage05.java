package com.aohaitong.utils.offshore.message;


/*
 *更新账号昵称语句
 */
public class OffshoreCommunicationMessage05 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 2908373007947485564L;

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
    public OffshoreCommunicationMessage05() {
        super(5);
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
