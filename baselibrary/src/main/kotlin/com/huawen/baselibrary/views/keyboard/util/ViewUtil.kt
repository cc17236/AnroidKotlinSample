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
package com.huawen.baselibrary.views.keyboard.util

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

/**
 * Created by Jacksgong on 3/28/16.
 *
 *
 * For wrap some utils for view.
 */
internal object ViewUtil {

    private val TAG = "ViewUtil"

    fun refreshHeight(view: View, aimHeight: Int): Boolean {
        if (view.isInEditMode) {
            return false
        }
        Log.d(TAG, String.format("refresh Height %d %d", view.height, aimHeight))

        if (view.height == aimHeight) {
            return false
        }

        if (Math.abs(view.height - aimHeight) == StatusBarHeightUtil.getStatusBarHeight(view.context)) {
            return false
        }

        val validPanelHeight = KPSKeyboardUtil.getValidPanelHeight(view.context)
        var layoutParams: ViewGroup.LayoutParams? = view.layoutParams
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    validPanelHeight)
            view.layoutParams = layoutParams
        } else {
            layoutParams.height = validPanelHeight
            view.requestLayout()
        }

        return true
    }

    fun isFullScreen(activity: Activity): Boolean {
        return activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun isTranslucentStatus(activity: Activity): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS != 0
        } else false
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    internal fun isFitsSystemWindows(activity: Activity): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0).fitsSystemWindows
        } else false

    }

}
