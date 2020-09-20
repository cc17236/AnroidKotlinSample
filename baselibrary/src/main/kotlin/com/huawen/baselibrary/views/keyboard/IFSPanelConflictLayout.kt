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
package com.huawen.baselibrary.views.keyboard

import android.app.Activity
import android.view.Window

/**
 * Created by Jacksgong on 3/31/16.
 *
 *
 * The interface used for the panel's container layout and it used in the case of full-screen theme
 * window.
 */
interface IFSPanelConflictLayout {

    /**
     * Record the current keyboard status on [Activity.onPause] and will be restore
     * the keyboard status automatically [Activity.onResume]
     *
     *
     * Recommend invoke this method on the [Activity.onPause], to record the keyboard
     * status for the right keyboard status and non-layout-conflict when the activity on resume.
     *
     *
     * For fix issue#12 Bug1&Bug2.
     *
     * @param window The current window of the current visual activity.
     */
    fun recordKeyboardStatus(window: Window)
}
