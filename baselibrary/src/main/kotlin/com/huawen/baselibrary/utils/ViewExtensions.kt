package com.huawen.baselibrary.utils

import android.app.Activity
import android.content.ContextWrapper
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View

/**
 * try get host activity from view.
 * views hosted on floating window like dialog and toast will sure return null.
 * @return host activity; or null if not available
 */
inline fun View.getActivityFromView(): Activity? {
    var context = getContext()
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = (context as ContextWrapper).baseContext
    }
    return null
}

inline fun View.getFragmentFromView(): androidx.fragment.app.Fragment? {
    var context = getContext()
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            val f = context.supportFragmentManager?.fragments
            if (f != null && f.size > 0) {
                loop@ f.forEach {
                    if (it != null && it.isVisible) {
                        return it
                    }
                }
            }
            return null
        }
        context = (context as ContextWrapper).baseContext
    }
    return null
}

/**
 * 唯一编号
 * 包名+activity类名
 * 用于存储最后刷新时间
 */
inline fun View.getUniqueComponentName(): String {
    var componentName = ""
    val activity = getActivityFromView()
    componentName = activity?.componentName?.className ?: ""
    return componentName
}

/**
 * 唯一编号
 * 包名+activity类名+fragment路径+fragment类名+fragment在activity中的索引位置
 * 用于存储最后刷新时间
 */
inline fun View.getUniqueComponentNameWithFragment(): String {
    var componentName = ""
    val activity = getActivityFromView()
    val fragment = getFragmentFromView()

    componentName = activity?.componentName?.className ?: ""
    var fragmentName = fragment?.javaClass?.canonicalName ?: ""
    if (activity != null && activity is AppCompatActivity && fragment != null) {
        val idx = activity.indexOfFragment(fragment)
        fragmentName += "index$idx"
    }
    componentName += fragmentName
    return componentName
}