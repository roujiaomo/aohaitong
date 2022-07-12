package com.aohaitong.domain.usecase

import com.aohaitong.business.IPController
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.FlowUseCase
import com.aohaitong.kt.util.VersionUtil.isTestVersion
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PingIPUseCase @Inject constructor(@IoDispatcher dispatcher: CoroutineDispatcher) :
    FlowUseCase<Unit, Int>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Int> {
        if (isTestVersion()) {
            IPController.loadTestIp()
        } else {
            IPController.loadIp()
        }
        return flow {
            emit(IPController.CONNECT_TYPE)
        }
    }
}