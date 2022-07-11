package com.aohaitong.ui.group.create

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aohaitong.bean.entity.GroupFriendBean
import com.aohaitong.domain.common.Resource
import com.aohaitong.domain.usecase.GetFriendListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupCreateViewModel @Inject constructor(
    private val getFriendListUseCase: GetFriendListUseCase,
) : ViewModel() {

    private val _getFriendListAction = MutableSharedFlow<Resource<List<GroupFriendBean>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getFriendListResponse = _getFriendListAction


    private val _addCheckFriendAction = MutableLiveData<String>()
    private val _removeCheckFriendAction = MutableLiveData<String>()
    val friendListLivedata = MediatorLiveData<MutableList<String>>()
    val tempFriendList = mutableListOf<String>()

    init {
        friendListLivedata.addSource(_addCheckFriendAction) {
            if (!tempFriendList.contains(it)) {
                tempFriendList.add(it)
            }
            friendListLivedata.value = tempFriendList
        }
        friendListLivedata.addSource(_removeCheckFriendAction) {
            if (tempFriendList.contains(it)) {
                tempFriendList.remove(it)
            }
            friendListLivedata.value = tempFriendList
        }
    }

    fun getFriendList(searchStr: String) {
        viewModelScope.launch {
            getFriendListUseCase(searchStr)
                .collect {
                    _getFriendListAction.tryEmit(it)
                }
        }
    }

    fun addCheckFriend(friendTel: String) {
        _addCheckFriendAction.value = friendTel
    }

    fun removeCheckFriend(friendTel: String) {
        _removeCheckFriendAction.value = friendTel
    }


}