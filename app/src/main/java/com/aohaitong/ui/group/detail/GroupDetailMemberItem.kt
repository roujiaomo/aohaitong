package com.aohaitong.ui.group.detail

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.FriendBean
import com.aohaitong.databinding.ItemGroupMemberBinding
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.utils.StringUtil
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class GroupDetailMemberItem(
    private val viewModel: GroupDetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val friendBean: FriendBean,
    private val context: Context
) : BindableItem<ItemGroupMemberBinding>() {

    override fun bind(viewBinding: ItemGroupMemberBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.userName = StringUtil.getFirstNotNullString(
            arrayOf(
                friendBean.nickName,
                friendBean.name,
                friendBean.telephone,
            )
        )
        viewBinding.consUser.onClickWithAvoidRapidAction {
            viewModel.navigationUserDetail(friendBean)
        }
    }

    override fun getLayout(): Int = R.layout.item_group_member

    override fun initializeViewBinding(view: View): ItemGroupMemberBinding =
        ItemGroupMemberBinding.bind(view)

    override fun isSameAs(other: Item<*>): Boolean {
        return other is GroupDetailMemberItem
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is GroupDetailMemberItem
                && friendBean.name == other.friendBean.name
                && friendBean.nickName == other.friendBean.nickName
    }
}
