package com.aohaitong.domain.usecase

import com.aohaitong.business.IPController
import com.aohaitong.constant.IPAddress
import com.aohaitong.constant.StatusConstant.*
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.FlowUseCase
import com.aohaitong.utils.PingUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PingIPUseCase @Inject constructor(@IoDispatcher dispatcher: CoroutineDispatcher) :
    FlowUseCase<Unit, Int>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Int> {
        IPController.CONNECT_TYPE = CONNECT_DISCONNECTED
        if (PingUtil.pingHost(IPAddress.getMqAddress().split(":")[0])) {
            IPController.IP = IPAddress.getMqAddress().split(":")[0]
            IPController.PORT = IPAddress.getMqAddress().split(":")[1]
            IPController.CONNECT_TYPE = CONNECT_MQ
            return flow { emit(CONNECT_MQ) }
        } else {
            if (PingUtil.pingHost(IPAddress.getSocketAddress().split(":")[0])) {
                IPController.IP = IPAddress.getSocketAddress().split(":")[0]
                IPController.PORT = IPAddress.getSocketAddress().split(":")[1]
                IPController.CONNECT_TYPE = CONNECT_SOCKET
                return flow {
                    emit(CONNECT_SOCKET)
                }
            }
        }
        return flow {
            emit(CONNECT_DISCONNECTED)
        }
    }
}