package com.aohaitong.http

import com.aohaitong.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

open class ApiClient private constructor(url: String? = null) {

    private var token: String? = null

    companion object : SingletonHolder<ApiClient, String>(::ApiClient) {

        private const val API_TIMEOUT = 10L // 10 minutes
        private const val API_READ_TIMEOUT = 1L
    }


    val httpService: HttpService
        get() {
            return createHttpService()
        }

    fun createHttpService(): HttpService {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.interceptors().add(Interceptor { chain ->
            val original = chain.request()
            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .method(original.method, original.body)
            if (token != null) {

            }
//            requestBuilder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5ODk2ODcsImlhdCI6MTYyODM5NzY4NywiaXNzIjoiZnItY2lyY2xlLWFwaS5jb20iLCJuYmYiOjE2MjgzOTc2ODcsImNvbnRleHQiOnsidXNlciI6eyJpZCI6MjI2NDN9fX0.DTTlXe74Z77ZaIWeK6szu7rpZGXhByKDVyJgjO4s4lE")
//            requestBuilder.addHeader("client-id","bYY8tDSJtsyNKO5apY6WO9GIxuCcBTaO")
//            requestBuilder.addHeader("Content-Type", "application/json")
//            requestBuilder.addHeader("accept-language",  "US-en")
//            requestBuilder.addHeader("Accept-Region", "US")
//            requestBuilder.addHeader("User-Agent", "Circle-JP/3.4.0 Android/10 HUAWEI LIO-AN00")
            val request = requestBuilder.build()
            chain.proceed(request)
        })

        val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLog())
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        httpClientBuilder.interceptors().add(httpLoggingInterceptor)

        val client = httpClientBuilder
            .connectTimeout(API_TIMEOUT, TimeUnit.MINUTES)
            .writeTimeout(API_TIMEOUT, TimeUnit.MINUTES)
            .readTimeout(API_READ_TIMEOUT, TimeUnit.MINUTES)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(NullOnEmptyConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CustomRxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .client(client)
            .build()
        return retrofit.create(HttpService::class.java)
    }
}

class HttpLog : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        Timber.tag("com.aohaitong.http.HttpLog").d("HttpLogInfo: $message")
    }
}

open class SingletonHolder<out T, in A>(private var creator: (A?) -> T) {

    @kotlin.jvm.Volatile
    private var instance: T? = null

    fun getInstance(arg: A?): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator(arg)
                instance = created
                created
            }
        }
    }

    /**
     * Clear current instance
     */
    fun clearInstance() {
        instance = null
    }
}
