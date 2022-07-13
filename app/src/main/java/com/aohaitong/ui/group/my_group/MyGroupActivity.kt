package com.aohaitong.ui.group.my_group

import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.GroupEventEntity
import com.aohaitong.databinding.ActivityMyGroupBinding
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.aohaitong.ui.chat.chat.NewChatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class MyGroupActivity : BaseActivity() {
    private var binding: ActivityMyGroupBinding by autoCleared()
    private var groupieAdapter: GroupAdapter<GroupieViewHolder> by autoCleared()
    private lateinit var section: Section
    private val viewModel by viewModels<MyGroupViewModel>()

    override fun getLayout() = R.layout.activity_my_group

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        initRecyclerView()
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyGroup()
    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewModel.getGroupListResponse.collect {
                when (it) {
                    is SuccessResource -> {
                        section.update(
                            it.data.map { groupBean ->
                                MyGroupItem(viewModel, this@MyGroupActivity, groupBean)
                            })
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }

            }
        }

        binding.ivBack.onClickWithAvoidRapidAction {
            finish()
        }

        lifecycleScope.launch {
            viewModel.onToChatActivityAction.collect {
                val showGroupMember = it.groupManagerTel + "/" + it.groupMemberTel
                NewChatActivity.startChatActivity(
                    this@MyGroupActivity,
                    isGroup = true,
                    groupId = it.groupId,
                    groupName = it.groupName,
                )
            }
        }
    }

    private fun initRecyclerView() {
        groupieAdapter = GroupAdapter<GroupieViewHolder>()
        val rvLayoutManager = LinearLayoutManager(context)
        binding.rvMyGroup.apply {
            layoutManager = rvLayoutManager
            adapter = groupieAdapter
            itemAnimator = null
        }
        section = Section()
        groupieAdapter.add(section)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(groupEvent: GroupEventEntity) {
        when (groupEvent.groupEventId) {
            16, 19, 20, 22, 23, 31 -> { //刷新群聊列表
                viewModel.getMyGroup()
            }
        }
    }

    override fun isDataBinding() = true
}