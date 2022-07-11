package com.aohaitong.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 好友类
 */
@Entity
public class FriendBean implements Parcelable {
    //当前登录用户号码
    public String nowLoginTel;
    //好友姓名
    public String name;
    //好友备注
    public String nickName;
    //好友手机号
    @Id
    public String telephone;

    @Generated(hash = 1307401888)
    public FriendBean(String nowLoginTel, String name, String nickName,
                      String telephone) {
        this.nowLoginTel = nowLoginTel;
        this.name = name;
        this.nickName = nickName;
        this.telephone = telephone;
    }

    @Generated(hash = 152145004)
    public FriendBean() {
    }

    protected FriendBean(Parcel in) {
        nowLoginTel = in.readString();
        name = in.readString();
        nickName = in.readString();
        telephone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nowLoginTel);
        dest.writeString(name);
        dest.writeString(nickName);
        dest.writeString(telephone);
    }


    public String getName() {
        return this.name == null ? "" : this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return this.nickName == null ? "" : this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTelephone() {
        return this.telephone == null ? "" : this.telephone;
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


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FriendBean> CREATOR = new Creator<FriendBean>() {
        @Override
        public FriendBean createFromParcel(Parcel in) {
            return new FriendBean(in);
        }

        @Override
        public FriendBean[] newArray(int size) {
            return new FriendBean[size];
        }
    };
}
