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

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log

/**
 * Created by Jacksgong on 3/26/16.
 *
 *
 * In order to avoid the layout of the Status bar.
 */
object StatusBarHeightUtil {

    private var init_STATUS = false
    private var init_Navigation = false
    private var statusBarHeight = 50
    private var navigationBarHeight = 0

    private val STATUS_BAR_DEF_PACKAGE = "android"
    private val STATUS_BAR_DEF_TYPE = "dimen"
    private val STATUS_BAR_NAME = "status_bar_height"
    private val NAVIGATION_BAR_NAME = "navigation_bar_height"

    @Synchronized
    fun getStatusBarHeight(context: Context): Int {
        if (!init_STATUS) {
            val resourceId = context.resources.getIdentifier(STATUS_BAR_NAME, STATUS_BAR_DEF_TYPE, STATUS_BAR_DEF_PACKAGE)
            if (resourceId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
                init_STATUS = true
                Log.d("StatusBarHeightUtil",
                        String.format("Get status bar height %d", statusBarHeight))
            }
        }
        return statusBarHeight
    }

    @Synchronized
    fun getNavigationBarHeight(context: Activity): Int {
        if (!init_Navigation) {
            val resourceId = context.resources.getIdentifier(NAVIGATION_BAR_NAME, STATUS_BAR_DEF_TYPE, STATUS_BAR_DEF_PACKAGE)
            if (resourceId > 0&&checkHasNavigationBar(context)) {
                navigationBarHeight = context.resources.getDimensionPixelSize(resourceId)
                init_Navigation = true
                Log.d("StatusBarHeightUtil",
                        String.format("Get navigation bar height %d", statusBarHeight))
            }
        }
        return navigationBarHeight
    }


    /**
     * 判断是否有NavigationBar
     *
     * @param activity
     * @return
     */
    fun checkHasNavigationBar(activity: Activity): Boolean {
        val windowManager = activity.getWindowManager();
        val d = windowManager.getDefaultDisplay();
        val realDisplayMetrics = DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }
        val realHeight = realDisplayMetrics.heightPixels;
        val realWidth = realDisplayMetrics.widthPixels;
        val displayMetrics = DisplayMetrics();
        d.getMetrics(displayMetrics);
        val displayHeight = displayMetrics.heightPixels;
        val displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }



}
