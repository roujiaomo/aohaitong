package com.aohaitong.http

import io.reactivex.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class CustomRxJava2CallAdapter<R>(
    private val type: Type,
    private val rxJava2CallAdapter: CallAdapter<R, *>,
    private val retrofit: Retrofit

) : CallAdapter<R, Any> {

    override fun adapt(call: Call<R>): Any {

        return when (val adapter = rxJava2CallAdapter.adapt(call)) {
            is Maybe<*> -> adapter.map(this::handleCustomError)
                .onErrorResumeNext(this::handleMaybeRetrofitError)
            is Single<*> -> adapter.map(this::handleCustomError)
                .onErrorResumeNext(this::handleSingleRetrofitError)
            is Flowable<*> -> adapter.map(this::handleCustomError)
                .onErrorResumeNext(this::handleFlowableRetrofitError)
            is Observable<*> -> adapter.map(this::handleCustomError)
                .onErrorResumeNext(this::handleObservableRetrofitError)
            is Completable -> adapter.onErrorResumeNext(this::handleCompletableRetrofitError)
            else -> adapter
        }
    }

    override fun responseType(): Type = type

    private fun createExceptionForSuccessResponse(response: Any?): Throwable? = null

    private fun handleCustomError(response: Any): Any {

        createExceptionForSuccessResponse(response)?.let {
            return it
        }

        return response
    }

    private fun handleMaybeRetrofitError(throwable: Throwable): MaybeSource<R> =
        MaybeSubject.error {
            convertRetrofitExceptionToApiException(throwable, retrofit)
        }

    private fun handleSingleRetrofitError(throwable: Throwable): SingleSource<R> =
        SingleSubject.error {
            convertRetrofitExceptionToApiException(throwable, retrofit)
        }

    private fun handleFlowableRetrofitError(throwable: Throwable): Flowable<R> = Flowable.error {
        convertRetrofitExceptionToApiException(throwable, retrofit)
    }

    private fun handleObservableRetrofitError(throwable: Throwable): ObservableSource<R> =
        Observable.error {
            convertRetrofitExceptionToApiException(throwable, retrofit)
        }

    private fun handleCompletableRetrofitError(throwable: Throwable): CompletableSource =
        CompletableSubject.error {
            convertRetrofitExceptionToApiException(throwable, retrofit)
        }
}