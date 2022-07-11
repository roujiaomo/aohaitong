package com.aohaitong.widget

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.aohaitong.R
import com.aohaitong.interfaces.OnDialogItemClickListener
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.google.android.material.bottomsheet.BottomSheetDialog


class ChatBottomDialog(context: Context) : BottomSheetDialog(context) {

    private var mListener: OnDialogItemClickListener? = null
    private var dialog: BottomSheetDialog? = null

    init {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.chat_bottom_sheet_dialog, null)
        dialog = BottomSheetDialog(context)
        dialog?.setContentView(view)
        val ivPhotoGraph = view.findViewById<ImageView>(R.id.iv_photo_graph)
        val ivPhotoAlbum = view.findViewById<ImageView>(R.id.iv_photo_album)
        ivPhotoGraph?.onClickWithAvoidRapidAction {
            mListener?.onItemClick(0)
        }
        ivPhotoAlbum?.onClickWithAvoidRapidAction {
            mListener?.onItemClick(1)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun setOnItemListener(listener: OnDialogItemClickListener?) {
        mListener = listener
    }

    fun showDialog() {
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.hide()
    }
}