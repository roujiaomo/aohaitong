package com.aohaitong.kt.common

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ActivityAutoClearedValue<T : Any>(val activity: ComponentActivity) :
    ReadWriteProperty<ComponentActivity, T> {

    private var _value: T? = null

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                owner.lifecycle.addObserver((object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        _value = null
                    }
                }))
            }
        })
    }

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )
    }

    override fun setValue(thisRef: ComponentActivity, property: KProperty<*>, value: T) {
        _value = value
    }
}

/**
 * Creates an [AutoClearedValue] associated with this fragment.
 */
fun <T : Any> ComponentActivity.autoCleared() = ActivityAutoClearedValue<T>(this)