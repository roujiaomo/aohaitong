package com.aohaitong.kt.util

import com.aohaitong.BuildConfig
import com.aohaitong.constant.StatusConstant

object VersionUtil {
    fun isTestVersion(): Boolean {
        return BuildConfig.FLAVOR == StatusConstant.BUILD_IN_TEST
    }
}