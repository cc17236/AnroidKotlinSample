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

import com.huawen.baselibrary.schedule.navi2.Event
import com.huawen.baselibrary.schedule.navi2.Listener
import com.huawen.baselibrary.schedule.navi2.NaviComponent
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

internal class NaviOnSubscribe<T>(val component: NaviComponent, val event: Event<T>) : ObservableOnSubscribe<T> {

    override fun subscribe(emitter: ObservableEmitter<T>) {
        val listener = EmitterListener(emitter)
        emitter.setDisposable(listener)
        component.addListener(event, listener)
    }

    internal inner class EmitterListener(val emitter: ObservableEmitter<T>) : AtomicBoolean(), Listener<T>, Disposable {

        override fun call(t: T) {
            emitter.onNext(t)
        }

        override fun dispose() {
            if (compareAndSet(false, true)) {
                component.removeListener(this)
            }
        }

        override fun isDisposed(): Boolean {
            return get()
        }
    }
}
