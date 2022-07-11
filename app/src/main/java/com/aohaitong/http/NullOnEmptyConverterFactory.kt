package com.aohaitong.http

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NullOnEmptyConverterFactory : Converter.Factory() {

    companion object {
        /**
         * Create instance
         */
        fun create(): Converter.Factory = NullOnEmptyConverterFactory()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, Any> {
        val nextResponseBodyConverter =
            retrofit.nextResponseBodyConverter<Any?>(this, type, annotations)

        return Converter {
            if (it.contentLength() != 0L) {
                nextResponseBodyConverter.convert(it)
            } else null
        }
    }
}