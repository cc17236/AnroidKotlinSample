package com.huawen.baselibrary.utils

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils

internal object ProcessUtil {
    fun judgeMainProcess(context: Context): Boolean {
        val processName = getCurProcessName(context)
        return !TextUtils.isEmpty(processName) && processName == context.packageName
    }

    fun getCurProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val mActivityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in mActivityManager
                .runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }
}
