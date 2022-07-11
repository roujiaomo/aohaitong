package com.aohaitong.ui.chat.chat

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.databinding.ItemChatGroupAddMemberBinding
import com.xwray.groupie.viewbinding.BindableItem


class ChatGroupAddMemberItem(
    private val viewModel: ChatViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val chatMsgBusinessBean: ChatMsgBusinessBean,
    private var context: Context,
) :
    BindableItem<ItemChatGroupAddMemberBinding>() {
    override fun bind(viewBinding: ItemChatGroupAddMemberBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.entity = chatMsgBusinessBean
    }

    override fun getLayout() = R.layout.item_chat_group_add_member


    override fun initializeViewBinding(view: View): ItemChatGroupAddMemberBinding =
        ItemChatGroupAddMemberBinding.bind(view)
}