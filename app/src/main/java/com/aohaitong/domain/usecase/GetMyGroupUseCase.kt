package com.aohaitong.domain.usecase

import com.aohaitong.bean.GroupBean
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMyGroupUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val dataBaseRepository: DataBaseRepository,
) : FlowUseCase<String, List<GroupBean>>(dispatcher) {
    override fun execute(parameters: String): Flow<List<GroupBean>> {
        return flow {
            emit(dataBaseRepository.getMyGroupByOwnerTel(parameters))
        }
    }
}
