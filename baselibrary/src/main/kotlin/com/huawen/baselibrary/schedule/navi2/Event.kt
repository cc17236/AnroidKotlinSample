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

package com.huawen.baselibrary.schedule.navi2

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.huawen.baselibrary.schedule.navi2.model.ActivityResult
import com.huawen.baselibrary.schedule.navi2.model.BundleBundle
import com.huawen.baselibrary.schedule.navi2.model.RequestPermissionsResult
import com.huawen.baselibrary.schedule.navi2.model.ViewCreated

/**
 * Represents an event that can be listened to in an Activity or Fragment.
 *
 * Comes with a set of predefined events.
 *
 * Events will vary in their timing in relation to the normally-required super() call. Generally,
 * component creation (`onCreate()`, `onStart()`, etc.) is emitted *after*
 * their super calls are made. Component destruction (`onPause()`, `onStop()`, etc.)
 * are called *before* their super calls are made. Events that are neither are called *after*
 * their super calls.
 *
 * @param <T> the callback type for the event. If Object, then the value is just a signal and has
 * no contents.
</T> */
class Event<T>// This is purposefully hidden so that we can control available events
private constructor(private val eventType: Type, private val callbackType: Class<T>) {

    fun type(): Type {
        return eventType
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val event = o as Event<*>?

        return if (eventType != event!!.eventType) false else callbackType == event.callbackType
    }

    override fun hashCode(): Int {
        var result = eventType.hashCode()
        result = 31 * result + callbackType.hashCode()
        return result
    }

    override fun toString(): String {
        return "Event{" +
                "eventType=" + eventType +
                ", callbackType=" + callbackType +
                '}'.toString()
    }

    enum class Type {
        ALL,

        // Shared
        CREATE,
        START,
        RESUME,
        PAUSE,
        STOP,
        DESTROY,
        SAVE_INSTANCE_STATE,
        CONFIGURATION_CHANGED,
        ACTIVITY_RESULT,
        REQUEST_PERMISSIONS_RESULT,

        // Activity-only
        CREATE_PERSISTABLE,
        POST_CREATE,
        POST_CREATE_PERSISTABLE,
        RESTART,
        SAVE_INSTANCE_STATE_PERSISTABLE,
        RESTORE_INSTANCE_STATE,
        RESTORE_INSTANCE_STATE_PERSISTABLE,
        NEW_INTENT,
        BACK_PRESSED,
        ATTACHED_TO_WINDOW,
        DETACHED_FROM_WINDOW,

        // Fragment-only
        ATTACH,
        CREATE_VIEW,
        VIEW_CREATED,
        ACTIVITY_CREATED,
        VIEW_STATE_RESTORED,
        DESTROY_VIEW,
        DETACH
    }

    companion object {

        /**
         * Emits all events (though without any extra data).
         */
        val ALL = Event(Type.ALL, Type::class.java)

        /**
         * Emits [Activity.onCreate] and [Fragment.onCreate]. Emitted after
         * super().
         */
        val CREATE = Event(Type.CREATE, Bundle::class.java)

        /**
         * Emits [Activity.onCreate]. Emitted after super().
         */
        val CREATE_PERSISTABLE = Event(Type.CREATE_PERSISTABLE, BundleBundle::class.java)

        /**
         * Emits [Activity.onStart] and [Fragment.onStart]. Emitted after super().
         */
        val START = Event(Type.START, Any::class.java)

        /**
         * Emits [Activity.onPostCreate]. Emitted after super().
         */
        val POST_CREATE = Event(Type.POST_CREATE, Bundle::class.java)

        /**
         * Emits [Activity.onCreate]. Emitted after super().
         */
        val POST_CREATE_PERSISTABLE = Event(Type.POST_CREATE_PERSISTABLE, BundleBundle::class.java)

        /**
         * Emits [Activity.onResume] and [Fragment.onResume]. Emitted after super().
         */
        val RESUME = Event(Type.RESUME, Any::class.java)

        /**
         * Emits [Activity.onPause] and [Fragment.onPause]. Emitted before super().
         */
        val PAUSE = Event(Type.PAUSE, Any::class.java)

        /**
         * Emits [Activity.onStop] and [Fragment.onStop]. Emitted before super().
         */
        val STOP = Event(Type.STOP, Any::class.java)

        /**
         * Emits [Activity.onDestroy] and [Fragment.onDestroy]. Emitted before super().
         */
        val DESTROY = Event(Type.DESTROY, Any::class.java)

        /**
         * Emits [Activity.onSaveInstanceState] and
         * [Fragment.onSaveInstanceState]. Emitted after super().
         */
        val SAVE_INSTANCE_STATE = Event(Type.SAVE_INSTANCE_STATE, Bundle::class.java)

        /**
         * Emits [Activity.onSaveInstanceState]. Emitted after super().
         */
        val SAVE_INSTANCE_STATE_PERSISTABLE = Event(Type.SAVE_INSTANCE_STATE_PERSISTABLE, BundleBundle::class.java)

        /**
         * Emits [Activity.onConfigurationChanged] and
         * [Fragment.onConfigurationChanged]. Emitted after super().
         */
        val CONFIGURATION_CHANGED = Event(Type.CONFIGURATION_CHANGED, Configuration::class.java)

        /**
         * Emits [Activity.onActivityResult] and
         * [Fragment.onActivityResult]. Emitted after super().
         */
        val ACTIVITY_RESULT = Event(Type.ACTIVITY_RESULT, ActivityResult::class.java)

        /**
         * Emits [Activity.onRequestPermissionsResult] and
         * [Fragment.onRequestPermissionsResult]. Emitted after super().
         */
        val REQUEST_PERMISSIONS_RESULT = Event(Type.REQUEST_PERMISSIONS_RESULT, RequestPermissionsResult::class.java)

        /**
         * Emits [Activity.onRestart]. Emitted after super().
         */
        val RESTART = Event(Type.RESTART, Any::class.java)

        /**
         * Emits [Activity.onRestoreInstanceState]. Emitted after super().
         */
        val RESTORE_INSTANCE_STATE = Event(Type.RESTORE_INSTANCE_STATE, Bundle::class.java)

        /**
         * Emits [Activity.onRestoreInstanceState]. Emitted after
         * super().
         */
        val RESTORE_INSTANCE_STATE_PERSISTABLE =
            Event(Type.RESTORE_INSTANCE_STATE_PERSISTABLE, BundleBundle::class.java)

        /**
         * Emits [Activity.onNewIntent]. Emitted after super().
         */
        val NEW_INTENT = Event(Type.NEW_INTENT, Intent::class.java)

        /**
         * Emits [Activity.onBackPressed]. Emitted after super().
         */
        val BACK_PRESSED = Event(Type.BACK_PRESSED, Any::class.java)

        /**
         * Emits [Activity.onAttachedToWindow]. Emitted after super().
         */
        val ATTACHED_TO_WINDOW = Event(Type.ATTACHED_TO_WINDOW, Any::class.java)

        /**
         * Emits [Activity.onDetachedFromWindow]. Emitted after super().
         */
        val DETACHED_FROM_WINDOW = Event(Type.DETACHED_FROM_WINDOW, Any::class.java)

        /**
         * Emits [Fragment.onAttach]. Emitted after super().
         */
        val ATTACH = Event(Type.ATTACH, Context::class.java)

        /**
         * Emits [Fragment.onCreateView]. Emitted after super().
         */
        val CREATE_VIEW = Event(Type.CREATE_VIEW, Bundle::class.java)

        /**
         * Emits [Fragment.onViewCreated] ()}. Emitted before super().
         */
        val VIEW_CREATED = Event(Type.VIEW_CREATED, ViewCreated::class.java)

        /**
         * Emits [Fragment.onActivityCreated]. Emitted after super().
         */
        val ACTIVITY_CREATED = Event(Type.ACTIVITY_CREATED, Bundle::class.java)

        /**
         * Emits [Fragment.onViewStateRestored]. Emitted after super().
         */
        val VIEW_STATE_RESTORED = Event(Type.VIEW_STATE_RESTORED, Bundle::class.java)

        /**
         * Emits [Fragment.onDestroyView]. Emitted before super().
         */
        val DESTROY_VIEW = Event(Type.DESTROY_VIEW, Any::class.java)

        /**
         * Emits [Fragment.onDetach]. Emitted before super().
         */
        val DETACH = Event(Type.DETACH, Any::class.java)
    }
}
