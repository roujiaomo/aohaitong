package com.aohaitong.widget

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.aohaitong.R
import com.aohaitong.constant.StatusConstant
import com.aohaitong.interfaces.OnDialogItemClickListener
import com.lxj.xpopup.core.BubbleAttachPopupView

class ChatMsgMenuPopup(
    context: Context,
    private val messageType: Int
) : BubbleAttachPopupView(context) {
    private var mListener: OnDialogItemClickListener? = null

    override fun getImplLayoutId(): Int {
        return R.layout.popup_chat_menu
    }

    override fun onCreate() {
        super.onCreate()
        val consDelete: ConstraintLayout = findViewById(R.id.cons_delete)
        val consCopy: ConstraintLayout = findViewById(R.id.cons_copy)
        val consTransfer: ConstraintLayout = findViewById(R.id.cons_transfer)
        consDelete.visibility = VISIBLE
        when (messageType) {
            StatusConstant.TYPE_TEXT_MESSAGE -> {
                consCopy.visibility = VISIBLE
                consTransfer.visibility = VISIBLE
            }
            else -> {
                consCopy.visibility = GONE
                consTransfer.visibility = GONE
            }
        }
        consDelete.setOnClickListener {
            mListener?.onItemClick(0)
        }
        consCopy.setOnClickListener {
            mListener?.onItemClick(1)
        }
        consTransfer.setOnClickListener {
            mListener?.onItemClick(2)
        }
    }

    fun setOnItemListener(listener: OnDialogItemClickListener?) {
        mListener = listener
    }
}