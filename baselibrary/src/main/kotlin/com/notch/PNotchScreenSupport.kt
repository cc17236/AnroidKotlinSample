package com.notch

import android.graphics.Rect
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.huawen.baselibrary.views.keyboard.util.StatusBarHeightUtil
import org.jetbrains.anko.dip
import java.util.*

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 */
internal class PNotchScreenSupport : INotchScreenSupport {
    @RequiresApi(api = 28)
    override fun hasNotchInScreen(window: Window): Boolean {
        val decorView = window.decorView
        val windowInsets = decorView.rootWindowInsets
        if (windowInsets==null){
           val height= StatusBarHeightUtil.getStatusBarHeight(window.context)
           val normalHeight= window.context.dip(25)
            if (height-10>normalHeight){
                return true
            }
            return false
        }
        val dct = windowInsets.displayCutout
        return dct != null && (dct.safeInsetTop != 0
            || dct.safeInsetBottom != 0
            || dct.safeInsetLeft != 0
            || dct.safeInsetRight != 0)
    }

    @RequiresApi(api = 28)
    override fun getNotchSize(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val decorView = window.decorView
        val windowInsets = decorView.rootWindowInsets ?: return result
        val dct = windowInsets.displayCutout
        if (dct != null) {
            result.addAll(dct.boundingRects)
        }
        return result
    }

    @RequiresApi(api = 28)
    override fun setWindowLayoutAroundNotch(window: Window) {
        val attributes = window.attributes
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = attributes
    }

    @RequiresApi(api = 28)
    override fun setWindowLayoutBlockNotch(window: Window) {
        val attributes = window.attributes
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        window.attributes = attributes
    }
}
