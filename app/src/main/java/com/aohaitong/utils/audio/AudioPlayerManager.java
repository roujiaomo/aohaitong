package com.aohaitong.utils.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayerManager {

    private static AudioPlayerManager mInstance;
    private MediaPlayer mediaPlayer;

    public static AudioPlayerManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioPlayerManager();
                }
            }
        }
        return mInstance;
    }

    public Boolean preparePlay(String filePath, MediaPlayer.OnPreparedListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(listener);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void reStart() {
        Log.d("wwwww", "音频restart" + mediaPlayer);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.seekTo(0, MediaPlayer.SEEK_CLOSEST);
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isOnPlaying() {
        if (mediaPlayer == null) {
            return false;
        }
        try {
            return mediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void replay(final String url) {
        try {
            stopPlay();
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnPreparedListener(player -> {
                if (mediaPlayer != null) {
                    player.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
