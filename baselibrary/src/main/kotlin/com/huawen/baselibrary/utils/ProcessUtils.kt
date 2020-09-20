package com.huawen.baselibrary.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import java.util.*


/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/10/18
 * desc  : 进程相关工具类
</pre> *
 */
class ProcessUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 获取前台线程包名
         *
         * 当不是查看当前App，且SDK大于21时，
         * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
         *
         * @return 前台应用包名
         */
        // 有"有权查看使用权限的应用"选项
        val foregroundProcessName: String?
            get() {
                val manager = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val pInfo = manager.runningAppProcesses
                if (pInfo != null && pInfo.size != 0) {
                    for (aInfo in pInfo) {
                        if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            return aInfo.processName
                        }
                    }
                }
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val packageManager = Utils.getContext().packageManager
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    println(list)
                    if (list.size > 0) {
                        try {
                            val info = packageManager.getApplicationInfo(Utils.getContext().packageName, 0)
                            val aom = Utils.getContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                            if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) != AppOpsManager.MODE_ALLOWED) {
                                Utils.getContext().startActivity(intent)
                            }
                            if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) != AppOpsManager.MODE_ALLOWED) {
                                LogUtils.d("getForegroundApp", "没有打开\"有权查看使用权限的应用\"选项")
                                return null
                            }
                            val usageStatsManager = Utils.getContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                            val endTime = System.currentTimeMillis()
                            val beginTime = endTime - 86400000 * 7
                            val usageStatses = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime)
                            if (usageStatses == null || usageStatses.isEmpty()) return null
                            var recentStats: UsageStats? = null
                            for (usageStats in usageStatses) {
                                if (recentStats == null || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                                    recentStats = usageStats
                                }
                            }
                            return if (recentStats == null) null else recentStats.packageName
                        } catch (e: PackageManager.NameNotFoundException) {
                            e.printStackTrace()
                        }

                    } else {
                        LogUtils.d("getForegroundApp", "无\"有权查看使用权限的应用\"选项")
                    }
                }
                return null
            }

        /**
         * 获取后台服务进程
         *
         * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
         *
         * @return 后台服务进程
         */
        val allBackgroundProcesses: Set<String>
            get() {
                val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = am.runningAppProcesses
                val set = HashSet<String>()
                for (aInfo in info) {
                    Collections.addAll(set, *aInfo.pkgList)
                }
                return set
            }

        @SuppressLint("MissingPermission")
                /**
         * 杀死所有的后台服务进程
         *
         * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
         *
         * @return 被暂时杀死的服务集合
         */
        fun killAllBackgroundProcesses(): Set<String> {
            val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var info: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
            val set = HashSet<String>()
            for (aInfo in info) {
                for (pkg in aInfo.pkgList) {
                    am.killBackgroundProcesses(pkg)
                    set.add(pkg)
                }
            }
            info = am.runningAppProcesses
            for (aInfo in info) {
                for (pkg in aInfo.pkgList) {
                    set.remove(pkg)
                }
            }
            return set
        }

        @SuppressLint("MissingPermission")
                /**
         * 杀死后台服务进程
         *
         * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
         *
         * @param packageName 包名
         * @return `true`: 杀死成功<br></br>`false`: 杀死失败
         */
        fun killBackgroundProcesses(packageName: String): Boolean {
            val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var info: List<ActivityManager.RunningAppProcessInfo>? = am.runningAppProcesses
            if (info == null || info.size == 0) return true
            for (aInfo in info) {
                if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                    am.killBackgroundProcesses(packageName)
                }
            }
            info = am.runningAppProcesses
            if (info == null || info.size == 0) return true
            for (aInfo in info) {
                if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                    return false
                }
            }
            return true
        }


        fun isRunningTaskExist(context: Context, processName: String): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processList = am.runningAppProcesses
            for (info in processList) {
                if (info.processName == processName) {
                    return true
                }
            }
            return false
        }

    }
}
