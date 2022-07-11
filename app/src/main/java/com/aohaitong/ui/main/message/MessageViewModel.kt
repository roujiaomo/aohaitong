package com.aohaitong.ui.main.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aohaitong.bean.entity.DeleteChatParams
import com.aohaitong.bean.entity.MessageBusinessBean
import com.aohaitong.domain.common.Resource
import com.aohaitong.domain.usecase.DeleteChatUseCase
import com.aohaitong.domain.usecase.GetMessageListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val getMessageListUseCase: GetMessageListUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
) : ViewModel() {

    private val _getMessageListAction = MutableSharedFlow<Resource<List<MessageBusinessBean>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getMessageListResponse = _getMessageListAction

    private val _showNewMessageAction = MutableLiveData<Boolean>()
    val isShowNewMessage = _showNewMessageAction

    private val _navigateToChatActivityAction = MutableLiveData<MessageBusinessBean>()
    val navigateToChatActivity = _navigateToChatActivityAction

    private val _showDeleteMessageAction = MutableLiveData<DeleteChatParams>()
    val showDeleteMessageAction = _showDeleteMessageAction

    private val _deleteMessageAction =
        MutableSharedFlow<Resource<Unit>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val deleteMessage = _deleteMessageAction

    fun getMessageList(searchStr: String) {
        viewModelScope.launch {
            getMessageListUseCase(searchStr)
                .collect {
                    _getMessageListAction.tryEmit(it)
                }
        }
    }

    fun showNewMessage(isShowNewMessage: Boolean) {
        _showNewMessageAction.value = isShowNewMessage
    }

    fun navigateToChatActivity(messageBusinessBean: MessageBusinessBean) {
        _navigateToChatActivityAction.value = messageBusinessBean
    }

    fun showDeleteMessage(deleteChatParams: DeleteChatParams) {
        _showDeleteMessageAction.value = deleteChatParams
    }

    fun deleteMessageByTel(telephone: DeleteChatParams) {
        viewModelScope.launch {
            deleteChatUseCase(telephone)
                .collect {
                    _deleteMessageAction.tryEmit(it)
                }

        }
    }
}