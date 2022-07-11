package com.aohaitong.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 好友申请类
 */
@Entity
public class FriendApplyBean {
    //申请人姓名
    private String name;
    //申请人昵称
    private String nickName;
    //申请人申请消息
    private String description;
    //申请人手机号
    @Id
    private String telephone;
    //申请时间
    private String time;//时间戳
    //是否通过
    private int type;
    //当前登录用户号码
    private String nowLoginTel;
    //发送还是接收
    private int sendType;
    //是否已读
    private int status;


    @Generated(hash = 728630646)
    public FriendApplyBean(String name, String nickName, String description,
                           String telephone, String time, int type, String nowLoginTel,
                           int sendType, int status) {
        this.name = name;
        this.nickName = nickName;
        this.description = description;
        this.telephone = telephone;
        this.time = time;
        this.type = type;
        this.nowLoginTel = nowLoginTel;
        this.sendType = sendType;
        this.status = status;
    }

    @Generated(hash = 1534469370)
    public FriendApplyBean() {
    }


    public String getName() {
        return this.name == null ? "" : this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description == null ? "" : this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSendType() {
        return this.sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getNowLoginTel() {
        return this.nowLoginTel;
    }

    public void setNowLoginTel(String nowLoginTel) {
        this.nowLoginTel = nowLoginTel;
    }

    public String getNickName() {
        return this.nickName == null ? "" : this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
