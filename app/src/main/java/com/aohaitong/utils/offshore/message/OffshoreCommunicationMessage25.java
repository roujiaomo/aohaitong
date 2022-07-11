package com.aohaitong.utils.offshore.message;

import androidx.annotation.NonNull;

/*
 * B接收A的信息语句
 */
public class OffshoreCommunicationMessage25 extends OffshoreCommunicationAccountMessage implements Cloneable {

    private static final long serialVersionUID = 1229695954284647370L;

    /*
     *消息类型 0=文本信息；1=语音消息；2=图片消息
     */
    private Integer msgType;


    /**
     * 总报文数
     */
    private Integer total;

    /**
     * 当前报文号
     */
    private Integer current;

    /**
     * 语音时长
     */
    private Integer timeLong;
//
//    /*
//     *备用
//     */
//    @Getter
//    @Setter
//    private Integer spare;

    /*
     *二进制数据
     */
    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage25() {
        super(25);
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(Integer timeLong) {
        this.timeLong = timeLong;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
