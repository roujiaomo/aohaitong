package com.aohaitong.utils

import com.aohaitong.bean.ChatMsgBean
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.constant.StatusConstant

object DataUtils {

    fun convertChatBusinessList(list: List<ChatMsgBean>): List<ChatMsgBusinessBean> {
        val chatMsgBusinessBeanList = mutableListOf<ChatMsgBusinessBean>()
        list.map { chatMsgBean ->
            val chatMsgBusinessBean = ChatMsgBusinessBean(
                id = chatMsgBean.id,
                msg = chatMsgBean.msg,
                telephone = chatMsgBean.telephone,
                sendType = chatMsgBean.sendType,
                time = chatMsgBean.time,
                status = chatMsgBean.status,
                messageType = chatMsgBean.messageType,
                filePath = chatMsgBean.filePath,
                recordTime = chatMsgBean.recordTime,
                isRecording = false
            )
            chatMsgBusinessBeanList.add(chatMsgBusinessBean)
        }
        return chatMsgBusinessBeanList
    }

    fun convertChatBusinessBean(chatMsgBean: ChatMsgBean): ChatMsgBusinessBean {
        return ChatMsgBusinessBean(
            id = chatMsgBean.id,
            msg = chatMsgBean.msg,
            telephone = chatMsgBean.telephone,
            currentUserTel = chatMsgBean.nowLoginTel,
            sendType = chatMsgBean.sendType,
            time = chatMsgBean.time,
            status = chatMsgBean.status,
            messageType = chatMsgBean.messageType,
            filePath = chatMsgBean.filePath,
            recordTime = chatMsgBean.recordTime,
            isRecording = false,
            isLoading = chatMsgBean.status == StatusConstant.SEND_LOADING,
            isFailed = chatMsgBean.status == StatusConstant.SEND_FAIL,
            showTime = TimeUtil.stampToDateMinWithOutDay(chatMsgBean.time),
            isGroup = chatMsgBean.isGroup,
            groupId = chatMsgBean.groupId
        )
    }

    fun copyChatMsgBean(chatMsgBean: ChatMsgBean): ChatMsgBean {
        return ChatMsgBean(
            chatMsgBean.id,
            chatMsgBean.msg,
            chatMsgBean.telephone,
            chatMsgBean.nowLoginTel,
            chatMsgBean.sendType,
            chatMsgBean.time,
            chatMsgBean.insertTime,
            chatMsgBean.status,
            chatMsgBean.messageType,
            chatMsgBean.filePath,
            chatMsgBean.recordTime,
            chatMsgBean.isGroup,
            chatMsgBean.groupId
        )
    }

}