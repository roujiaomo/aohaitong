package com.aohaitong.utils.audio;

import android.content.Context;
import android.media.AudioManager;

import com.aohaitong.MyApplication;

public class AudioFocusManager {
    /**
     * 用AudioManager获取音频焦点避免音视频声音并发问题
     */
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;

    //请求音频焦点 设置监听
    public int requestTheAudioFocus() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioFocusChangeListener == null) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//监听器
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                            //播放操作
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            //暂停操作
                            break;
                        default:
                            break;
                    }
                }
            };
        }

        int requestFocusResult = mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        return requestFocusResult;
    }

    //暂停、播放完成或退到后台释放音频焦点
    public void releaseTheAudioFocus() {
        if (mAudioManager != null && mAudioFocusChangeListener != null) {
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }
    }

    public interface AudioListener {
        void start();

        void pause();
    }
}
