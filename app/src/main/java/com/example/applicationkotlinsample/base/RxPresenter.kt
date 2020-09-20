package cn.aihuaiedu.school.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import cn.aihuaiedu.school.base.event.IOMessageEvent
import cn.aihuaiedu.school.base.event.MessageEvent
import cn.aihuaiedu.school.base.event.UIMessageEvent
import cn.aihuaiedu.school.base.gson.HttpErrorHandler
import com.example.applicationkotlinsample.DisposalApp
import com.huawen.baselibrary.schedule.BaseRxActivityHost
import com.huawen.baselibrary.schedule.BaseRxFragmentHost
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleProvider
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleTransformer
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxAppCompatActivity
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxFragment
import com.huawen.baselibrary.schedule.rxresult2.RxActivityResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.internals.AnkoInternals


/**
 * 基于Rx的Presenter封装,控制订阅的生命周期(presenter的基类，对应mvp中的presenter)
 */

open class RxPresenter<T : BaseContract.BaseView> : BaseContract.BasePresenter<T>, HttpErrorHandler {
    private var lastEventQueue: ArrayList<MessageEvent> = ArrayList()
    private var pageIndex = 1


    override fun tokenExpired() {
        DisposalApp.app?.exitLogin()
    }


    override fun networkChange() {

    }

    private var mLifecycle: LifecycleTransformer<Any>? = null
    override fun bindLifeCycle(lifecycle: LifecycleTransformer<Any>) {
        mLifecycle = lifecycle
    }

    protected fun <T> lifeCycle(): LifecycleTransformer<T>? {
        return mLifecycle as? LifecycleTransformer<T>
    }

    //View的引用，可操控View的逻辑
    protected var mView: T? = null
    //观察者订阅管理对象
    private var mCompositeSubscription: CompositeDisposable? = null
    //当前最新的订阅者
    private var subscription: Disposable? = null

    /**
     * 取消所有的订阅 Unsubscribes itself and all inner subscriptions.
     */
    private fun unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription!!.dispose()
        }
    }

    fun currentSubscription(): Disposable? {
        return subscription
    }

    fun unsubscribeAllRequest() {
        unSubscribe()
    }

    /**
     * 添加订阅
     * @param subscription
     */
    fun addSubscribe(subscription: Disposable) {
        this.subscription = subscription
        if (mCompositeSubscription?.isDisposed == true) {
            mCompositeSubscription = null
        }
        if (mCompositeSubscription == null) {
            mCompositeSubscription = CompositeDisposable()
        }
        mCompositeSubscription!!.add(subscription)
    }

    override fun attachView(view: T) {
        this.mView = view
        onViewComplete()
    }

    open fun onViewComplete() {

    }

    final internal fun registerPresenterBus() {
        EventBus.getDefault().register(this)
    }

    final internal fun unregisterPresenterBus() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    final public fun onUIEvent(event: UIMessageEvent) {
        if (event.clazz != null) {
            loop@ for (clazz in event.clazz!!) {
                if (clazz.name.equals(this::class.java.name)) {
                    if (!lastEventQueue.contains(event))
                        lastEventQueue.add(event)
                    val state = getLifeCycleState()
                    if (state != null && (state != ActivityEvent.PAUSE && state != ActivityEvent.DESTROY && state != ActivityEvent.STOP)) {
                        doUIEvent(event)
                        if (lastEventQueue.contains(event))
                            lastEventQueue.remove(event)
                    }
                    //如果不在可见范围内,延迟执行
                    return@loop
                }
            }
        } else if (event.signleClazz != null) {
            if (event.signleClazz!!.name.equals(this::class.java.name)) {
                if (!lastEventQueue.contains(event))
                    lastEventQueue.add(event)
                val state = getLifeCycleState()
                if (state != null && (state != ActivityEvent.PAUSE && state != ActivityEvent.DESTROY && state != ActivityEvent.STOP)) {
                    doUIEvent(event)
                    if (lastEventQueue.contains(event))
                        lastEventQueue.remove(event)
                }
                //如果不在可见范围内,延迟执行
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    final public fun onIOEvent(event: IOMessageEvent) {
        if (event.clazz != null) {
            loop@ for (clazz in event.clazz!!) {
                if (clazz.name.equals(this::class.java.name)) {
                    doIOEvent(event)
                    return@loop
                }
            }
        } else if (event.signleClazz != null) {
            if (event.signleClazz!!.name.equals(this::class.java.name)) {
                doIOEvent(event)
            }
        }
    }

    final override fun executeQueue() {
        if (!lastEventQueue.isEmpty()) {
//            Debuger.print(this::class.java)
            val queues = arrayListOf<MessageEvent>()
            queues.addAll(lastEventQueue)
            var idx = 0
            for (queue in queues) {
                if (queue is UIMessageEvent) {
                    doUIEvent(queue)
                    lastEventQueue.removeAt(idx)
                    idx--
                } else if (queue is IOMessageEvent) {
                    doAsync {
                        doIOEvent(queue)
                    }
                    lastEventQueue.removeAt(idx)
                    idx--
                }
                idx++
            }
            queues.clear()
        }
    }

    /**
     * 发送ui事件(注意:此事件是指在可见情况下的事件,包括网络请求)
     */
    open fun doUIEvent(event: UIMessageEvent) {

    }

    /**
     * 不可见的情况下,后台线程执行任务,当前接收的host(宿主)可能不在可见范围内
     */
    open fun doIOEvent(event: IOMessageEvent) {

    }


    override fun detachView() {
        this.mView = null
        unSubscribe()
        onViewFinished()
        lastEventQueue.clear()
    }

    open fun onViewFinished() {

    }


    /**
     * 取消当前的订阅者，当前的请求也会中断
     */
    fun cancelCurrent() {
        if (subscription == null) {
            return
        }
        if (!subscription!!.isDisposed) {
            mCompositeSubscription?.delete(subscription!!)
            subscription!!.dispose()
            Log.i(TAG, "cancelCurrent: 您取消了一个订阅")
        }
        subscription = null
    }

    inline fun <reified T : Activity> startActivity(vararg params: Pair<String, Any?>) {
        val ctx = getContext()
        if (ctx == null || !(ctx is Activity)) return
        AnkoInternals.internalStartActivity(ctx, T::class.java, params)
    }

    @SuppressLint("CheckResult")
    inline fun androidx.fragment.app.Fragment.startActivityForResult(
        crossinline fun0: (Intent?) -> Unit,
        intent: Intent? = null
    ) {
        RxActivityResult.on(this).startIntent(intent)
            .subscribe { result ->
                val data = result?.data()
                val resultCode = result?.resultCode()
                // the requestCode using which the activity is started can be received here.
                val requestCode = result.requestCode()
                if (resultCode == Activity.RESULT_OK) {
                    result.targetUI().apply {
                        fun0(data)
                    }
                } else {
                    result.targetUI().apply {
                        fun0(null)
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    inline fun <reified T : Activity> startActivityForResult(
        crossinline fun0: (Intent?) -> Unit,
        vararg params: Pair<String, Any?>
    ) {
        RxActivityResult.on(getContext() as Activity)
            .startIntent(AnkoInternals.createIntent(getContext() as Activity, T::class.java, params))
            .subscribe { result ->
                val data = result.data()
                val resultCode = result.resultCode()
                // the requestCode using which the activity is started can be received here.
                if (resultCode == Activity.RESULT_OK) {
                    result.targetUI().apply {
                        fun0(data)
                    }
                } else {
                    result.targetUI().apply {
                        fun0(null)
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    inline fun <reified T : Activity> startActivityForResult(
        vararg params: Pair<String, Any?>,
        crossinline fun0: (Intent?, result: Int) -> Unit
    ) {
        RxActivityResult.on(getContext() as Activity)
            .startIntent(AnkoInternals.createIntent(getContext() as Activity, T::class.java, params))
            .subscribe { result ->
                val data = result.data()
                val resultCode = result.resultCode()
                // the requestCode using which the activity is started can be received here.
                result.targetUI().apply {
                    fun0(data, resultCode)
                }
            }
    }

    inline fun <reified T : Activity> startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) {
        val ctx = getContext()
        if (ctx == null || !(ctx is Activity)) return
        AnkoInternals.internalStartActivityForResult(ctx, T::class.java, requestCode, params)
    }

    fun getLifeCycleState(): ActivityEvent? {
        if (mView is BaseRxFragmentHost) {
            return (mView as BaseRxFragmentHost).getCurrentEvent()
        } else if (mView is BaseRxActivityHost) {
            return (mView as BaseRxActivityHost).getCurrentEvent()
        }
        return null
    }

    fun getLifecycleProvider(): LifecycleProvider<*>? {
        if (mView is RxFragment) {
            return mView as LifecycleProvider<*>
        } else if (mView is RxAppCompatActivity) {
            return mView as LifecycleProvider<*>
        }
        return null
    }

    final internal fun intents(): Intent? {
        var ctx: Intent? = null
        if (mView is Activity) {
            ctx = (mView as Activity).intent
        } else if (mView is androidx.fragment.app.Fragment) {
            ctx = (mView as androidx.fragment.app.Fragment).activity?.intent
        }
        return ctx
    }

    final fun getString(@StringRes res: Int,vararg format:Any): String {
        return getContext()?.getString(res,*format) ?: ""
    }

    final internal fun bundle(arg: String? = null): Bundle? {
        var ctx: Bundle? = null
        if (mView is androidx.fragment.app.Fragment) {
            if (arg != null) {
                ctx = (mView!! as androidx.fragment.app.Fragment).arguments?.getBundle(arg)
            } else {
                ctx = (mView!! as androidx.fragment.app.Fragment).arguments
            }
        } else if (mView is Activity) {
            if (arg != null) {
                ctx = (mView as Activity).intent?.getBundleExtra(arg)
            } else {
                ctx = (mView as Activity).intent?.extras
            }
        }
        return ctx
    }

    final internal fun dataString(): String? {
        if (mView is Activity) {
            return (mView as Activity).intent.dataString
        }
        return null
    }

    final internal fun data(): Uri? {
        var ctx: Uri? = null
        if (mView is androidx.fragment.app.Fragment) {
        } else if (mView is Activity) {
            ctx = (mView as Activity).intent?.data
        }
        return ctx
    }


    fun getChildContext(): Context? {
        var ctx: Context? = null
        if (mView is androidx.fragment.app.Fragment) {
            ctx = (mView!! as androidx.fragment.app.Fragment).context
        } else if (mView is Activity) {
            ctx = mView as Activity
        } else if (mView is Context) {
            ctx = mView as Context
        }
        return ctx
    }

    fun getFragmentSelf(): androidx.fragment.app.Fragment? {
        if (mView is androidx.fragment.app.Fragment) {
            return (mView!! as androidx.fragment.app.Fragment)
        }
        return null
    }

    fun getActivitySelf(): Activity? {
        if (mView is Activity) {
            return (mView!! as Activity)
        }
        return null
    }


    fun getContext(): Context? {
        var ctx: Context? = null
        if (mView is androidx.fragment.app.Fragment) {
            ctx = (mView!! as androidx.fragment.app.Fragment).activity
        } else if (mView is Activity) {
            ctx = mView as Activity
        } else if (mView is Context) {
            ctx = mView as Context
        }
        return ctx
    }

    companion object {
        private val TAG = "RxPresenter"
    }
}
