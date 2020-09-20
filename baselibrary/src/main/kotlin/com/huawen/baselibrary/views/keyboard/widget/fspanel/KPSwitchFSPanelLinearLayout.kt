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
package com.huawen.baselibrary.views.keyboard.widget.fspanel

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Window
import android.widget.LinearLayout

import com.huawen.baselibrary.views.keyboard.IFSPanelConflictLayout
import com.huawen.baselibrary.views.keyboard.IPanelHeightTarget
import com.huawen.baselibrary.views.keyboard.handler.KPSwitchFSPanelLayoutHandler
import com.huawen.baselibrary.views.keyboard.util.KPSKeyboardUtil
import com.huawen.baselibrary.views.keyboard.util.ViewUtil

/**
 * Created by Jacksgong on 3/27/16.
 *
 *
 * The panel container linear layout for full-screen theme window, and this layout's height would
 * be always equal to the height of the keyboard.
 *
 *
 * For non-full-screen theme window, please use [KPSwitchPanelLinearLayout] instead.
 *
 * @see KPSKeyboardUtil.attach
 * @see .recordKeyboardStatus
 * @see KPSwitchFSPanelFrameLayout
 *
 * @see KPSwitchFSPanelRelativeLayout
 */
class KPSwitchFSPanelLinearLayout : LinearLayout, IPanelHeightTarget, IFSPanelConflictLayout {

    override var height: Int?=0

    private var panelHandler: KPSwitchFSPanelLayoutHandler? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,
                defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        panelHandler = KPSwitchFSPanelLayoutHandler(this)
    }

    override fun refreshHeight(panelHeight: Int) {
        ViewUtil.refreshHeight(this, panelHeight)
    }

    override fun onKeyboardShowing(showing: Boolean) {
        panelHandler?.onKeyboardShowing(showing)
    }


    override fun recordKeyboardStatus(window: Window) {
        panelHandler?.recordKeyboardStatus(window)
    }
}
