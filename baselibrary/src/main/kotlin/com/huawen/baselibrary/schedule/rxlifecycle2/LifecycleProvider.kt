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

package com.huawen.baselibrary.schedule.rxlifecycle2


import javax.annotation.CheckReturnValue

import io.reactivex.Observable

/**
 * Common base interface for activity and fragment lifecycle providers.
 *
 *
 * Useful if you are writing utilities on top of rxlifecycle-components
 * or implementing your own component not supported by this library.
 */
interface LifecycleProvider<E> {
    /**
     * @return a sequence of lifecycle events
     */
    @CheckReturnValue
    fun lifecycle(): Observable<E>

    /**
     * Binds a source until a specific event occurs.
     *
     * @param event the event that triggers unsubscription
     * @return a reusable [LifecycleTransformer] which unsubscribes when the event triggers.
     */
    @CheckReturnValue
    fun <T> bindUntilEvent(event: E): LifecycleTransformer<T>

    /**
     * Binds a source until the next reasonable event occurs.
     *
     * @return a reusable [LifecycleTransformer] which unsubscribes at the correct time.
     */
    @CheckReturnValue
    fun <T> bindToLifecycle(): LifecycleTransformer<T>
}
