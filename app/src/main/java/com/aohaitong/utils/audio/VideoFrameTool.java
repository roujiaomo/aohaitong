package com.aohaitong.utils.audio;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class VideoFrameTool {

    private static VideoFrameTool instance;

    public static VideoFrameTool getInstance() {
        if (instance == null) {
            instance = new VideoFrameTool();
        }
        return instance;
    }

    /**
     * 获取网络视频第一帧
     *
     * @param videoUrl
     * @return
     */
    public void loadFirst(String videoUrl, ImageView cover) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            Log.e("zhu", e.toString());
        } finally {
            retriever.release();
        }
        if (bitmap != null) {
            cover.setImageBitmap(bitmap);
        }
    }

    /**
     * 获取本地视频的第一帧
     *
     * @param localPath
     * @return
     */
    public Bitmap getLocalVideoBitmap(String localPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }


    /**
     * 使用Glide方式获取视频某一帧
     *
     * @param context         上下文
     * @param uri             视频地址
     * @param imageView       设置image
     * @param frameTimeMicros 获取某一时间帧.
     */
    public void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }

            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }

}