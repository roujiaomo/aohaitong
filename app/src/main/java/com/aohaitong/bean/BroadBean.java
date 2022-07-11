package com.aohaitong.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 广播类
 * 存放近三天收到的广播并展示出来
 */
@Entity
public class BroadBean {
    //id 数据库中存储用
    @Id(autoincrement = true)
    private Long id;

    private String content;
    //业务类型 0=天气预报信息；1=航行预警信息；2=广告信息；3-15未使用
    private int businessType;

    private long receiveTime;

    @Generated(hash = 719656706)
    public BroadBean(Long id, String content, int businessType, long receiveTime) {
        this.id = id;
        this.content = content;
        this.businessType = businessType;
        this.receiveTime = receiveTime;
    }

    @Generated(hash = 2005665179)
    public BroadBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getReceiveTime() {
        return this.receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public int getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

}
