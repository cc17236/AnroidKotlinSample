/*
 * Copyright 2014 Toxic Bakery
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

package com.huawen.baselibrary.views.banners.transformer

import android.view.View

class RotateUpTransformer : ABaseTransformer() {

    protected override val isPagingEnabled: Boolean
        get() = true

    override fun onTransform(view: View, position: Float) {
        val width = view.width.toFloat()
        val rotation = ROT_MOD * position

        view.pivotX = width * 0.5f
        view.pivotY = 0f
        view.translationX = 0f
        view.rotation = rotation
    }

    companion object {

        private val ROT_MOD = -15f
    }

}
