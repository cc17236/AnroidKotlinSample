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

package com.huawen.baselibrary.schedule.rxlifecycle2.navi


import com.huawen.baselibrary.schedule.navi2.NaviComponent
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleProvider
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.android.FragmentEvent

import androidx.annotation.CheckResult
import com.huawen.baselibrary.schedule.rxlifecycle2.internal.Preconditions


class NaviLifecycle private constructor() {

    init {
        throw AssertionError("No instances!")
    }

    companion object {

        @CheckResult
        fun createActivityLifecycleProvider(activity: NaviComponent): LifecycleProvider<ActivityEvent> {
            Preconditions.checkNotNull(activity, "activity == null")
            return ActivityLifecycleProviderImpl(activity)
        }

        @CheckResult
        fun createFragmentLifecycleProvider(fragment: NaviComponent): LifecycleProvider<FragmentEvent> {
            Preconditions.checkNotNull(fragment, "fragment == null")
            return FragmentLifecycleProviderImpl(fragment)
        }
    }
}
