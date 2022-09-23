package com.aohaitong.data

import android.content.Context
import com.aohaitong.bean.ChatMsgBean
import com.aohaitong.bean.FriendBean
import com.aohaitong.bean.GroupBean
import com.aohaitong.bean.MessageBean
import com.aohaitong.db.DBManager
import javax.inject.Inject

class DataBaseRepository @Inject constructor(
    private val context: Context
) {
    //获取MessageFragment页列表消息(MessageFragment)
    fun getMessageList(searchStr: String): List<MessageBean> =
        DBManager.getInstance(context).getMsgList(searchStr)

    //长按删除页列表消息(MessageFragment)
    fun deleteChatMessage(telephone: String, isGroup: Boolean) {
        DBManager.getInstance(context).delMsgByTel(telephone, isGroup)
    }

    //获取好友列表
    fun getFriendList(searchStr: String): List<FriendBean> =
        DBManager.getInstance(context).selectAllFriend(searchStr)

    //获取分页聊天记录(NewChatActivity)
    fun getChatHistoryListByPage(userTel: String, pageNum: Int): List<ChatMsgBean> =
        DBManager.getInstance(context).getNewsMsgByPage(userTel, pageNum)

    //删除单条聊天记录(NewChatActivity)
    fun deleteChatMsgById(chatMsgId: Long) {
        DBManager.getInstance(context).delMsgById(chatMsgId)
    }

    //获取我的群聊(MyGroupActivity)
    fun getMyGroupByOwnerTel(ownerTel: String): List<GroupBean> =
        DBManager.getInstance(context).getGroupListByTel(ownerTel)

    //获取群聊聊天记录(NewChatActivity)
    fun getGroupChatHistoryList(groupId: Long): List<ChatMsgBean> =
        DBManager.getInstance(context).getGroupMessageByGroupId(groupId)

    //获取分页群聊聊天记录(NewChatActivity)
    fun getGroupChatListByPage(groupId: Long, pageNum: Int): List<ChatMsgBean> =
        DBManager.getInstance(context).getGroupMessageListByPage(groupId, pageNum)


    //获取我的群聊(GroupDetailActivity)
    fun getGroupInfo(groupId: String): GroupBean =
        DBManager.getInstance(context).getGroupInfoById(groupId)
}