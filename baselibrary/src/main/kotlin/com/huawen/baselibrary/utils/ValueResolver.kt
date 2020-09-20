package com.huawen.baselibrary.utils

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.util.Log

/** 根据百分比改变颜色透明度  */
inline fun Any.changeAlpha(color: Int, fraction: Float): Int {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    val alpha = (Color.alpha(color) * fraction).toInt()
    return Color.argb(alpha, red, green, blue)
}

inline fun Any.changeBetweenColor(startColor: Int, endColor: Int, fraction: Float): Int {
    val argbEvaluator = ArgbEvaluator()//渐变色计算类
    return (argbEvaluator.evaluate(fraction, startColor, endColor)) as Int
}

var lastTime: Long = 0L
fun isDoubleClick(): Boolean {
    val currenTime = System.currentTimeMillis()
    if ((currenTime - lastTime) <= 500) {
        lastTime = currenTime
        Log.v("currenTime", "拦截")
        return true
    }
    Log.v("currenTime", "过")
    lastTime = currenTime
    return false
}
