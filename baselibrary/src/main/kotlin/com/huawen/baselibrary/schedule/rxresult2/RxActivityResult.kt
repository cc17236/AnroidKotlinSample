/*
 * Copyright 2016 Víctor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawen.baselibrary.schedule.rxresult2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


object RxActivityResult {
    @SuppressLint("StaticFieldLeak")
    internal var activitiesLifecycle: ActivitiesLifecycleCallbacks? = null

    fun register(application: Application) {
        activitiesLifecycle = ActivitiesLifecycleCallbacks(application)
    }

    fun <T : Activity> on(activity: T): Builder<T> {
        return Builder(activity)
    }

    fun <T : androidx.fragment.app.Fragment> on(fragment: T): Builder<T> {
        return Builder(fragment)
    }

    fun <T : android.app.Fragment> on(fragment: T): Builder<T> {
        return Builder(fragment)
    }

    fun <T : android.app.Service> on(service: T): Builder<T> {
        return Builder(service)
    }

    class Builder<T>(t: T) {
        internal var clazz: Class<T>? = null
        internal val subject = PublishSubject.create<Result<T>>()
        private var uiTargetActivity: Boolean = false

        init {
            if (activitiesLifecycle == null) {
                throw IllegalStateException(Locale.RX_ACTIVITY_RESULT_NOT_REGISTER)
            }

            if (t != null) {
                this.clazz = (t as? Any)?.javaClass as? Class<T>
                this.uiTargetActivity = t is Activity
            }

        }

        @JvmOverloads
        fun startIntentSender(
            intentSender: IntentSender, @Nullable fillInIntent: Intent,
            flagsMask: Int,
            flagsValues: Int,
            extraFlags: Int, @Nullable options: Bundle? = null
        ): Observable<Result<T>> {
            val requestIntentSender =
                RequestIntentSender(intentSender, fillInIntent, flagsMask, flagsValues, extraFlags, options)
            return startHolderActivity(requestIntentSender, null)
        }

        @JvmOverloads
        fun startIntent(intent: Intent?, @Nullable onPreResult: OnPreResult<*>? = null): Observable<Result<T>> {
            return startHolderActivity(Request(intent), onPreResult)
        }

        @SuppressLint("CheckResult")
        private fun startHolderActivity(request: Request, @Nullable onPreResult: OnPreResult<*>?): Observable<Result<T>> {

            val onResult = if (uiTargetActivity) onResultActivity() else onResultFragment()
            request.setOnResult(onResult)
            if (onPreResult != null)
                request.setOnPreResult(onPreResult)

            HolderActivity.setRequest(request)

            activitiesLifecycle!!.oLiveActivity.subscribe { activity ->
                activity.startActivity(
                    Intent(activity, HolderActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                )
            }

            return subject
        }

        private fun onResultActivity(): OnResult {
            return object : OnResult {

                override fun response(requestCode: Int, resultCode: Int, data: Intent?) {
                    if (activitiesLifecycle!!.liveActivity == null) return
                    //If true it means some other activity has been stacked as a secondary process.
                    //Wait until the current activity be the target activity
                    if (activitiesLifecycle!!.liveActivity!!.javaClass != clazz) {
                        return
                    }

                    val activity = activitiesLifecycle!!.liveActivity as? T
                    if (activity != null)
                        subject.onNext(Result<T>(activity, requestCode, resultCode, data))
                    subject.onComplete()
                }

                override fun error(throwable: Throwable) {
                    subject.onError(throwable)
                }
            }
        }

        private fun onResultFragment(): OnResult {
            return object : OnResult {
                override fun response(requestCode: Int, resultCode: Int, data: Intent?) {
                    if (activitiesLifecycle!!.liveActivity == null) return
                    val activity = activitiesLifecycle!!.liveActivity
                    val fragmentActivity = activity as androidx.fragment.app.FragmentActivity?
                    val fragmentManager = fragmentActivity!!.supportFragmentManager

                    val targetFragment = getTargetFragment(fragmentManager.fragments)

                    if (targetFragment != null) {
                        subject.onNext(Result<T>(targetFragment as T, requestCode, resultCode, data))
                        subject.onComplete()
                    }
                    //If code reaches this point it means some other activity has been stacked as a secondary process.
                    //Do nothing until the current activity be the target activity to get the associated fragment
                }

                //
                override fun error(throwable: Throwable) {
                    subject.onError(throwable)
                }
            }
        }

        @Nullable
        internal fun getTargetFragment(fragments: List<androidx.fragment.app.Fragment>?): androidx.fragment.app.Fragment? {
            if (fragments == null) return null

            for (fragment in fragments) {
                if (fragment != null && fragment.isVisible && fragment.javaClass == clazz) {
                    return fragment
                } else if (fragment != null && fragment.isAdded && fragment.childFragmentManager != null) {
                    val childFragments = fragment.childFragmentManager.fragments
                    val candidate = getTargetFragment(childFragments)
                    if (candidate != null) return candidate
                }
            }

            return null
        }
    }
}
