package com.aohaitong.domain

import com.aohaitong.domain.common.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*

/**
 * 使用说明：
 * 数据类从IO线程起，到flowOn回到自己期望的线程。
 */
abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: P): Flow<Resource<R>> {
        return execute(parameters)
            .map {
                Resource.success(it)
            }
            .onStart {
                emit(Resource.loading())
            }
            .catch { throwable -> emit(Resource.error(throwable)) }
            .flowOn(coroutineDispatcher)
    }

    operator fun invoke(parameters: P, unit: Unit) {
        execute(parameters)
            .map {
                Resource.success(true)
            }
            .onStart {
                emit(Resource.loading())
            }
            .catch { throwable -> emit(Resource.error(throwable)) }
            .flowOn(coroutineDispatcher)
    }


    protected abstract fun execute(parameters: P): Flow<R>
}