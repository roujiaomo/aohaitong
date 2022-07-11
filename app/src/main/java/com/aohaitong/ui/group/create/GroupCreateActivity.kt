package com.aohaitong.ui.group.create

import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.GroupEventEntity
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.constant.NumConstant
import com.aohaitong.constant.StatusConstant
import com.aohaitong.databinding.ActivityCreateGroupBinding
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.kt.util.onClick
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.ui.chat.chat.NewChatActivity
import com.aohaitong.utils.CommonUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@AndroidEntryPoint
class GroupCreateActivity : BaseActivity() {
    private lateinit var mBinding: ActivityCreateGroupBinding
    private val viewModel by viewModels<GroupCreateViewModel>()
    private var searchStr = ""
    private lateinit var friendSection: Section
    private val indexMap = HashMap<String, Int>()
    private var groupName = ""
    private var groupMember = ""
    private var groupOwner = ""

    override fun getLayout() = R.layout.activity_create_group

    override fun initView() {
        mBinding = DataBindingUtil.setContentView(this, layout)
        initIndexView()
        initRecyclerView()
    }

    override fun initData() {
        searchStr = mBinding.etGroupName.text.toString().trim()
        indexMap["#"] = 0
        viewModel.getFriendList(searchStr)
    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewModel.getFriendListResponse.collect {
                when (it) {
                    is SuccessResource -> {
                        friendSection.update(
                            it.data.mapIndexed { index, groupFriendBean ->
                                if (groupFriendBean.itemType == StatusConstant.ITEM_TYPE_HEADER) {
                                    groupFriendBean.name?.let {
                                        indexMap[groupFriendBean.name] = index + 1
                                    }
                                }

                                if (groupFriendBean.itemType == StatusConstant.ITEM_TYPE_CONTENT) {
                                    GroupCreateBodyItem(
                                        viewModel,
                                        this@GroupCreateActivity,
                                        groupFriendBean,
                                        this@GroupCreateActivity
                                    )
                                } else {
                                    GroupCreateHeaderItem(
                                        this@GroupCreateActivity,
                                        groupFriendBean
                                    )
                                }
                            })
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }

        viewModel.friendListLivedata.observe(this@GroupCreateActivity, {
            //修改底部按钮状态
            if (it.size > 0) {
                handleCreateGroupButton(true)
            } else {
                handleCreateGroupButton(false)
            }

        })

        mBinding.etGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length >= 16) {
                    toast(getString(R.string.group_toast_group_name_max_length))
                }
            }
        })

        mBinding.imgBack.onClick {
            finish()
        }
        //---------------------------点击事件----------------------------------------
        //创建群组
        mBinding.tvCreateGroup.onClickWithAvoidRapidAction {
            if (CommonUtil.containsEmoji(mBinding.etGroupName.text.toString())) {
                toast(getString(R.string.group_toast_group_name_not_match))
                return@onClickWithAvoidRapidAction
            }

            //创建群聊
            groupName = mBinding.etGroupName.text.toString()
            if (isGroupNameEmpty()) {
                groupName = context.getString(R.string.group_create_default_name)
//                    String.format(
//                    context.getString(R.string.group_create_default_name),
//                    viewModel.tempFriendList.size + 1
//                )
            }
            val stringBuilder = StringBuilder()
            viewModel.tempFriendList.map {
                stringBuilder.append(it)
                stringBuilder.append("/")
            }
            groupMember = stringBuilder.toString()
            groupOwner = MyApplication.TEL.toString()
            BusinessController.sendCreateGroup(
                groupName, groupMember, NumConstant.getJHDNum(),
                object : ISendListener {
                    override fun sendSuccess() {
                    }

                    override fun sendFail(reason: String?) {
                        runOnUiThread {
                            toast(reason)
                        }

                    }

                }
            )


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

    private fun initIndexView() {
        mBinding.vLetterIndex.addLetter(0, "#")
        mBinding.vLetterIndex.setOnStateChangeListener { _: Int, position: Int, letter: String?, itemCenterY: Int ->
            indexMap[letter]?.let {
                mBinding.rvFriend.scrollToPosition(it)
                val mLayoutManager =
                    mBinding.rvFriend.layoutManager as LinearLayoutManager
                mLayoutManager.scrollToPositionWithOffset(it, 0)
            }
        }
    }

    private fun isGroupNameEmpty(): Boolean =
        mBinding.etGroupName.text.toString().trim().isEmpty()

    private fun handleCreateGroupButton(isEnable: Boolean) {
        if (isEnable) {
            mBinding.tvCreateGroup.background =
                AppCompatResources.getDrawable(this, R.drawable.shape_tv_bg_selected)
            mBinding.tvCreateGroup.setTextColor(ContextCompat.getColor(this, R.color.white))
            mBinding.tvCreateGroup.isEnabled = true

        } else {
            mBinding.tvCreateGroup.background =
                AppCompatResources.getDrawable(this, R.drawable.shape_tv_bg_unselected)
            mBinding.tvCreateGroup.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gray_edit_hint
                )
            )
            mBinding.tvCreateGroup.isEnabled = false
        }
    }

    override fun isDataBinding() = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(groupEvent: GroupEventEntity) {
        //后端返回群组Id,创建群聊
        if (groupEvent.groupEventId == 14) {
            NewChatActivity.startChatActivity(
                this,
                isGroup = true,
                groupId = groupEvent.groupId,
                groupName = groupName,
            )
            finish()
        }
    }

}