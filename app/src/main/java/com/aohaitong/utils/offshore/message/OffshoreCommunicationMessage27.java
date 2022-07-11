package com.aohaitong.utils.offshore.message;


import androidx.annotation.NonNull;

/*
 * 群组成员接收A发送群组信息语句
 */
public class OffshoreCommunicationMessage27 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = 8153294163804686773L;

    /*
     *群组ID
     */

    private Long groupId;

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
    public OffshoreCommunicationMessage27() {
        super(27);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
