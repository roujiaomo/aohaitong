package com.aohaitong.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    const val CAMERA_PERMISSION = 123
    const val STORAGE_PERMISSION = 124
    const val AUDIO_PERMISSION = 125

    fun checkPermissionAllGranted(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

}