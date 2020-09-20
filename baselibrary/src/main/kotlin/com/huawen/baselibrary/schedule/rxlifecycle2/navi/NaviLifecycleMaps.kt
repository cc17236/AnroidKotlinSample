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


import com.huawen.baselibrary.schedule.navi2.Event
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.android.FragmentEvent
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate

/**
 * Maps from Navi events to RxLifecycleAndroid events
 */
internal class NaviLifecycleMaps private constructor() {

    init {
        throw AssertionError("No instances!")
    }

    companion object {

        val ACTIVITY_EVENT_FILTER: Predicate<Event.Type> = Predicate { type ->
            when (type) {
                Event.Type.CREATE, Event.Type.START, Event.Type.RESUME, Event.Type.PAUSE, Event.Type.STOP, Event.Type.DESTROY -> true
                else -> false
            }
        }

        val ACTIVITY_EVENT_MAP: Function<Event.Type, ActivityEvent> = Function { type ->
            when (type) {
                Event.Type.CREATE -> ActivityEvent.CREATE
                Event.Type.START -> ActivityEvent.START
                Event.Type.RESUME -> ActivityEvent.RESUME
                Event.Type.PAUSE -> ActivityEvent.PAUSE
                Event.Type.STOP -> ActivityEvent.STOP
                Event.Type.DESTROY -> ActivityEvent.DESTROY
                else -> throw IllegalArgumentException("Cannot map $type to a ActivityEvent.")
            }
        }

        val FRAGMENT_EVENT_FILTER: Predicate<Event.Type> = Predicate { type ->
            when (type) {
                Event.Type.ATTACH, Event.Type.CREATE, Event.Type.CREATE_VIEW, Event.Type.START, Event.Type.RESUME, Event.Type.PAUSE, Event.Type.STOP, Event.Type.DESTROY_VIEW, Event.Type.DESTROY, Event.Type.DETACH -> true
                else -> false
            }
        }

        val FRAGMENT_EVENT_MAP: Function<Event.Type, FragmentEvent> = Function { type ->
            when (type) {
                Event.Type.ATTACH -> FragmentEvent.ATTACH
                Event.Type.CREATE -> FragmentEvent.CREATE
                Event.Type.CREATE_VIEW -> FragmentEvent.CREATE_VIEW
                Event.Type.START -> FragmentEvent.START
                Event.Type.RESUME -> FragmentEvent.RESUME
                Event.Type.PAUSE -> FragmentEvent.PAUSE
                Event.Type.STOP -> FragmentEvent.STOP
                Event.Type.DESTROY_VIEW -> FragmentEvent.DESTROY_VIEW
                Event.Type.DESTROY -> FragmentEvent.DESTROY
                Event.Type.DETACH -> FragmentEvent.DETACH
                else -> throw IllegalArgumentException("Cannot map $type to a FragmentEvent.")
            }
        }
    }
}
