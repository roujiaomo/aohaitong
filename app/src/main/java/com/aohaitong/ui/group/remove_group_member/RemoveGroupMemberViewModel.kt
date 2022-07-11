package com.aohaitong.ui.group.remove_group_member

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aohaitong.bean.entity.GroupFriendBean
import com.aohaitong.domain.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class RemoveGroupMemberViewModel @Inject constructor() : ViewModel() {

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

    fun addCheckFriend(friendTel: String) {
        _addCheckFriendAction.value = friendTel
    }

    fun removeCheckFriend(friendTel: String) {
        _removeCheckFriendAction.value = friendTel
    }


}