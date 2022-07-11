package com.aohaitong.ui.group.remove_group_member

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.FriendBean
import com.aohaitong.databinding.ItemGroupSelectFriendBodyBinding
import com.aohaitong.kt.util.onClick
import com.aohaitong.utils.StringUtil
import com.xwray.groupie.viewbinding.BindableItem

class RemoveGroupMemberItem(
    private val viewModel: RemoveGroupMemberViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val groupFriendBean: FriendBean?,
) : BindableItem<ItemGroupSelectFriendBodyBinding>() {

    override fun bind(viewBinding: ItemGroupSelectFriendBodyBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        groupFriendBean?.let {
            viewBinding.tvName.text = StringUtil.getFirstNotNullString(
                arrayOf<String>(
                    it.nickName,
                    it.name,
                    it.telephone
                )
            )
        }
        var isCheck = false
        //处理勾选框
        viewBinding.root.onClick {
            //从未选中到选中
            if (!isCheck) {
                groupFriendBean?.let {
                    viewModel.addCheckFriend(it.telephone)
                    viewBinding.checkBoxGroup.isChecked = true
                }
            } else {
                groupFriendBean?.let {
                    viewModel.removeCheckFriend(it.telephone)
                    viewBinding.checkBoxGroup.isChecked = false
                }
            }
            isCheck = !isCheck
        }

        viewBinding.checkBoxGroup.onClick {
            //从未选中到选中
            if (!isCheck) {
                groupFriendBean?.let {
                    viewModel.addCheckFriend(it.telephone)
                    viewBinding.checkBoxGroup.isChecked = true
                }
            } else {
                groupFriendBean?.let {
                    viewModel.removeCheckFriend(it.telephone)
                    viewBinding.checkBoxGroup.isChecked = false
                }
            }
            isCheck = !isCheck
        }
    }

    override fun getLayout(): Int = R.layout.item_group_select_friend_body

    override fun initializeViewBinding(view: View): ItemGroupSelectFriendBodyBinding =
        ItemGroupSelectFriendBodyBinding.bind(view)

}
