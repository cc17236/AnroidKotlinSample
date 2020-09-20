package com.notch

import android.graphics.Rect
import android.view.Window
import java.util.*

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 */
internal class DefaultNotchScreenSupport : INotchScreenSupport {
    override fun hasNotchInScreen(window: Window): Boolean {
        val decorView = window.decorView
        if (android.os.Build.VERSION.SDK_INT >= 28){
            val windowInsets = decorView.rootWindowInsets
            if (windowInsets != null)
                return windowInsets.displayCutout !=null
        }
        return false
    }

    override fun getNotchSize(window: Window): List<Rect> {
        return ArrayList()
    }

    override fun setWindowLayoutAroundNotch(window: Window) {}

    override fun setWindowLayoutBlockNotch(window: Window) {}
}
