package com.aohaitong.ui.group.my_group

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.GroupBean
import com.aohaitong.databinding.ItemMyGroupBinding
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class MyGroupItem(
    private val viewModel: MyGroupViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val groupBean: GroupBean
) : BindableItem<ItemMyGroupBinding>() {

    override fun bind(viewBinding: ItemMyGroupBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        viewBinding.entity = groupBean
        viewBinding.consMyGroup.onClickWithAvoidRapidAction {
            //跳转到聊天界面
            viewModel.onItemClick(groupBean)
        }
    }

    override fun getLayout(): Int = R.layout.item_my_group

    override fun initializeViewBinding(view: View): ItemMyGroupBinding =
        ItemMyGroupBinding.bind(view)

    override fun isSameAs(other: Item<*>): Boolean {
        return other is MyGroupItem
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        return other is MyGroupItem && other.groupBean.groupId == groupBean.groupId
                && other.groupBean.groupName == groupBean.groupName
                && other.groupBean.groupMemberTel == groupBean.groupMemberTel

    }
}
