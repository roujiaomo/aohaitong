package com.aohaitong.ui.group.add_group_member

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.GroupFriendBean
import com.aohaitong.databinding.ItemGroupSelectFriendHeaderBinding
import com.xwray.groupie.viewbinding.BindableItem

class AddGroupMemberHeaderItem(
    private val lifecycleOwner: LifecycleOwner,
    private val groupFriendBean: GroupFriendBean
) : BindableItem<ItemGroupSelectFriendHeaderBinding>() {

    override fun bind(viewBinding: ItemGroupSelectFriendHeaderBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.tvName.text = groupFriendBean.name
    }

    override fun getLayout(): Int = R.layout.item_group_select_friend_header

    override fun initializeViewBinding(view: View): ItemGroupSelectFriendHeaderBinding =
        ItemGroupSelectFriendHeaderBinding.bind(view)

}
