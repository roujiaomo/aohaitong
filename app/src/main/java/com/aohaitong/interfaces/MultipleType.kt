package com.example.kotlindemo.interfaces

/**
 *  多布局条目类型
 */

interface MultipleType<in T> {
    /**
     * 返回值为布局Id 也为布局类型
     */
    fun getLayoutId(item: T, position: Int): Int
}
