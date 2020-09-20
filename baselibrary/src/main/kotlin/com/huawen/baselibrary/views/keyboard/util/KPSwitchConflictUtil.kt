/*
 * Copyright (C) 2015-2017 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawen.baselibrary.views.keyboard.util

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import com.huawen.baselibrary.views.keyboard.IPanelConflictLayout

/**
 * Created by Jacksgong on 3/30/16.
 *
 *
 * This util will help you control your panel and keyboard easily and exactly with
 * non-layout-conflict.
 *
 *
 * This util just support the application layer encapsulation, more detail for how to resolve
 * the layout-conflict please Ref  [KPSwitchRootLayoutHandler]、
 * [KPSwitchPanelLayoutHandler]、[KPSwitchFSPanelLayoutHandler]
 *
 *
 * Any problems: https://github.com/Jacksgong/JKeyboardPanelSwitch
 *
 * @see KPSwitchRootLayoutHandler
 *
 * @see KPSwitchPanelLayoutHandler
 *
 * @see KPSwitchFSPanelLayoutHandler
 */
object KPSwitchConflictUtil {

    /**
     * Attach the action of `switchPanelKeyboardBtn` and the `focusView` to
     * non-layout-conflict.
     *
     *
     * You do not have to use this method to attach non-layout-conflict, in other words, you can
     * attach the action by yourself with invoke methods manually: [.showPanel]、
     * [.showKeyboard]、[.hidePanelAndKeyboard], and in the case of
     * don't invoke this method to attach, and if your activity with the fullscreen-theme, please
     * ensure your panel layout is [View.INVISIBLE] before the keyboard is going to show.
     *
     * @param panelLayout            the layout of panel.
     * @param switchPanelKeyboardBtn the view will be used to trigger switching between the panel
     * and the keyboard.
     * @param focusView              the view will be focused or lose the focus.
     * @param switchClickListener    the click listener is used to listening the click event for
     * `switchPanelKeyboardBtn`.
     * @see .attach
     */
    fun attach(panelLayout: View,
            /* Nullable **/switchPanelKeyboardBtn: View?,
            /* Nullable **/focusView: View?,
               forceOverlapping: Boolean = false,
            /* Nullable **/switchClickListener: SwitchClickListener? = null,stateBlock:(()->Boolean)?=null) {
        if (focusView == null) return
        val activity = panelLayout.context as Activity

        switchPanelKeyboardBtn?.setOnClickListener { v ->
            val switchToPanel = switchPanelAndKeyboard(panelLayout, focusView,forceOverlapping,stateBlock = stateBlock)
            switchClickListener?.onClickSwitch(v, switchToPanel)
        }

        if (forceOverlapping||isHandleByPlaceholder(activity)) {
            focusView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    /*
                         * Show the fake empty keyboard-same-height panel to fix the conflict when
                         * keyboard going to show.
                         * @see KPSwitchConflictUtil#showKeyboard(View, View)
                         */
                    panelLayout.visibility = View.INVISIBLE
                }
                return@setOnTouchListener false
            }
        }
    }

    /**
     * The same to [.attach].
     */
    fun attach(panelLayout: View,
               focusView: View,
               vararg subPanelAndTriggers: SubPanelAndTrigger) {
        attach(panelLayout, focusView, null, *subPanelAndTriggers)
    }

    /**
     * If you have multiple sub-panels in the `panelLayout`, you can use this method to simply
     * attach them to non-layout-conflict. otherwise you can use [.attach]
     * or [.attach].
     *
     * @param panelLayout         the layout of panel.
     * @param focusView           the view will be focused or lose the focus.
     * @param switchClickListener the listener is used to listening whether the panel is showing or
     * keyboard is showing with toggle the panel/keyboard state.
     * @param subPanelAndTriggers the array of the trigger-toggle-view and
     * the sub-panel which bound trigger-toggle-view.
     */
    fun attach(panelLayout: View,
               focusView: View,
               /** Nullable  */
               switchClickListener: SwitchClickListener?,
               vararg subPanelAndTriggers: SubPanelAndTrigger) {
        val activity = panelLayout.context as Activity

        for (subPanelAndTrigger in subPanelAndTriggers) {

            bindSubPanel(subPanelAndTrigger, subPanelAndTriggers,
                    focusView, panelLayout, switchClickListener)
        }

        if (KPSwitchConflictUtil.isHandleByPlaceholder(activity)) {
            focusView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    /**
                     * Show the fake empty keyboard-same-height panel to fix the conflict when
                     * keyboard going to show.
                     * @see KPSwitchConflictUtil.showKeyboard
                     */
                    /**
                     * Show the fake empty keyboard-same-height panel to fix the conflict when
                     * keyboard going to show.
                     * @see KPSwitchConflictUtil.showKeyboard
                     */
                    panelLayout.visibility = View.INVISIBLE
                }
                false
            }
        }
    }

    /**
     * @see .attach
     */
    class SubPanelAndTrigger(
            /**
             * The sub-panel view is the child of panel-layout.
             */
            internal val subPanelView: View,
            /**
             * The trigger view is used for triggering the `subPanelView` VISIBLE state.
             */
            internal val triggerView: View)

    /**
     * To show the panel(hide the keyboard automatically if the keyboard is showing) with
     * non-layout-conflict.
     *
     * @param panelLayout the layout of panel.
     * @see KPSwitchPanelLayoutHandler
     */
    fun showPanel(panelLayout: View, stateBlock: (() -> Boolean)?) {
        val activity = panelLayout.context as Activity
        if (stateBlock!=null){
            if (activity.currentFocus != null) {
                KPSKeyboardUtil.hideKeyboard(activity.currentFocus!!)
            }
        }else{
            if (activity.currentFocus != null) {
                KPSKeyboardUtil.hideKeyboard(activity.currentFocus!!)
            }
            panelLayout.visibility = View.VISIBLE
        }
    }

    /**
     * To show the keyboard(hide the panel automatically if the panel is showing) with
     * non-layout-conflict.
     *
     * @param panelLayout the layout of panel.
     * @param focusView   the view will be focused.
     */
    fun showKeyboard(panelLayout: View, focusView: View, forceOverlapping: Boolean, stateBlock: (() -> Boolean)?) {
        val activity = panelLayout.context as? Activity

        KPSKeyboardUtil.showKeyboard(focusView)
        if (activity != null &&(forceOverlapping|| isHandleByPlaceholder(activity))) {
            if (stateBlock==null)
            panelLayout.visibility = View.INVISIBLE
        }
    }

    /**
     * If the keyboard is showing, then going to show the `panelLayout`,
     * and hide the keyboard with non-layout-conflict.
     *
     *
     * If the panel is showing, then going to show the keyboard,
     * and hide the `panelLayout` with non-layout-conflict.
     *
     *
     * If the panel and the keyboard are both hiding. then going to show the `panelLayout`
     * with non-layout-conflict.
     *
     * @param panelLayout the layout of panel.
     * @param focusView   the view will be focused or lose the focus.
     * @return If true, switch to showing `panelLayout`; If false, switch to showing Keyboard.
     */
    fun switchPanelAndKeyboard(panelLayout: View, focusView: View,forceOverlapping: Boolean,stateBlock:(()->Boolean)?=null): Boolean {
        var switchToPanel =if (stateBlock!=null)  stateBlock.invoke() else panelLayout.visibility != View.VISIBLE
        if (panelLayout is IPanelConflictLayout){
            if (switchToPanel&&!panelLayout.isKeyboardShowing){
//                if (stateBlock==null)
                switchToPanel=false
            }
        }
        if (!switchToPanel) {
            showKeyboard(panelLayout, focusView,forceOverlapping,stateBlock)
        } else {
            showPanel(panelLayout,stateBlock)
        }

        return switchToPanel
    }

    /**
     * Hide the panel and the keyboard.
     *
     * @param panelLayout the layout of panel.
     */
    fun hidePanelAndKeyboard(panelLayout: View): Boolean {
        var rlt = false
        val activity = panelLayout.context as Activity

        val focusView = activity.currentFocus
        if (focusView != null) {
            rlt = KPSKeyboardUtil.hideKeyboard(activity.currentFocus!!)
            focusView.clearFocus()
        }

        panelLayout.visibility = View.GONE

        return rlt
    }

    /**
     * This listener is used to listening the click event for a view which is received the click
     * event to switch between Panel and Keyboard.
     *
     * @see .attach
     */
    interface SwitchClickListener {
        /**
         * @param v The view that was clicked.
         * @param switchToPanel If true, switch to showing Panel; If false, switch to showing
         * Keyboard.
         */
        fun onClickSwitch(v: View, switchToPanel: Boolean)
    }

    /**
     * @param isFullScreen        Whether in fullscreen theme.
     * @param isTranslucentStatus Whether in translucent status theme.
     * @param isFitsSystem        Whether the root view(the child of the content view) is in
     * `getFitSystemWindow()` equal true.
     * @return Whether handle the conflict by show panel placeholder, otherwise, handle by delay the
     * visible or gone of panel.
     */
    fun isHandleByPlaceholder(isFullScreen: Boolean, isTranslucentStatus: Boolean,
                              isFitsSystem: Boolean): Boolean {
        return isFullScreen || isTranslucentStatus && !isFitsSystem
    }

    internal fun isHandleByPlaceholder(activity: Activity): Boolean {
        return isHandleByPlaceholder(ViewUtil.isFullScreen(activity),
                ViewUtil.isTranslucentStatus(activity), ViewUtil.isFitsSystemWindows(activity))
    }

    private fun bindSubPanel(subPanelAndTrigger: SubPanelAndTrigger,
                             subPanelAndTriggers: Array<out SubPanelAndTrigger>,
                             focusView: View, panelLayout: View,
            /* Nullable */switchClickListener: SwitchClickListener?) {

        val triggerView = subPanelAndTrigger.triggerView
        val boundTriggerSubPanelView = subPanelAndTrigger.subPanelView

        triggerView.setOnClickListener { v ->
            var switchToPanel: Boolean? = null
            if (panelLayout.visibility == View.VISIBLE) {
                // panel is visible.

                if (boundTriggerSubPanelView.visibility == View.VISIBLE) {

                    // bound-trigger panel is visible.
                    // to show keyboard.
                    KPSwitchConflictUtil.showKeyboard(panelLayout, focusView, false, null)
                    switchToPanel = false

                } else {
                    // bound-trigger panel is invisible.
                    // to show bound-trigger panel.
                    showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggers)
                }
            } else {
                // panel is gone.
                // to show panel.
                KPSwitchConflictUtil.showPanel(panelLayout, null)
                switchToPanel = true

                // to show bound-trigger panel.
                showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggers)
            }

            if (switchClickListener != null && switchToPanel != null) {
                switchClickListener.onClickSwitch(v, switchToPanel)
            }
        }
    }

    private fun showBoundTriggerSubPanel(boundTriggerSubPanelView: View,
                                         subPanelAndTriggers: Array<out SubPanelAndTrigger>) {
        // to show bound-trigger panel.
        for (panelAndTrigger in subPanelAndTriggers) {
            if (panelAndTrigger.subPanelView !== boundTriggerSubPanelView) {
                // other sub panel.
                panelAndTrigger.subPanelView.visibility = View.GONE
            }
        }
        boundTriggerSubPanelView.visibility = View.VISIBLE
    }
}
/**
 * @see .attach
 *//* Nullable **//* Nullable **/
