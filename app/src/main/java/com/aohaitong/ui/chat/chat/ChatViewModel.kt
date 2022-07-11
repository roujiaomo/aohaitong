package com.aohaitong.ui.chat.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.DeleteChatMsgParams
import com.aohaitong.bean.entity.VoicePlayParams
import com.aohaitong.domain.usecase.DeleteChatMsgUseCase
import com.aohaitong.domain.usecase.DeleteGroupChatMsgUseCase
import com.aohaitong.domain.usecase.GetChatHistoryListUseCase
import com.aohaitong.domain.usecase.GetGroupChatHistoryListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatHistoryListUseCase: GetChatHistoryListUseCase,
    private val getGroupChatHistoryListUseCase: GetGroupChatHistoryListUseCase,
    private val deleteGroupChatMsgUseCase: DeleteGroupChatMsgUseCase,
    private val deleteChatMsgUseCase: DeleteChatMsgUseCase
) : ViewModel() {

    private val _doServiceRefreshAction = MutableLiveData<String>()
    val doServiceRefreshResponse = _doServiceRefreshAction.switchMap {
        getChatHistoryListUseCase(it)
    }

    private val _doServiceRefreshGroupAction = MutableLiveData<Long>()
    val doServiceRefreshGroupResponse = _doServiceRefreshGroupAction.switchMap {
        getGroupChatHistoryListUseCase(it)
    }

    private val _handleDeleteMessageAction = MutableLiveData<DeleteChatMsgParams>()
    val handleDeleteMessageResponse = _handleDeleteMessageAction.switchMap {
        deleteChatMsgUseCase(it)
    }

    private val _handleDeleteGroupMessageAction = MutableLiveData<DeleteChatMsgParams>()
    val handleDeleteGroupMessageResponse = _handleDeleteGroupMessageAction.switchMap {
        deleteGroupChatMsgUseCase(it)
    }

    private val _deleteItemClickAction = MutableSharedFlow<DeleteChatMsgParams>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val deleteItemClickAction = _deleteItemClickAction

    private val _copyItemClickAction = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val copyItemClickAction = _copyItemClickAction

    private val _transferItemClickAction = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val transferClickAction = _transferItemClickAction

    private val _resendClickAction = MutableSharedFlow<ChatMsgBusinessBean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val resendClickAction = _resendClickAction

    private val _voicePlayClickAction = MutableSharedFlow<VoicePlayParams>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val voicePlayClickAction = _voicePlayClickAction

    private val _viewLargeImageOrVideoClickAction = MutableSharedFlow<ChatMsgBusinessBean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val viewLargeImageOrVideoClickAction = _viewLargeImageOrVideoClickAction


    /**
     * 刷新消息状态
     */
    fun doServiceRefresh(currentUserTel: String) {
        _doServiceRefreshAction.value = currentUserTel
    }

    /**
     * 群聊刷新消息状态
     */
    fun doServiceGroupRefresh(groupId: Long) {
        _doServiceRefreshGroupAction.value = groupId
    }

    /**
     * 删除私聊单条聊天记录
     */
    fun handleDeleteMsg(deleteChatMsgParams: DeleteChatMsgParams) {
        _handleDeleteMessageAction.value = deleteChatMsgParams
    }

    /**
     * 删除群聊单条聊天记录
     */
    fun handleDeleteGroupMsg(deleteChatMsgParams: DeleteChatMsgParams) {
        _handleDeleteGroupMessageAction.value = deleteChatMsgParams
    }

    /**
     * 删除消息
     */
    fun onDeleteItemClick(deleteChatMsgParams: DeleteChatMsgParams) {
        _deleteItemClickAction.tryEmit(deleteChatMsgParams)
    }

    /**
     * 复制消息
     */
    fun onCopyItemClick(message: String) {
        _copyItemClickAction.tryEmit(message)
    }

    /**
     * 转发消息
     */
    fun onTransferItemClick(message: String) {
        _transferItemClickAction.tryEmit(message)
    }

    /**
     * 重新发送
     */
    fun onReSendClick(chatMsgBusinessBean: ChatMsgBusinessBean) {
        _resendClickAction.tryEmit(chatMsgBusinessBean)
    }

    /**
     * 点击播放/停止音频
     */
    fun onVoicePlayClick(voicePlayParams: VoicePlayParams) {
        _voicePlayClickAction.tryEmit(voicePlayParams)
    }

    /**
     * 跳转到查看大图/视频界面
     */
    fun onViewLargeImageOrVideo(chatMsgBusinessBean: ChatMsgBusinessBean) {
        _viewLargeImageOrVideoClickAction.tryEmit(chatMsgBusinessBean)
    }
}