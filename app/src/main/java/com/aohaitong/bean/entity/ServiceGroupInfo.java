package com.aohaitong.bean.entity;

public class ServiceGroupInfo {
    /**
     * 群组ID
     */
    private Long groupId;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 群主账号
     */
    private Long groupOwner;

    /**
     * 群成员账号
     */
    private String groupMember;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(Long groupOwner) {
        this.groupOwner = groupOwner;
    }

    public String getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(String groupMember) {
        this.groupMember = groupMember;
    }
}
