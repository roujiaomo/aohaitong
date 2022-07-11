package com.gyf.cactus.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Process
import com.gyf.cactus.entity.CactusConfig
import com.gyf.cactus.entity.Constant
import com.gyf.cactus.entity.ICactusInterface
import com.gyf.cactus.exception.CactusUncaughtExceptionHandler
import com.gyf.cactus.ext.*
import kotlin.system.exitProcess

/**
 * 远程服务
 *
 * @author geyifeng
 * @date 2019-08-28 17:05
 */
class RemoteService : Service(), IBinder.DeathRecipient {

    /**
     * 配置信息
     */
    private lateinit var mCactusConfig: CactusConfig

    /**
     * 服务连接次数
     */
    private var mConnectionTimes = sTimes

    /**
     * 停止标识符
     */
    private var mIsStop = false

    /**
     * 是否已经绑定
     */
    private var mIsBind = false

    /**
     * 是否已经注册linkToDeath
     */
    private var mIsDeathBind = false

    private lateinit var remoteBinder: RemoteBinder

    private var mIInterface: ICactusInterface? = null

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            log("onServiceDisconnected")
            if (!mIsStop) {
                mIsBind = startLocalService(this, mCactusConfig)
            }
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            log("onServiceConnected")
            service?.let {
                mIInterface = ICactusInterface.Stub.asInterface(it)
                    ?.apply {
                        if (asBinder().isBinderAlive && asBinder().pingBinder()) {
                            try {
                                ++mConnectionTimes
                                wakeup(mCactusConfig)
                                connectionTimes(mConnectionTimes)
                                if (!mIsDeathBind) {
                                    mIsDeathBind = true
                                    asBinder().linkToDeath(this@RemoteService, 0)
                                }
                            } catch (e: Exception) {
                                --mConnectionTimes
                            }
                        }
                    }
            }
        }
    }

    override fun binderDied() {
        log("binderDied")
        try {
            unlinkToDeath(mIInterface) {
                mIsDeathBind = false
                mIInterface = null
                if (!mIsStop) {
                    mIsBind = startLocalService(mServiceConnection, mCactusConfig)
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onCreate() {
        super.onCreate()
        CactusUncaughtExceptionHandler.instance
        try {
            log("handleNotification")
            mCactusConfig = getConfig()
            setNotification(mCactusConfig.notificationConfig)
        } catch (e: Exception) {
        }
        registerStopReceiver {
            mIsStop = true
            sTimes = mConnectionTimes
            stopService()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getParcelableExtra<CactusConfig>(Constant.CACTUS_CONFIG)?.let {
            sCactusConfig = it
            mCactusConfig = it
        }
        setNotification(mCactusConfig.notificationConfig)
        mIsBind = startLocalService(mServiceConnection, mCactusConfig, false)
        log("RemoteService is running")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopBind()
        log("RemoteService has stopped")
        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    override fun onBind(intent: Intent?): IBinder? {
        remoteBinder = RemoteBinder()
        return remoteBinder
    }

    inner class RemoteBinder : ICactusInterface.Stub() {

        override fun wakeup(config: CactusConfig) {
            mCactusConfig = config
            setNotification(mCactusConfig.notificationConfig)
        }

        override fun connectionTimes(time: Int) {
            mConnectionTimes = time
            if (mConnectionTimes > 4 && mConnectionTimes % 2 == 1) {
                ++mConnectionTimes
            }
            sTimes = mConnectionTimes
        }
    }

    /**
     * 解除相关绑定
     */
    private fun stopBind() {
        try {
            if (mIsDeathBind) {
                mIsDeathBind = false
                unlinkToDeath(mIInterface)
            }
            if (mIsBind) {
                unbindService(mServiceConnection)
                mIsBind = false
            }
        } catch (e: Exception) {
        }
    }
}