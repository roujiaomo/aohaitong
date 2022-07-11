package com.aohaitong.bean;

import java.util.Objects;

/**
 * 首页聊天记录实体类
 */
public class MessageBean {
    //姓名
    private String name;
    //昵称
    private String nickName;
    //手机号
    private String telephone;
    //最新信息
    private String message;
    //最新消息类型
    private int messageType;
    //最新消息时间
    private String time;
    //未阅读的条数
    private String unReadCount;
    //群聊Id
    private String groupId;
    //是否是群组
    private boolean isGroup;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(String unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        isGroup = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageBean that = (MessageBean) o;
        return Objects.equals(name, that.name) && Objects.equals(nickName, that.nickName) && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nickName, telephone, message, messageType, time, unReadCount, groupId);
    }
}
