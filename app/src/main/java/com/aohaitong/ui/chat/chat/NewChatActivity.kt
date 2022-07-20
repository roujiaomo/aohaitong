package com.aohaitong.ui.chat.chat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.ChatMsgBean
import com.aohaitong.bean.MsgEntity
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.PhotoDetailParams
import com.aohaitong.bean.entity.VoicePlayParams
import com.aohaitong.business.IPController
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.business.transmit.TransmitManager
import com.aohaitong.constant.CommonConstant
import com.aohaitong.constant.NumConstant
import com.aohaitong.constant.StatusConstant
import com.aohaitong.constant.StatusConstant.TAKE_PHOTO_CAMERA
import com.aohaitong.constant.StatusConstant.TAKE_VIDEO_CAMERA
import com.aohaitong.databinding.ActivityNewChatBinding
import com.aohaitong.db.DBManager
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.kt.common.onClick
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.aohaitong.kt.util.VersionUtil
import com.aohaitong.ui.chat.ForwardActivity
import com.aohaitong.ui.chat.PhotoDetailActivity
import com.aohaitong.ui.chat.PhotoDetailActivity.Companion.CURRENT_PHOTO_PATH
import com.aohaitong.ui.chat.PhotoDetailActivity.Companion.PHOTO_PATH_LIST
import com.aohaitong.ui.friend.FriendDetailActivity
import com.aohaitong.ui.group.detail.GroupDetailActivity
import com.aohaitong.ui.group.detail.GroupDetailActivity.Companion.REQUEST_CODE_GROUP_DETAIL
import com.aohaitong.ui.group.detail.GroupDetailActivity.Companion.RESUlT_CODE_DISSOLVE_GROUP
import com.aohaitong.ui.group.detail.GroupDetailActivity.Companion.RESUlT_CODE_EXIT_GROUP
import com.aohaitong.ui.group.detail.GroupDetailActivity.Companion.RESUlT_CODE_MODIFY_GROUP_NAME
import com.aohaitong.utils.*
import com.aohaitong.utils.audio.AudioFocusManager
import com.aohaitong.utils.audio.AudioModeManger
import com.aohaitong.utils.audio.AudioPlayerManager
import com.aohaitong.widget.ActionDownListener
import com.aohaitong.widget.AudioFinishRecorderListener
import com.aohaitong.widget.ChatBottomDialog
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.zhaoss.weixinrecorded.activity.RecordedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import pub.devrel.easypermissions.PermissionRequest
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


/**
 * 消息的界面
 * 发送消息是先存入本地库，把本地缓存数据add一条新数据进去，刷新这一条,等服务器返回应答后，再刷新这一条的loading状态
 *
 * 接收消息是服务器推送消息过来，先存入本地库，然后页面再刷新消息
 *
 * 删除消息逻辑不同于于发送和接收消息，只涉及本地库。
 *
 * 初始化加载数据 拿去本地库的消息集合，缓存本地数据集合data，然后加载数据
 * 发送新消息，本地缓存数据data不动，data.add(), 刷新列表
 *
 * 发送图片/视频逻辑:
 * 1. 普通版本只有岸-岸才能发录制视频发10s视频和图片, 都不压缩
 * 2. 测试版本 不限制船岸, 视频和图片都可以发, 都是压缩到最大版本
 */
@AndroidEntryPoint
class NewChatActivity : BaseActivity(), ViewTreeObserver.OnGlobalLayoutListener,
    PermissionCallbacks {

    private var groupieAdapter: GroupAdapter<GroupieViewHolder> by autoCleared()
    private lateinit var section: Section
    private var binding: ActivityNewChatBinding by autoCleared()
    private var cacheRvList = mutableListOf<ChatMsgBusinessBean>()

    private var msgGroupList = mutableListOf<Group>()
    private val viewModel by viewModels<ChatViewModel>()
    private lateinit var rvLayoutManager: LinearLayoutManager
    private lateinit var audioModeManger: AudioModeManger
    private lateinit var chatBottomDialog: ChatBottomDialog
    private lateinit var userTelephone: String
    private var userName: String? = null
    private var userNickName: String? = null
    private var isFriend = false
    private var isGroup = false
    private var groupId = ""
    private var groupName = ""
    private var messageType = 0
    private var isTextInput = true //默认文字输入
    private var lastPlayPosition = -1
    private var userLoginStatus = ""
    private var currentFilePathFromCamera = ""

    companion object {
        const val PROVIDER_STRING = "com.aohaitong.fileProvider"

        fun startChatActivity(
            context: Context,
            name: String? = "",
            nickName: String? = "",
            tel: String? = "",
            isGroup: Boolean = false,
            groupId: String = "",
            groupName: String = "",
        ) {
            val intent = Intent(context, NewChatActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("nickName", nickName)
            intent.putExtra("tel", tel)
            intent.putExtra("isGroup", isGroup)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            context.startActivity(intent)
        }
    }

    override fun getLayout() = R.layout.activity_new_chat

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        initRecyclerView()
        initRefreshView()
    }

    var handler = Handler()


    var runnable: Runnable = object : Runnable {
        override fun run() {
            //保存本地数据库
            handleSendMessage(
                text = "哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈",
                messageType = StatusConstant.TYPE_TEXT_MESSAGE,
                isSendToService = true,
                isGroup = isGroup
            )
            handler.postDelayed(this, 1000)
        }
    }

    override fun initData() {
        isGroup = intent.getBooleanExtra("isGroup", false)
        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")
        userTelephone = intent.getStringExtra("tel") ?: ""
        userName = intent.getStringExtra("name") ?: ""
        userNickName = intent.getStringExtra("nickName") ?: ""
        if (isGroup) {
            binding.tvTitle.text = groupName
        } else {
            binding.tvTitle.text =
                StringUtil.getFirstNotNullString(arrayOf(userNickName, userName, userTelephone))
        }
        try {
            NotificationUtil.getInstance().cancelNotification(true, userTelephone.toInt())
        } catch (ignored: Exception) {
        }
        audioModeManger = AudioModeManger()
        if (isGroup) {
            binding.btnChatMenu.visibility = View.GONE
            binding.btnSend.visibility = View.VISIBLE
            viewModel.doServiceGroupRefresh(groupId.toLong())
        } else {
            viewModel.doServiceRefresh(userTelephone)
        }
//        handler.postDelayed(runnable, 2000);
    }

    override fun initEvent() {
        //服务器返回, 刷新列表数据
        lifecycleScope.launch {
            viewModel.doServiceRefreshResponse.observe(this@NewChatActivity) {
                when (it) {
                    is SuccessResource -> {
                        //修改消息的状态
                        cacheRvList.clear()
                        cacheRvList.addAll(it.data.toMutableList())
                        convertGroupieList(cacheRvList)
                        section.update(msgGroupList)
                        scrollToBottom()
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }

        //刷新群聊聊天数据
        lifecycleScope.launch {
            viewModel.doServiceRefreshGroupResponse.observe(this@NewChatActivity) {
                when (it) {
                    is SuccessResource -> {
                        //修改消息的状态
                        cacheRvList.clear()
                        cacheRvList.addAll(it.data.toMutableList())
                        convertGroupieList(cacheRvList)
                        section.update(msgGroupList)
                        scrollToBottom()
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }


        //删除私聊消息
        lifecycleScope.launch {
            viewModel.handleDeleteMessageResponse.observe(this@NewChatActivity) {
                when (it) {
                    is SuccessResource -> {
                        cacheRvList.clear()
                        cacheRvList.addAll(it.data.toMutableList())
                        convertGroupieList(cacheRvList)
                        section.update(msgGroupList)
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }

        //删除群聊消息
        lifecycleScope.launch {
            viewModel.handleDeleteGroupMessageResponse.observe(this@NewChatActivity) {
                when (it) {
                    is SuccessResource -> {
                        cacheRvList.clear()
                        cacheRvList.addAll(it.data.toMutableList())
                        convertGroupieList(cacheRvList)
                        section.update(msgGroupList)
                    }
                    is ErrorResource -> {

                    }
                    else -> {
                    }
                }
            }
        }

        //---------------------------点击事件----------------------------------------
        binding.ivSwitchInput.onClick {
            //申请权限
            if (hasAudioPermission()) {
                switchChatType()
            } else {
                requestAudioPermission()
            }
        }

        //发送文字消息
        binding.btnSend.onClickWithAvoidRapidAction {
            val text: String = binding.etEditMessage.text.toString()
            binding.etEditMessage.setText("")
            if (TextUtils.isEmpty(text)) {
                toast(getString(R.string.chat_toast_edit_empty))
                return@onClickWithAvoidRapidAction
            }
            //保存本地数据库
            handleSendMessage(
                text = text,
                messageType = StatusConstant.TYPE_TEXT_MESSAGE,
                isSendToService = true,
                isGroup = isGroup
            )
        }

        //发送语音消息
        binding.btnRecord.setAudioFinishRecorderListener(object : AudioFinishRecorderListener {
            override fun onFinish(seconds: Double, filePath: String?) {
                //取整录音时间，存储本地，发送
                val time = ceil(seconds).toInt()
                val recordStringData = FileUtils.fileToString(filePath)
                runOnUiThread {
                    handleSendMessage(
                        text = recordStringData,
                        messageType = StatusConstant.TYPE_RECORD_MESSAGE,
                        filePath = filePath,
                        recordTime = time,
                        isSendToService = true,
                        isGroup = isGroup
                    )
                }

            }
        })

        //重新发送
        lifecycleScope.launch {
            viewModel.resendClickAction.collect {
                val currentIndex = cacheRvList.indexOf(it)
                if (currentIndex != -1) {
                    //发送文字
                    when (it.messageType) {
                        0 -> {
                            handleSendMessage(
                                id = it.id,
                                text = it.msg,
                                messageType = it.messageType,
                                filePath = it.filePath,
                                insertTime = it.insertTime,
                                sendTime = it.time,
                                recordTime = it.recordTime,
                                isSendToService = true,
                                isResend = true,
                                isGroup = isGroup

                            )
                        }
                        else -> {
                            val fileDataRunnable = Runnable {
                                val photoStringData =
                                    FileUtils.fileToString(it.filePath)
                                runOnUiThread {
                                    handleSendMessage(
                                        id = it.id,
                                        text = photoStringData,
                                        messageType = it.messageType,
                                        filePath = it.filePath,
                                        insertTime = it.insertTime,
                                        sendTime = it.time,
                                        recordTime = it.recordTime,
                                        isSendToService = true,
                                        isResend = true,
                                        isGroup = isGroup
                                    )
                                }
                            }
                            ThreadPoolManager.getInstance().execute(fileDataRunnable)
                        }
                    }

                }
            }
        }

        //删除消息
        lifecycleScope.launch {
            viewModel.deleteItemClickAction.collect {
                //为了加对话框准备
                if (isGroup) {
                    viewModel.handleDeleteGroupMsg(it)
                } else {
                    viewModel.handleDeleteMsg(it)
                }
            }
        }

        //复制消息
        lifecycleScope.launch {
            viewModel.copyItemClickAction.collect {
                CommonUtil.copyToClipBoard(it, this@NewChatActivity)
                toast(getString(R.string.chat_toast_copy_to_clipBoard))
            }
        }

        //转发消息
        lifecycleScope.launch {
            viewModel.transferClickAction.collect {
                ForwardActivity.startForwardActivity(this@NewChatActivity, it)
            }
        }

        //播放音频
        lifecycleScope.launch {
            viewModel.voicePlayClickAction.collect {
                //修改播放/非播放的数据
                val currentBean = cacheRvList[it.position]
                val newCurrentBean: ChatMsgBusinessBean
                if (AudioPlayerManager.getInstance().isOnPlaying) {
                    AudioPlayerManager.getInstance().stopPlay()
                }
                if (currentBean.isRecording) {  //当前点击的正在播放
                    newCurrentBean = currentBean.copy(isRecording = false)
                } else { //取消其他正在播放的音频播放效果
                    var currentPlayIndex = -1
                    cacheRvList.mapIndexed { index, chatMsgBusinessBean ->
                        if (chatMsgBusinessBean.isRecording) {
                            currentPlayIndex = index
                        }
                    }
                    if (currentPlayIndex != -1) {
                        cacheRvList[currentPlayIndex] =
                            cacheRvList[currentPlayIndex].copy(isRecording = false)
                    }
                    newCurrentBean = currentBean.copy(isRecording = true)
                    //播放音频
                    playRecord(it)
                }
                cacheRvList[it.position] = newCurrentBean
                convertGroupieList(cacheRvList)
                section.update(msgGroupList)
                lastPlayPosition = it.position
            }
        }

        //跳转到查看大图/视频界面
        lifecycleScope.launch {
            viewModel.viewLargeImageOrVideoClickAction.collect {
                val photoDetailParamsList = ArrayList<PhotoDetailParams>()
                photoDetailParamsList.addAll(
                    cacheRvList.filter { filterChatMsgBusinessBean ->
                        !filterChatMsgBusinessBean.filePath.isNullOrEmpty()
                                && (filterChatMsgBusinessBean.messageType == StatusConstant.TYPE_PHOTO_MESSAGE
                                || filterChatMsgBusinessBean.messageType == StatusConstant.TYPE_VIDEO_MESSAGE)
                    }.map { mapChatMsgBusinessBean ->
                        PhotoDetailParams(
                            mapChatMsgBusinessBean.filePath,
                            mapChatMsgBusinessBean.messageType
                        )
                    }
                )
                val intent = Intent(this@NewChatActivity, PhotoDetailActivity::class.java)
                intent.putExtra(PHOTO_PATH_LIST, photoDetailParamsList)
                intent.putExtra(CURRENT_PHOTO_PATH, it.filePath)
                startActivity(intent)
            }
        }
        //发送图片/视频
        binding.btnChatMenu.onClick {
            KeyBoardUtil.hideKeyBoard(this@NewChatActivity, binding.btnChatMenu)
            showChatMenuDialog()
        }

        binding.ivRight.onClickWithAvoidRapidAction {
            setCurrentRecordStop()
            if (isGroup) {
                GroupDetailActivity.startGroupDetailActivity(
                    this,
                    groupId,
                )
            } else {
                FriendDetailActivity.startFriendDetailActivity(
                    context,
                    userName,
                    userTelephone,
                    userNickName,
                    isFriend
                )
            }

        }

        binding.ivBack.onClick {
            finish()
        }

        binding.rvContent.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.rvContent.postDelayed(
                    {
                        scrollToBottom()
                    }, 100
                )
            }
        }

        //点击录音按钮前,关闭其他播放的音频
        binding.btnRecord.setActionDownListener(object : ActionDownListener {
            override fun onEvent() {
                setCurrentRecordStop()
            }
        })

        binding.etEditMessage.let {
            it.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(90))
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    val contentLength = s.toString().trim().length
                    if (contentLength > 90) {
                        toast(getString(R.string.chat_toast_edit_max_length))
                        binding.etEditMessage.setText(s.toString().substring(0, 90))
                    }
                    if (isGroup) {
                        binding.btnSend.visibility = View.VISIBLE
                    } else {
                        if (isGroup) {
                            binding.btnSend.visibility = View.VISIBLE
                            binding.btnChatMenu.visibility = View.GONE
                            return
                        }
                        if (contentLength > 0) {
                            messageType = 0 //切换类型为文字
                            binding.btnSend.visibility = View.VISIBLE
                            binding.btnChatMenu.visibility = View.GONE
                        } else {
                            binding.btnChatMenu.visibility = View.VISIBLE
                            binding.btnSend.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    /**
     * 发送消息
     * (返回值为了获取视频压缩后)
     */
    private fun handleSendMessage(
        id: Long? = null,
        text: String? = "",
        messageType: Int = StatusConstant.TYPE_TEXT_MESSAGE,
        filePath: String? = "",
        sendTime: String? = System.currentTimeMillis().toString(),
        insertTime: Date = Date(),
        recordTime: Int = 0,
        isSendToService: Boolean = true,
        isResend: Boolean = false,
        isGroup: Boolean = false
    ): ChatMsgBean {
        val isFile = when (messageType) {
            StatusConstant.TYPE_TEXT_MESSAGE -> false
            else -> true
        }
        //保存本地数据库,文件类消息在本地库不保存text String内容
        val chatMsgBean = ChatMsgBean(
            id,
            if (isFile) "" else text,
            if (isGroup) MyApplication.TEL.toString() else userTelephone,
            MyApplication.TEL.toString(),
            StatusConstant.SEND_TYPE_SENDER,
            sendTime,
            insertTime,
            StatusConstant.SEND_LOADING, messageType, filePath, recordTime,
            isGroup, groupId
        )
        if (isResend) { // 重新发送更新本地库对应消息
            DBManager.getInstance(this).updateMsg(chatMsgBean)
        } else { //本地库新建消息
            DBManager.getInstance(this).createMsg(chatMsgBean)
        }
        //刷新列表界面(loading状态)
        val chatMsgBusinessBean = DataUtils.convertChatBusinessBean(chatMsgBean)
        chatMsgBusinessBean.msg = text //列表里的数据需要保存发送的文字信息, 为了重新发送
        cacheRvList.add(chatMsgBusinessBean)
        addItemToGroupieList(chatMsgBusinessBean)
        section.update(msgGroupList)
        scrollToBottom()
        //发送到服务器
        if (isSendToService) { //直接发送到服务器
            handleSendToService(isFile, text, chatMsgBean)
        }
        return chatMsgBean
    }


    fun handleSendToService(isFile: Boolean, msgContentStr: String?, chatMsgBean: ChatMsgBean) {
        val runnable = {
            if (isFile) {
                val sendBean: ChatMsgBean = DataUtils.copyChatMsgBean(chatMsgBean)
                sendBean.msg = msgContentStr
                TransmitManager.instance.sendText(sendBean)
            } else {
                TransmitManager.instance.sendText(chatMsgBean)
            }
        }
        ThreadPoolManager.getInstance().execute(runnable)
    }

    private fun handleSendVideoToService(
        msgContentList: List<String>,
        chatMsgBean: ChatMsgBean
    ) {
        val runnable = {
            val sendBean: ChatMsgBean = DataUtils.copyChatMsgBean(chatMsgBean)
            TransmitManager.instance.sendVideo(sendBean, msgContentList)
        }
        ThreadPoolManager.getInstance().execute(runnable)
    }

    /**
     * 从相册拿的数据
     * 视频逻辑: 先使用原视频的文件地址 刷新列表(包含跳转) 压缩成功后发送到服务器
     */
    private fun handleSendPhotoFromAlbum(photo: Photo?) {
        photo?.let { selectFile ->
            //视频
            val originalFile = File(selectFile.path) //原视频文件
            if (selectFile.type.contains("mp4")) {
                //发送原文件 刷新列表
                val chatMsgBean = handleSendMessage(
                    text = "",
                    messageType = StatusConstant.TYPE_VIDEO_MESSAGE,
                    filePath = originalFile.absolutePath,
                    isSendToService = false,
                    isGroup = isGroup,
                )
                if (isSendVideoGeneral() && !VersionUtil.isTestVersion()) {
                    val photoStringDataRunnable = Runnable {
                        val photoStringData =
                            FileUtils.fileToString(originalFile.absolutePath)
                        handleSendToService(true, photoStringData, chatMsgBean)
                    }
                    ThreadPoolManager.getInstance().execute(photoStringDataRunnable)
                } else {
                    //输出文件
                    val outputFile = File(
                        Environment.getExternalStorageDirectory().absolutePath
                                + CommonConstant.PHOTO_FILE_PATH, FileUtils.generateFileName("mp4")
                    )
                    //压缩视频
                    val compressRunnable = Runnable {
                        val isCompressSuccess =
                            FileUtils.compressVideo(
                                originalFile.absolutePath,
                                outputFile.absolutePath
                            )
                        if (isCompressSuccess) {
                            chatMsgBean.filePath = outputFile.absolutePath
                            //更新本地库视频的路径
                            DBManager.getInstance(this).updateMsg(chatMsgBean)
                            val photoStringData = FileUtils.fileToString(outputFile.absolutePath)
                            //发送到服务器
                            handleSendToService(true, photoStringData, chatMsgBean)
                        } else {
                            //将此条消息状态更新为发送失败
                        }
                    }
                    ThreadPoolManager.getInstance().execute(compressRunnable)
                }
            }
            //图片
            else {
//
//                val fileName = FileUtils.generateFileName("jpg")
//                val compressImageFile = CompressHelper.Builder(this)
//                    .setMaxWidth(300f) // 最大宽度
//                    .setMaxHeight(400f) // 最大高度
//                    .setQuality(50) // 压缩质量
//                    .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
//                    .setFileName(fileName) // 设置你的文件名
//                    .setDestinationDirectoryPath(FileUtils.BASE_FILE_PATH + CommonConstant.PHOTO_FILE_PATH)
//                    .build()
//                    .compressToFile(originalFile)
                val photoStringData = FileUtils.fileToString(originalFile.path)

                handleSendMessage(
                    text = photoStringData,
                    messageType = StatusConstant.TYPE_PHOTO_MESSAGE,
                    filePath = originalFile.path,
                    isSendToService = true,
                    isGroup = isGroup
                )
            }
        }


    }

    private fun handleSendPhotoFromCamera(filePath: String) {
        //视频
        val originalFile = File(filePath) //原视频文件
        if (filePath.contains("mp4")) {
            //发送原文件 刷新列表
            val chatMsgBean = handleSendMessage(
                text = "",
                messageType = StatusConstant.TYPE_VIDEO_MESSAGE,
                filePath = originalFile.absolutePath,
                isSendToService = false,
                isGroup = isGroup,
            )
            val photoStringDataRunnable = Runnable {
                val photoStringData =
                    FileUtils.fileToString(originalFile.absolutePath)
                handleSendToService(true, photoStringData, chatMsgBean)
            }
            ThreadPoolManager.getInstance().execute(photoStringDataRunnable)
        }
        //图片
        else {
            val photoStringData = FileUtils.fileToString(originalFile.path)
            handleSendMessage(
                text = photoStringData,
                messageType = StatusConstant.TYPE_PHOTO_MESSAGE,
                filePath = originalFile.path,
                isSendToService = true,
                isGroup = isGroup
            )
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(entity: MsgEntity) {
        when (entity.type) {
            StatusConstant.EVENT_CHAT_SERVICE_REFRESH -> {
                //服务器返回应答 刷新界面数据
                if (isGroup) {
                    viewModel.doServiceGroupRefresh(groupId.toLong())
                } else {
                    viewModel.doServiceRefresh(userTelephone)
                }
            }
            //接收好友消息 刷新界面数据
            StatusConstant.EVENT_CHAT_RECEIVE_MESSAGE -> {
                if (isGroup) {
                    viewModel.doServiceGroupRefresh(groupId.toLong())
                    //获取群组名称显示标题
                    binding.tvTitle.text =
                        DBManager.getInstance(this).getGroupNameById(groupId).toString()
                } else {
                    viewModel.doServiceRefresh(userTelephone)
                }
            }
            StatusConstant.EVENT_CHAT_RECEIVE_USER_LOGIN_STATUS -> {
                val friendBean = DBManager.getInstance(context).selectFriend(userTelephone)
                if (friendBean != null) {
                    userNickName = friendBean.getNickName()
                    isFriend = true
                } else {
                    isFriend = false
                }
                binding.tvTitle.text =
                    StringUtil.getFirstNotNullString(
                        arrayOf(
                            userNickName,
                            userName,
                            userTelephone
                        )
                    )
                userLoginStatus = entity.msg
            }
        }
    }

    private fun convertGroupieList(list: MutableList<ChatMsgBusinessBean>) {
        msgGroupList.clear()
        list.map {
            addItemToGroupieList(it)
        }
    }

    private fun addItemToGroupieList(chatMsgBusinessBean: ChatMsgBusinessBean) {
        if (chatMsgBusinessBean.sendType == StatusConstant.SEND_TYPE_SENDER) {
            msgGroupList.add(
                when (chatMsgBusinessBean.messageType) {
                    StatusConstant.TYPE_TEXT_MESSAGE -> {
                        ChatSendTextItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    StatusConstant.TYPE_RECORD_MESSAGE -> {
                        ChatSendVoiceItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    StatusConstant.TYPE_PHOTO_MESSAGE, StatusConstant.TYPE_VIDEO_MESSAGE -> {
                        ChatSendImageOrVideoItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    else -> {
                        ChatSendTextItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                }
            )
        } else {
            msgGroupList.add(
                when (chatMsgBusinessBean.messageType) {
                    StatusConstant.TYPE_TEXT_MESSAGE -> {
                        ChatReceiveTextItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    StatusConstant.TYPE_RECORD_MESSAGE -> {
                        ChatReceiveVoiceItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    StatusConstant.TYPE_PHOTO_MESSAGE, StatusConstant.TYPE_VIDEO_MESSAGE -> {
                        ChatReceivedImageOrVideoItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    StatusConstant.TYPE_GROUP_NOTIFY_MESSAGE -> {
                        ChatGroupAddMemberItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                    else -> {
                        ChatReceiveTextItem(
                            viewModel,
                            this@NewChatActivity,
                            chatMsgBusinessBean,
                            this@NewChatActivity
                        )
                    }
                }
            )
        }

    }

    private fun showChatMenuDialog() {
        chatBottomDialog = ChatBottomDialog(this)
        chatBottomDialog.setOnItemListener { position ->
            if (!VersionUtil.isTestVersion()) {
                if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
                    toast(getString(R.string.chat_mine_not_support_file))
                    return@setOnItemListener
                }
                if (userLoginStatus == "1" || userLoginStatus == "2") {
                    toast(getString(R.string.chat_other_side_not_support_file))
                    return@setOnItemListener
                }
            }
            when (position) {
                0 -> { //拍照
                    if (hasCameraPermission()) {
                        val intent = Intent(
                            this,
                            RecordedActivity::class.java
                        )
                        intent.putExtra(
                            RecordedActivity.INTENT_MAX_RECORD_TIME,
                            if (VersionUtil.isTestVersion()) 5 * 1000 else 10 * 1000
                        )
                        startActivityForResult(intent, TAKE_PHOTO_CAMERA)

//                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//                        currentFilePathFromCamera =
//                            Environment.getExternalStorageDirectory().absolutePath +
//                                    CommonConstant.PHOTO_FILE_PATH + FileUtils.generateFileName("mp4")
//                        val outputFile = File(
//                            currentFilePathFromCamera
//                        )
//                            val imageUri = Uri.fromFile(outputFile)
////                        val imageUri: Uri = FileProvider.getUriForFile(
////                            this,
////                            "$packageName.fileprovider",
////                            outputFile
////                        )
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//                        //设置视频录制的最长时间
//                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
//                        //设置视频录制的画质
//                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5)
////                        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5 * 1024 * 1024L)
//                        startActivityForResult(intent, TAKE_VIDEO_CAMERA)
                        chatBottomDialog.hideDialog()
                    } else {
                        requestCameraPermission()
                    }
                }
                1 -> {//相册
                    if (hasCameraPermission()) {
                        val albumBuilder = EasyPhotos.createAlbum(
                            this,
                            true,
                            false,
                            GlideEngine.getInstance()
                        )
                            .setCount(1) //设置最多选择一张
                            .setVideo(VersionUtil.isTestVersion()) //设置显示视频
                            .setPuzzleMenu(false) //不显示拼图按钮
                            .setFileProviderAuthority(PROVIDER_STRING)
                        albumBuilder.setVideoMaxSecond(6) //设置最大视频
                        albumBuilder.start(StatusConstant.TAKE_PHOTO_ALBUM)
                        chatBottomDialog.hideDialog()
                    } else {
                        requestCameraPermission()
                    }
                }
            }
        }
        chatBottomDialog.showDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        when (requestCode) {
            //相册/相机选择照片
            StatusConstant.TAKE_PHOTO_ALBUM -> {
                if (Activity.RESULT_OK == resultCode) {
                    val resultPhotos: ArrayList<Photo>? =
                        intentData?.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
                    val photo = resultPhotos?.get(0)
                    handleSendPhotoFromAlbum(photo)
                }
            }
            //群聊界面返回
            REQUEST_CODE_GROUP_DETAIL -> {
                when (resultCode) {
                    RESUlT_CODE_DISSOLVE_GROUP, RESUlT_CODE_EXIT_GROUP -> {//解散群聊/退出群聊
                        finish()
                    }
                    RESUlT_CODE_MODIFY_GROUP_NAME -> {//修改群聊名称

                    }
                }
            }
            TAKE_PHOTO_CAMERA -> {
                intentData?.let {
                    val photoPath: String =
                        intentData.getStringExtra(RecordedActivity.INTENT_PATH) ?: ""
                    handleSendPhotoFromCamera(photoPath)
                }
            }
            TAKE_VIDEO_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    handleSendPhotoFromCamera(currentFilePathFromCamera)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioModeManger.register()
        val friendBean = DBManager.getInstance(context).selectFriend(userTelephone)
        if (friendBean != null) {
            userNickName = friendBean.getNickName()
            isFriend = true
        } else {
            isFriend = false
        }
        //私聊更新好友昵称
        if (!isGroup) {
            ThreadPoolManager.getInstance().execute(getUserLoginStatusRunnable)
        }
        //群聊更新群聊名称
        else {
            groupName = DBManager.getInstance(context).getGroupNameById(groupId)
            binding.tvTitle.text = groupName
        }

    }

    private val getUserLoginStatusRunnable = Runnable {
        BusinessController.sendGetUserLoginStatusList(userTelephone, object : ISendListener {
            override fun sendSuccess() {

            }

            override fun sendFail(reason: String?) {

            }

        }, NumConstant.getJHDNum())
    }

    private fun playRecord(voicePlayParams: VoicePlayParams) {
        if (voicePlayParams.position > cacheRvList.size) {
            return
        }
        val audioFocusManager = AudioFocusManager()
        val requestCode = audioFocusManager.requestTheAudioFocus()
        if (requestCode == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            val isPrepareSuccess = AudioPlayerManager.getInstance().preparePlay(
                voicePlayParams.filePath
            ) { mediaPlayer: MediaPlayer ->
                try {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.setOnSeekCompleteListener { mp -> mp.start() }
                    mediaPlayer.setOnCompletionListener { mp: MediaPlayer? ->
                        AudioPlayerManager.getInstance().stopPlay()
                        audioFocusManager.releaseTheAudioFocus() //释放播放资源
                        audioModeManger.setScreenOn() //屏幕亮
                        //防止播放过程中删除
                        if (voicePlayParams.position > cacheRvList.size) {
                            return@setOnCompletionListener
                        }
                        cacheRvList[voicePlayParams.position] =
                            cacheRvList[voicePlayParams.position].copy(isRecording = false)
                        convertGroupieList(cacheRvList)
                        section.update(msgGroupList)
                    }
                } catch (e: Exception) {
                    setCurrentRecordStop()
                    val toast = Toast.makeText(
                        context,
                        "播放失败",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
            if (!isPrepareSuccess) {
                setCurrentRecordStop()
                val toast = Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }


    private fun switchChatType() {
        if (isTextInput) {
            //文字切换语音,关闭软键盘
            messageType = 1
            binding.btnRecord.visibility = View.VISIBLE
            binding.etEditMessage.visibility = View.GONE
            binding.btnSend.visibility = View.GONE
            KeyBoardUtil.hideKeyBoard(this, binding.ivSwitchInput)
            binding.ivSwitchInput.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_word_input
                )
            )
        } else {
            messageType = 0
            binding.btnRecord.visibility = View.GONE
            binding.etEditMessage.visibility = View.VISIBLE
            binding.btnSend.visibility = View.VISIBLE
            KeyBoardUtil.showKeyBoard(this)
            binding.etEditMessage.requestFocus()
            binding.ivSwitchInput.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_record_input
                )
            )
        }
        isTextInput = !isTextInput
    }

    private fun initRecyclerView() {
        groupieAdapter = GroupAdapter<GroupieViewHolder>()
        rvLayoutManager = LinearLayoutManager(context)
        binding.rvContent.apply {
            layoutManager = rvLayoutManager
            adapter = groupieAdapter
            itemAnimator = null
            viewTreeObserver.addOnGlobalLayoutListener(this@NewChatActivity)
        }
        section = Section()
        groupieAdapter.add(section)
    }

    private fun initRefreshView() {
//        binding.refreshLayout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
//        binding.refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
//        binding.refreshLayout.setOnRefreshListener { refreshLayout ->
//            refreshLayout.finishRefresh(2000 /*,false*/) //传入false表示刷新失败
//        }
//        binding.refreshLayout.setOnLoadMoreListener { refreshLayout ->
//            refreshLayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
//        }
    }


    private fun setCurrentRecordStop() {
        if (cacheRvList.isNotEmpty() && AudioPlayerManager.getInstance().isOnPlaying) {
            AudioPlayerManager.getInstance().stopPlay()
            if (lastPlayPosition < cacheRvList.size) {
                cacheRvList[lastPlayPosition] =
                    cacheRvList[lastPlayPosition].copy(isRecording = false)
            }
            runOnUiThread {
                convertGroupieList(cacheRvList)
                section.update(msgGroupList)
            }
        }
        audioModeManger.setSpeakerPhoneOn(true) //重置为扬声器模式

    }

    override fun onGlobalLayout() {
        val canScroll: Boolean =
            binding.rvContent.computeVerticalScrollRange() > binding.rvContent.computeVerticalScrollExtent()

        if (canScroll && !rvLayoutManager.stackFromEnd) {
            rvLayoutManager.stackFromEnd = true
            return
        }

        if (!canScroll && rvLayoutManager.stackFromEnd) {
            rvLayoutManager.stackFromEnd = false
        }
    }

    private fun scrollToBottom() {
        if (cacheRvList.size > 0) {
            binding.rvContent.scrollToPosition(cacheRvList.size - 1)
        }
    }

    private fun isSendVideoGeneral(): Boolean {
        return IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ && userLoginStatus == "0"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setRationale(
                when (requestCode) {
                    PermissionUtils.CAMERA_PERMISSION -> {
                        getString(R.string.camera_permission_final)
                    }
                    PermissionUtils.AUDIO_PERMISSION -> {
                        getString(R.string.audio_permission_final)
                    }
                    else -> {
                        ""
                    }
                }
            )
                .setTitle(R.string.permission_title)
                .build().show()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasAudioPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.RECORD_AUDIO)
    }


    private fun requestCameraPermission() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this,
                PermissionUtils.CAMERA_PERMISSION,
                Manifest.permission.CAMERA,
            )
                .setRationale(R.string.camera_permission_deny)
                .setPositiveButtonText(R.string.chat_permission_ok)
                .setNegativeButtonText(R.string.chat_permission_cancel)
                .build()
        )
    }

    private fun requestAudioPermission() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this,
                PermissionUtils.AUDIO_PERMISSION,
                Manifest.permission.RECORD_AUDIO,
            )
                .setRationale(R.string.audio_permission_deny)
                .setPositiveButtonText(R.string.chat_permission_ok)
                .setNegativeButtonText(R.string.chat_permission_cancel)
                .build()
        )
    }

    override fun onStop() {
        super.onStop()
        setCurrentRecordStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::chatBottomDialog.isInitialized && chatBottomDialog.isShowing) {
            chatBottomDialog.hideDialog()
        }
        audioModeManger.unregister()
    }

}