package com.aohaitong.domain.common

import androidx.lifecycle.Observer

/**
 * 用于处理事件被消费后，后续监听不触发
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

/**
 * 脱壳，同 ResourceObserver
 */
class EventObserver<T>(
    private val onEventUnhandledContent: (T) -> Unit
) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}