package com.aohaitong.utils.offshore.message;


/**
 * 近海通信 消息基类。
 */
public class OffshoreCommunicationMessage {

    private static final long serialVersionUID = -9127783021537118752L;

    /*
     *消息ID
     */

    private Integer msgId;

    /*
     *序号
     */

    private Integer sequenceNumber;

    /*
     *年
     */

    private Integer year;

    /*
     *月
     */

    private Integer month;

    /*
     *日
     */

    private Integer day;

    /*
     *小时
     */

    private Integer hour;

    /*
     *分
     */

    private Integer minute;

    /*
     *秒
     */

    private Integer second;

    /*
     *应答标志
     */

    private Integer resFlag;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getResFlag() {
        return resFlag;
    }

    public void setResFlag(Integer resFlag) {
        this.resFlag = resFlag;
    }

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage() {
    }

    /**
     * 构造方法。
     *
     * @param msgId 消息 ID
     */
    protected OffshoreCommunicationMessage(Integer msgId) {
        setMsgId(msgId);
    }
}
