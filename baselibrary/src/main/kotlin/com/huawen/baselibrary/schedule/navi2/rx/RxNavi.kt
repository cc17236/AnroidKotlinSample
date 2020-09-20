/*
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

package com.huawen.baselibrary.schedule.navi2.rx

import androidx.annotation.CheckResult

import com.huawen.baselibrary.schedule.navi2.Event
import com.huawen.baselibrary.schedule.navi2.NaviComponent

import io.reactivex.Observable

class RxNavi private constructor() {

    init {
        throw AssertionError("No instances!")
    }

    companion object {

        @CheckResult
        fun <T> observe(component: NaviComponent, event: Event<T>): Observable<T> {
            if (component == null) throw IllegalArgumentException("component == null")
            if (event == null) throw IllegalArgumentException("event == null")
            return Observable.create(NaviOnSubscribe(component, event))
        }
    }
}
