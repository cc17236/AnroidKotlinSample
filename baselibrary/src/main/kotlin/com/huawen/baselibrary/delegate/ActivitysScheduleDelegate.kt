package com.huawen.baselibrary.delegate

import android.app.Activity
import java.util.*


/**
 * @作者: #Administrator #
 *@日期: #2018/4/28 #
 *@时间: #2018年04月28日 14:49 #
 *@File:Kotlin Interface
 */
interface ActivitiesScheduler {
    fun <T : Activity> queryStack(clazz: Class<T>): T?
    fun cleanStack()
    fun finishStack(clazz: Class<out Activity>)
    fun finishAffinityStack(clazz: Class<out Activity>)
    fun cleanStackWithoutInstance(vararg clazz: Class<out Activity>?,retry:Boolean=false)
    fun cleanStackWithoutHashStack(clazz: Class<out Activity>,hashCode:Int)
    fun getStack(): Stack<Activity>?
    fun cleanOpen(clazz: Class<out Activity>,activity: Activity?=null)
    fun stackSize(): Int
    fun ifRequiredNecessaryInheritSplashHost(): Boolean {
        return false
    }
}