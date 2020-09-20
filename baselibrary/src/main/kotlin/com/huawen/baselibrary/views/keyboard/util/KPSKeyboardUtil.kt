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
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.huawen.baselibrary.R
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.WindowFrameCompat
import com.huawen.baselibrary.views.keyboard.IPanelHeightTarget


/**
 * Created by Jacksgong on 15/7/6.
 *
 *
 * For save the keyboard height, and provide the valid-panel-height
 * [.getValidPanelHeight].
 *
 *
 * Adapt the panel height with the keyboard height just relate
 * [.attach].
 *
 * @see KeyBoardSharedPreferences
 */
object KPSKeyboardUtil {


    private var lastSaveKeyboardHeight = 0


    private var maxPanelHeight = 0
    private var minPanelHeight = 0
    private var minKeyboardHeight = 0

    fun showKeyboard(view: View?) {
        view?.requestFocus()
        val inputManager = view?.context?.getSystemService(
                Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputManager?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View?): Boolean {
        val imm = view?.context
                ?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        return imm?.hideSoftInputFromWindow(view.windowToken, 0) ?: false
    }

    private fun saveKeyboardHeight(context: Context, keyboardHeight: Int): Boolean {
        if (lastSaveKeyboardHeight == keyboardHeight) {
            return false
        }

        if (keyboardHeight < 0) {
            return false
        }

        lastSaveKeyboardHeight = keyboardHeight
        Log.d("KeyBordUtil", String.format("save keyboard: %d", keyboardHeight))

        return KeyBoardSharedPreferences.save(context, keyboardHeight)
    }

    /**
     * @param context the keyboard height is stored by shared-preferences, so need context.
     * @return the stored keyboard height.
     * @see .getValidPanelHeight
     * @see .attach
     */
    fun getKeyboardHeight(context: Context): Int {
        if (lastSaveKeyboardHeight == 0) {
            lastSaveKeyboardHeight = KeyBoardSharedPreferences
                    .get(context, getMinPanelHeight(context.resources))
        }

        return lastSaveKeyboardHeight
    }

    /**
     * @param context the keyboard height is stored by shared-preferences, so need context.
     * @return the valid panel height refer the keyboard height
     * @see .getMaxPanelHeight
     * @see .getMinPanelHeight
     * @see .getKeyboardHeight
     * @see .attach
     */
    fun getValidPanelHeight(context: Context): Int {
        val maxPanelHeight = getMaxPanelHeight(context.resources)
        val minPanelHeight = getMinPanelHeight(context.resources)

        var validPanelHeight = getKeyboardHeight(context)

        validPanelHeight = Math.max(minPanelHeight, validPanelHeight)
        validPanelHeight = Math.min(maxPanelHeight, validPanelHeight)
        return validPanelHeight
    }

    fun getMaxPanelHeight(res: Resources): Int {
        if (maxPanelHeight == 0) {
            maxPanelHeight = res.getDimensionPixelSize(R.dimen.max_panel_height)
        }

        return maxPanelHeight
    }

    fun getMinPanelHeight(res: Resources): Int {
        if (minPanelHeight == 0) {
            minPanelHeight = res.getDimensionPixelSize(R.dimen.min_panel_height)
        }

        return minPanelHeight
    }

    fun getMinKeyboardHeight(context: Context): Int {
        if (minKeyboardHeight == 0) {
            minKeyboardHeight = context.resources
                    .getDimensionPixelSize(R.dimen.min_keyboard_height)
        }
        return minKeyboardHeight
    }


    /**
     * Recommend invoked by [Activity.onCreate]
     * For align the height of the keyboard to `target` as much as possible.
     * For save the refresh the keyboard height to shared-preferences.
     *
     * @param activity contain the view
     * @param target   whose height will be align to the keyboard height.
     * @param lis      the listener to listen in: keyboard is showing or not.
     * @see .saveKeyboardHeight
     */



    fun dettach(activity: Activity) {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        contentView.viewTreeObserver.removeAllOnGlobalLayoutListener()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    fun attach(activity: Activity, forceOverlapping: Boolean = false,
               target: IPanelHeightTarget?,
            /* Nullable */
               lis: OnKeyboardShowingListener?): KeyboardStatusListener {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val isFullScreen = ViewUtil.isFullScreen(activity)
        val isTranslucentStatus =ViewUtil.isTranslucentStatus(activity)
        val isFitSystemWindows =ViewUtil.isFitsSystemWindows(activity)

        // get the screen height.
        val display = activity.windowManager.defaultDisplay
        var screenHeight: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val screenSize = Point()
            display.getSize(screenSize)
            screenHeight = screenSize.y
        } else {
            screenHeight = display.height
        }
        val globalLayoutListener = KeyboardStatusListener(
                forceOverlapping,
                isFullScreen,
                isTranslucentStatus,
                isFitSystemWindows,
                contentView,
                target,
                lis,
                screenHeight)

//        val watcher = KeyboardWatcher(activity)
//        watcher.setListener(object : KeyboardWatcher.OnKeyboardToggleListener {
//            override fun onKeyboardShown(keyboardSize: Int) {
//                globalLayoutListener.onGlobalLayout()
//            }
//
//            override fun onKeyboardClosed() {
//                globalLayoutListener.onGlobalLayout()
//            }
//        })


        contentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        return globalLayoutListener
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    fun attach(activity: Activity,
               forceOverlapping: Boolean = false,
               targetScaleView: View,
               target: IPanelHeightTarget,
            /* Nullable */
               lis: OnKeyboardShowingListener?): KeyboardStatusListener {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val isFullScreen = ViewUtil.isFullScreen(activity)
        val isTranslucentStatus = ViewUtil.isTranslucentStatus(activity)
        val isFitSystemWindows = ViewUtil.isFitsSystemWindows(activity)

        // get the screen height.
        val display = activity.windowManager.defaultDisplay
        var screenHeight: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val screenSize = Point()
            display.getSize(screenSize)
            screenHeight = screenSize.y
        } else {
            screenHeight = display.height
        }
        val globalLayoutListener = SimpleTargetStatusListener(
                forceOverlapping,
                isFullScreen,
                isTranslucentStatus,
                isFitSystemWindows,
                contentView,
                target,
                lis,
                screenHeight
                , targetScaleView)

//        val watcher = KeyboardWatcher(activity)
//        watcher.setListener(object : KeyboardWatcher.OnKeyboardToggleListener {
//            override fun onKeyboardShown(keyboardSize: Int) {
//                globalLayoutListener.onGlobalLayout()
//            }
//
//            override fun onKeyboardClosed() {
//                globalLayoutListener.onGlobalLayout()
//            }
//        })


        contentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        return globalLayoutListener
    }


    /**
     * @see .attach
     */
    fun attach(activity: Activity,
               target: IPanelHeightTarget): ViewTreeObserver.OnGlobalLayoutListener {
        return attach(activity, false, target, null)
    }

    /**
     * Remove the OnGlobalLayoutListener from the activity root view
     *
     * @param activity same activity used in [.attach] method
     * @param l        ViewTreeObserver.OnGlobalLayoutListener returned by [.attach] method
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun detach(activity: Activity, l: ViewTreeObserver.OnGlobalLayoutListener) {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(l)
        } else {

            contentView.viewTreeObserver.removeGlobalOnLayoutListener(l)
        }
    }

    public class SimpleTargetStatusListener : KeyboardStatusListener {
        private var targetScaleView: View? = null
        private var targetLayoutParams: ViewGroup.LayoutParams? = null
        private var beginH = 0

        constructor(forceOverlapping: Boolean, isFullScreen: Boolean, isTranslucentStatus: Boolean, isFitSystemWindows: Boolean,
                    contentView: ViewGroup, panelHeightTarget: IPanelHeightTarget, keyboardShowingListener: OnKeyboardShowingListener?, screenHeight: Int,
                    targetScaleView: View
        ) :
                super(forceOverlapping, isFullScreen, isTranslucentStatus, isFitSystemWindows, contentView, panelHeightTarget, keyboardShowingListener, screenHeight) {
            this.targetScaleView = targetScaleView
        }

        override fun statusChange(keyboardShowingChange: Boolean, displayHeight: Int) {
            if (keyboardShowingChange) {//显示键盘了
                if (targetLayoutParams == null) {
                    val olp = targetScaleView?.layoutParams
                    if (olp != null) {
                        beginH = olp.height
                        val c = olp.javaClass
                        val cons = c.declaredConstructors
                        var parameterType: Class<*>? = null
                        for (i in 0 until cons.size) {
                            val types = cons.get(i).parameterTypes
                            if (types.size == 1) {
                                val a = types[0]
                                if (a is ViewGroup.LayoutParams)
                                    parameterType = a
                            }
                        }
                        if (parameterType == null) return
                        val c1 = c.getDeclaredConstructor(parameterType)
                        c1.isAccessible = true
                        targetLayoutParams = c1.newInstance(arrayOf(olp))
                    }
                }
                targetScaleView?.layoutParams?.height = displayHeight
            } else {//隐藏键盘了
                if (targetLayoutParams != null) {
                    targetLayoutParams!!.height = beginH
                    targetScaleView?.layoutParams = targetLayoutParams
                    beginH = 0
                    targetLayoutParams = null
                }
            }
        }

    }

    open public class KeyboardStatusListener internal constructor(
            private val forceOverlapping: Boolean,
            private val isFullScreen: Boolean, private val isTranslucentStatus: Boolean,
            private val isFitSystemWindows: Boolean,
            private val contentView: ViewGroup, private val panelHeightTarget: IPanelHeightTarget?,
            private val keyboardShowingListener: OnKeyboardShowingListener?, private val screenHeight: Int) : ViewTreeObserver.OnGlobalLayoutListener {

        private var previousDisplayHeight = 0
        private val statusBarHeight: Int
        private var lastKeyboardShowing: Boolean = false


        private var isOverlayLayoutDisplayHContainStatusBar = false

        private var maxOverlayLayoutHeight: Int = 0

        private val context: Context
            get() = contentView.context

        init {
            this.statusBarHeight = StatusBarHeightUtil.getStatusBarHeight(contentView.context)
        }


        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        override fun onGlobalLayout() {
//            Debuger.print("能监听到了??")
            val userRootView = contentView.getChildAt(0)
            val actionBarOverlayLayout = contentView.parent as View

            // Step 1. calculate the current display frame's height.
            val r = Rect()

            val displayHeight: Int
            val notReadyDisplayHeight = -1
            if (isTranslucentStatus) {
                // status bar translucent.

                // In the case of the Theme is Status-Bar-Translucent, we calculate the keyboard
                // state(showing/hiding) and the keyboard height based on assuming that the
                // displayHeight includes the height of the status bar.
                WindowFrameCompat.parse(actionBarOverlayLayout, r)

                var space = r.bottom - r.top
                if (forceOverlapping) {
                    isOverlayLayoutDisplayHContainStatusBar = true
                    space += statusBarHeight
                }
                val overlayLayoutDisplayHeight = space

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    // in case of the keyboard is hiding, the display height of the
                    // action-bar-overlay-layout would be possible equal to the screen height.

                    // and if isOverlayLayoutDisplayHContainStatusBar has already been true, the
                    // display height of action-bar-overlay-layout must include the height of the
                    // status bar always.
                    isOverlayLayoutDisplayHContainStatusBar = overlayLayoutDisplayHeight == screenHeight
                }

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    // In normal case, we need to plus the status bar height manually.
                    displayHeight = overlayLayoutDisplayHeight + statusBarHeight
                } else {
                    // In some case(such as Samsung S7 edge), the height of the
                    // action-bar-overlay-layout display bound already included the height of the
                    // status bar, in this case we doesn't need to plus the status bar height
                    // manually.
                    displayHeight = overlayLayoutDisplayHeight
                }

            } else {
                if (userRootView != null) {
                    WindowFrameCompat.parse(userRootView, r)
                    var space = r.bottom - r.top
                    if (forceOverlapping) {
                        isOverlayLayoutDisplayHContainStatusBar = true
                        space += statusBarHeight
//                        if (statusBarHeight==r.top-2){
//                            space+=2
//                        }
                    }
                    displayHeight = space
                } else {
                    Log.w("KeyBordUtil",
                            "user root view not ready so ignore global layout changed!")
                    displayHeight = notReadyDisplayHeight
                }

            }

            if (displayHeight == notReadyDisplayHeight) {
                return
            }
            hasChange = false
            calculateKeyboardHeight(displayHeight, forceOverlapping)
            calculateKeyboardShowing(displayHeight, forceOverlapping)
            previousDisplayHeight = displayHeight
            if (hasChange)
                statusChange(lastKeyboardShowing, displayHeight)
        }

        open protected fun statusChange(keyboardShowingChange: Boolean, displayHeight: Int) {
        }

        private var hasChange = false

        private fun calculateKeyboardHeight(displayHeight: Int, forceOverlapping: Boolean) {
            // first result.
            if (previousDisplayHeight == 0) {
                previousDisplayHeight = displayHeight

                // init the panel height for target.
                panelHeightTarget?.refreshHeight(KPSKeyboardUtil.getValidPanelHeight(context))
                return
            }

            val keyboardHeight: Int
            if (forceOverlapping || KPSwitchConflictUtil.isHandleByPlaceholder(isFullScreen, isTranslucentStatus,
                            isFitSystemWindows)) {
                // the height of content parent = contentView.height + actionBar.height
                val actionBarOverlayLayout = contentView.parent as View

                keyboardHeight = actionBarOverlayLayout.height - displayHeight

                Log.d(TAG, String.format("action bar over layout %d display height: %d",
                        (contentView.parent as View).height, displayHeight))

            } else {
                keyboardHeight = Math.abs(displayHeight - previousDisplayHeight)
            }
            // no change.
            if (keyboardHeight <= getMinKeyboardHeight(context)) {
                return
            }

            Log.d(TAG, String.format("pre display height: %d display height: %d keyboard: %d ",
                    previousDisplayHeight, displayHeight, keyboardHeight))

            // influence from the layout of the Status-bar.
            if (keyboardHeight == this.statusBarHeight) {
                Log.w(TAG, String.format("On global layout change get keyboard height just equal" + " statusBar height %d", keyboardHeight))
                return
            }

            // save the keyboardHeight
            val changed = KPSKeyboardUtil.saveKeyboardHeight(context, keyboardHeight)
            if (changed) {
                val validPanelHeight = KPSKeyboardUtil.getValidPanelHeight(context)
                if (this.panelHeightTarget?.height != validPanelHeight) {
                    // Step3. refresh the panel's height with valid-panel-height which refer to
                    // the last keyboard height
                    this.panelHeightTarget?.refreshHeight(validPanelHeight)
                }
            }
        }

        private fun calculateKeyboardShowing(displayHeight: Int, forceOverlapping: Boolean) {

            val isKeyboardShowing: Boolean

            // the height of content parent = contentView.height + actionBar.height
            val actionBarOverlayLayout = contentView.parent as View
            // in the case of FragmentLayout, this is not real ActionBarOverlayLayout, it is
            // LinearLayout, and is a child of DecorView, and in this case, its top-padding would be
            // equal to the height of status bar, and its height would equal to DecorViewHeight -
            // NavigationBarHeight.
            var actionBarOverlayLayoutHeight = actionBarOverlayLayout.height - actionBarOverlayLayout.paddingTop
            if (forceOverlapping) {
//                actionBarOverlayLayoutHeight+=this.statusBarHeight
//                if (actionBarOverlayLayoutHeight>displayHeight){
////                    actionBarOverlayLayoutHeight
//                }
            }

            if (forceOverlapping || KPSwitchConflictUtil.isHandleByPlaceholder(isFullScreen, isTranslucentStatus,
                            isFitSystemWindows)) {
                if (!isTranslucentStatus && actionBarOverlayLayoutHeight - displayHeight == this.statusBarHeight) {
                    // handle the case of status bar layout, not keyboard active.
                    isKeyboardShowing = lastKeyboardShowing
                } else {
                    isKeyboardShowing = actionBarOverlayLayoutHeight > displayHeight
                }

            } else {
                val phoneDisplayHeight = contentView.resources
                        .displayMetrics.heightPixels
                if (!isTranslucentStatus && phoneDisplayHeight == actionBarOverlayLayoutHeight) {
                    // no space to settle down the status bar, switch to fullscreen,
                    // only in the case of paused and opened the fullscreen page.
                    Log.w(TAG, String.format("skip the keyboard status calculate, the current"
                            + " activity is paused. and phone-display-height %d,"
                            + " root-height+actionbar-height %d", phoneDisplayHeight,
                            actionBarOverlayLayoutHeight))
                    return
                }

                if (maxOverlayLayoutHeight == 0) {
                    // non-used.
                    isKeyboardShowing = lastKeyboardShowing
                } else {
                    isKeyboardShowing = displayHeight < maxOverlayLayoutHeight - getMinKeyboardHeight(context)
                }

                maxOverlayLayoutHeight = Math
                        .max(maxOverlayLayoutHeight, actionBarOverlayLayoutHeight)
            }

            if (lastKeyboardShowing != isKeyboardShowing) {
                hasChange = true
                Log.d(TAG, String.format("displayHeight %d actionBarOverlayLayoutHeight %d " + "keyboard status change: %B",
                        displayHeight, actionBarOverlayLayoutHeight, isKeyboardShowing))
                this.panelHeightTarget?.onKeyboardShowing(isKeyboardShowing)
                keyboardShowingListener?.onKeyboardShowing(isKeyboardShowing)
            }

            lastKeyboardShowing = isKeyboardShowing

        }

        companion object {
            private val TAG = "KeyboardStatusListener"
        }
    }

    /**
     * The interface is used to listen the keyboard showing state.
     *
     * @see .attach
     * @see KeyboardStatusListener.calculateKeyboardShowing
     */
    interface OnKeyboardShowingListener {

        /**
         * Keyboard showing state callback method.
         *
         *
         * This method is invoked in
         * [ViewTreeObserver.OnGlobalLayoutListener.onGlobalLayout] which is one of the
         * ViewTree lifecycle callback methods. So deprecating those time-consuming operation(I/O,
         * complex calculation, alloc objects, etc.) here from blocking main ui thread is
         * recommended.
         *
         *
         * @param isShowing Indicate whether keyboard is showing or not.
         */
        fun onKeyboardShowing(isShowing: Boolean)

    }

}

inline fun ViewTreeObserver.removeAllOnGlobalLayoutListener() {
    try {
        val field = ViewTreeObserver::class.java.getDeclaredField("mOnGlobalLayoutListeners")
        field.isAccessible = true
        val objs = field.get(this)
        val m = objs.javaClass.getDeclaredMethod("clear")
        m.isAccessible = true
        m.invoke(objs)
    }catch (e:Exception){}

}