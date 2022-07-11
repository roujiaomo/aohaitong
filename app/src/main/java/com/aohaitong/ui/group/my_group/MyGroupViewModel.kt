package com.aohaitong.ui.group.my_group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aohaitong.MyApplication
import com.aohaitong.bean.GroupBean
import com.aohaitong.domain.common.Resource
import com.aohaitong.domain.usecase.GetMyGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGroupViewModel @Inject constructor(
    private val getMyGroupUseCase: GetMyGroupUseCase
) : ViewModel() {

    private val _getGroupListAction = MutableSharedFlow<Resource<List<GroupBean>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getGroupListResponse = _getGroupListAction

    private val _onItemClickAction = MutableSharedFlow<GroupBean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val onToChatActivityAction = _onItemClickAction

    fun getMyGroup() {
        viewModelScope.launch {
            getMyGroupUseCase(MyApplication.TEL.toString())
                .collect {
                    _getGroupListAction.tryEmit(it)
                }
        }
    }

    fun onItemClick(groupBean: GroupBean) {
        _onItemClickAction.tryEmit(groupBean)
    }

}