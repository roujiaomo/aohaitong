package com.aohaitong.utils.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlayLocalMusicController {
    private static PlayLocalMusicController instance;
    private AudioModeManger audioModeManger;
    private MediaPlayer mediaPlayer;
    private int position;
    private int mBufferProgress;
    private int startTime;
    private int endTime;
    private PlayMusicCompleteListener mCompleteListener;
    private PlayMusicErrorListener mErrorListener;
    private PlayOnBufferingUpdateListener mPlayOnBufferingUpdateListener;
    private PlayMusicPrepareCompleteListener mPlayMusicPrepareCompleteListener;

    public interface PlayMusicCompleteListener {
        void playMusicComplete();
    }

    public interface PlayMusicErrorListener {
        void playMusicError();
    }

    public interface PlayMusicPrepareCompleteListener {
        void playMusicPrepareComplete();
    }

    public interface PlayOnBufferingUpdateListener {
        void PlayOnBufferingUpdate(int progress);
    }

    public void setCompleteListener(PlayMusicCompleteListener mListener) {
        if (mListener != null) {
            mCompleteListener = mListener;
        }
    }

    public void setOnErrorListener(PlayMusicErrorListener listener) {
        if (listener != null) {
            mErrorListener = listener;
        }
    }

    public void setOnPrepareCompleteListener(PlayMusicPrepareCompleteListener listener) {
        if (listener != null) {
            mPlayMusicPrepareCompleteListener = listener;
        }
    }

    public void setPlayOnBufferingUpdateListener(PlayOnBufferingUpdateListener listener) {
        if (listener != null) {
            mPlayOnBufferingUpdateListener = listener;
            if (mediaPlayer != null) {
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        mBufferProgress = percent;
                        mPlayOnBufferingUpdateListener.PlayOnBufferingUpdate(percent);
                    }
                });
            }
        }
    }

    /**
     * 获得单例
     *
     * @return
     */
    public static PlayLocalMusicController getInstance() {
        if (instance == null) {
            instance = new PlayLocalMusicController();
        }
        return instance;
    }

    public PlayLocalMusicController() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

    }

    /**
     * 重新播放音频，not fix
     */
    public void reStartPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(startTime);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.start();
                    endTime = mp.getDuration();
                    if (mPlayMusicPrepareCompleteListener != null)
                        mPlayMusicPrepareCompleteListener.playMusicPrepareComplete();
                }
            });
        }
    }

    private void registerAudioModeManger() {
        if (audioModeManger == null) {
            audioModeManger = new AudioModeManger();
        }
        audioModeManger.register();
    }

    public void playMusic(final String url, final PlayMusicCompleteListener listener, final PlayMusicErrorListener errListener) {
        try {
            if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
            this.mCompleteListener = listener;
            this.mErrorListener = errListener;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(false);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
//                        registerAudioModeManger();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    if (mCompleteListener != null)
                        mCompleteListener.playMusicComplete();
                    stopMediaPlayer();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer player, int what, int extra) {
                    stopMediaPlayer();
                    if (mErrorListener != null)
                        mErrorListener.playMusicError();
                    return false;
                }
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            if (mErrorListener != null)
                mErrorListener.playMusicError();

        }
    }

    public void playMusicSeekTo(final String url, final int startTime) {
        try {
            if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
            this.startTime = startTime;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(false);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(startTime);
//                        registerAudioModeManger();
                    }
                }
            });
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.start();
                    endTime = mp.getDuration();
                    if (mPlayMusicPrepareCompleteListener != null)
                        mPlayMusicPrepareCompleteListener.playMusicPrepareComplete();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    if (mCompleteListener != null)
                        mCompleteListener.playMusicComplete();
                    stopMediaPlayer();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer player, int what, int extra) {
                    stopMediaPlayer();
                    if (mErrorListener != null)
                        mErrorListener.playMusicError();
                    return false;
                }
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            if (mErrorListener != null)
                mErrorListener.playMusicError();
        }
    }

    public int getCurrentPlayTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getTotalTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void seekTo(int seekPos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(seekPos);
        }
    }

    /**
     * 页面被挂起的时候调用
     */
    public void onStopForMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (mCompleteListener != null) {
            mCompleteListener.playMusicComplete();
        }
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
//            if (audioModeManger != null)audioModeManger.unregister();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        try {
            boolean isPlaying = mediaPlayer.isPlaying();
            return isPlaying;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
