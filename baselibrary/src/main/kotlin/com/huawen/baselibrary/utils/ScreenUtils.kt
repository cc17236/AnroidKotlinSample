package com.huawen.baselibrary.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.view.WindowManager

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : 屏幕相关工具类
</pre> *
 */
class ScreenUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 获取屏幕的宽度（单位：px）
         *
         * @return 屏幕宽px
         */
        // 创建了一张白纸
        // 给白纸设置宽高
        val screenWidth: Int
            get() {
                val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dm)
                return dm.widthPixels
            }

        /**
         * 获取屏幕的高度（单位：px）
         *
         * @return 屏幕高px
         */
        // 创建了一张白纸
        // 给白纸设置宽高
        val screenHeight: Int
            get() {
                val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dm)
                return dm.heightPixels
            }

        /**
         * 设置屏幕为横屏
         *
         * 还有一种就是在Activity中加属性android:screenOrientation="landscape"
         *
         * 不设置Activity的android:configChanges时，切屏会重新调用各个生命周期，切横屏时会执行一次，切竖屏时会执行两次
         *
         * 设置Activity的android:configChanges="orientation"时，切屏还是会重新调用各个生命周期，切横、竖屏时只会执行一次
         *
         * 设置Activity的android:configChanges="orientation|keyboardHidden|screenSize"（4.0以上必须带最后一个参数）时
         * 切屏不会重新调用各个生命周期，只会执行onConfigurationChanged方法
         *
         * @param activity activity
         */
        fun setLandscape(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        /**
         * 设置屏幕为竖屏
         *
         * @param activity activity
         */
        fun setPortrait(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        /**
         * 判断是否横屏
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isLandscape: Boolean
            get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        /**
         * 判断是否竖屏
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isPortrait: Boolean
            get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        /**
         * 获取屏幕旋转角度
         *
         * @param activity activity
         * @return 屏幕旋转角度
         */
        fun getScreenRotation(activity: Activity): Int {
            when (activity.windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> return 0
                Surface.ROTATION_90 -> return 90
                Surface.ROTATION_180 -> return 180
                Surface.ROTATION_270 -> return 270
                else -> return 0
            }
        }

        /**
         * 获取当前屏幕截图，包含状态栏
         *
         * @param activity activity
         * @return Bitmap
         */
        fun captureWithStatusBar(activity: Activity): Bitmap {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bmp = view.drawingCache
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
            view.destroyDrawingCache()
            return ret
        }

        /**
         * 获取当前屏幕截图，不包含状态栏
         *
         * @param activity activity
         * @return Bitmap
         */
        fun captureWithoutStatusBar(activity: Activity): Bitmap {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bmp = view.drawingCache
            val statusBarHeight = BarUtils.getStatusBarHeight(activity)
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight)
            view.destroyDrawingCache()
            return ret
        }

        /**
         * 判断是否锁屏
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isScreenLock: Boolean
            get() {
                val km = Utils.getContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                return km.inKeyguardRestrictedInputMode()
            }

        /**
         * 获取进入休眠时长
         *
         * @return 进入休眠时长，报错返回-123
         */
        /**
         * 设置进入休眠时长
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         *
         * @param duration 时长
         */
        var sleepDuration: Int
            get() {
                try {
                    return Settings.System.getInt(Utils.getContext().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                    return -123
                }

            }
            set(duration) {
                Settings.System.putInt(Utils.getContext().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, duration)
            }
    }
}