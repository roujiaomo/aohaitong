package com.aohaitong.ui.chat.chat

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.DeleteChatMsgParams
import com.aohaitong.bean.entity.VoicePlayParams
import com.aohaitong.databinding.ItemVoiceSendBinding
import com.aohaitong.kt.util.onClick
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.widget.ChatMsgMenuPopup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem


class ChatSendVoiceItem(
    private val viewModel: ChatViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val chatMsgBusinessBean: ChatMsgBusinessBean,
    private var context: Context,
) :
    BindableItem<ItemVoiceSendBinding>() {
    override fun bind(viewBinding: ItemVoiceSendBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.entity = chatMsgBusinessBean
        viewBinding.tvPlay.text = String.format(
            context.getString(R.string.chat_record_time_unit),
            chatMsgBusinessBean.recordTime
        )
        //处理播放语音动画
        if (chatMsgBusinessBean.isRecording) {
            Glide.with(context).asGif().load(R.drawable.ic_send_playing).diskCacheStrategy(
                DiskCacheStrategy.RESOURCE
            ).centerCrop()
                .into(viewBinding.ivPlay)
        } else {
            viewBinding.ivPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_record_send
                )
            )
        }
        //if 群聊
        handleUserHeader(viewBinding, chatMsgBusinessBean.isGroup)
        viewBinding.llPlayRecord.onClick {
            chatMsgBusinessBean.filePath?.let {
                viewModel.onVoicePlayClick(
                    VoicePlayParams(
                        position = position,
                        filePath = it,
                        isRecording = chatMsgBusinessBean.isRecording
                    )
                )
            }
        }
        viewBinding.llPlayRecord.setOnLongClickListener {
            showChatMenu(viewBinding.llPlayRecord)
            false
        }
        viewBinding.ivFailed.onClickWithAvoidRapidAction {
            viewModel.onReSendClick(chatMsgBusinessBean)
        }
    }

    /**
     * 设置头像位置(群聊显示用户手机号、名称/私聊不显示)
     */
    private fun handleUserHeader(viewBinding: ItemVoiceSendBinding, isGroup: Boolean) {
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


    override fun getLayout() = R.layout.item_voice_send


    override fun initializeViewBinding(view: View): ItemVoiceSendBinding =
        ItemVoiceSendBinding.bind(view)


    override fun isSameAs(other: Item<*>): Boolean {
        return other is ChatSendVoiceItem
    }

    /**
     * isSameAs和hasSameContentAs第一次是不会走的
     */
    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is ChatSendVoiceItem && other.chatMsgBusinessBean.id == chatMsgBusinessBean.id
                && other.chatMsgBusinessBean.isLoading == chatMsgBusinessBean.isLoading
                && other.chatMsgBusinessBean.filePath == chatMsgBusinessBean.filePath
                && other.chatMsgBusinessBean.isRecording == chatMsgBusinessBean.isRecording
                && other.chatMsgBusinessBean.status == chatMsgBusinessBean.status

    }
}