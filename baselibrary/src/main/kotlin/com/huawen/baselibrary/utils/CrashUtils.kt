package com.huawen.baselibrary.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/27
 * desc  : 崩溃相关工具类
</pre> *
 */
class CrashUtils private constructor() : UncaughtExceptionHandler {

    private var mHandler: UncaughtExceptionHandler? = null

    private var mInitialized: Boolean = false
    private var crashDir: String? = null
    private var versionName: String? = null
    private var versionCode: Int = 0

    /**
     * 获取崩溃头
     *
     * @return 崩溃头
     */
    private// 设备厂商
    // 设备型号
    // 系统版本
    // SDK版本
    val crashHead: String
        get() = "\n************* Crash Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +
                "\nDevice Model       : " + Build.MODEL +
                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n"

    /**
     * 初始化
     *
     * @return `true`: 成功<br></br>`false`: 失败
     */
    fun init(): Boolean {
        if (mInitialized) return true
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val baseCache = Utils.getContext().externalCacheDir ?: return false
            crashDir = baseCache.path + File.separator + "crash" + File.separator
        } else {
            val baseCache = Utils.getContext().cacheDir ?: return false
            crashDir = baseCache.path + File.separator + "crash" + File.separator
        }
        try {
            val pi = Utils.getContext().packageManager.getPackageInfo(Utils.getContext().packageName, 0)
            versionName = pi.versionName
            versionCode = pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }

        mHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        return {mInitialized = true;mInitialized}()
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val now = SimpleDateFormat("yyMMdd HH-mm-ss", Locale.getDefault()).format(Date())
        val fullPath = "$crashDir$now.txt"
        if (!createOrExistsFile(fullPath)) return
        Thread(Runnable {
            var pw: PrintWriter? = null
            try {
                pw = PrintWriter(FileWriter(fullPath, false))
                pw.write(crashHead)
                throwable.printStackTrace(pw)
                var cause: Throwable? = throwable.cause
                while (cause != null) {
                    cause.printStackTrace(pw)
                    cause = cause.cause
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.closeIO(pw)
            }
        }).start()
        if (mHandler != null) {
            mHandler!!.uncaughtException(thread, throwable)
        }
    }

    companion object {

        @Volatile
        private var mInstance: CrashUtils? = null

        /**
         * 获取单例
         *
         * 在Application中初始化`CrashUtils.getInstance().init(this);`
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
         *
         * @return 单例
         */
        val instance: CrashUtils?
            get() {
                if (mInstance == null) {
                    synchronized(CrashUtils::class.java) {
                        if (mInstance == null) {
                            mInstance = CrashUtils()
                        }
                    }
                }
                return mInstance
            }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功
         *
         * @param filePath 文件路径
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        private fun createOrExistsFile(filePath: String): Boolean {
            val file = File(filePath)
            // 如果存在，是文件则返回true，是目录则返回false
            if (file.exists()) return file.isFile
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }


        /**
         * 判断目录是否存在，不存在则判断是否创建成功
         *
         * @param file 文件
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        private fun createOrExistsDir(file: File?): Boolean {
            // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }
    }
}
