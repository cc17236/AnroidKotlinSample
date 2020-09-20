package com.huawen.baselibrary.schedule.rxresult2

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.Nullable
import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

internal class ActivitiesLifecycleCallbacks(val application: Application) {
    @Volatile
    @get:Nullable
    var liveActivity: Activity? = null
    var activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks? = null

    /**
     * Emits just one time a valid reference to the current activity
     * @return the current activity
     */
    @Volatile
    var emitted = false
    val oLiveActivity: Observable<Activity>
        get() {
            emitted = false
            return Observable.interval(50, 50, TimeUnit.MILLISECONDS)
                    .map(object : Function<Long, Any> {
                        override fun apply(t: Long): Any {
                            return if (liveActivity == null) 0 else liveActivity!!
                        }
                    })
                    .takeWhile { candidate ->
                        var continueEmitting = true
                        if (emitted) continueEmitting = false
                        if (candidate is Activity) emitted = true
                        continueEmitting
                    }
                    .filter { candidate -> candidate is Activity }
                    .map { activity -> activity as Activity }
        }

    init {
        registerActivityLifeCycle()
    }

    private fun registerActivityLifeCycle() {
        if (activityLifecycleCallbacks != null) application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)

        activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                liveActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                liveActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                liveActivity = null
            }

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        }

        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

}
