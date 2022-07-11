package com.aohaitong.utils.offshore.message;


/*
 * 请求更新群组信息语句
 */
public class OffshoreCommunicationMessage33 extends OffshoreCommunicationAccountMessage {

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
    public OffshoreCommunicationMessage33() {
        super(33);
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
