package com.aohaitong.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aohaitong.domain.common.ErrorResource
import com.aohaitong.domain.common.Resource
import com.aohaitong.domain.common.SuccessResource
import com.aohaitong.domain.usecase.PingIPUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val pingIPUseCase: PingIPUseCase
) : ViewModel() {

    private val _pingIp =
        MutableSharedFlow<Resource<Int>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val pingIp = _pingIp

    fun doPingIp() {
        viewModelScope.launch(context = IO) {
            pingIPUseCase(Unit)
                .collect {
                    when (it) {
                        is SuccessResource -> {
                            _pingIp.tryEmit(it)
                        }
                        is ErrorResource -> {
                        }
                        else -> {
                        }
                    }
                }
        }
    }
}