package com.huawen.baselibrary.schedule

import android.os.Bundle
import com.huawen.baselibrary.schedule.host.BaseFragmentHost
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxFragment


/**
 * @作者: #Administrator #
 *@日期: #2018/5/19 #
 *@时间: #2018年05月19日 21:54 #
 *@File:Kotlin Class
 */
abstract class BaseRxFragmentHost : RxFragment(), BaseFragmentHost {
    init {
        if (arguments == null)
            arguments = Bundle()
    }

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


}