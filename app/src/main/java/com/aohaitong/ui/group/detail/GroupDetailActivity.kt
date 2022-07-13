package com.aohaitong.ui.group.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.FriendBean
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.constant.NumConstant
import com.aohaitong.databinding.ActivityGroupDetailBinding
import com.aohaitong.db.DBManager
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.aohaitong.ui.friend.FriendDetailActivity
import com.aohaitong.ui.group.add_group_member.AddGroupMemberActivity
import com.aohaitong.ui.group.add_group_member.AddGroupMemberActivity.Companion.RESUlT_CODE_INVITE_GROUP_MEMBER
import com.aohaitong.ui.group.modify_group_name.ModifyGroupNameActivity
import com.aohaitong.ui.group.remove_group_member.RemoveGroupMemberActivity
import com.aohaitong.ui.group.remove_group_member.RemoveGroupMemberActivity.Companion.RESUlT_CODE_REMOVE_GROUP_MEMBER
import com.aohaitong.utils.SPUtil
import com.aohaitong.utils.ThreadPoolManager
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupDetailActivity : BaseActivity() {
    private var binding: ActivityGroupDetailBinding by autoCleared()
    private var groupieAdapter: GroupAdapter<GroupieViewHolder> by autoCleared()
    private lateinit var section: Section
    private var groupieList: MutableList<Group> = mutableListOf()
    private val viewModel by viewModels<GroupDetailViewModel>()
    private var groupId = ""
    private var groupName = ""
    private var groupManager = ""
    private var groupMember = ""
    private lateinit var groupMemberList: MutableList<FriendBean> // 顶部列表数据

    companion object {
        const val GROUP_ID = "group_id"
        const val GROUP_MEMBER = "group_member"
        const val GROUP_NAME = "group_name"
        const val REQUEST_CODE_GROUP_DETAIL = 995
        const val RESUlT_CODE_DISSOLVE_GROUP = 994
        const val RESUlT_CODE_EXIT_GROUP = 993
        const val RESUlT_CODE_MODIFY_GROUP_NAME = 992

        fun startGroupDetailActivity(
            context: Context,
            groupID: String,
        ) {
            val intent = Intent(context, GroupDetailActivity::class.java)
            intent.putExtra(GROUP_ID, groupID)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_GROUP_DETAIL)
        }
    }

    override fun getLayout() = R.layout.activity_group_detail

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        initHeaderRecyclerView()
    }

    private fun initHeaderRecyclerView() {
        val rvLayoutManager = GridLayoutManager(this, 5)
        groupieAdapter = GroupAdapter<GroupieViewHolder>()
        binding.rvGroupMember.apply {
            layoutManager = rvLayoutManager
            adapter = groupieAdapter
            itemAnimator = null
        }
        section = Section()
        groupieAdapter.add(section)
    }

    override fun initData() {
        groupId = intent.getStringExtra(GROUP_ID)
        viewModel.getGroupInfo(groupId)
    }

    private fun loadRvData() {
        groupieList.addAll(groupMemberList.map {
            GroupDetailMemberItem(
                viewModel,
                this,
                it,
                this
            )
        })
        if (groupMemberList.size < 5) {
            groupieList.add(
                GroupDetailAddMemberItem(
                    viewModel,
                    this,
                )
            )
        }
        if (groupMemberList.size > 1 && isGroupManager()) {
            groupieList.add(
                GroupDetailRemoveMemberItem(
                    viewModel,
                    this,
                )
            )
        }
        section.update(groupieList)
        val stringBuilder = StringBuilder()
        groupMemberList.filter {
            it.telephone != groupManager
        }.map {
            stringBuilder.append(it.telephone + "/")
        }
        groupMember = stringBuilder.toString()
    }

    private val refreshRunnable = Runnable {
        groupMemberList.mapIndexed { index, it ->
            if (it.telephone == MyApplication.TEL.toString()) {
                val name = SPUtil.instance.getString(MyApplication.TEL.toString() + "")
                name?.let { myName ->
                    groupMemberList[index] = FriendBean(
                        MyApplication.TEL.toString(),
                        myName,
                        myName,
                        MyApplication.TEL.toString()
                    )
                }

            }

            val friendBean = DBManager.getInstance(this).selectFriend(it.telephone)
            if (friendBean != null) {
                groupMemberList[index] = friendBean
            }
        }
        runOnUiThread {
            groupieList.clear()
            loadRvData()
        }
    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewModel.getGroupInfoResponse.collect {
                when (it) {
                    is SuccessResource -> {
                        groupName = it.data.groupName
                        groupManager = it.data.groupManagerTel
                        groupMember = groupManager + "/" + it.data.groupMemberTel
                        //设置解散/退出群聊
                        binding.btnExitGroup.text =
                            if (isGroupManager()) getString(R.string.group_detail_dissolve) else getString(
                                R.string.group_detail_exit
                            )
                        binding.tvGroupName.text = groupName
                        groupMemberList = groupMember.split("/").filter { groupMemberValue ->
                            groupMemberValue.isNotEmpty()
                        }.map { telephone ->
                            FriendBean(
                                "", "", "", telephone
                            )
                        } as ArrayList
                        loadRvData()
                        //获取昵称,备注 重新显示列表
                        ThreadPoolManager.getInstance().execute(refreshRunnable)
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }


        //添加群成员
        lifecycleScope.launch {
            viewModel.addGroupMemberListAction.collect {
                AddGroupMemberActivity.startAddGroupMemberActivity(
                    this@GroupDetailActivity,
                    groupId, isGroupManager(),
                    if (isGroupManager()) groupMember else "$groupManager/$groupMember"
                )
            }
        }
        //移除群成员
        lifecycleScope.launch {
            viewModel.removeGroupMemberListAction.collect {
                //传入除了群主以外的其他群成员(filter为过滤出符合条件的)
                val removeGroupMembers = groupMemberList.filter {
                    it.telephone != groupManager
                } as ArrayList
                RemoveGroupMemberActivity.startRemoveGroupMemberActivity(
                    this@GroupDetailActivity,
                    groupId,
                    removeGroupMembers
                )
            }
        }

        //跳转到群里详情
        lifecycleScope.launch {
            viewModel.navigationUserDetailAction.collect {
                val friendBean = DBManager.getInstance(context).selectFriend(it.telephone)
                FriendDetailActivity.startFriendDetailActivity(
                    context,
                    it.name,
                    it.telephone,
                    it.nickName,
                    friendBean != null
                )
            }
        }
        //解散/退出群聊
        binding.btnExitGroup.onClickWithAvoidRapidAction {
            showLoading()
            //群主解散群聊
            if (isGroupManager()) {
                BusinessController.sendDissolveGroup(
                    groupId.toLong(), NumConstant.getJHDNum(),
                    object : ISendListener {
                        override fun sendSuccess() {
                            hideLoading()
                            //删除对应群聊消息
                            DBManager.getInstance(context).delMsgByTel(groupId, true)
                            setResult(RESUlT_CODE_DISSOLVE_GROUP)
                            finish()
                        }

                        override fun sendFail(reason: String?) {
                            runOnUiThread {
                                hideLoading()
                                toast(reason)
                            }

                        }

                    }
                )
            }
            //群员退出群聊
            else {
                BusinessController.sendExitGroup(
                    groupId.toLong(), NumConstant.getJHDNum(),
                    object : ISendListener {
                        override fun sendSuccess() {
                            hideLoading()
                            //删除对应群聊消息
                            DBManager.getInstance(context).delMsgByTel(groupId, true)
                            setResult(RESUlT_CODE_EXIT_GROUP)
                            finish()
                        }

                        override fun sendFail(reason: String?) {
                            runOnUiThread {
                                hideLoading()
                                toast(reason)
                            }

                        }

                    }
                )
            }
        }

        binding.consGroupName.onClickWithAvoidRapidAction {
            ModifyGroupNameActivity.startModifyGroupNameActivity(
                this, groupId, groupName
            )
        }

        binding.imgBack.onClickWithAvoidRapidAction {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            //添加群组成员
            RESUlT_CODE_INVITE_GROUP_MEMBER -> {
                //选中的群成员数据
                val newMemberList = data?.let { intent ->
                    intent.getStringExtra(GROUP_MEMBER)?.split("/")?.filter {
                        it.isNotEmpty()
                    }
                }
                //更新现有的群成员数据
                newMemberList?.map {
                    groupMemberList.add(
                        FriendBean(
                            "", "", "", it
                        )
                    )
                }
                ThreadPoolManager.getInstance().execute(refreshRunnable)
            }
            //移除群组成员
            RESUlT_CODE_REMOVE_GROUP_MEMBER -> {
                //选中的群成员数据
                val newMemberList = data?.let { intent ->
                    intent.getStringExtra(GROUP_MEMBER)?.split("/")?.filter {
                        it.isNotEmpty()
                    }
                }
                newMemberList?.map { removeMemberTel ->
                    for (index in groupMemberList.size - 1 downTo 0) {
                        if (groupMemberList[index].telephone == removeMemberTel) {
                            groupMemberList.removeAt(index)
                        }
                    }
                }
                ThreadPoolManager.getInstance().execute(refreshRunnable)
            }
            //修改群聊名称
            RESUlT_CODE_MODIFY_GROUP_NAME -> {
                val newGroupName = data?.getStringExtra(GROUP_NAME)
                newGroupName?.let {
                    binding.tvGroupName.text = it
                    groupName = it
                }
            }
            else -> {
            }
        }


    }

    private fun isGroupManager(): Boolean {
        return MyApplication.TEL.toString() == groupManager
    }

}