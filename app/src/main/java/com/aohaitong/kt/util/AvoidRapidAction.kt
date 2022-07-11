package com.aohaitong.kt.util

import android.os.SystemClock
import kotlin.math.abs

object AvoidRapidAction {
    internal var lastClickTime: Long = 0
    private const val DEFAULT_DELAY_TIME = 300 //ms
    internal const val DELAY_TIME = 500 //ms
    internal fun action(action: () -> Unit) {
        action(DEFAULT_DELAY_TIME, action)
    }

    internal fun action(milis: Int, action: () -> Unit) {
        if (abs(SystemClock.elapsedRealtime() - lastClickTime) > milis) {
            action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }
}
