package com.aohaitong.widget

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.aohaitong.R
import com.aohaitong.utils.ThreadPoolManager
import com.aohaitong.utils.audio.AudioFocusManager
import com.aohaitong.utils.audio.RecordManager
import java.text.DecimalFormat

class RecordButton(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {

    companion object {
        //当前录音状态 1:未操作 2:录音中（手指长按中） 3:准备取消（手指已上滑）
        const val STATE_NORMAL = 1
        const val STATE_RECORDING = 2
        const val STATE_WANT_TO_CANCEL = 3

        //手指滑动 距离
        const val DISTANCE_Y_CANCEL = 50
    }

    private var currentRecordStatus = STATE_NORMAL

    private var isRecording = false // 已经开始录音

    private var mDialog: RecordDialog = RecordDialog(context, R.style.record_dialog)
    private var mRecordManager: RecordManager = RecordManager()
    private var mListener: AudioFinishRecorderListener? = null
    private var actionDownListener: ActionDownListener? = null
    private var mTime: Double = 0.0

    var defaultFormat: DecimalFormat = DecimalFormat("0.0")

    private var isLongClicking = false // 触发长按

    private var isRecordOverTime = false

    private lateinit var audioFocusManager: AudioFocusManager

    init {
        //长按后，录音的回调
        mRecordManager.setOnAudioStateListener {
            //TODO 真正显示应该在audio end prepared以后
            post {
                mDialog.showDialog()
                ThreadPoolManager.getInstance().execute(mGetVoiceLevelRunnable)
            }
        }

        //按钮长按 准备录音 包括start
        setOnLongClickListener {
            isLongClicking = true
            changeState(STATE_RECORDING)
            //抢焦点，播放音频
            audioFocusManager = AudioFocusManager()
            val requestCode = audioFocusManager.requestTheAudioFocus()
            if (requestCode == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mRecordManager.prepareAudio()
            }
            false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                isRecording = true
                actionDownListener?.onEvent()
//                changeState(STATE_RECORDING)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isRecording) {
                    //根据想x,y的坐标，判断是否想要取消
                    if (moveToCancel(x, y)) { //是否移动到了取消的区域
                        changeState(STATE_WANT_TO_CANCEL) //取消发送
                    } else {
                        if (isRecordOverTime) {
                            return super.onTouchEvent(event)
                        }
                        changeState(STATE_RECORDING) //保持录音状态
                    }
                } else {
                    return super.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isLongClicking) { //长按未触发
                    reset()
                    return super.onTouchEvent(event)
                }
                //已经触发长按了但是时间小于0.6秒
                //所以消除文件夹
                if (mTime < 0.6) {
                    val toast = Toast.makeText(context, "录音时间过短", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    mRecordManager.cancel()
                    mDialog.hideDialog()
                    reset()
                } else if (currentRecordStatus == STATE_RECORDING) { //正常录制结束
                    if (defaultFormat.format(mTime).toDouble() == 0.0
                        || defaultFormat.format(mTime).toDouble() == 15.0
                    ) {
                        return super.onTouchEvent(event)
                    }
                    currentRecordStatus = STATE_NORMAL
                    mDialog.hideDialog()
                    mRecordManager.release()
                    if (mListener != null) {
                        mListener?.onFinish(mTime, mRecordManager.currentFilePath)
                    }
                    audioFocusManager.releaseTheAudioFocus()
                    reset()
                } else if (currentRecordStatus == STATE_WANT_TO_CANCEL) {
                    mDialog.hideDialog()
                    mRecordManager.cancel()
                    reset()
                }

            }
        }
        return super.onTouchEvent(event)
    }

    //改变状态
    private fun changeState(state: Int) {
        if (state == STATE_NORMAL) {
            setBackgroundResource(R.drawable.shape_btn_normal)
            setText(R.string.chat_record_normal)
        }
        if (currentRecordStatus != state) {
            when (state) {
                STATE_NORMAL -> {//正常状态
                    setBackgroundResource(R.drawable.shape_btn_normal)
                    setText(R.string.chat_record_normal)
                }
                STATE_RECORDING -> {//录音中，松开结束
                    setBackgroundResource(R.drawable.shape_btn_press)
                    setText(R.string.chat_record_btn_press)
                    if (isRecording) {
                        mDialog.recording()
                    }
                }
                STATE_WANT_TO_CANCEL -> {
                    setBackgroundResource(R.drawable.shape_btn_press)
                    setText(R.string.chat_record_btn_cancel)
                    mDialog.recordPrepareCancel()
                }
            }
            currentRecordStatus = state
        }
    }

    /**
     * 恢复到正常状态
     */
    private fun reset() {
        isRecording = false
        isLongClicking = false
        try {
            ThreadPoolManager.getInstance().remove(mGetVoiceLevelRunnable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mTime = 0.0
        isRecordOverTime = false
        changeState(STATE_NORMAL)
    }

    fun setAudioFinishRecorderListener(listener: AudioFinishRecorderListener?) {
        mListener = listener
    }

    fun setActionDownListener(listener: ActionDownListener?) {
        actionDownListener = listener
    }

    //获取音量大小的Runnable
    private val mGetVoiceLevelRunnable = Runnable {
        mTime = 0.0
        isRecordOverTime = false
        while (isRecording) {
            try {
                Thread.sleep(500)
                mTime = defaultFormat.format(mTime + 0.5).toDouble()
                when (defaultFormat.format(mTime).toDouble()) {
                    10.0, 11.0, 12.0, 13.0, 14.0 -> post {
                        mDialog.recordCountDown(15 - mTime.toInt())
                    }
                }
                if (mTime > 14.5 && defaultFormat.format(mTime).toDouble() == 15.0) {
                    if (currentRecordStatus == STATE_NORMAL) {
                        return@Runnable
                    }
                    isRecordOverTime = true
//                    //解决与Action_UP重复发送消息
                    currentRecordStatus = STATE_NORMAL
                    isRecording = false
                    post {
                        mDialog.hideDialog()
                    }
                    mRecordManager.release()
                    if (mListener != null) {
                        mListener?.onFinish(
                            defaultFormat.format(mTime).toDouble(),
                            mRecordManager.currentFilePath
                        )
                    }
                    reset()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                mTime = 0.0
            }
        }
    }

    /**
     * 移动出了按钮的范围
     */
    private fun moveToCancel(x: Int, y: Int): Boolean {
        //如果左右滑出 button
        if (x < 0 || x > width) {
            return true
        }
        //如果上下滑出 button  加上我们自定义的距离
        return y < -DISTANCE_Y_CANCEL || y > height + DISTANCE_Y_CANCEL
    }

}


/**
 * 录音完成后的回调
 */
interface AudioFinishRecorderListener {
    //时长  和 文件
    fun onFinish(seconds: Double, filePath: String?)
}

/**
 * 录音完成后的回调
 */
interface ActionDownListener {
    fun onEvent()
}