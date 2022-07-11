package com.aohaitong.http

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FlowCallAdapter<T>(
    private val responseType: Type,
    private val retrofit: Retrofit
) : CallAdapter<T, Flow<T>> {

    override fun adapt(call: Call<T>): Flow<T> {
        return flow {

            emit(suspendCancellableCoroutine { continuation ->

                continuation.invokeOnCancellation {
                    call.cancel()
                }

                try {
                    val response = call.execute()

                    if (response.isSuccessful) {
                        response.body()?.let {
                            continuation.resume(it)
                        }
                    } else {

                        continuation.resumeWithException(
                            convertRetrofitExceptionToApiException(
                                HttpException(response),
                                retrofit
                            )
                        )
                    }
                } catch (throwable: Throwable) {
                    continuation.resumeWithException(
                        convertRetrofitExceptionToApiException(throwable, retrofit)
                    )
                }
            })
        }
    }

    override fun responseType() = responseType
}