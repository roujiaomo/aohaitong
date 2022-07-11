package com.aohaitong.utils;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.ui.chat.chat.NewChatActivity;
import com.aohaitong.ui.friend.FriendApplyActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 上方通知消息的工具类
 * create by wyq 8/17
 */
public class NotificationUtil {
    private static final NotificationUtil notificationUtil = new NotificationUtil();
    private long lastRingTime = 0;
    private long lastVerTime = 0;
    private final List<Integer> friendsAddTel = new ArrayList<>();

    //删除通知的请求码
    private static final int REQUEST_CODE = 2323;

    //通知组别的id
    private static final int NOTIFICATION_GROUP_SUMMARY_ID = 1;

    //通知管理类
    private final NotificationManager mNotificationManager;

    private NotificationUtil() {
        mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    public static NotificationUtil getInstance() {
        return notificationUtil;
    }

    public void playVoice() {
        if (!MyApplication.PLAY_RING)
            return;
        if (0 < (System.currentTimeMillis() - lastRingTime) && (System.currentTimeMillis() - lastRingTime) < 500)
            return;
        lastRingTime = System.currentTimeMillis();
        Uri uri;
        String ring = SPUtil.instance.getString(CommonConstant.SP_RING_NAME);
        if (!TextUtils.isEmpty(ring)) {
            uri = Uri.parse(ring);
        } else {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone rt = RingtoneManager.getRingtone(MyApplication.getContext(), uri);
        rt.play();
    }

    public void playVibrator() {
        if (!MyApplication.PLAY_VIBRATOR)
            return;
        if (0 < (System.currentTimeMillis() - lastVerTime) && (System.currentTimeMillis() - lastVerTime) < 600)
            return;
        lastVerTime = System.currentTimeMillis();
        Vibrator vib = (Vibrator) MyApplication.getContext().getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(600);
    }

    public void newMsgNotification(String msg, long tel, String name, String nickName) {
        playVoice();
        playVibrator();
        Intent intent = new Intent(MyApplication.getContext(), NewChatActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("nickName", nickName);
        intent.putExtra("tel", tel + "");
        intent.putExtra("fromPage", "");
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        addNotificationAndUpdateSummaries("您有一条新消息来自" + tel, msg, (int) tel, pendingIntent, "NEW_MSG");
    }

    public void newGroupMsgNotification(String msg, String groupId, String groupName) {
        playVoice();
        playVibrator();
        Intent intent = new Intent(MyApplication.getContext(), NewChatActivity.class);
        intent.putExtra("isGroup", true);
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupName", groupName);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        addNotificationAndUpdateSummaries("您有一条新消息来自" + groupName, msg, Integer.parseInt(groupId.substring(0, groupId.length() - 2)), pendingIntent, "NEW_MSG");
    }


    public void newFriendApplyNotification(long tel, String msg) {
        friendsAddTel.add((int) tel);
        playVoice();
        playVibrator();
        Intent intent = new Intent(MyApplication.getContext(), FriendApplyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        addNotificationAndUpdateSummaries(tel + "请求添加好友", msg, (int) tel, pendingIntent, "FRIEND_APPLY");
    }

    /**
     * 发送通知到系统。如果设备支持消息的归类功能，则进行消息归类。
     */
    private void addNotificationAndUpdateSummaries(String title, String msg, int notificationId, PendingIntent intent, String groupKey) {
        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("update", "update result", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(MyApplication.getContext(), "update")
                    .setSmallIcon(R.mipmap.icon_app)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true) //自动取消
                    .setContentIntent(intent) //设置删除的意图
                    .setGroup(groupKey)
                    .setNumber(MyApplication.msgCount)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyApplication.getContext(), "update")
                    .setSmallIcon(R.mipmap.icon_app)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true) //自动取消
                    .setContentIntent(intent) //设置删除的意图
                    .setGroup(groupKey)
                    .setNumber(MyApplication.msgCount)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
        notificationManager.notify(notificationId, notification);
        updateNotificationSummary(msg, groupKey);
        if (MyApplication.isBackGround) {
            BadgeUtils.setCount(MyApplication.msgCount, MyApplication.getContext(), notification);
        }
    }

    // 如果有必要，增加/更新/移除通知的归类
    private void updateNotificationSummary(String msg, String groupKey) {
        int numberOfNotifications = getNumberOfNotifications();
        if (numberOfNotifications > 1) { //如果数量>=2,说明有了同样组key的通知，需要归类起来
            //将通知添加/更新归类到同一组下面
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext())
                    .setSmallIcon(R.mipmap.icon_app)
                    //添加富样式到通知的显示样式中，如果当前系统版本不支持，那么将不起作用，依旧用原来的通知样式
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setSummaryText(msg))
                    .setGroup(groupKey) //设置类组key，说明此条通知归属于哪一个归类
                    .setGroupSummary(true); //这句话必须和上面那句一起调用，否则不起作用
            final Notification notification = builder.build();
            //发送通知到状态栏
            //测试发现，发送归类状态栏也是算一条通知的。所以返回值得时候，需要-1
            mNotificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, notification);
        } else {
            //移除归类
            mNotificationManager.cancel(NOTIFICATION_GROUP_SUMMARY_ID);
        }
    }

    /**
     * 获取当前状态栏具有统一id的通知的数量
     *
     * @return 数量
     */
    private int getNumberOfNotifications() {
        //查询当前展示的所有通知的状态列表
        final StatusBarNotification[] activeNotifications = mNotificationManager
                .getActiveNotifications();
        //获取当前通知栏里头，NOTIFICATION_GROUP_SUMMARY_ID归类id的组别
        //因为发送分组的通知也算一条通知，所以需要-1
        for (StatusBarNotification notification : activeNotifications) {
            if (notification.getId() == NOTIFICATION_GROUP_SUMMARY_ID) {
                //-1是因为
                return activeNotifications.length - 1;
            }
        }
        return activeNotifications.length;
    }


    public void cancelNotification(boolean isMsg, int NOTIFICATION_ID) {
        if (isMsg)
            mNotificationManager.cancel(NOTIFICATION_ID);
        else {
            for (Integer integer : friendsAddTel) {
                mNotificationManager.cancel(integer);
            }
            friendsAddTel.clear();
        }
    }

}
