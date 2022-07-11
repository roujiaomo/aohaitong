package com.aohaitong.kt.util

import android.graphics.Point
import android.view.View
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun android.view.View.onClick(
    context: CoroutineContext = Dispatchers.Main,
    handler: suspend CoroutineScope.(v: android.view.View?) -> Unit
) {
    setOnClickListener { v ->
        GlobalScope.launch(context, CoroutineStart.DEFAULT) {
            handler(v)
        }
    }
}

internal fun View.onClickWithAvoidRapidAction(delayTime: Int, click: () -> Unit) {
    onClick {
        AvoidRapidAction.action(delayTime, click)
    }
}

internal fun View.onClickWithAvoidRapidAction(click: () -> Unit) {
    onClick {
        AvoidRapidAction.action(click)
    }
}

//internal fun View.isYLessThanScreenHeight(appBarHeight: Int): Boolean {
//    val viewXY = IntArray(2)
//    getLocationInWindow(viewXY)
//    return viewXY[1] <= (context.getHeightScreen() - appBarHeight)
//}
//
//internal fun View.isEndYLessThanScreenHeight(appBarHeight: Int): Boolean {
//    val viewXY = IntArray(2)
//    getLocationInWindow(viewXY)
//    return viewXY[1] + height <= (context.getHeightScreen() - appBarHeight)
//}

internal fun View.isViewOverlapping(secondView: View): Boolean {
    val vertices = mutableListOf<Point>()
    val baseView = if (width * height >= secondView.width * secondView.height) {
        vertices.add(Point(secondView.x.toInt(), secondView.y.toInt()))
        vertices.add(Point(secondView.x.toInt() + width, secondView.y.toInt()))
        vertices.add(Point(secondView.x.toInt(), secondView.y.toInt() + secondView.height))
        vertices.add(
            Point(
                secondView.x.toInt() + secondView.width,
                secondView.y.toInt() + secondView.height
            )
        )
        this
    } else {
        vertices.add(Point(x.toInt(), y.toInt()))
        vertices.add(Point(x.toInt() + width, y.toInt()))
        vertices.add(Point(x.toInt(), y.toInt() + height))
        vertices.add(Point(x.toInt() + width, y.toInt() + height))
        secondView
    }
    vertices.forEach {
        if (it.isInsideView(baseView)) {
            return true
        }
    }
    return false
}

private fun Point.isInsideView(view: View) =
    x >= view.x && x <= view.x + view.width && y >= view.y && y <= view.y + view.height
