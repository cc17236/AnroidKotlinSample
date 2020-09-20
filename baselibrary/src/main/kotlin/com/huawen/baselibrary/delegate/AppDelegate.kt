package com.huawen.baselibrary.delegate

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.huawen.baselibrary.schedule.host.BaseApplication
import java.util.*

interface AppDelegate : ActivitiesScheduler {

    fun getDbDelegate(): DatabaseDelegate?

    fun killSelf()

    fun <T : BaseApplication?> getApp(): T

    fun lowMemoryConfigure()

    fun createDelegateBind(app: Application, fun0: (() -> Stack<Activity>), dbDelegate: DatabaseDelegate? = null): BaseApplication?

    fun getContext(): Context?

    fun queryLaunch(packageName: String, activity: Activity?, packageManager: PackageManager?): Boolean





}

