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

import android.view.View
import android.view.Window
import com.huawen.baselibrary.views.keyboard.util.KPSKeyboardUtil

import com.huawen.baselibrary.views.keyboard.IFSPanelConflictLayout

/**
 * Created by Jacksgong on 3/31/16.
 *
 *
 * The handler for handling the layout-conflict in the full-screen panel-layout.
 */
class KPSwitchFSPanelLayoutHandler(private val panelLayout: View) : IFSPanelConflictLayout {
    private var isKeyboardShowing: Boolean = false

    private var recordedFocusView: View? = null

    fun onKeyboardShowing(showing: Boolean) {
        isKeyboardShowing = showing
        if (!showing && panelLayout.visibility == View.INVISIBLE) {
            panelLayout.visibility = View.GONE
        }

        if (!showing && recordedFocusView != null) {
            restoreFocusView()
            recordedFocusView = null
        }
    }

    override fun recordKeyboardStatus(window: Window) {
        val focusView = window.currentFocus ?: return

        if (isKeyboardShowing) {
            saveFocusView(focusView)
        } else {
            focusView.clearFocus()
        }
    }

    private fun saveFocusView(focusView: View) {
        recordedFocusView = focusView
        focusView.clearFocus()
        panelLayout.visibility = View.GONE
    }

    private fun restoreFocusView() {
        panelLayout.visibility = View.INVISIBLE
        KPSKeyboardUtil.showKeyboard(recordedFocusView)

    }
}
