package com.aohaitong.bean.entity

/**
 * 删除整个人/群组聊天记录
 */
class DeleteChatParams(
    val isGroup: Boolean,
    val deleteParams: String = "",
)