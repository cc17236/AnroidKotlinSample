/**
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

import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleTransformer
import com.huawen.baselibrary.schedule.rxlifecycle2.OutsideLifecycleException

import androidx.annotation.CheckResult
import com.huawen.baselibrary.schedule.rxlifecycle2.RxLifecycle
import io.reactivex.Observable
import io.reactivex.functions.Function

import com.huawen.baselibrary.schedule.rxlifecycle2.internal.Preconditions


class RxLifecycleAndroid private constructor() {

    init {
        throw AssertionError("No instances")
    }

    companion object {

        /**
         * Binds the given source to an Activity lifecycle.
         *
         *
         * This helper automatically determines (based on the lifecycle sequence itself) when the source
         * should stop emitting items. In the case that the lifecycle sequence is in the
         * creation phase (CREATE, START, etc) it will choose the equivalent destructive phase (DESTROY,
         * STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
         * for example, if used in PAUSE, it will unsubscribe in STOP.
         *
         *
         * Due to the differences between the Activity and Fragment lifecycles, this method should only
         * be used for an Activity lifecycle.
         *
         * @param lifecycle the lifecycle sequence of an Activity
         * @return a reusable [LifecycleTransformer] that unsubscribes the source during the Activity lifecycle
         */
        @CheckResult
        fun <T> bindActivity(lifecycle: Observable<ActivityEvent>): LifecycleTransformer<T> {
            return RxLifecycle.bind(lifecycle, ACTIVITY_LIFECYCLE)
        }

        /**
         * Binds the given source to a Fragment lifecycle.
         *
         *
         * This helper automatically determines (based on the lifecycle sequence itself) when the source
         * should stop emitting items. In the case that the lifecycle sequence is in the
         * creation phase (CREATE, START, etc) it will choose the equivalent destructive phase (DESTROY,
         * STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
         * for example, if used in PAUSE, it will unsubscribe in STOP.
         *
         *
         * Due to the differences between the Activity and Fragment lifecycles, this method should only
         * be used for a Fragment lifecycle.
         *
         * @param lifecycle the lifecycle sequence of a Fragment
         * @return a reusable [LifecycleTransformer] that unsubscribes the source during the Fragment lifecycle
         */
        @CheckResult
        fun <T> bindFragment(lifecycle: Observable<FragmentEvent>): LifecycleTransformer<T> {
            return RxLifecycle.bind(lifecycle, FRAGMENT_LIFECYCLE)
        }

        /**
         * Binds the given source to a View lifecycle.
         *
         *
         * Specifically, when the View detaches from the window, the sequence will be completed.
         *
         *
         * Warning: you should make sure to use the returned Transformer on the main thread,
         * since we're binding to a View (which only allows binding on the main thread).
         *
         * @param view the view to bind the source sequence to
         * @return a reusable [LifecycleTransformer] that unsubscribes the source during the View lifecycle
         */
        @CheckResult
        fun <T> bindView(view: View): LifecycleTransformer<T> {
            Preconditions.checkNotNull(view, "view == null")
            return RxLifecycle.bind(Observable.create(ViewDetachesOnSubscribe(view)))
        }

        // Figures out which corresponding next lifecycle event in which to unsubscribe, for Activities
        private val ACTIVITY_LIFECYCLE = Function<ActivityEvent, ActivityEvent> { lastEvent ->
            when (lastEvent) {
                ActivityEvent.CREATE -> ActivityEvent.DESTROY
                ActivityEvent.START -> ActivityEvent.STOP
                ActivityEvent.RESUME -> ActivityEvent.PAUSE
                ActivityEvent.PAUSE -> ActivityEvent.STOP
                ActivityEvent.STOP -> ActivityEvent.DESTROY
                ActivityEvent.DESTROY -> throw OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.")
                else -> throw UnsupportedOperationException("Binding to $lastEvent not yet implemented")
            }
        }

        // Figures out which corresponding next lifecycle event in which to unsubscribe, for Fragments
        private val FRAGMENT_LIFECYCLE = Function<FragmentEvent, FragmentEvent> { lastEvent ->
            when (lastEvent) {
                FragmentEvent.ATTACH -> FragmentEvent.DETACH
                FragmentEvent.CREATE -> FragmentEvent.DESTROY
                FragmentEvent.CREATE_VIEW -> FragmentEvent.DESTROY_VIEW
                FragmentEvent.START -> FragmentEvent.STOP
                FragmentEvent.RESUME -> FragmentEvent.PAUSE
                FragmentEvent.PAUSE -> FragmentEvent.STOP
                FragmentEvent.STOP -> FragmentEvent.DESTROY_VIEW
                FragmentEvent.DESTROY_VIEW -> FragmentEvent.DESTROY
                FragmentEvent.DESTROY -> FragmentEvent.DETACH
                FragmentEvent.DETACH -> throw OutsideLifecycleException("Cannot bind to Fragment lifecycle when outside of it.")
                else -> throw UnsupportedOperationException("Binding to $lastEvent not yet implemented")
            }
        }
    }

}
