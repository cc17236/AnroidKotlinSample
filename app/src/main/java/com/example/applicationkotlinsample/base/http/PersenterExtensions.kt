package cn.aihuaiedu.school.base.http

import android.annotation.SuppressLint
import cn.aihuaiedu.school.base.ObserverImp
import cn.aihuaiedu.school.base.RxPresenter
import cn.aihuaiedu.school.base.context.BaseDialog
import com.example.applicationkotlinsample.DisposalApp
import com.example.applicationkotlinsample.base.http.HttpService
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleTransformer
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.android.FragmentEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxAppCompatActivity
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxFragment
import com.huawen.baselibrary.utils.Debuger
import io.reactivex.Observable
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

inline fun Any?.isLoginLose(): Boolean {
    return DisposalApp.app?.isLoginLose() ?: true
}

@Throws(Exception::class)
inline fun <A> RxPresenter<*>.bindToLifecycle(): LifecycleTransformer<A>? {
    val provider = getLifecycleProvider()
    if (provider is RxAppCompatActivity) {
        return provider.bindToLifecycle()
    } else if (provider is RxFragment) {
        return provider.bindToLifecycle()
    }
    return null
}

@Throws(Exception::class)
inline fun <A> RxPresenter<*>.bindUntilEvent(action: ActivityEvent): LifecycleTransformer<A>? {
    val provider = getLifecycleProvider()
    if (provider is RxAppCompatActivity) {
        return provider.bindUntilEvent(action)
    }
    return null
}

@Throws(Exception::class)
inline fun <A> RxPresenter<*>.bindUntilEvent(action: FragmentEvent): LifecycleTransformer<A>? {
    val provider = getLifecycleProvider()
    if (provider != null) {
        if (provider is RxFragment) {
            return provider.bindUntilEvent(action)
        }
    }
    return null
}


@Throws(Exception::class)
inline fun RxPresenter<*>.lifecycle(): Observable<*>? {
    val provider = getLifecycleProvider()
    if (provider is RxAppCompatActivity) {
        return provider.lifecycle()
    } else if (provider is RxFragment) {
        return provider.lifecycle()
    }
    return null
}


@SuppressLint("CheckResult")
@Suppress("UNCHECKED_CAST")
inline fun <A : Any> BaseDialog.requestApi(
    @NonNull crossinline fun0: ((HttpService?) -> Observable<A>?),
    @NonNull observerImp: ObserverImp<in A>
) {
    try {
        if (DisposalApp.app?.isLoginLose() == true) {
            if (observerImp.shouldApiHandler) {
                observerImp.onError(ExitException())
                return
            }
        }
        val observer = fun0.invoke(HttpManager.getProjectWorkHttpService())
            ?.compose(ScheduleObserverTransformer.instance)
        val disposable = observer?.subscribe({
            if (it == null) {
                observerImp.onError(Throwable("连接错误"))
            } else
                observerImp.onNext(it as A)
        }, {
            observerImp.onError(it)
        })
        if (disposable == null) {
            observerImp.onError(NullPointerException("请求失败,请检查参数是否正确"))
            return
        }
        addSubscribe(disposable)
    } catch (e: Exception) {
        observerImp.onError(NullPointerException("请求失败,请检查参数是否正确"))
        e.printStackTrace()
    }
}


@SuppressLint("CheckResult")
@Suppress("UNCHECKED_CAST")
inline fun <A : Any> RxPresenter<*>.requestApi(
    @NonNull crossinline fun0: (HttpService?) -> Observable<A>?,
    @NonNull observerImp: ObserverImp<in A>
) {
    requestApi(
        try {
            fun0.invoke(HttpManager.getProjectWorkHttpService())
        } catch (e: Exception) {
            Debuger.print(e.toString())
            null
        }, observerImp
    )
}


inline fun <A : Any> RxPresenter<*>.requestApi(
    @NonNull fun0: Observable<A>?,
    @NonNull observerImp: ObserverImp<in A>
) {
    try {
        if (DisposalApp.app?.isLoginLose() == true) {
            if (observerImp.shouldApiHandler) {
                observerImp?.onError(ExitException())
                return
            }
        }
        var observer = fun0?.compose(ScheduleObserverTransformer.instance)
        var disposable: Disposable? = null
        disposable = observer?.subscribe({
            if (it == null) {
                observerImp?.onError(Throwable("连接错误"))
            } else {
                observerImp?.onNext(it as A)
            }
        }, {
            observerImp?.onError(it)
        })
        if (disposable == null) {
            observerImp.onError(NullPointerException("请求失败,请检查参数是否正确"))
            return
        }
        addSubscribe(disposable)
    } catch (e: Exception) {
        observerImp.onError(NullPointerException("请求失败,请检查参数是否正确"))
        e.printStackTrace()
    }
}

