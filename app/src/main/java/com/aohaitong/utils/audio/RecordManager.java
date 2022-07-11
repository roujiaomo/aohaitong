package com.aohaitong.utils.audio;


import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.aohaitong.constant.CommonConstant;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RecordManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static RecordManager mInstance;

    private boolean isPrepared;

    public final String RECORD_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + CommonConstant.RECORD_FILE_PATH;
    public static final int MAX_LENGTH = 1000 * 60 * 20;// 最大录音时长1000*20*10;

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public static RecordManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 准备
     */
    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(RECORD_FILE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            mCurrentFilePath = RECORD_FILE_PATH + generateFileName();

            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
            }
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setAudioSamplingRate(1);//设置录制的音频采样率
            mMediaRecorder.setAudioEncodingBitRate(4000);// 录制的音频编码比特率
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            } catch (IllegalStateException | IOException e) {
                Log.e(CommonConstant.LOGCAT_TAG, "录音异常1: " + e.getMessage());
            }
            //准备结束
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(CommonConstant.LOGCAT_TAG, "录音异常2: " + e.getMessage());
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public void release() {
        if (!isPrepared) {
            return;
        }
        try {
            if (mMediaRecorder != null) {
//                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            Log.e(CommonConstant.LOGCAT_TAG, "录音释放异常");
        }
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}