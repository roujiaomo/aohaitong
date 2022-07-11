package com.aohaitong.ui.chat.chat

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.DeleteChatMsgParams
import com.aohaitong.databinding.ItemTextSendBinding
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.widget.ChatMsgMenuPopup
import com.blankj.utilcode.util.ScreenUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem


class ChatSendTextItem(
    private val viewModel: ChatViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val chatMsgBusinessBean: ChatMsgBusinessBean,
    private var context: Context,
) :
    BindableItem<ItemTextSendBinding>() {
    override fun bind(viewBinding: ItemTextSendBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.entity = chatMsgBusinessBean
        //设置聊天内容显示最大宽度
        val screenWidth: Float =
            ScreenUtils.getScreenWidth() / ScreenUtils.getScreenDensity() * 0.7.toFloat()
        viewBinding.tvText.maxWidth = XPopupUtils.dp2px(context, screenWidth)
        //if 群聊
        handleUserHeader(viewBinding, chatMsgBusinessBean.isGroup)
        viewBinding.tvText.setOnLongClickListener {
            showChatMenu(viewBinding.tvText)
            false
        }
        viewBinding.ivFailed.onClickWithAvoidRapidAction {
            viewModel.onReSendClick(chatMsgBusinessBean)
        }

    }

    /**
     * 设置头像位置(群聊显示用户手机号、名称/私聊不显示)
     */
    private fun handleUserHeader(viewBinding: ItemTextSendBinding, isGroup: Boolean) {
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
                1 -> {//复制消息
                    chatMsgBusinessBean.msg?.let {
                        viewModel.onCopyItemClick(it)
                    }
                    popup.dismiss()
                }
                2 -> {//转发消息
                    chatMsgBusinessBean.msg?.let {
                        viewModel.onTransferItemClick(it)
                    }
                    popup.dismiss()
                }
            }
        }
        popup.show()
    }


    override fun getLayout() = R.layout.item_text_send


    override fun initializeViewBinding(view: View): ItemTextSendBinding =
        ItemTextSendBinding.bind(view)

    /**
     * 当 isSameAs返回true，hasSameContentAs返回false时触发
     */
//    override fun getChangePayload(newItem: Item<*>): Any {
//        return chatMsgBusinessBean.isLoading
//    }

    override fun isSameAs(other: Item<*>): Boolean {
        return other is ChatSendTextItem
    }

    /**
     * isSameAs和hasSameContentAs第一次是不会走的
     */
    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is ChatSendTextItem && other.chatMsgBusinessBean.id == chatMsgBusinessBean.id
                && other.chatMsgBusinessBean.msg == chatMsgBusinessBean.msg
                && other.chatMsgBusinessBean.status == chatMsgBusinessBean.status
                && other.chatMsgBusinessBean.isLoading == chatMsgBusinessBean.isLoading

    }
}