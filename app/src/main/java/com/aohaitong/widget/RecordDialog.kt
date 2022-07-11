package com.aohaitong.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.aohaitong.R
import com.aohaitong.databinding.DialogRecordBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.qmuiteam.qmui.util.QMUIDisplayHelper

class RecordDialog constructor(context: Context, style: Int) : Dialog(context, style) {

    private lateinit var binding: DialogRecordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_record, null, false)
        reset()
        setContentView(binding.root)
    }

    //长按录音时的状态
    fun recording() {
        if (isShowing) {
            reset()
        }
    }

    /**
     * 已经上滑到取消录音区域的状态
     */
    fun recordPrepareCancel() {
        if (isShowing) {
            binding.ivVoice.visibility = View.GONE
            binding.tvMsg.visibility = View.VISIBLE
            binding.ivRecord.setImageResource(R.drawable.ic_record_cancel)
            binding.tvMsg.text = context.getString(R.string.chat_record_cancel)
        }
    }

    /**
     * 语音消息倒计时
     */

    fun recordCountDown(restTIme: Int) {
        binding.tvMsg.text =
            String.format(context.getString(R.string.chat_record_countDown), restTIme)
    }

    /**
     * 手指离开屏幕后，重置dialog
     */
    private fun reset() {
        binding.ivVoice.visibility = View.VISIBLE
        binding.tvMsg.visibility = View.VISIBLE
        binding.tvMsg.text = context.getString(R.string.chat_record_recording)
        Glide.with(context).asGif().load(R.drawable.ic_recording).diskCacheStrategy(
            DiskCacheStrategy.RESOURCE
        ).centerCrop()
            .override(QMUIDisplayHelper.dp2px(context, 58), QMUIDisplayHelper.dp2px(context, 32))
            .into(binding.ivRecord)
    }

    fun showDialog() {
        if (!isShowing) {
            if (::binding.isInitialized) {
                reset()
            }
            show()
        }
    }

    fun hideDialog() {
        if (isShowing) {
            reset()
            dismiss()
        }
    }
}