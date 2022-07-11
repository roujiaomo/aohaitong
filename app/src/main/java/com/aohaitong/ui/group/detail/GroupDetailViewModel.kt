package com.aohaitong.ui.group.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aohaitong.bean.FriendBean
import com.aohaitong.bean.GroupBean
import com.aohaitong.domain.common.Resource
import com.aohaitong.domain.usecase.GetGroupInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val getGroupInfoUseCase: GetGroupInfoUseCase
) : ViewModel() {

    private val _getGroupInfoAction = MutableSharedFlow<Resource<GroupBean>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getGroupInfoResponse = _getGroupInfoAction

    private val _addGroupMemberListAction = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val addGroupMemberListAction = _addGroupMemberListAction

    private val _removeGroupMemberListAction = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val removeGroupMemberListAction = _removeGroupMemberListAction

    private val _navigationUserDetailAction = MutableSharedFlow<FriendBean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationUserDetailAction = _navigationUserDetailAction


    fun addGroupMember() {
        _addGroupMemberListAction.tryEmit(Unit)
    }

    fun removeGroupMember() {
        _removeGroupMemberListAction.tryEmit(Unit)
    }

    fun navigationUserDetail(friendBean: FriendBean) {
        _navigationUserDetailAction.tryEmit(friendBean)
    }

    fun getGroupInfo(groupId: String) {
        viewModelScope.launch {
            getGroupInfoUseCase(groupId)
                .collect {
                    _getGroupInfoAction.tryEmit(it)
                }
        }

    }
}