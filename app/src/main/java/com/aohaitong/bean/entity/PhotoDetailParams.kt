package com.aohaitong.bean.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoDetailParams(
    val filePath: String?,
    val type: Int?
) : Parcelable