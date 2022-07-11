package com.aohaitong.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenBroadcastReceiver :
    BroadcastReceiver() {

    private var action: String? = null

    override fun onReceive(context: Context?, intent: Intent) {
        action = intent.action
        when {
            Intent.ACTION_SCREEN_ON == action -> {
                // 开屏
                Log.e("qqqqq", "onReceive: 开屏")
            }
            Intent.ACTION_SCREEN_OFF == action -> {
                // 锁屏
                Log.e("qqqqq", "onReceive: 锁屏")
            }
            Intent.ACTION_USER_PRESENT == action -> {
                // 解锁
                Log.e("qqqqq", "onReceive: 解锁")
                //解锁时对比心跳的时间和当前时间 >30秒则重连
//                if (System.currentTimeMillis() - DateUtil.getInstance().time > 30 * 1000) {
//                    EventBus.getDefault().post(MsgEntity("", StatusConstant.TYPE_NO_NET))
//                    BaseController.stopHeartBeat()
//                    try {
//                        ThreadPoolManager.getInstance().cancelAllThread()
//                    } catch (e: Exception) {
//                        Log.e("qqqqq", "结束所有线程异常: ${e.message}")
//                    }
//                    BaseController.onConnectStop()
//                }
            }
        }
    }
}