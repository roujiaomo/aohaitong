package com.aohaitong.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class GroupBean {
    @Id(autoincrement = true)
    private Long id;
    private String groupId;

    private String groupName;

    private String groupMemberTel;

    private String groupManagerTel;

    @Generated(hash = 2052490365)
    public GroupBean(Long id, String groupId, String groupName, String groupMemberTel,
                     String groupManagerTel) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupMemberTel = groupMemberTel;
        this.groupManagerTel = groupManagerTel;
    }

    @Generated(hash = 405578774)
    public GroupBean() {
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupMemberTel() {
        return this.groupMemberTel;
    }

    public void setGroupMemberTel(String groupMemberTel) {
        this.groupMemberTel = groupMemberTel;
    }

    public String getGroupManagerTel() {
        return this.groupManagerTel;
    }

    public void setGroupManagerTel(String groupManagerTel) {
        this.groupManagerTel = groupManagerTel;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


}
