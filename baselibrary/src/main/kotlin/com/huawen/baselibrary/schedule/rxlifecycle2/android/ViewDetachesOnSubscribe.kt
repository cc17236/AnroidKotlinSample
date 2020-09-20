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

package com.huawen.baselibrary.schedule.rxlifecycle2.android

import android.view.View
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.MainThreadDisposable

import io.reactivex.android.MainThreadDisposable.verifyMainThread

internal class ViewDetachesOnSubscribe(val view: View) : ObservableOnSubscribe<Any> {

    @Throws(Exception::class)
    override fun subscribe(emitter: ObservableEmitter<Any>) {
        verifyMainThread()
        val listener = EmitterListener(emitter)
        emitter.setDisposable(listener)
        view.addOnAttachStateChangeListener(listener)
    }

    internal inner class EmitterListener(val emitter: ObservableEmitter<Any>) : MainThreadDisposable(),
        View.OnAttachStateChangeListener {

        override fun onViewAttachedToWindow(view: View) {
            // Do nothing
        }

        override fun onViewDetachedFromWindow(view: View) {
            emitter.onNext(SIGNAL)
        }

        override fun onDispose() {
            view.removeOnAttachStateChangeListener(this)
        }
    }

    companion object {

        val SIGNAL = Any()
    }

}
