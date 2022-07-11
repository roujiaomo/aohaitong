package com.aohaitong.domain

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aohaitong.domain.common.Resource
import com.aohaitong.kt.util.observeOnUiThread
import io.reactivex.Single

/**
 * 应用于有返回值的
 */
abstract class SingleUseCase<in P, R> {


    @SuppressLint("CheckResult")
    operator fun invoke(requestParams: P, responseLiveData: MutableLiveData<Resource<R>>) {
        responseLiveData.postValue(Resource.loading())

        try {
            execute(requestParams)
                .observeOnUiThread()
                .subscribe(
                    { body ->
                        responseLiveData.postValue(Resource.success(body))
                    },
                    { throwable ->
                        responseLiveData.postValue(Resource.error(throwable))
                    }
                )
        } catch (e: Exception) {
            responseLiveData.postValue(Resource.error(e))
        }
    }

    operator fun invoke(requestParams: P): LiveData<Resource<R>> {
        val liveCallback: MutableLiveData<Resource<R>> = MutableLiveData()
        this(requestParams, liveCallback)
        return liveCallback
    }

    @Throws(RuntimeException::class)
    abstract fun execute(parameter: P): Single<R>
}

operator fun <R> SingleUseCase<Unit, R>.invoke(): LiveData<Resource<R>> = this(Unit)
operator fun <R> SingleUseCase<Unit, R>.invoke(result: MutableLiveData<Resource<R>>) =
    this(Unit, result)