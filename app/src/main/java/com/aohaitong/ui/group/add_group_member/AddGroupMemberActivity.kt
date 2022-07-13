package com.aohaitong.ui.group.add_group_member

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.constant.NumConstant
import com.aohaitong.databinding.ActivityAddGroupMemberBinding
import com.aohaitong.db.DBManager
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.aohaitong.ui.group.detail.GroupDetailActivity.Companion.GROUP_MEMBER
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * 传入已有群组成员控制最大数量
 */
@AndroidEntryPoint
class AddGroupMemberActivity : BaseActivity() {
    private lateinit var mBinding: ActivityAddGroupMemberBinding
    private val viewModel by viewModels<AddGroupMemberViewModel>()
    private lateinit var friendSection: Section
    private var groupId = ""
    private var groupExistMembers = ""
    private var groupOwner = ""
    private var isGroupManager = false

    companion object {
        const val GROUP_ID = "group_id"
        const val GROUP_EXIST_MEMBER = "group_exist_member"
        const val IS_GROUP_MANAGER = "is_group_manager"
        const val MAX_GROUP_MEMBER = 5
        const val REQUEST_CODE_INVITE_GROUP_MEMBER = 998
        const val RESUlT_CODE_INVITE_GROUP_MEMBER = 999

        fun startAddGroupMemberActivity(
            context: Context,
            groupId: String = "",
            isGroupManager: Boolean = false,
            groupExistMembers: String = "",
        ) {
            val intent = Intent(context, AddGroupMemberActivity::class.java)
            intent.putExtra(GROUP_ID, groupId)
            intent.putExtra(GROUP_EXIST_MEMBER, groupExistMembers)
            intent.putExtra(IS_GROUP_MANAGER, isGroupManager)

            (context as Activity).startActivityForResult(intent, REQUEST_CODE_INVITE_GROUP_MEMBER)
        }
    }

    override fun getLayout() = R.layout.activity_add_group_member

    override fun initView() {
        mBinding = DataBindingUtil.setContentView(this, layout)
        initRecyclerView()
    }

    override fun initData() {
        //处理已有群员,不显示已在群组里的人
        groupId = intent.getStringExtra(GROUP_ID)
        groupExistMembers = intent.getStringExtra(GROUP_EXIST_MEMBER)
        isGroupManager = intent.getBooleanExtra(IS_GROUP_MANAGER, false)
        viewModel.getFriendList("")
    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewModel.getFriendListResponse.collect {
                when (it) {
                    is SuccessResource -> {
                        val unExistGroupMember = it.data.filterNot { groupFriendBean ->
                            groupExistMembers.contains(groupFriendBean.tel.toString())
                        }
                        friendSection.update(
                            unExistGroupMember.map { groupFriendBean ->
                                //当前存在的群组成员数量
                                val groupMemberNum =
                                    groupExistMembers.split("/").filter { groupExistMember ->
                                        groupExistMember.isNotEmpty()
                                    }.size
                                AddGroupMemberBodyItem(
                                    viewModel,
                                    this@AddGroupMemberActivity,
                                    groupFriendBean,
                                    if (isGroupManager) MAX_GROUP_MEMBER - groupMemberNum - 1 else MAX_GROUP_MEMBER - groupMemberNum,
                                    this@AddGroupMemberActivity
                                )
                            })
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }

        viewModel.friendListLivedata.observe(this, {
            //修改底部按钮状态
            if (it.size > 0) {
                handleCreateGroupButton(true)
            } else {
                handleCreateGroupButton(false)
            }

        })

        mBinding.tvInviteFriend.onClickWithAvoidRapidAction {
            val stringBuilder = StringBuilder()
            viewModel.tempFriendList.map {
                stringBuilder.append(it)
                stringBuilder.append("/")
            }
            val groupMember = stringBuilder.toString() //有可能多个/
            showLoading()
            BusinessController.sendInviteJoinGroup(
                groupId.toLong(), groupMember, NumConstant.getJHDNum(),
                object : ISendListener {
                    override fun sendSuccess() {
                        loadingDialog.dismiss()
                        //更新本地群聊信息
                        DBManager.getInstance(this@AddGroupMemberActivity).updateGroupMember(
                            groupId, groupMember
                        )
                        //新成员发回到群聊详情
                        val resultIntent = Intent()
                        resultIntent.putExtra(GROUP_MEMBER, groupMember)
                        setResult(RESUlT_CODE_INVITE_GROUP_MEMBER, resultIntent)
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
            mBinding.tvInviteFriend.background =
                AppCompatResources.getDrawable(this, R.drawable.shape_tv_bg_selected)
            mBinding.tvInviteFriend.setTextColor(ContextCompat.getColor(this, R.color.white))
            mBinding.tvInviteFriend.isEnabled = true

        } else {
            mBinding.tvInviteFriend.background =
                AppCompatResources.getDrawable(this, R.drawable.shape_tv_bg_unselected)
            mBinding.tvInviteFriend.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gray_edit_hint
                )
            )
            mBinding.tvInviteFriend.isEnabled = false
        }
    }

    override fun isDataBinding() = true
}