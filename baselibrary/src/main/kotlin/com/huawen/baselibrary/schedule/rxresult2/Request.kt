/*
 * Copyright 2016 Copyright 2016 Víctor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawen.baselibrary.schedule.rxresult2

import android.content.Intent
import androidx.annotation.Nullable


open class Request(@param:Nullable private val intent: Intent?) {
    private var onPreResult: OnPreResult<*>? = null
    private var onResult: OnResult? = null

    fun setOnPreResult(@Nullable onPreResult: OnPreResult<*>) {
        this.onPreResult = onPreResult
    }

    fun onPreResult(): OnPreResult<*>? {
        return onPreResult
    }

    fun setOnResult(onResult: OnResult) {
        this.onResult = onResult
    }

    fun onResult(): OnResult? {
        return onResult
    }

    @Nullable
    fun intent(): Intent? {
        return intent
    }
}
