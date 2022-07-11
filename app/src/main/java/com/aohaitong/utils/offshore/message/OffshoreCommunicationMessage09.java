package com.aohaitong.utils.offshore.message;


/*
 * B对A添加好友申请的处理结果语句
 */
public class OffshoreCommunicationMessage09 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -4761509563877878977L;

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
    public OffshoreCommunicationMessage09() {
        super(9);
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
