package com.huawen.baselibrary.schedule.rxlifecycle2.android.lifecycle


import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleTransformer
import com.huawen.baselibrary.schedule.rxlifecycle2.OutsideLifecycleException
import com.huawen.baselibrary.schedule.rxlifecycle2.RxLifecycle
import io.reactivex.Observable
import io.reactivex.functions.Function


class RxLifecycleAndroidLifecycle private constructor() {

    init {
        throw AssertionError("No instances")
    }

    companion object {

        /**
         * Binds the given source to an Android lifecycle.
         *
         *
         * This helper automatically determines (based on the lifecycle sequence itself) when the source
         * should stop emitting items. In the case that the lifecycle sequence is in the
         * creation phase (ON_CREATE, ON_START, etc) it will choose the equivalent destructive phase (ON_DESTROY,
         * ON_STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
         * for example, if used in ON_PAUSE, it will unsubscribe in ON_STOP.
         *
         * @param lifecycle the lifecycle sequence of an Activity
         * @return a reusable [LifecycleTransformer] that unsubscribes the source during the Activity lifecycle
         */
        @CheckResult
        fun <T> bindLifecycle(lifecycle: Observable<Lifecycle.Event>): LifecycleTransformer<T> {
            return RxLifecycle.bind(lifecycle, LIFECYCLE)
        }

        private val LIFECYCLE = Function<Lifecycle.Event, Lifecycle.Event> { lastEvent ->
            when (lastEvent) {
                Lifecycle.Event.ON_CREATE -> Lifecycle.Event.ON_DESTROY
                Lifecycle.Event.ON_START -> Lifecycle.Event.ON_STOP
                Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_PAUSE
                Lifecycle.Event.ON_PAUSE -> Lifecycle.Event.ON_STOP
                Lifecycle.Event.ON_STOP -> Lifecycle.Event.ON_DESTROY
                Lifecycle.Event.ON_DESTROY -> throw OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.")
                else -> throw UnsupportedOperationException("Binding to $lastEvent not yet implemented")
            }
        }
    }
}
