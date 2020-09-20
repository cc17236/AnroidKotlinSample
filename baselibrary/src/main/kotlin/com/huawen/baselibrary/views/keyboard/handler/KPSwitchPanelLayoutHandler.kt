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

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import com.huawen.baselibrary.R
import com.huawen.baselibrary.views.keyboard.IPanelConflictLayout
import com.huawen.baselibrary.views.keyboard.util.KPSKeyboardUtil
import com.huawen.baselibrary.views.keyboard.util.KPSwitchConflictUtil
import com.huawen.baselibrary.views.keyboard.util.ViewUtil
import com.huawen.baselibrary.views.keyboard.widget.panel.KPSwitchPanelFrameLayout
import com.huawen.baselibrary.views.keyboard.widget.panel.KPSwitchPanelLinearLayout
import com.huawen.baselibrary.views.keyboard.widget.panel.KPSwitchPanelRelativeLayout


/**
 * Created by Jacksgong on 3/30/16.
 *
 *
 * Keyboard->Panel: if the keyboard is showing, and `visibility` equal [View.VISIBLE]
 * then must by Keyboard->Panel, then show the panel after the keyboard is real gone, and will be
 * show by [IPanelConflictLayout.handleShow].
 * Easy and Safe way: [KPSwitchConflictUtil.showPanel].
 *
 *
 * Panel->Keyboard: do not need to invoke [View.setVisibility] to let the panel gone,
 * just show keyboard, the panel will be gone automatically when keyboard is real visible, and will
 * be hide by [.handleHide] -> [.processOnMeasure].
 * Easy and safe way:
 * [KPSwitchConflictUtil.showKeyboard].
 *
 * @see KPSwitchPanelFrameLayout
 *
 * @see KPSwitchPanelLinearLayout
 *
 * @see KPSwitchPanelRelativeLayout
 */
class KPSwitchPanelLayoutHandler(private val panelLayout: View, attrs: AttributeSet?) : IPanelConflictLayout {

    /**
     * The real status of Visible or not
     *
     * @see .handleHide
     * @see .filterSetVisibility
     */
    private var mIsHide = false

    /**
     * Whether ignore the recommend panel height, what would be equal to the height of keyboard in
     * most situations.
     *
     *
     * If the value is true, the panel's height will not be follow the height of the keyboard.
     *
     *
     * Default is false.
     *
     * @attr ref cn.dreamtobe.kpswitch.R.styleable#KPSwitchPanelLayout_ignore_recommend_height
     */
    private var mIgnoreRecommendHeight = false

    override var isKeyboardShowing = false


    override val isVisible: Boolean
        get() = !mIsHide

    init {
        if (attrs != null) {
            var typedArray: TypedArray? = null
            try {
                typedArray = panelLayout.context.obtainStyledAttributes(attrs, R.styleable.KPSwitchPanelLayout)
                mIgnoreRecommendHeight = typedArray!!.getBoolean(R.styleable.KPSwitchPanelLayout_ignore_recommend_height,
                        false)
            } finally {
                typedArray?.recycle()
            }
        }
    }

    /**
     * Filter the [View.setVisibility] for handling Keyboard->Panel.
     *
     * @param visibility [View.setVisibility]
     * @return whether filtered out or not.
     */
    fun filterSetVisibility(visibility: Int): Boolean {
        if (visibility == View.VISIBLE) {
            this.mIsHide = false
        }

        if (visibility == panelLayout.visibility) {
            return true
        }

        /**
         * For handling Keyboard->Panel.
         *
         * Will be handled on [KPSwitchRootLayoutHandler.handleBeforeMeasure] ->
         * [IPanelConflictLayout.handleShow] Delay show, until the
         * [KPSwitchRootLayoutHandler] discover
         * the size is changed by keyboard-show. And will show, on the next frame of the above
         * change discovery.
         */
        if (this.panelLayout.visibility != View.VISIBLE && visibility == View.VISIBLE) return false

        return if (isKeyboardShowing && visibility == View.VISIBLE) {
            true
        } else false

    }


    /**
     * Handle Panel -> Keyboard.
     *
     *
     * Process the [View.onMeasure] for handling the case of Panel->Keyboard.
     *
     * @return the processed measure-width-spec and measure-height-spec.
     * @see .handleHide
     */
    fun processOnMeasure(widthMeasureSpec_: Int, heightMeasureSpec_: Int): IntArray {
        var widthMeasureSpec = widthMeasureSpec_
        var heightMeasureSpec = heightMeasureSpec_
        if (mIsHide) {
            panelLayout.visibility = View.GONE
            /*
             * The current frame will be visible nil.
             */
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY)
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY)
        }

        val processedMeasureWHSpec = IntArray(2)
        processedMeasureWHSpec[0] = widthMeasureSpec
        processedMeasureWHSpec[1] = heightMeasureSpec

        return processedMeasureWHSpec
    }

    override fun handleShow() {
        throw IllegalAccessError("You can't invoke handle show in handler,"
                + " please instead of handling in the panel layout, maybe just need invoke "
                + "super.setVisibility(View.VISIBLE)")
    }

    /**
     * @see .processOnMeasure
     */
    override fun handleHide() {
        this.mIsHide = true
    }

    /**
     * @param recommendPanelHeight the recommend panel height, in the most situations, the value
     * would be equal to the height of the keyboard.
     * @see KPSKeyboardUtil.getValidPanelHeight
     */
    fun resetToRecommendPanelHeight(recommendPanelHeight: Int) {
        if (mIgnoreRecommendHeight) {
            // In this way, the panel's height will be not follow the height of keyboard.
            return
        }

        ViewUtil.refreshHeight(panelLayout, recommendPanelHeight)
    }

    /**
     * @param isIgnoreRecommendHeight Whether ignore the recommend panel height, what would be equal
     * to the height of keyboard in most situations.
     * @attr ref cn.dreamtobe.kpswitch.R.styleable#KPSwitchPanelLayout_ignore_recommend_height
     * @see .resetToRecommendPanelHeight
     */
    override fun setIgnoreRecommendHeight(isIgnoreRecommendHeight: Boolean) {
        this.mIgnoreRecommendHeight = isIgnoreRecommendHeight
    }
}
