package com.aohaitong;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.utils.ContactsUtil;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.worker.SocketMessageService;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jms.Session;

import cn.feng.skin.manager.loader.SkinManager;
import dagger.hilt.android.HiltAndroidApp;
import xcrash.XCrash;

@HiltAndroidApp
public class MyApplication extends Application implements Configuration.Provider {
    private static Context context;
    public static long TEL;
    public static String PASSWORD = "";
    public static boolean isBackGround = true;

    public static boolean PLAY_RING;
    public static boolean PLAY_VIBRATOR;

    public static int msgCount = 0;
    public static Session session;
    public static boolean isHaveLogin = false;

    public MyApplication() {
        context = this;
    }

    /**
     * Must call init first
     */
    private void initSkinLoader() {
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        initSkinLoader();
//        initBaiduMap();
//        new DoKit.Builder(this).build();
        setForeground();
        WorkManager.getInstance(this).cancelAllWork();
        new Thread(() -> DBManager.getInstance(MyApplication.getContext()).updateAllMsgFail()).start();
        PLAY_RING = SPUtil.instance.getBoolean(CommonConstant.SP_RING_OPEN);
        PLAY_VIBRATOR = SPUtil.instance.getBoolean(CommonConstant.SP_VIBRATOR_OPEN);
        new Thread(() -> ContactsUtil.getAllContacts(context)).start();
        Intent intent = new Intent(this, SocketMessageService.class);
        startService(intent);
        xcrash.XCrash.InitParameters initParameters = new XCrash.InitParameters();
        initParameters.setLogDir(Environment.getExternalStorageDirectory().
                getAbsolutePath() + CommonConstant.CRASH_FILE_PATH);
//        initCloudChannel(this);
        xcrash.XCrash.init(this, initParameters);
        copyAssetAndWrite("dusk.skin");
        copyAssetAndWrite("night.skin");
    }

    private void initBaiduMap() {
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }


    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("wwwww", "init cloudchannel success" + pushService.getDeviceId());
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
            }
        });
    }

    /**
     * 将asset文件写入缓存
     */
    private void copyAssetAndWrite(String fileName) {
        try {
            File cacheDir = getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return;
                }
            } else {
                if (outFile.length() > 10) {//表示已经写入一次
                    return;
                }
            }
            InputStream is = getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static Context getContext() {
        return context;
    }

    //判断APP是否在前台
    private void setForeground() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.i("qqqqq", "onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i("qqqqq", "onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i("qqqqq", "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i("qqqqq", "onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.i("qqqqq", "onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.i("qqqqq", "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i("qqqqq", "onActivityDestroyed");
            }
        });
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .build();
    }
}
