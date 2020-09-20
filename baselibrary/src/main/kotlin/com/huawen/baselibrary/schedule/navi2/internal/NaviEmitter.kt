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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import com.huawen.baselibrary.schedule.navi2.Event
import com.huawen.baselibrary.schedule.navi2.Listener
import com.huawen.baselibrary.schedule.navi2.NaviComponent
import com.huawen.baselibrary.schedule.navi2.internal.Constants.Companion.SIGNAL
import com.huawen.baselibrary.schedule.navi2.model.ActivityResult
import com.huawen.baselibrary.schedule.navi2.model.BundleBundle
import com.huawen.baselibrary.schedule.navi2.model.RequestPermissionsResult
import com.huawen.baselibrary.schedule.navi2.model.ViewCreated
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Emitter of Navi events which contains all the actual logic
 *
 * This makes it easier to port [NaviComponent] to Activities and Fragments
 * without duplicating quite as much code.
 */
class NaviEmitter(handledEvents: Collection<Event<*>>) : NaviComponent {

    private val handledEvents: Set<Event<*>>

    private val listenerMap: ConcurrentHashMap<Event<*>, CopyOnWriteArrayList<Listener<Any>>>

    // Only used for fast removal of listeners
    private val eventMap: MutableMap<Listener<*>, Event<*>>

    init {
        this.handledEvents = Collections.unmodifiableSet(HashSet(handledEvents))
        this.listenerMap = ConcurrentHashMap()
        this.eventMap = ConcurrentHashMap()
    }

    override fun handlesEvents(vararg events: Event<*>): Boolean {
        for (a in events.indices) {
            val event = events[a]
            if (event !== Event.ALL && !handledEvents.contains(event)) {
                return false
            }
        }

        return true
    }

    override fun <T> addListener(event: Event<T>, listener: Listener<T>) {
        if (!handlesEvents(event)) {
            throw IllegalArgumentException("This component cannot handle event $event")
        }

        // Check that we're not adding the same listener in multiple places
        // For the same event, it's idempotent; for different events, it's an error
        if (eventMap.containsKey(listener)) {
            val otherEvent = eventMap[listener]
            if (event != otherEvent) {
                throw IllegalStateException(
                    "Cannot use the same listener for two events! e1: $event e2: $otherEvent"
                )
            }
            return
        }

        eventMap[listener] = event

        if (!listenerMap.containsKey(event)) {
            listenerMap[event] = CopyOnWriteArrayList()
        }

        val listeners = listenerMap[event]
        listeners!!.add(listener as Listener<Any>)
    }

    override fun <T> removeListener(listener: Listener<T>) {
        val event = eventMap.remove(listener)
        if (event != null && listenerMap.containsKey(event)) {
            listenerMap[event]!!.remove(listener as Listener<Any>)
        }
    }

    private fun emitEvent(event: Event<Any>) {
        emitEvent(event, SIGNAL)
    }

    private fun <T> emitEvent(event: Event<T>, data: T) {
        // We gather listener iterators  all at once so adding/removing listeners during emission
        // doesn't change the listener list.
        val listeners = listenerMap[event]
        val listenersIterator = listeners?.listIterator()

        val allListeners = listenerMap[Event.ALL]
        val allListenersIterator = allListeners?.iterator()

        if (allListenersIterator != null) {
            val type = event.type()
            while (allListenersIterator.hasNext()) {
                allListenersIterator.next().call(type)
            }
        }

        if (listeners != null) {
            while (listenersIterator!!.hasNext()) {
                listenersIterator.next().call(data!!)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Events

    fun onActivityCreated(savedInstanceState: Bundle?) {
        emitEvent(
            Event.ACTIVITY_CREATED,
            savedInstanceState ?: Bundle()
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        emitEvent(Event.ACTIVITY_RESULT, ActivityResult.create(requestCode, resultCode, data))
    }

    fun onAttach(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            emitEvent(Event.ATTACH, activity)
        }
    }

    fun onAttach(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            emitEvent(Event.ATTACH, context)
        }
    }

    fun onAttachedToWindow() {
        emitEvent(Event.ATTACHED_TO_WINDOW)
    }

    fun onBackPressed() {
        emitEvent(Event.BACK_PRESSED)
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        emitEvent(Event.CONFIGURATION_CHANGED, newConfig)
    }

    fun onCreate(savedInstanceState: Bundle?) {
        emitEvent(Event.CREATE, savedInstanceState ?: Bundle())
    }

    fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        emitEvent(Event.CREATE_PERSISTABLE, BundleBundle.create(savedInstanceState, persistentState))
    }

    fun onCreateView(savedInstanceState: Bundle?) {
        emitEvent(Event.CREATE_VIEW, savedInstanceState ?: Bundle())
    }

    fun onViewCreated(view: View, bundle: Bundle?) {
        emitEvent(Event.VIEW_CREATED, ViewCreated.create(view, bundle))
    }

    fun onDestroy() {
        emitEvent(Event.DESTROY)
    }

    fun onDestroyView() {
        emitEvent(Event.DESTROY_VIEW)
    }

    fun onDetach() {
        emitEvent(Event.DETACH)
    }

    fun onDetachedFromWindow() {
        emitEvent(Event.DETACHED_FROM_WINDOW)
    }

    fun onNewIntent(intent: Intent) {
        emitEvent(Event.NEW_INTENT, intent)
    }

    fun onPause() {
        emitEvent(Event.PAUSE)
    }

    fun onPostCreate(savedInstanceState: Bundle?) {
        emitEvent(Event.POST_CREATE, savedInstanceState ?: Bundle())
    }

    fun onPostCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        emitEvent(
            Event.POST_CREATE_PERSISTABLE,
            BundleBundle.create(savedInstanceState, persistentState)
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        emitEvent(
            Event.REQUEST_PERMISSIONS_RESULT,
            RequestPermissionsResult.create(requestCode, permissions, grantResults)
        )
    }

    fun onRestart() {
        emitEvent(Event.RESTART)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        emitEvent(
            Event.RESTORE_INSTANCE_STATE,
            savedInstanceState ?: Bundle()
        )
    }

    fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        emitEvent(
            Event.RESTORE_INSTANCE_STATE_PERSISTABLE,
            BundleBundle.create(savedInstanceState, persistentState)
        )
    }

    fun onResume() {
        emitEvent(Event.RESUME)
    }

    fun onSaveInstanceState(outState: Bundle) {
        emitEvent(Event.SAVE_INSTANCE_STATE, outState)
    }

    fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        emitEvent(
            Event.SAVE_INSTANCE_STATE_PERSISTABLE,
            BundleBundle.create(outState, outPersistentState)
        )
    }

    fun onStart() {
        emitEvent(Event.START)
    }

    fun onStop() {
        emitEvent(Event.STOP)
    }

    fun onViewStateRestored(savedInstanceState: Bundle?) {
        emitEvent(
            Event.VIEW_STATE_RESTORED,
            savedInstanceState ?: Bundle()
        )
    }

    companion object {

        fun createActivityEmitter(): NaviEmitter {
            return NaviEmitter(HandledEvents.ACTIVITY_EVENTS)
        }

        fun createFragmentEmitter(): NaviEmitter {
            return NaviEmitter(HandledEvents.FRAGMENT_EVENTS)
        }
    }
}
