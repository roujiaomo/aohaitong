package com.aohaitong.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import java.util.Objects;

/**
 * 聊天内容实体类
 */
@Entity
public class ChatMsgBean {
    //id 数据库中存储用
    @Id(autoincrement = true)
    private Long id;
    //文字消息内容/文件类消息文件转换成的String类型内容
    private String msg;
    //私聊:发消息、接收消息的手机号/ 群聊:发消息的手机号
    private String telephone;
    //当前登录用户的手机号
    private String nowLoginTel;
    //发送/接收
    private int sendType;
    //发送时间
    private String time;//存时间戳

    private Date insertTime;//消息到达时间
    //发送消息的状态 0:失败 1:成功 2:正在加载中
    private int status;

    private int messageType;//消息类型 0:文字 1:语音 2:图片 3:视频

    private String filePath;//文件类消息的路径

    private int recordTime;//语音消息的时间

    //是否为群聊 群聊显示用户手机号(或昵称备注)
    private boolean isGroup;

    //群聊ID
    private String groupId;

    @Generated(hash = 1102524599)
    public ChatMsgBean(Long id, String msg, String telephone, String nowLoginTel, int sendType,
                       String time, Date insertTime, int status, int messageType, String filePath, int recordTime,
                       boolean isGroup, String groupId) {
        this.id = id;
        this.msg = msg;
        this.telephone = telephone;
        this.nowLoginTel = nowLoginTel;
        this.sendType = sendType;
        this.time = time;
        this.insertTime = insertTime;
        this.status = status;
        this.messageType = messageType;
        this.filePath = filePath;
        this.recordTime = recordTime;
        this.isGroup = isGroup;
        this.groupId = groupId;
    }

    @Generated(hash = 251455779)
    public ChatMsgBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNowLoginTel() {
        return this.nowLoginTel;
    }

    public void setNowLoginTel(String nowLoginTel) {
        this.nowLoginTel = nowLoginTel;
    }

    public int getSendType() {
        return this.sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getInsertTime() {
        return this.insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getRecordTime() {
        return this.recordTime;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMsgBean that = (ChatMsgBean) o;
        return Objects.equals(msg, that.msg) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, time);
    }

    public boolean getIsGroup() {
        return this.isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
