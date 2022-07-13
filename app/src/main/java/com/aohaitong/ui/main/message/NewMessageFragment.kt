package com.aohaitong.ui.main.message

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseFragment
import com.aohaitong.bean.MessageBean
import com.aohaitong.bean.MsgEntity
import com.aohaitong.constant.StatusConstant
import com.aohaitong.databinding.FragmentNewMessageBinding
import com.aohaitong.db.DBManager
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.interfaces.OnItemClickListener
import com.aohaitong.kt.common.onClick
import com.aohaitong.ui.chat.chat.NewChatActivity
import com.aohaitong.ui.group.create.GroupCreateActivity
import com.aohaitong.utils.TelUtil
import com.aohaitong.utils.dialog.MyQmuiDialog
import com.aohaitong.widget.MessageMenuPopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NewMessageFragment : BaseFragment() {

    private lateinit var mBinding: FragmentNewMessageBinding
    private lateinit var messageList: MutableList<MessageBean>
    private val viewModel by viewModels<MessageViewModel>()
    private var searchStr = ""
    private lateinit var messageSection: Section
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, setLayout(), container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMessageList(searchStr)
    }

    override fun setLayout() = R.layout.fragment_new_message

    override fun initView() {
        initRecyclerView()
        mBinding.tvTitle.text = getString(R.string.message_title)
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchStr = editable.toString().trim { it <= ' ' }
                viewModel.getMessageList(searchStr)
            }
        })

        mBinding.ivMenu.onClick {
            showChatMenu()
        }
    }

    private fun initRecyclerView() {
        val groupieAdapter = GroupAdapter<GroupieViewHolder>()
        val linearLayoutManager = LinearLayoutManager(context)
        mBinding.rvMessage.apply {
            itemAnimator = null
            layoutManager = linearLayoutManager
            adapter = groupieAdapter
        }
        messageSection = Section()
        groupieAdapter.add(messageSection)
    }

    override fun initData() {
        messageList = mutableListOf()
    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewModel.getMessageListResponse.collect {
                when (it) {
                    is SuccessResource -> {
                        messageSection.update(
                            it.data.map { messageBean ->
                                MessageItem(viewModel, viewLifecycleOwner, messageBean)
                            })
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }
        viewModel.isShowNewMessage.observe(viewLifecycleOwner, { isShowNewMessage ->
            if (isShowNewMessage) {
                mBinding.imgNoNet.visibility = View.VISIBLE
                mBinding.tvNoNet.visibility = View.VISIBLE
            } else {
                mBinding.imgNoNet.visibility = View.GONE
                mBinding.tvNoNet.visibility = View.GONE
            }
        })

        viewModel.navigateToChatActivity.observe(viewLifecycleOwner, {
            if (it.groupId.isNotEmpty()) {
                NewChatActivity.startChatActivity(
                    requireContext(),
                    "",
                    "",
                    "", true, it.groupId, it.showName
                )

            } else {
                NewChatActivity.startChatActivity(
                    requireContext(),
                    it.name,
                    it.nickName,
                    it.telephone,
                )
            }

        })

        viewModel.showDeleteMessageAction.observe(viewLifecycleOwner, {
            MessageDialogBuilder(activity)
                .setTitle("是否删除此通话记录")
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .addAction("取消") { dialog: QMUIDialog, index: Int -> dialog.dismiss() }
                .addAction(
                    0,
                    "删除",
                    QMUIDialogAction.ACTION_PROP_NEGATIVE
                ) { dialog: QMUIDialog, _: Int ->
                    dialog.dismiss()
                    viewModel.deleteMessageByTel(it)
                }
                .create().show()
        })

        lifecycleScope.launch {
            viewModel.deleteMessage.collect {
                when (it) {
                    is SuccessResource -> {
                        viewModel.getMessageList(searchStr)
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onReceiveData(msgEntity: MsgEntity) {
        //更新页面,加上登录验证
        when (msgEntity.type) {
            StatusConstant.TYPE_CHAT_REFRESH -> {
                viewModel.getMessageList(searchStr)
            }
            StatusConstant.TYPE_NO_NET -> {
                viewModel.showNewMessage(true)
                Timber.d("服务断开")
            }
            StatusConstant.TYPE_NET_CONNECTED -> {
                Timber.d("服务连接成功")
                viewModel.showNewMessage(false)
            }
        }
    }

    private fun showChatMenu() {
        val popup = XPopup.Builder(requireContext())
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .atView(mBinding.ivMenu)
            .hasShadowBg(false) // 去掉半透明背景
            .offsetY(XPopupUtils.dp2px(requireContext(), 6f))
            .asCustom(
                MessageMenuPopup(requireContext())
                    .setArrowWidth(XPopupUtils.dp2px(requireContext(), 5f))
                    .setArrowHeight(
                        XPopupUtils.dp2px(
                            getContext(),
                            6f
                        )
                    )
                    .setArrowRadius(XPopupUtils.dp2px(requireContext(), 3f))

            ) as MessageMenuPopup

        popup.setOnItemListener(object :
            OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {//发起聊天
                        popup.dismiss()
                        addNewChat()
                    }
                    1 -> {//发起群聊
                        popup.dismiss()
                        addNewGroup()
                    }
                }
            }

            override fun onItemLongClick(position: Int) {
            }

        })
        popup.show()
    }

    private fun addNewChat() {
        val builder = QMUIDialog.EditTextDialogBuilder(context)
        builder.setTitle("创建新的聊天")
            .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
            .setPlaceholder("在此输入手机号")
            .setInputType(InputType.TYPE_CLASS_PHONE)
            .addAction("取消") { dialog: QMUIDialog, index: Int -> dialog.dismiss() }
            .addAction("确定") { dialog: QMUIDialog, index: Int ->
                val text: CharSequence = builder.editText.text
                if (MyApplication.TEL.toString() + "" == text.toString()
                        .trim { it <= ' ' }
                ) {
                    MyQmuiDialog.showErrorDialog(requireActivity(), "不可给自己发送信息")
                } else if (!TelUtil.isChinaPhoneLegal(text.toString())) {
                    MyQmuiDialog.showErrorDialog(requireActivity(), "请输入正确的手机号")
                } else {
                    val friendBean =
                        DBManager.getInstance(getContext())
                            .selectFriend(text.toString())
                    NewChatActivity.startChatActivity(
                        requireContext(),
                        if (friendBean == null) "" else friendBean.getName(),
                        if (friendBean == null) "" else friendBean.getNickName(),
                        text.toString(),
                    )
                }
                dialog.dismiss()
            }
            .create().show()
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEvent(groupEvent: GroupEventEntity) {
//        when (groupEvent.groupEventId) {
//            14,15,16,17,18,19,31 -> {
//                if(!isResumed){
//                    viewModel.getMessageList(searchStr)
//                }
//            }
//        }
//    }

    private fun addNewGroup() {
        startActivity(Intent(requireActivity(), GroupCreateActivity::class.java))
    }

    companion object {
        fun getInstance() = NewMessageFragment()
    }
}
