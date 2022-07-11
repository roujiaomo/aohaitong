package com.aohaitong.ui.group.add_group_member

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.aohaitong.R
import com.aohaitong.bean.entity.GroupFriendBean
import com.aohaitong.databinding.ItemGroupSelectFriendBodyBinding
import com.aohaitong.kt.util.onClick
import com.xwray.groupie.viewbinding.BindableItem

class AddGroupMemberBodyItem(
    private val viewModel: AddGroupMemberViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val groupFriendBean: GroupFriendBean,
    private val maxEnableSelectNum: Int,//最大选择数量 = 群组总数(5) - 当前群组人员
    private val context: Context
) : BindableItem<ItemGroupSelectFriendBodyBinding>() {

    override fun bind(viewBinding: ItemGroupSelectFriendBodyBinding, position: Int) {
        viewBinding.lifecycleOwner = lifecycleOwner
        groupFriendBean.showName?.let {
            viewBinding.tvName.text = it
        }
        var isCheck = false
        //处理勾选框
        viewBinding.root.onClick {
            //从未选中到选中
            if (!isCheck) {
                groupFriendBean.tel?.let {
                    if (viewModel.tempFriendList.size >= maxEnableSelectNum) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.group_toast_group_member_over),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@onClick
                    }
                    viewModel.addCheckFriend(it)
                    viewBinding.checkBoxGroup.isChecked = true
                }
            } else {
                groupFriendBean.tel?.let {
                    viewModel.removeCheckFriend(it)
                    viewBinding.checkBoxGroup.isChecked = false
                }
            }
            isCheck = !isCheck
        }
        viewBinding.checkBoxGroup.onClick {
            //从未选中到选中
            if (!isCheck) {
                groupFriendBean.tel?.let {
                    if (viewModel.tempFriendList.size >= maxEnableSelectNum) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.group_toast_group_member_over),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewBinding.checkBoxGroup.isChecked = false
                        return@onClick
                    }
                    viewModel.addCheckFriend(it)
                    viewBinding.checkBoxGroup.isChecked = true
                }
            } else {
                groupFriendBean.tel?.let {
                    viewModel.removeCheckFriend(it)
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
