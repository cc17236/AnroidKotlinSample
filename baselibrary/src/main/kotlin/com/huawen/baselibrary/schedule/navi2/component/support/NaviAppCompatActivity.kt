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

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity

import com.huawen.baselibrary.schedule.navi2.Event
import com.huawen.baselibrary.schedule.navi2.Listener
import com.huawen.baselibrary.schedule.navi2.NaviComponent
import com.huawen.baselibrary.schedule.navi2.internal.NaviEmitter


abstract class NaviAppCompatActivity : AppCompatActivity(), NaviComponent {

    private val base = NaviEmitter.createActivityEmitter()

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        base.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        base.onCreate(savedInstanceState, persistentState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        base.onStart()
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        base.onPostCreate(savedInstanceState)
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        base.onPostCreate(savedInstanceState, persistentState)
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
    override fun onDestroy() {
        base.onDestroy()
        super.onDestroy()
    }

    @CallSuper
    override fun onRestart() {
        super.onRestart()
        base.onRestart()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        base.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        base.onSaveInstanceState(outState, outPersistentState)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        base.onRestoreInstanceState(savedInstanceState)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle, persistentState: PersistableBundle) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        base.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    @CallSuper
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        base.onNewIntent(intent)
    }

    @CallSuper
    override fun onBackPressed() {
        super.onBackPressed()
        base.onBackPressed()
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        base.onAttachedToWindow()
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        base.onDetachedFromWindow()
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
