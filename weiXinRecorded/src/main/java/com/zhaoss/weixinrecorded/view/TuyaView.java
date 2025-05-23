package com.zhaoss.weixinrecorded.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhaoss.weixinrecorded.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoshuang on 16/12/14.
 * 手绘涂鸦界面
 */

public class TuyaView extends View {

    private Paint mPaint;
    private Path mPath;
    private final List<Path> savePathList = new ArrayList<>();
    private final List<Paint> paintList = new ArrayList<>();
    private boolean isDrawMode;
    private OnLineChangeListener onLineChangeListener;
    private boolean touchMode;//触摸状态
    private OnTouchListener onTouchListener;

    public TuyaView(Context context) {
        super(context);
        init();
    }

    public TuyaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TuyaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setNewPaintColor(int color) {

        mPaint.setColor(color);
    }

    public int getPathSum() {
        return savePathList.size();
    }

    public Paint newPaint(int color) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.dp3));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        return paint;
    }

    private void init() {

        mPaint = newPaint(Color.WHITE);
        mPath = new Path();
    }

    public void setDrawMode(boolean flag) {
        isDrawMode = flag;
    }

    /**
     * 清除上一个绘制线条
     */
    public void backPath() {

        if (savePathList.size() != 0) {
            if (savePathList.size() == 1) {
                mPath.reset();
                savePathList.clear();
                paintList.clear();
            } else {
                savePathList.remove(savePathList.size() - 1);
                paintList.remove(paintList.size() - 1);
                mPath = savePathList.get(savePathList.size() - 1);
                mPaint = paintList.get(paintList.size() - 1);
            }
            if (onLineChangeListener != null)
                onLineChangeListener.onDeleteLine(savePathList.size());
        }
        invalidate();
    }

    /**
     * 触摸状态的回调
     */
    public interface OnTouchListener {
        void onDown();

        void onUp();
    }

    /**
     * 绘制状态的回调
     */
    public interface OnLineChangeListener {
        /**
         * @param sum 现在总共绘制线条的数目
         */
        void onDrawLine(int sum);

        void onDeleteLine(int sum);
    }

    public void setOnLineChangeListener(OnLineChangeListener onLineChangeListener) {
        this.onLineChangeListener = onLineChangeListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isDrawMode) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchMode = true;
                    touchDown(event);
                    if (onTouchListener != null) onTouchListener.onDown();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(event);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchMode = false;
                    savePathList.add(new Path(mPath));
                    paintList.add(new Paint(mPaint));
                    if (onTouchListener != null) onTouchListener.onUp();
                    if (onLineChangeListener != null)
                        onLineChangeListener.onDrawLine(savePathList.size());
                    break;
            }
            invalidate();
        }
        return isDrawMode;
    }

    private float mX;
    private float mY;

    //手指点下屏幕时调用
    private void touchDown(MotionEvent event) {

        mPath = new Path();

        float x = event.getX();
        float y = event.getY();

        mX = x;
        mY = y;

        //mPath绘制的绘制起点
        mPath.moveTo(x, y);
    }

    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();

        final float previousX = mX;
        final float previousY = mY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        //两点之间的距离大于等于3时，连接连接两点形成直线
        if (dx >= 3 || dy >= 3) {
            //两点连成直线
            mPath.lineTo(x, y);

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制之前的线条
        for (int x = 0; x < savePathList.size(); x++) {
            Path path = savePathList.get(x);
            Paint paint = paintList.get(x);
            canvas.drawPath(path, paint);
        }
        //绘制刚画的线条
        if (touchMode) canvas.drawPath(mPath, mPaint);
    }
}