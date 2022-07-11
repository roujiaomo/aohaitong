package com.aohaitong.widget;

import android.content.Context;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.blankj.utilcode.util.ScreenUtils;

public class MatchParentVideoView extends VideoView {
    private int videoWidth = 0;
    private int videoHeight = 0;
    private int videoRotation = 0;
    private int contentWidth = 0;
    private int contentHeight = 0;

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }


    public MatchParentVideoView(Context context) {
        this(context, null);
    }

    public MatchParentVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchParentVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getWidth(), widthMeasureSpec);
        int height = getDefaultSize(getHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void setVideoPath(String path) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String widthString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String heightString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String rotationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

            videoWidth = Integer.parseInt(widthString);
            videoHeight = Integer.parseInt(heightString);
            int tempRotation = Integer.parseInt(rotationString);
            videoRotation = (tempRotation % 360 + 360) % 360;
            if (videoRotation == 90 || videoRotation == 270) {
                int temp = videoWidth;
                videoWidth = videoHeight;
                videoHeight = temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setVideoPath(path);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (contentHeight > 0 && contentWidth > 0) { // always call onSizeChanged, make sure only do measure rect once.
            return;
        } else {
            contentHeight = h;
            contentWidth = w;

            int width = getVideoWidth();
            int height = getVideoHeight();
            if (width > 0 && height > 0) { //make sure validate video
                float measuredWidth = 0;
                float measuredHeight = 0;

                Rect measureRect = getMeasuredRect(width, height, contentWidth, contentHeight);
                measuredWidth = measureRect.width();
                measuredHeight = measureRect.height();

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
//                layoutParams.height =  QMUIDisplayHelper.dp2px(getContext(), (int) measuredHeight);
//                layoutParams.width =QMUIDisplayHelper.dp2px(getContext(),  (int) measuredWidth);
                //判断屏幕宽高
                int screenWidth = ScreenUtils.getScreenWidth();
                int screenHeight = ScreenUtils.getAppScreenHeight();
                //判断
                //视频宽>高
                if (measuredWidth > measuredHeight) {
                    measuredHeight = measuredHeight * (screenWidth / measuredWidth);
                    measuredWidth = screenWidth;
                } else {
                    double radio = (measuredWidth / screenWidth) / (measuredHeight / screenHeight);
                    //视频宽高比 > 屏幕宽高比
                    if (radio > 1) {
                        measuredHeight = measuredHeight * (screenWidth / measuredWidth);
                        measuredWidth = screenWidth;
                    } else {
                        measuredWidth = measuredWidth * (screenHeight / measuredHeight);
                        measuredHeight = screenHeight;
                    }
                }

                layoutParams.height = (int) measuredHeight;
                layoutParams.width = (int) measuredWidth;

                layoutParams.leftMargin = Math.max(0, (contentWidth - (int) measuredWidth) / 2);
                layoutParams.topMargin = Math.max(0, (contentHeight - (int) measuredHeight) / 2);
                setLayoutParams(layoutParams);
                requestLayout();
            }
        }
    }

    private Rect getMeasuredRect(int videoWidth, int videoHeight, int contentWidth, int contentHeight) {
        float measuredWidth = 0;
        float measuredHeight = 0;

        if (videoWidth <= contentWidth && videoHeight <= contentHeight) {
            measuredWidth = videoWidth;
            measuredHeight = videoHeight;
        }

        if (videoWidth > contentWidth && videoHeight <= contentHeight) {
            measuredWidth = contentWidth;
            measuredHeight = measuredWidth * ((float) videoHeight / (float) videoWidth);
        }

        if (videoHeight > contentHeight && videoWidth <= contentWidth) {
            measuredHeight = contentHeight;
            measuredWidth = measuredHeight * ((float) videoWidth / (float) videoHeight);
        }

        if (videoHeight > contentHeight && videoWidth > contentWidth) {
            measuredWidth = contentWidth;
            measuredHeight = (float) contentWidth * (float) videoHeight / (float) videoWidth;
            return getMeasuredRect((int) measuredWidth, (int) measuredHeight, contentWidth, contentHeight);
        }

        Rect rect = new Rect();
        rect.right = (int) measuredWidth;
        rect.bottom = (int) measuredHeight;

        return rect;
    }
}