package com.huawen.baselibrary.utils.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object ApkUtil {

    // 版本名
    fun getVersionName(context: Context): String {
        return getPackageInfo(context)!!.versionName
    }

    // 版本号
    fun getVersionCode(context: Context): Int {
        return getPackageInfo(context)!!.versionCode
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        var pi: PackageInfo? = null
        try {
            val pm = context.packageManager
            pi = pm.getPackageInfo(context.packageName,
                    PackageManager.GET_CONFIGURATIONS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pi
    }
}
