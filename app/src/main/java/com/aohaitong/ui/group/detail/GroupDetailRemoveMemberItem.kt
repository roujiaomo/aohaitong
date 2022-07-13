package com.aohaitong.ui.group.detail

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.databinding.ItemGroupRemoveMemberBinding
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.xwray.groupie.viewbinding.BindableItem

class GroupDetailRemoveMemberItem(
    private val viewModel: GroupDetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : BindableItem<ItemGroupRemoveMemberBinding>() {

    override fun bind(viewBinding: ItemGroupRemoveMemberBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.ivRemoveGroupMember.onClickWithAvoidRapidAction {
            viewModel.removeGroupMember()
        }
    }

    override fun getLayout(): Int = R.layout.item_group_remove_member

    override fun initializeViewBinding(view: View): ItemGroupRemoveMemberBinding =
        ItemGroupRemoveMemberBinding.bind(view)

}
