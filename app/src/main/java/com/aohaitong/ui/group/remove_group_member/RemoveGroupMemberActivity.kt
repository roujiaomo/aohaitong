package com.aohaitong.ui.group.remove_group_member

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.FriendBean
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.constant.NumConstant
import com.aohaitong.databinding.ActivityRemoveGroupMemberBinding
import com.aohaitong.db.DBManager
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.ui.group.add_group_member.AddGroupMemberActivity
import com.aohaitong.ui.group.detail.GroupDetailActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveGroupMemberActivity : BaseActivity() {

    private lateinit var mBinding: ActivityRemoveGroupMemberBinding
    private val viewModel by viewModels<RemoveGroupMemberViewModel>()
    private lateinit var friendSection: Section
    private var groupMembersList = mutableListOf<FriendBean?>()
    private var groupId: String = ""

    companion object {
        const val GROUP_ID = "group_id"
        const val GROUP_MEMBERS = "group_members"
        const val REQUEST_CODE_REMOVE_GROUP_MEMBER = 997
        const val RESUlT_CODE_REMOVE_GROUP_MEMBER = 996

        fun startRemoveGroupMemberActivity(
            context: Context,
            groupId: String = "",
            groupMembersList: ArrayList<FriendBean>,
        ) {
            val intent = Intent(context, RemoveGroupMemberActivity::class.java)
            intent.putExtra(GROUP_ID, groupId)
            intent.putParcelableArrayListExtra(GROUP_MEMBERS, groupMembersList)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_REMOVE_GROUP_MEMBER)
        }
    }

    override fun getLayout() = R.layout.activity_remove_group_member

    override fun initView() {
        mBinding = DataBindingUtil.setContentView(this, layout)
        initRecyclerView()
    }

    override fun initData() {
        groupId = intent.getStringExtra(AddGroupMemberActivity.GROUP_ID)
        groupMembersList = intent.getParcelableArrayListExtra<FriendBean>(GROUP_MEMBERS)
        friendSection.update(
            groupMembersList.map {
                RemoveGroupMemberItem(
                    viewModel,
                    this@RemoveGroupMemberActivity,
                    it,
                )
            })

    }

    override fun initEvent() {
        mBinding.tvRemoveFriend.onClickWithAvoidRapidAction {
            val stringBuilder = StringBuilder()
            viewModel.tempFriendList.map {
                stringBuilder.append(it)
                stringBuilder.append("/")
            }
            val groupMember = stringBuilder.toString() //有可能多个/
            showLoading()
            BusinessController.sendRemoveGroupMember(
                groupId.toLong(), groupMember, NumConstant.getJHDNum(),
                object : ISendListener {
                    override fun sendSuccess() {
                        loadingDialog.dismiss()
                        //更新本地群聊信息
                        DBManager.getInstance(this@RemoveGroupMemberActivity).removeGroupMember(
                            groupId, groupMember
                        )
                        //新成员发回到群聊详情
                        val resultIntent = Intent()
                        resultIntent.putExtra(GroupDetailActivity.GROUP_MEMBER, groupMember)
                        setResult(
                            RESUlT_CODE_REMOVE_GROUP_MEMBER,
                            resultIntent
                        )
                        finish()
                    }

                    override fun sendFail(reason: String?) {
                        loadingDialog.dismiss()
                        runOnUiThread {
                            toast(reason)
                        }

                    }

                }
            )

        }

        viewModel.friendListLivedata.observe(this, {
            //修改底部按钮状态
            if (it.size > 0) {
                handleCreateGroupButton(true)
            } else {
                handleCreateGroupButton(false)
            }
        })

        mBinding.imgBack.onClickWithAvoidRapidAction {
            finish()
        }
    }

    private fun initRecyclerView() {
        val groupieAdapter = GroupAdapter<GroupieViewHolder>()
        val linearLayoutManager = LinearLayoutManager(context)
        mBinding.rvFriend.apply {
            itemAnimator = null
            layoutManager = linearLayoutManager
            adapter = groupieAdapter
        }
        friendSection = Section()
        groupieAdapter.add(friendSection)
    }


    private fun handleCreateGroupButton(isEnable: Boolean) {
        if (isEnable) {
            mBinding.tvRemoveFriend.background =
                AppCompatResources.getDrawable(this, R.drawable.shape_tv_bg_selected)
            mBinding.tvRemoveFriend.setTextColor(ContextCompat.getColor(this, R.color.white))
            mBinding.tvRemoveFriend.isEnabled = true

        } else {
            mBinding.tvRemoveFriend.background =
                AppCompatResources.getDrawable(this, R.drawable.shape_tv_bg_unselected)
            mBinding.tvRemoveFriend.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gray_edit_hint
                )
            )
            mBinding.tvRemoveFriend.isEnabled = false
        }
    }

    override fun isDataBinding() = true
}