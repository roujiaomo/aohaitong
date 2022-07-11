package com.aohaitong.bean.entity

/**
 * 删除单条聊天记录
 */
class DeleteChatMsgParams(
    val chatMsgId: Long = -1,
    val telephone: String = "",
    val filePath: String? = "",
    val groupId: String = ""
)