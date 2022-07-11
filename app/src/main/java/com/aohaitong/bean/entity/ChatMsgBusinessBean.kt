package com.aohaitong.bean.entity

import java.util.*

data class ChatMsgBusinessBean(
    var id: Long = -1,
    var msg: String? = "",
    var telephone: String? = "",
    var currentUserTel: String = "",
    var sendType: Int = -1,
    var time: String? = "",
    var insertTime: Date = Date(),
    var status: Int = 0,
    var isLoading: Boolean = false,
    var isFailed: Boolean = false,
    var showTime: String? = "",
    var isRecording: Boolean = false,
    var messageType: Int = 0, //消息类型 0,1,2 文字，语音，图片
    var filePath: String? = "",
    var recordTime: Int = 0,
    var isGroup: Boolean = false,
    var groupId: String = ""
)