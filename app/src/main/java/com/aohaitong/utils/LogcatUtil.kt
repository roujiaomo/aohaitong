package com.aohaitong.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.kt.util.SingletonHolder
import com.aohaitong.worker.WorkerName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class LogcatUtil private constructor(context: Context) {

    private var path: String = ""

    companion object : SingletonHolder<LogcatUtil, Context>(::LogcatUtil) {
        const val KEY_FILENAME = "KEY_FILENAME"

    }

    init {
        path = (context.filesDir.absolutePath
                + File.separator + "AoHaiTongLog")
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun start() {
        val workSendRequest = OneTimeWorkRequest.Builder(LogUtilWorker::class.java)
            .setInputData(workDataOf(KEY_FILENAME to path)).build()
        WorkManager.getInstance(MyApplication.getContext()).beginUniqueWork(
            WorkerName.Logcat_UNIQUE_NAME,
            ExistingWorkPolicy.REPLACE, workSendRequest
        ).enqueue()
    }

    fun stop() {
        WorkManager.getInstance(MyApplication.getContext())
            .cancelUniqueWork(WorkerName.Logcat_UNIQUE_NAME)
    }


    class LogUtilWorker(
        context: Context,
        workerParams: WorkerParameters
    ) : CoroutineWorker(context, workerParams) {

        private lateinit var logcatProc: Process
        private var mReader: BufferedReader? = null
        private var mRunning = true
        var cmds: String? = null
        private var out: FileOutputStream? = null
        private var mPId = ""

        init {
            mPId = android.os.Process.myPid().toString()
            val dir = inputData.getString(KEY_FILENAME)
            try {
                out = FileOutputStream(
                    File(
                        dir, "log_" + getFileName() + ".log"
                    )
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat *:e | grep \"($mPId)\""
        }

        override suspend fun doWork() = withContext(Dispatchers.IO) {
            setForegroundAsync(doLogWork())
            Result.success()
        }

        private suspend fun doLogWork(): ForegroundInfo {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds)
                mReader = BufferedReader(
                    InputStreamReader(
                        logcatProc.inputStream
                    ), 1024
                )
                var line: String = ""
                while (mRunning && mReader?.readLine().also {
                        if (it != null) {
                            line = it
                        }
                    } != null) {
                    if (!mRunning) {
                        break
                    }
                    if (line.isEmpty()) {
                        continue
                    }
                    if (out != null && line.contains(mPId)) {
                        out!!.write(
                            """$line
    """.toByteArray()
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                logcatProc.destroy()
//                logcatProc = null
                if (mReader != null) {
                    try {
                        mReader!!.close()
                        mReader = null
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (out != null) {
                    try {
                        out!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    out = null
                }
            }

            val intent = WorkManager.getInstance(applicationContext)
                .createCancelPendingIntent(id)
            val notification = NotificationCompat.Builder(applicationContext, "MQReceive")
                .setContentTitle("遨海通")
                .setContentText("正在后台运行")
                .setSmallIcon(R.mipmap.icon_app)
                .setOngoing(true)
                .build()
            return ForegroundInfo(0, notification)
        }

        fun getFileName(): String? {
            val format = SimpleDateFormat("yyyy-MM-dd")
            return format.format(Date(System.currentTimeMillis())) // 2012年10月03日 23:41:31
        }
    }

}