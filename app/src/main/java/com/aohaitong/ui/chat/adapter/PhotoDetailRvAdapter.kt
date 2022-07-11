package com.aohaitong.ui.chat.adapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.RelativeLayout
import com.aohaitong.R
import com.aohaitong.base.BaseRvAdapter
import com.aohaitong.bean.entity.PhotoDetailParams
import com.aohaitong.interfaces.OnChatItemClickListener
import com.aohaitong.utils.BitmapUtil
import com.aohaitong.widget.MatchParentVideoView
import com.github.chrisbanes.photoview.PhotoView

class PhotoDetailRvAdapter(
    val context: Context,
    layoutId: Int,
    private val mListData: List<PhotoDetailParams>
) :
    BaseRvAdapter<PhotoDetailParams>(context, layoutId) {
    private var currentVideoFilePath: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        layoutId = viewType
        var view: View = LayoutInflater.from(mContext).inflate(layoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindData(holder, position, mListData[position])
    }

    override fun bindData(holder: BaseViewHolder, position: Int, data: PhotoDetailParams) {
        mListData[position].filePath?.let {
            if (mListData[position].type == 2) {
                val bitmap = BitmapUtil.getDiskBitmap(data.filePath)
                val ivPhoto = holder.itemView.findViewById<PhotoView>(R.id.iv_photo_detail)
                ivPhoto.setImageBitmap(bitmap)
                if (bitmap != null) {
                    val drawable = BitmapDrawable(context.resources, bitmap)
                    ivPhoto.setImageDrawable(drawable)
                    ivPhoto.setOnClickListener {
                        onItemClickListener?.onItemClick(position, R.id.iv_photo)
                    }
                }
            } else {
                val videoView = holder.itemView.findViewById<MatchParentVideoView>(R.id.video_view)
                val videoParent = holder.itemView.findViewById<RelativeLayout>(R.id.rl_video_parent)
                currentVideoFilePath = mListData[position].filePath.toString()
                videoView.setVideoPath(currentVideoFilePath)
                val mediaController = MediaController(context)
//                        mediaController.visibility = View.GONE
                videoView.setMediaController(mediaController)
                videoView.requestFocus()
                videoView.seekTo(0)
                videoView.start()
                videoParent.setOnClickListener {
                    if (mediaController.isShowing) {
                        mediaController.hide()
                    } else {
                        mediaController.show()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mListData[position].type == 2) {
            R.layout.item_chat_photo
        } else {
            R.layout.item_chat_video
        }
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    private var onItemClickListener: OnChatItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnChatItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

}