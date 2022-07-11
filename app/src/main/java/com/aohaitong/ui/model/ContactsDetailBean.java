package com.aohaitong.ui.model;

import com.aohaitong.bean.ContactsBean;

public class ContactsDetailBean extends ContactsBean {
    public int friendApplyType;
    public int friendType;
    public int type;
    public String friendName;
    public String nickName;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setFriendApplyType(int friendApplyType) {
        this.friendApplyType = friendApplyType;
    }

    public void setFriendType(int friendType) {
        this.friendType = friendType;
    }

    public int getFriendApplyType() {
        return friendApplyType;
    }

    public int getFriendType() {
        return friendType;
    }

}
