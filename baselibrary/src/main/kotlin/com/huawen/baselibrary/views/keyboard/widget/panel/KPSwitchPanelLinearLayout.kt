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
package com.huawen.baselibrary.views.keyboard.widget.panel

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

import com.huawen.baselibrary.views.keyboard.IPanelConflictLayout
import com.huawen.baselibrary.views.keyboard.IPanelHeightTarget
import com.huawen.baselibrary.views.keyboard.handler.KPSwitchPanelLayoutHandler

/**
 * Created by Jacksgong on 9/1/15.
 *
 *
 * The panel container linear layout.
 * Resolve the layout-conflict from switching the keyboard and the Panel.
 *
 *
 * For full-screen theme window, please use [KPSwitchFSPanelLinearLayout] instead.
 *
 * @see KPSwitchPanelFrameLayout
 *
 * @see KPSwitchPanelRelativeLayout
 *
 * @see KPSwitchPanelLayoutHandler
 */
class KPSwitchPanelLinearLayout : LinearLayout, IPanelHeightTarget, IPanelConflictLayout {
    override var height: Int? = 0


    private var panelLayoutHandler: KPSwitchPanelLayoutHandler? = null

    override val isKeyboardShowing: Boolean
        get() = panelLayoutHandler?.isKeyboardShowing ?: false

    override val isVisible: Boolean
        get() = panelLayoutHandler?.isVisible ?: false

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        panelLayoutHandler = KPSwitchPanelLayoutHandler(this, attrs)
    }

    override fun refreshHeight(panelHeight: Int) {
        height=panelHeight
        panelLayoutHandler?.resetToRecommendPanelHeight(panelHeight)
    }

    override fun onKeyboardShowing(showing: Boolean) {
        panelLayoutHandler?.isKeyboardShowing = (showing)
    }

    override fun setVisibility(visibility: Int) {
        if (panelLayoutHandler?.filterSetVisibility(visibility) == true) {
            return
        }
        super.setVisibility(visibility)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val processedMeasureWHSpec = panelLayoutHandler?.processOnMeasure(widthMeasureSpec,
                heightMeasureSpec) ?: intArrayOf(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(processedMeasureWHSpec[0], processedMeasureWHSpec[1])
    }


    override fun handleShow() {
        super.setVisibility(View.VISIBLE)
    }


    override fun handleHide() {
        panelLayoutHandler?.handleHide()
    }

    override fun setIgnoreRecommendHeight(isIgnoreRecommendHeight: Boolean) {
        panelLayoutHandler?.setIgnoreRecommendHeight(isIgnoreRecommendHeight)
    }

}
