package com.aohaitong.bean;

/**
 * 用于刷新 GroupCreateActivity/MyGroupActivity/NewMessageFragment/NewChatActivity
 * 14 创建群聊处理
 * 15 群主邀请人进入群聊 更新首页聊天页面 聊天页面
 * 16 被邀请进入群聊 更新我的群聊页面 首页聊天页面
 * 17 群里其他人接收群主邀请其他人入群 更新首页聊天页面 聊天页面
 * 18 群主移除群成员 更新首页聊天页面 聊天页面
 * 19 自己被移除群聊 更新我的群聊页面 首页聊天页面 聊天页面
 * 20 自己退出群聊 更新我的群聊页面 聊天页面
 * 21 其他群组成员接收有人被移除群聊/群成员退群 更新首页聊天页面 聊天页面
 * 22 群主解散群聊 更新我的群聊页面
 * 30 自己修改群名称  更新首页聊天页面 聊天页面
 * 31 有人修改群名称 更新我的群聊页面 首页聊天页面 聊天页面
 */
public class GroupEventEntity {
    public GroupEventEntity(int groupEventId, String groupId) {
        this.GroupEventId = groupEventId;
        this.groupId = groupId;
    }

    private int GroupEventId;
    private String groupId;

    public int getGroupEventId() {
        return GroupEventId;
    }

    public void setGroupEventId(int groupEventId) {
        GroupEventId = groupEventId;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
