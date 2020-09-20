package com.huawen.baselibrary.schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import com.huawen.baselibrary.schedule.host.BaseActivityHost
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxAppCompatActivity
import java.lang.Exception


/**
 * @作者: #Administrator #
 *@日期: #2018/5/19 #
 *@时间: #2018年05月19日 21:47 #
 *@File:Kotlin Class
 */
abstract class BaseRxActivityHost : RxAppCompatActivity(), BaseActivityHost {
    private var activityEvent: ActivityEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        activityEvent = ActivityEvent.CREATE
        super.onCreate(savedInstanceState)
    }

    open fun getCurrentEvent(): ActivityEvent? {
        return activityEvent
    }

    override fun onStop() {
        activityEvent = ActivityEvent.STOP
        super.onStop()
    }

    override fun onPause() {
        activityEvent = ActivityEvent.PAUSE
        super.onPause()
    }

    override fun onDestroy() {
        activityEvent = ActivityEvent.DESTROY
        super.onDestroy()
        activityEvent = null
    }

    override fun onResume() {
        activityEvent = ActivityEvent.RESUME
        super.onResume()
    }

    override fun onStart() {
        activityEvent = ActivityEvent.START
        super.onStart()
    }

    /**
     * 每次启动activity都会调用此方法
     */
    @SuppressLint("RestrictedApi")
    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        try {
            if (checkRepeatedJump(intent)) {
                super.startActivityForResult(intent, requestCode, options)
            }
        }catch (e:Exception){

        }
    }

    private var mActivityJumpTag = "";        //activity跳转tag
    private var mClickTime: Long = 0               //activity跳转时间
    /**
     * 检查是否重复跳转，不需要则重写方法并返回true
     */
    protected fun checkRepeatedJump(intent: Intent?): Boolean {
        // 默认检查通过
        var result = true
        // 标记对象
        var tag = ""
        if (intent?.component != null) { // 显式跳转
            tag = intent.component?.className?:""
        } else if (intent?.action != null) { // 隐式跳转
            tag = intent.action?:""
        } else {
            return true
        }
        if (tag == mActivityJumpTag && mClickTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false
        }
        // 记录启动标记和时间
        mActivityJumpTag = tag
        mClickTime = SystemClock.uptimeMillis()
        return result
    }

}