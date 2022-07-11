package com.aohaitong.ui.chat.chat

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.DeleteChatMsgParams
import com.aohaitong.constant.StatusConstant
import com.aohaitong.databinding.ItemImageSendBinding
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.utils.BitmapUtil
import com.aohaitong.utils.audio.VideoFrameTool
import com.aohaitong.widget.ChatMsgMenuPopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem


class ChatSendImageOrVideoItem(
    private val viewModel: ChatViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val chatMsgBusinessBean: ChatMsgBusinessBean,
    private var context: Context,
) :
    BindableItem<ItemImageSendBinding>() {
    override fun bind(viewBinding: ItemImageSendBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.entity = chatMsgBusinessBean
        //if 群聊
        handleUserHeader(viewBinding, chatMsgBusinessBean.isGroup)
        viewBinding.ivFailed.onClickWithAvoidRapidAction {
            viewModel.onReSendClick(chatMsgBusinessBean)
        }
        //查看大图/视频
        viewBinding.rlImage.onClickWithAvoidRapidAction {
            viewModel.onViewLargeImageOrVideo(chatMsgBusinessBean)
        }
        viewBinding.rlImage.setOnLongClickListener {
            showChatMenu(viewBinding.rlImage)
            false
        }
        //判断图片/视频
        var bitmap: Bitmap? = null
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        when (chatMsgBusinessBean.messageType) {
            StatusConstant.TYPE_PHOTO_MESSAGE -> { //图片
                bitmap = BitmapUtil.getDiskBitmap(chatMsgBusinessBean.filePath)
            }
            StatusConstant.TYPE_VIDEO_MESSAGE -> { //视频
                bitmap =
                    VideoFrameTool.getInstance().getLocalVideoBitmap(chatMsgBusinessBean.filePath)
            }
        }
        bitmap?.let {
            if (it.width > it.height) {
                params.height = XPopupUtils.dp2px(context, 50f)
                val rio = it.width.toDouble() / it.height
                params.width = (params.height * rio).toInt()
            } else {
                params.width = XPopupUtils.dp2px(context, 100f)
                val rio = it.height.toDouble() / it.width
                params.height = (params.width * rio).toInt()
            }
        }
        viewBinding.ivPhoto.layoutParams = params
        viewBinding.ivPhoto.setImageBitmap(bitmap)
    }


    /**
     * 处理图片大小
     * 总体: 默认最大宽度300dp 最大高度200dp (宽:横向长度 高:竖向长度)
     * 算法:
     * 宽 >高: (1)宽 < 最大宽度, 则正常不处理正常显示 (2) 宽 > 最大宽度, 设置宽为最大宽度, 高等比例缩小
     * 高 >宽  (1)高 < 最大高度, 则正常不处理正常显示 (2) 高 > 最大高度, 设置高为最大高度, 宽等比例缩小
     */
//    private fun handlePhotoSize(bitmap: Bitmap): Bitmap {
//
////        if (bitmap.width > bitmap.height) {
////            params.height = QMUIDisplayHelper.dp2px(context, 50)
////            val rio = bitmap.width.toDouble() / bitmap.height
////            params.width = (params.height * rio).toInt()
////        } else {
////            params.width = QMUIDisplayHelper.dp2px(context, 100)
////            val rio = bitmap.height.toDouble() / bitmap.width
////            params.height = (params.width * rio).toInt()
////        }
//    }

    /**
     * 设置头像位置(群聊显示用户手机号、名称/私聊不显示)
     */
    private fun handleUserHeader(viewBinding: ItemImageSendBinding, isGroup: Boolean) {
        if (isGroup) {
            viewBinding.tvName.visibility = View.VISIBLE
            viewBinding.tvName.text = chatMsgBusinessBean.telephone
        } else {
            viewBinding.tvName.visibility = View.GONE
        }
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.marginEnd = XPopupUtils.dp2px(context, 15f)
        viewBinding.ivHeader.layoutParams = layoutParams
    }

    private fun showChatMenu(showAtView: View) {
        val popup = XPopup.Builder(context)
            .isDestroyOnDismiss(true)
            .atView(showAtView)
            .hasShadowBg(false) // 去掉半透明背景
            .offsetY(XPopupUtils.dp2px(context, 6f))
            .isCenterHorizontal(true)
            .asCustom(
                ChatMsgMenuPopup(context, chatMsgBusinessBean.messageType)
                    .setArrowWidth(XPopupUtils.dp2px(context, 5f))
                    .setArrowHeight(
                        XPopupUtils.dp2px(
                            context,
                            6f
                        )
                    )
                    .setArrowRadius(XPopupUtils.dp2px(context, 3f))

            ) as ChatMsgMenuPopup

        popup.setOnItemListener { position ->
            when (position) {
                0 -> {//删除消息
                    viewModel.onDeleteItemClick(
                        DeleteChatMsgParams(
                            chatMsgId = chatMsgBusinessBean.id,
                            telephone = chatMsgBusinessBean.telephone ?: "",
                            groupId = chatMsgBusinessBean.groupId
                        )
                    )
                    popup.dismiss()
                }
            }
        }
        popup.show()
    }


    override fun getLayout() = R.layout.item_image_send


    override fun initializeViewBinding(view: View): ItemImageSendBinding =
        ItemImageSendBinding.bind(view)


    override fun isSameAs(other: Item<*>): Boolean {
        return other is ChatSendImageOrVideoItem
    }

    /**
     * isSameAs和hasSameContentAs第一次是不会走的
     */
    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is ChatSendImageOrVideoItem && other.chatMsgBusinessBean.id == chatMsgBusinessBean.id
                && other.chatMsgBusinessBean.isLoading == chatMsgBusinessBean.isLoading
                && other.chatMsgBusinessBean.filePath == chatMsgBusinessBean.filePath
    }
}