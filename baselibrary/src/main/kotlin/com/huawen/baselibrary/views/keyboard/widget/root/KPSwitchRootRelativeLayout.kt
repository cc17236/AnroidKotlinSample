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
package com.huawen.baselibrary.views.keyboard.widget.root

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

import com.huawen.baselibrary.views.keyboard.handler.KPSwitchRootLayoutHandler

/**
 * Created by Jacksgong on 3/30/16.
 *
 *
 * To keep watch on the keyboard status before occur layout-conflict.
 *
 *
 * This layout must be the root layout in your Activity. In other words, must be the
 * child of content view.
 *
 *
 * Resolve the layout-conflict from switching the keyboard and the Panel.
 *
 * @see KPSwitchRootLinearLayout
 *
 * @see KPSwitchRootFrameLayout
 *
 * @see KPSwitchPanelLinearLayout
 */
class KPSwitchRootRelativeLayout : RelativeLayout {

    private var conflictHandler: KPSwitchRootLayoutHandler? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,
                defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        conflictHandler = KPSwitchRootLayoutHandler(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        conflictHandler?.handleBeforeMeasure(View.MeasureSpec.getSize(widthMeasureSpec),
                View.MeasureSpec.getSize(heightMeasureSpec))
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
