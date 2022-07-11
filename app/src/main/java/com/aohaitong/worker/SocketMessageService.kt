package com.aohaitong.worker

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.aohaitong.utils.ThreadPoolManager
import com.aohaitong.utils.offshore.util.JhdAnalysisGroupUtil
import com.aohaitong.utils.offshore.util.JhdAnalysisUtil

class SocketMessageService : Service() {

    //开启服务
    private val runnable1: Runnable = Runnable {
        while (true) {
            try {

                Thread.sleep(1000)
                JhdAnalysisUtil.messageAnalysis()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //开启服务
    private val runnable2: Runnable = Runnable {
        while (true) {
            try {
                Thread.sleep(1000)
                JhdAnalysisUtil.messageTimeOut()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //开启服务
    private val runnable3: Runnable = Runnable {
        while (true) {
            try {
                Thread.sleep(1000)
                JhdAnalysisUtil.messageResend()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //开启服务
    private val runnable4: Runnable = Runnable {
        while (true) {
            try {

                Thread.sleep(1000)
                JhdAnalysisGroupUtil.messageAnalysis()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //开启服务
    private val runnable5: Runnable = Runnable {
        while (true) {
            try {
                Thread.sleep(1000)
                JhdAnalysisGroupUtil.messageTimeOut()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //开启服务
    private val runnable6: Runnable = Runnable {
        while (true) {
            try {
                Thread.sleep(1000)
                JhdAnalysisGroupUtil.messageResend()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        //发送完所有包开始计时
        ThreadPoolManager.getInstance().execute(runnable1)
        ThreadPoolManager.getInstance().execute(runnable2)
        ThreadPoolManager.getInstance().execute(runnable3)
        ThreadPoolManager.getInstance().execute(runnable4)
        ThreadPoolManager.getInstance().execute(runnable5)
        ThreadPoolManager.getInstance().execute(runnable6)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        ThreadPoolManager.getInstance().remove(runnable1)
        ThreadPoolManager.getInstance().remove(runnable2)
        ThreadPoolManager.getInstance().remove(runnable3)
        ThreadPoolManager.getInstance().remove(runnable4)
        ThreadPoolManager.getInstance().remove(runnable5)
        ThreadPoolManager.getInstance().remove(runnable6)
    }
}