package com.aohaitong.http

import io.reactivex.Completable
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CustomRxJava2CallAdapterFactory : CallAdapter.Factory() {

    private var rxJava2CallAdapterFactory: RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.create()

    companion object {

        fun create(): CallAdapter.Factory = CustomRxJava2CallAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit)
            : CallAdapter<*, *>? {

        val rxJava2CallAdapter = rxJava2CallAdapterFactory.get(returnType, annotations, retrofit)

        rxJava2CallAdapter?.let {

            val rawType = getRawType(returnType)
            if (rawType == Completable::class.java) {
                return CustomRxJava2CallAdapter(
                    returnType,
                    it,
                    retrofit
                )
            }

            val type = getParameterUpperBound(0, returnType as ParameterizedType)
            return CustomRxJava2CallAdapter(
                type,
                it,
                retrofit
            )

        } ?: return null
    }
}