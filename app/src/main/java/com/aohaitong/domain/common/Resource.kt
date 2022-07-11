package com.aohaitong.domain.common

import androidx.lifecycle.Observer

/**
 * 装饰返回值，处理请求不同返回状态
 * @param <T>
 */
sealed class Resource<out T> {

    companion object {

        fun <T> success(data: T): Resource<T> {
            return SuccessResource(data)
        }

        fun <T> error(throwable: Throwable): Resource<T> {
            return ErrorResource(throwable)
        }

        fun <T> loading(): Resource<T> {
            return LoadingResource
        }
    }

    val isLoading get() = this is LoadingResource

    fun getErrorIfExists() = if (this is ErrorResource) throwable else null

    override fun toString(): String {
        return when (this) {
            is SuccessResource<*> -> "Success[data=$data]"
            is ErrorResource -> "Error[throwable=$throwable]"
            LoadingResource -> "Loading"
        }
    }
}

object LoadingResource : Resource<Nothing>()
class ErrorResource(val throwable: Throwable) : Resource<Nothing>()
class SuccessResource<T>(val data: T) : Resource<T>()

/**
 * `true` if [Resource] is of type [SuccessResource] & holds non-null [SuccessResource.data].
 */
val Resource<*>.succeeded
    get() = this is SuccessResource && data != null

fun <T> Resource<T>.successOr(fallback: T): T {
    return (this as? SuccessResource<T>)?.data ?: fallback
}

/**
 * 脱壳: 应用于 observe中
 *
viewModel.data.observe(lifecycleOwner, object : ResourceObserver<List<Data>>() {

override fun onSuccess(data: List<Banner>) {}

override fun onError(throwable: Throwable) {}

override fun onLoading(){}
})

 */
abstract class ResourceObserver<T> : Observer<Resource<T>> {

    override fun onChanged(resource: Resource<T>?) {
        when (resource) {
            is SuccessResource -> onSuccess(resource.data)
            is ErrorResource -> onError(resource.throwable)
            is LoadingResource -> onLoading()
        }
    }

    abstract fun onSuccess(data: T)

    open fun onError(throwable: Throwable) {}

    open fun onLoading() {}
}