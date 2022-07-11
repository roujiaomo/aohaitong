package com.aohaitong.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alibaba.sdk.android.push.MessageReceiver
import com.alibaba.sdk.android.push.notification.CPushMessage
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.ui.user.NewLoadActivity


class MyMessageReceiver : MessageReceiver() {
    override fun onNotification(
        context: Context?,
        title: String,
        summary: String,
        extraMap: Map<String?, String?>
    ) {
        // TODO 处理推送通知
        Log.e(
            "MyMessageReceiver",
            "Receive notification, title: $title, summary: $summary, extraMap: $extraMap"
        )
        notification(title, summary)
    }

    override fun onMessage(context: Context?, cPushMessage: CPushMessage) {
        Log.e(
            "MyMessageReceiver",
            "onMessage, messageId: " + cPushMessage.messageId
                .toString() + ", title: " + cPushMessage.title
                .toString() + ", content:" + cPushMessage.content
        )
        notification(cPushMessage.title, cPushMessage.content)
    }

    override fun onNotificationOpened(
        context: Context?,
        title: String,
        summary: String,
        extraMap: String
    ) {
        Log.e(
            "MyMessageReceiver",
            "onNotificationOpened, title: $title, summary: $summary, extraMap:$extraMap"
        )
        notification(title, summary)

    }

    override fun onNotificationClickedWithNoAction(
        context: Context?,
        title: String,
        summary: String,
        extraMap: String
    ) {
        Log.e(
            "MyMessageReceiver",
            "onNotificationClickedWithNoAction, title: $title, summary: $summary, extraMap:$extraMap"
        )
        notification(title, summary)

    }

    override fun onNotificationReceivedInApp(
        context: Context?,
        title: String,
        summary: String,
        extraMap: Map<String?, String?>,
        openType: Int,
        openActivity: String,
        openUrl: String
    ) {
        Log.e(
            "MyMessageReceiver",
            "onNotificationReceivedInApp, title: $title, summary: $summary, extraMap:$extraMap, openType:$openType, openActivity:$openActivity, openUrl:$openUrl"
        )

        notification(title, summary)
    }

    override fun onNotificationRemoved(context: Context?, messageId: String?) {
        Log.e("MyMessageReceiver", "onNotificationRemoved")
    }

    companion object {
        // 消息接收部分的LOG_TAG
        const val REC_TAG = "receiver"
    }

    fun notification(title: String, summary: String) {
        val intent = Intent(
            MyApplication.getContext(),
            NewLoadActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            MyApplication.getContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager = MyApplication.getContext()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel("update", "update result", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
            Notification.Builder(MyApplication.getContext(), "update")
                .setSmallIcon(R.mipmap.icon_app)
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true) //自动取消
                .setContentIntent(pendingIntent) //设置删除的意图
                .setGroup("NEW_MSG")
                .setNumber(MyApplication.msgCount)
                .build()
        } else {
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(MyApplication.getContext(), "update")
                    .setSmallIcon(R.mipmap.icon_app)
                    .setContentTitle(title)
                    .setContentText(summary)
                    .setAutoCancel(true) //自动取消
                    .setContentIntent(pendingIntent) //设置删除的意图
                    .setGroup("NEW_MSG")
                    .setNumber(MyApplication.msgCount)
                    .setOngoing(true)
            notificationBuilder.build()
        }
        notificationManager.notify(123, notification)
//        updateNotificationSummary(msg, groupKey)
    }
}