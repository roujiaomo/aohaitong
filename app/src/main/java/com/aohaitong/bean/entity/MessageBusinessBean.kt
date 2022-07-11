package com.aohaitong.bean.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageBusinessBean(
    val name: String? = "",
    val nickName: String? = "",
    val telephone: String = "",
    val showName: String = "",
    val message: String = "",
    val messageType: Int = 0,
    val showTime: String = "",
    val realTime: String = "",
    val unReadCount: String = "",
    val isShowUnRead: Boolean = false,
    val groupId: String = "",
    val isGroup: Boolean = false

) : Parcelable