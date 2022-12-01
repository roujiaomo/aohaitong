package com.aohaitong.ui.seachart

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.databinding.ItemFriendListBinding
import com.xwray.groupie.viewbinding.BindableItem

class ShipMemberItem(
    private val lifecycleOwner: LifecycleOwner,
    private val userTel: String
) : BindableItem<ItemFriendListBinding>() {

    override fun bind(viewBinding: ItemFriendListBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.tvName.text = userTel
//        viewBinding.consMyGroup.onClickWithAvoidRapidAction {
//            //跳转到聊天界面
//            viewModel.onItemClick(groupBean)
//        }
    }

    override fun getLayout(): Int = R.layout.item_friend_list

    override fun initializeViewBinding(view: View): ItemFriendListBinding =
        ItemFriendListBinding.bind(view)

//    override fun isSameAs(other: Item<*>): Boolean {
//        return other is ShipMemberItem
//    }
//
//    override fun hasSameContentAs(other: Item<*>): Boolean {
//        return other is ShipMemberItem && other.groupBean.groupId == groupBean.groupId
//                && other.groupBean.groupName == groupBean.groupName
//                && other.groupBean.groupMemberTel == groupBean.groupMemberTel
//
//    }
}
