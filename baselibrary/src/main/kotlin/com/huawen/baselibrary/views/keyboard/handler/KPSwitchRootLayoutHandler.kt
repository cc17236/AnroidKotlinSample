/*
 * Copyright (C) 2015-2017 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawen.baselibrary.views.keyboard.handler

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.huawen.baselibrary.schedule.host.BaseApplication
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.WindowFrameCompat
import com.huawen.baselibrary.views.keyboard.IPanelConflictLayout
import com.huawen.baselibrary.views.keyboard.util.KPSKeyboardUtil
import com.huawen.baselibrary.views.keyboard.util.StatusBarHeightUtil
import com.huawen.baselibrary.views.keyboard.util.ViewUtil
import com.huawen.baselibrary.views.keyboard.widget.fspanel.KPSwitchFSPanelRelativeLayout
import com.huawen.baselibrary.views.keyboard.widget.root.KPSwitchRootFrameLayout
import com.huawen.baselibrary.views.keyboard.widget.root.KPSwitchRootLinearLayout
import com.huawen.baselibrary.views.keyboard.widget.root.KPSwitchRootRelativeLayout


/**
 * Created by Jacksgong on 3/30/16.
 *
 * @see KPSwitchRootFrameLayout
 *
 * @see KPSwitchRootLinearLayout
 *
 * @see KPSwitchRootRelativeLayout
 */
class KPSwitchRootLayoutHandler(private val mTargetRootView: View, private val forceOverlapping: Boolean = false) {

    private var mOldHeight = -1

    private val mStatusBarHeight: Int
    private val mIsTranslucentStatus: Boolean

    private var mPanelLayout: IPanelConflictLayout? = null

    init {
        this.mStatusBarHeight = StatusBarHeightUtil.getStatusBarHeight(mTargetRootView.context)
        val activity = mTargetRootView.context as Activity
        this.mIsTranslucentStatus = ViewUtil.isTranslucentStatus(activity)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun handleBeforeMeasure(width: Int, height: Int) {
        var height_ = height
        // 由当前布局被键盘挤压，获知，由于键盘的活动，导致布局将要发生变化。

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && (if (forceOverlapping) true else mIsTranslucentStatus && mTargetRootView.fitsSystemWindows)) {
            // In this case, the height is always the same one, so, we have to calculate below.
            val rect = Rect()
            WindowFrameCompat.parse(mTargetRootView, rect)
            var space = rect.bottom - rect.top
            if (forceOverlapping) {
                space += mStatusBarHeight
            }
            height_ = space
        }

        Log.d(TAG, "onMeasure, width: $width height: $height_")
        if (height_ < 0) {
            return
        }

        if (mOldHeight < 0) {
            mOldHeight = height_
            return
        }

        val offset = mOldHeight - height_

        if (offset == 0) {
            Log.d(TAG, "$offset == 0 break;")
            return
        }

        if (Math.abs(offset) == mStatusBarHeight) {
            Log.w(TAG, String.format("offset just equal statusBar height %d", offset))
            // 极有可能是 相对本页面的二级页面的主题是全屏&是透明，但是本页面不是全屏，因此会有status bar的布局变化差异，进行调过
            // 极有可能是 该布局采用了透明的背景(windowIsTranslucent=true)，而背后的布局`full screen`为false，
            // 因此有可能第一次绘制时没有attach上status bar，而第二次status bar attach上去，导致了这个变化。
            return
        }

        mOldHeight = height_
        val panel = getPanelLayout(mTargetRootView)

        if (panel == null) {
            Log.w(TAG, "can't find the valid panel conflict layout, give up!")
            return
        }

        // 检测到布局变化非键盘引起
        if (Math.abs(offset) < KPSKeyboardUtil.getMinKeyboardHeight(mTargetRootView.context)) {
            Log.w(TAG, "system bottom-menu-bar(such as HuaWei Mate7) causes layout changed")
            return
        }

        if (offset > 0) {
            //键盘弹起 (offset > 0，高度变小)
            panel.handleHide()
        } else if (panel.isKeyboardShowing && panel.isVisible) {
            // 1. 总得来说，在监听到键盘已经显示的前提下，键盘收回才是有效有意义的。
            // 2. 修复在Android L下使用V7.Theme.AppCompat主题，进入Activity，默认弹起面板bug，
            // 第2点的bug出现原因:在Android L下使用V7.Theme.AppCompat主题，并且
            // 不使用系统的ActionBar/ToolBar，V7.Theme.AppCompat主题,还是会先默认绘制一帧默认ActionBar，
            // 然后再将他去掉（略无语）
            //键盘收回 (offset < 0，高度变大)

            // the panel is showing/will showing
            panel.handleShow()
        }
    }

    private fun getPanelLayout(view: View): IPanelConflictLayout? {
        if (mPanelLayout != null) {
            return mPanelLayout
        }

        mPanelLayout = deepQuery(view)
        return mPanelLayout
    }

    fun deepQuery(view: View?): IPanelConflictLayout? {
        if (view is IPanelConflictLayout) {
            return view
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                val v = deepQuery(child)
                if (v != null) {
                    return v
                }
            }
        }
        return null
    }

    companion object {
        private val TAG = "KPSRootLayoutHandler"
    }
}
