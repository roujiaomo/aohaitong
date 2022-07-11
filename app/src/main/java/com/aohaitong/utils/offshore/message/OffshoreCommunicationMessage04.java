package com.aohaitong.utils.offshore.message;


/*
 *更新好友群组语句
 */
public class OffshoreCommunicationMessage04 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 1448092571657820215L;

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
    public OffshoreCommunicationMessage04() {
        super(4);
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
