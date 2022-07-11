package com.aohaitong.utils.audio;

import static android.content.Context.POWER_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.PowerManager;

import com.aohaitong.MyApplication;

public class AudioModeManger {

    private AudioManager audioManager;
    private SensorManager sensorManager;
    private Sensor mProximiny;
    private onSpeakerListener mOnSpeakerListener;
    private PowerManager.WakeLock wakeLock;
    private PowerManager mPowerManager;

    /**
     * 扬声器状态监听器
     * 如果要做成类似微信那种切换后重新播放音频的效果，需要这个监听回调
     * isSpeakerOn 扬声器是否打开
     */
    public interface onSpeakerListener {
        void onSpeakerChanged(boolean isSpeakerOn);
    }


    public void setOnSpeakerListener(onSpeakerListener listener) {
        if (listener != null) {
            mOnSpeakerListener = listener;
        }
    }

    public AudioModeManger() {

    }

    /**
     * 距离传感器监听者
     */
    private final SensorEventListener mDistanceSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (audioManager == null) {
                return;
            }
            if (checkIsWired()) {
                // 如果耳机已插入，设置距离传感器失效
                return;
            }
            if (!AudioPlayerManager.getInstance().isOnPlaying()) {
                return;
            }
            float f_proximiny = event.values[0];
            if (f_proximiny >= mProximiny.getMaximumRange()) {
                setScreenOn();
                setSpeakerPhoneOn(true);
                if (mOnSpeakerListener != null) {
                    mOnSpeakerListener.onSpeakerChanged(true);
                }
            } else {//听筒模式
                setSpeakerPhoneOn(false);
                setScreenOff();
                if (mOnSpeakerListener != null) {
                    mOnSpeakerListener.onSpeakerChanged(false);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /**
     * 注册距离传感器监听
     */
    public void register() {
        if (audioManager == null) {
            audioManager = (AudioManager) MyApplication.getContext()
                    .getSystemService(Context.AUDIO_SERVICE);
        }
        if (sensorManager == null) {
            sensorManager = (SensorManager) MyApplication.getContext()
                    .getSystemService(Context.SENSOR_SERVICE);
        }
        if (mPowerManager == null) {
            mPowerManager = (PowerManager) MyApplication.getContext().getSystemService(POWER_SERVICE);
        }
        if (sensorManager != null && mDistanceSensorListener != null) {
            mProximiny = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(mDistanceSensorListener, mProximiny,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * 取消注册距离传感器监听
     */
    public void unregister() {
        if (sensorManager != null && mDistanceSensorListener != null) {
            sensorManager.unregisterListener(mDistanceSensorListener);
        }

    }


    /**
     * 听筒、扬声器切换
     * <p>
     * 注释： 敬那些年踩过的坑和那些网上各种千奇百怪坑比方案！！
     * <p>
     * AudioManager设置声音类型有以下几种类型（调节音量用的是这个）:
     * <p>
     * STREAM_ALARM 警报
     * STREAM_MUSIC 音乐回放即媒体音量
     * STREAM_NOTIFICATION 窗口顶部状态栏Notification,
     * STREAM_RING 铃声
     * STREAM_SYSTEM 系统
     * STREAM_VOICE_CALL 通话
     * <p>
     * ------------------------------------------
     * <p>
     * AudioManager设置声音模式有以下几个模式（切换听筒和扬声器时setMode用的是这个）
     * <p>
     * MODE_NORMAL 正常模式，即在没有铃音与电话的情况
     * MODE_RINGTONE 铃响模式
     * MODE_IN_CALL 接通电话模式 5.0以下
     * MODE_IN_COMMUNICATION 通话模式 5.0及其以上
     */
    public void setSpeakerPhoneOn(boolean on) {
        //开启扬声器模式
        if (on) {
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FX_KEY_CLICK);

        }
        //开启听筒模式,重播音频
        else {
            audioManager.setSpeakerphoneOn(false);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
        }
    }


    private boolean checkIsWired() {
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device : devices) {
            int deviceType = device.getType();

            if (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceType == AudioDeviceInfo.TYPE_USB_DEVICE
                    || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                    || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                return true;
            }
        }
        return false;
    }


    private void setScreenOff() {
        if (wakeLock == null) {
            wakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "wwwww");
        }
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }

    public void setScreenOn() {
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
            wakeLock = null;
        }
    }
}
