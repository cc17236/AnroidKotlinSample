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

package com.huawen.baselibrary.schedule.navi2.component.support

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.huawen.baselibrary.schedule.navi2.Event
import com.huawen.baselibrary.schedule.navi2.Listener
import com.huawen.baselibrary.schedule.navi2.NaviComponent
import com.huawen.baselibrary.schedule.navi2.internal.NaviEmitter

import androidx.annotation.CallSuper

abstract class NaviFragment : Fragment(), NaviComponent {

    private val base = NaviEmitter.createFragmentEmitter()

    override fun handlesEvents(vararg events: Event<*>): Boolean {
        return base.handlesEvents(*events)
    }

    override fun <T> addListener(event: Event<T>, listener: Listener<T>) {
        base.addListener(event, listener)
    }

    override fun <T> removeListener(listener: Listener<T>) {
        base.removeListener(listener)
    }

    @CallSuper
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        base.onAttach(activity)
    }

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        base.onAttach(context)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        base.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        base.onCreateView(savedInstanceState)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        base.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        base.onActivityCreated(savedInstanceState)
    }

    @CallSuper
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        base.onViewStateRestored(savedInstanceState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        base.onStart()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        base.onResume()
    }

    @CallSuper
    override fun onPause() {
        base.onPause()
        super.onPause()
    }

    @CallSuper
    override fun onStop() {
        base.onStop()
        super.onStop()
    }

    @CallSuper
    override fun onDestroyView() {
        base.onDestroyView()
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        base.onDestroy()
        super.onDestroy()
    }

    @CallSuper
    override fun onDetach() {
        base.onDetach()
        super.onDetach()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        base.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        base.onConfigurationChanged(newConfig)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        base.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        base.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
