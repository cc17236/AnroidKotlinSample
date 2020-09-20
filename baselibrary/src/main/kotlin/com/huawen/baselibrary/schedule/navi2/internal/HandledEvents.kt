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

package com.huawen.baselibrary.schedule.navi2.internal

import com.huawen.baselibrary.schedule.navi2.Event

import java.util.Arrays

/**
 * A place to store a common list of handled events by activities and fragments
 */
internal class HandledEvents private constructor() {

    init {
        throw AssertionError("No instances!")
    }

    companion object {

        val ACTIVITY_EVENTS = Arrays.asList(
            Event.CREATE,
            Event.CREATE_PERSISTABLE,
            Event.START,
            Event.POST_CREATE,
            Event.POST_CREATE_PERSISTABLE,
            Event.RESUME,
            Event.PAUSE,
            Event.STOP,
            Event.DESTROY,
            Event.RESTART,
            Event.SAVE_INSTANCE_STATE,
            Event.SAVE_INSTANCE_STATE_PERSISTABLE,
            Event.RESTORE_INSTANCE_STATE,
            Event.RESTORE_INSTANCE_STATE_PERSISTABLE,
            Event.NEW_INTENT,
            Event.BACK_PRESSED,
            Event.ATTACHED_TO_WINDOW,
            Event.DETACHED_FROM_WINDOW,
            Event.CONFIGURATION_CHANGED,
            Event.ACTIVITY_RESULT,
            Event.REQUEST_PERMISSIONS_RESULT
        )

        val FRAGMENT_EVENTS = Arrays.asList(
            Event.ATTACH,
            Event.CREATE,
            Event.CREATE_VIEW,
            Event.VIEW_CREATED,
            Event.ACTIVITY_CREATED,
            Event.VIEW_STATE_RESTORED,
            Event.START,
            Event.RESUME,
            Event.PAUSE,
            Event.STOP,
            Event.DESTROY_VIEW,
            Event.DESTROY,
            Event.DETACH,
            Event.SAVE_INSTANCE_STATE,
            Event.CONFIGURATION_CHANGED,
            Event.ACTIVITY_RESULT,
            Event.REQUEST_PERMISSIONS_RESULT
        )
    }
}
