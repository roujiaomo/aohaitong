package com.aohaitong.ui.group.detail

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.databinding.ItemGroupAddMemberBinding
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.xwray.groupie.viewbinding.BindableItem

class GroupDetailAddMemberItem(
    private val viewModel: GroupDetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : BindableItem<ItemGroupAddMemberBinding>() {

    override fun bind(viewBinding: ItemGroupAddMemberBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.ivAddGroupMember.onClickWithAvoidRapidAction {
            viewModel.addGroupMember()
        }
    }

    override fun getLayout(): Int = R.layout.item_group_add_member

    override fun initializeViewBinding(view: View): ItemGroupAddMemberBinding =
        ItemGroupAddMemberBinding.bind(view)

}
