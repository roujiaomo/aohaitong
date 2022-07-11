package com.gyf.cactus.exception

/**
 * 默认的全局捕获
 *
 * @author geyifeng
 * @date 2019-12-12 14:57
 */
internal class CactusUncaughtExceptionHandler private constructor() :
    Thread.UncaughtExceptionHandler {

    companion object {
        @JvmStatic
        val instance by lazy {
            CactusUncaughtExceptionHandler()
        }
    }

    private val mDefault = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        val message = e.message
        if (message != null && (message.contains(
                "Bad notification for startForeground: " +
                        "java.lang.RuntimeException: invalid channel for service notification"
            ) || message.contains("Context.startForegroundService() did not then call Service.startForeground()"))
        ) {
//            Process.killProcess(Process.myPid())
//            exitProcess(10)
        } else {
//            mDefault?.uncaughtException(t, e)
        }
    }
}