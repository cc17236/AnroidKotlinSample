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
package com.huawen.baselibrary.views.keyboard


/**
 * Created by Jacksgong on 3/26/16.
 *
 *
 * For align the height of the keyboard to panel height as much as possible.
 *
 * @see KeyboardUtil.KeyboardStatusListener
 */
interface IPanelHeightTarget {

    /**
     * @return get the height of target-view.
     */
    var height: Int?

    /**
     * for handle the panel's height, will be equal to the keyboard height which had saved last
     * time.
     */
    fun refreshHeight(panelHeight: Int)

    /**
     * Be invoked by onGlobalLayoutListener call-back.
     *
     * @param showing whether the keyboard is showing or not.
     */
    fun onKeyboardShowing(showing: Boolean)
}
