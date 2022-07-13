package com.aohaitong.ui.main.message

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.DeleteChatParams
import com.aohaitong.bean.entity.MessageBusinessBean
import com.aohaitong.databinding.ItemMessageBinding
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class MessageItem(
    private val viewModel: MessageViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val messageBusinessBean: MessageBusinessBean
) : BindableItem<ItemMessageBinding>() {

    override fun bind(viewBinding: ItemMessageBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.entity = messageBusinessBean
        viewBinding.viewModel = viewModel
        viewBinding.root.onClickWithAvoidRapidAction {
            viewModel.navigateToChatActivity(messageBusinessBean)
        }
        viewBinding.root.setOnLongClickListener {
            val deleteParam =
                if (messageBusinessBean.isGroup) messageBusinessBean.groupId else messageBusinessBean.telephone
            viewModel.showDeleteMessage(DeleteChatParams(messageBusinessBean.isGroup, deleteParam))
            true
        }
    }

    override fun getLayout(): Int = R.layout.item_message

    override fun isSameAs(other: Item<*>): Boolean {
        return other is MessageItem
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is MessageItem
                && other.messageBusinessBean.name == messageBusinessBean.name
                && other.messageBusinessBean.realTime == messageBusinessBean.realTime
                && other.messageBusinessBean.unReadCount == messageBusinessBean.unReadCount
                && other.messageBusinessBean.showName == messageBusinessBean.showName

    }

    override fun initializeViewBinding(view: View): ItemMessageBinding =
        ItemMessageBinding.bind(view)

}
