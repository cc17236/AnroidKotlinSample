package com.huawen.baselibrary.schedule.rxlifecycle2.android.lifecycle


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.annotation.CheckResult
import com.huawen.baselibrary.schedule.navi2.Event

import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleProvider
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleTransformer
import com.huawen.baselibrary.schedule.rxlifecycle2.RxLifecycle

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Wraps a [LifecycleOwner] so that it can be used as a [LifecycleProvider]. For example,
 * you can do
 * <pre>`LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(this);
 * myObservable
 * .compose(provider.bindLifecycle())
 * .subscribe();
`</pre> *
 * where `this` is a `android.arch.lifecycle.LifecycleActivity` or
 * `android.arch.lifecycle.LifecycleFragment`.
 */
class AndroidLifecycle private constructor(owner: LifecycleOwner) : LifecycleProvider<Lifecycle.Event>,
    LifecycleObserver {

    private val lifecycleSubject = BehaviorSubject.create<Lifecycle.Event>()

    init {
        owner.lifecycle.addObserver(this)
    }

    @CheckResult
    override fun lifecycle(): Observable<Lifecycle.Event> {
        return lifecycleSubject.hide()
    }

    @CheckResult
    override fun <T> bindUntilEvent(event: Lifecycle.Event): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent<T, Lifecycle.Event>(lifecycleSubject, event)
    }

    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroidLifecycle.bindLifecycle(lifecycleSubject)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    internal fun onEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        lifecycleSubject.onNext(event)
        if (event == Lifecycle.Event.ON_DESTROY) {
            owner.lifecycle.removeObserver(this)
        }
    }

    companion object {

        fun createLifecycleProvider(owner: LifecycleOwner): LifecycleProvider<Lifecycle.Event> {
            return AndroidLifecycle(owner)
        }
    }
}
