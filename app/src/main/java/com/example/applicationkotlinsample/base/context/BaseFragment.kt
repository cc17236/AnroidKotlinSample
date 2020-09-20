package cn.aihuaiedu.school.base.context

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import cn.aihuaiedu.school.base.BaseContract
import cn.aihuaiedu.school.base.RxPresenter
import com.example.applicationkotlinsample.BuildConfig
import com.huawen.baselibrary.jni.AppVerify
import com.huawen.baselibrary.schedule.BaseRxFragmentHost
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.utils.DebugerCaller
import com.huawen.baselibrary.utils.ToastUtils
import com.umeng.analytics.MobclickAgent
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.concurrent.TimeUnit

abstract class BaseFragment<in V : BaseContract.BaseView, P : BaseContract.BasePresenter<V>>() : BaseRxFragmentHost(),
    BaseContract.BaseView {

    //网络逻辑处理实例
    protected var mPresenter: P? = null

    protected var window: Window
        private set(value) {}
        get() {
            return activity!!.window
        }

    //提示框
    private var loading: LoadingDialog? = null

    var isShowing = false

    private var isInit = false

    override fun showError(e: Throwable) {
        disMissLoading()
        showMsgToast(e.message ?: "未知错误")
        (mPresenter as? RxPresenter<*>)?.unsubscribeAllRequest()
    }

    override fun showError(errMsg: String?) {
        disMissLoading()
        showMsgToast(errMsg ?: "未知错误")
        (mPresenter as? RxPresenter<*>)?.unsubscribeAllRequest()
    }

    override fun showError(@StringRes errMsg: Int) {
        disMissLoading()
        showMsgToast(getString(errMsg))
        (mPresenter as? RxPresenter<*>)?.unsubscribeAllRequest()
    }

    private fun showMsgToast(str: String) {
        when (getCurrentEvent()) {
            ActivityEvent.STOP, ActivityEvent.DESTROY, ActivityEvent.PAUSE -> {
                return
            }
            else -> {
                if (str.trim() == "未知错误") {
                    if (BuildConfig.DEBUG) {
                        val traceElement = DebugerCaller.getCallerStackTraceElement()
                        var logTag = "crashChecker"
                        var logBody = ""
                        logTag += "(方法名:${traceElement.methodName})"
                        val taskName = StringBuilder()
                        taskName.append("(")
                            .append(traceElement.fileName).append(":")
                            .append(traceElement.lineNumber).append(")")
                        logBody = taskName.toString() + logBody
                        ToastUtils.showDuration("$logTag ==== $logBody", 10 * 1000)
                    }
                    return
                }
                ToastUtils.showShort(str)
            }
        }
    }

    override fun complete() {

    }

    final fun getBaseActivity(): BaseActivity<*, *>? {
        return activity as? BaseActivity<*, *>
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val isValidate = AppVerify.isNativeValidate()
        if (isValidate) {
            mPresenter = initPresenter()
            mPresenter!!.attachView(this as V)
            return inflater!!.inflate(getLayoutId(), container, false)
        } else {
            return View(container?.context)
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is BaseActivity<*, *>) {
            val activity = activity as BaseActivity<*, *>
            val cutout = activity.hasCutoutScreen()
            if (cutout) {
                hasCutout()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }




    open protected fun hasCutout() {

    }

    //用于倒计时
    var countDownDisposable: Disposable? = null

    fun countDownTask(duration: (text: Int) -> Unit) {
        //先初始化
        countDownDisposable?.dispose()
        countDownDisposable = null

//        val count = (TOTAL_TIME).toInt()
        countDownDisposable = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
//                .take((count + 1).toLong()) //设置总共发送的次数
            .map(Function<Long, Int> {
                return@Function (it / 1000).toInt()
            })
            .subscribeOn(Schedulers.computation())
            // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
            .subscribe({
                duration.invoke(it)
            }, {
                it?.printStackTrace()
            }, {
                countDownDisposable?.dispose()
                countDownDisposable = null
            })
    }

    override fun onDestroy() {
        unLazySubscribeCreate()
        unSubscribeCreate()
        super.onDestroy()
        umengEnd()
        mPresenter!!.detachView()
        EventBus.getDefault().unregister(this)
    }

    final private fun unSubscribeCreate() {
        if (createSubscription != null) {
            createSubscription!!.dispose()
        }
    }

    final private fun unLazySubscribeCreate() {
        if (lazySubscription != null) {
            lazySubscription!!.dispose()
            lazySubscription = null
        }
        lazyDisposable = null
    }

    private var createSubscription: CompositeDisposable? = null
    final private fun addCreateSubscribe(subscription: Disposable?) {
        if (subscription == null) return
        if (createSubscription == null) {
            createSubscription = CompositeDisposable()
        }
        createSubscription!!.add(subscription)
    }


    private var lazySubscription: CompositeDisposable? = null
    private var lazyDisposable: ObservableEmitter<BaseFragment<V, P>>? = null
    final private fun addLazySubscribe(subscription: Disposable?) {
        if (subscription == null) return
        if (lazySubscription == null) {
            lazySubscription = CompositeDisposable()
        }
        lazySubscription!!.add(subscription)
    }


    private var postInvalidate = false
    override fun postInvalidate() {
        postInvalidate = true
        if (userVisibleHint) {
            if (isInit) {
                onShow(postInvalidate)
                hiddenState = false
                postInvalidate = false
            }
        }
    }

    final fun lazyInvalidate() {
        val disposable = Observable.create(ObservableOnSubscribe<BaseFragment<V, P>> {
            lazyDisposable = it
        })
            .delay(200.toLong(), TimeUnit.MILLISECONDS)
            .compose(bindToLifecycle())
            .doOnComplete {
                doAsync {
                    uiThread {
                        postInvalidate()
                    }
                }
            }
            .subscribe()
        addLazySubscribe(disposable)
    }

    protected fun hasEventQueue(): Boolean {
        if (postInvalidate) return true
        return false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isShowing = isVisibleToUser
        if (isVisibleToUser) {
            if (isInit) {
                if (hiddenState) {
                    onShow(postInvalidate)
                    hiddenState = false
                }
            }
            postInvalidate = false
        } else {
            unLazySubscribeCreate()
            if (!hiddenState) {
                onHidden()
                hiddenState = true
            }

        }
    }


    fun canCallOnShow(): Boolean {
        return hiddenState
    }

    private var hiddenState = true

    open fun onHidden() {

    }

    open fun onShow(needInvalidate: Boolean) {

    }

    final override fun networkChange() {
        mPresenter!!.networkChange()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val isValidate = AppVerify.isNativeValidate()
        if (isValidate) {
            addCreateSubscribe(
                Observable.create(ObservableOnSubscribe<Unit> {
                    configView()
                    it.onNext(Unit)
                })?.compose(bindToLifecycle())
                    ?.subscribe({
                        initData()
                        isInit = true
                        if (lazyDisposable != null) {
                            lazyDisposable?.onComplete()
                        }
                    }, {
                        it?.printStackTrace()
                        (this as? BaseContract.BaseView)?.showError(it)
                    })
            )
        }

    }

    override fun onResume() {
        super.onResume()
        umengStart()
        mPresenter?.executeQueue()
    }


    override fun onPause() {
        super.onPause()
        umengEnd()
    }

    private var flag = false

    private fun umengStart() {
        if (!flag) {
            flag = true
            try {
                MobclickAgent.onPageStart("Fragment${javaClass.simpleName}")
            } catch (e: Exception) {
            }

        }
    }

    private fun umengEnd() {
        if (flag) {
            flag = false
            try {
                MobclickAgent.onPageEnd("Fragment${javaClass.simpleName}")
            } catch (e: Exception) {
            }

        }
    }

    fun <V : View> find(id: Int): V? {
        return view?.findViewById(id)
    }

    fun findview(id: Int): View? {
        return find(id)
    }

    /**
     * 显示加载中弹窗
     */
    override fun showLoading() {
        showLoading(false)
    }

    fun showLoading(cancelFlag: Int) {
        showLoading(false, cancelFlag)
    }

    fun setCustomDialogStyle(fun0: (BaseActivity<*, *>?) -> BaseDialog, fun1: (BaseDialog) -> Unit) {
        this.customLoadingFunc0 = fun0
        this.customLoadingFunc1 = fun1
    }

    private var customLoadingFunc0: ((BaseActivity<*, *>?) -> BaseDialog)? = null
    private var customLoadingFunc1: ((BaseDialog) -> Unit)? = null
    private var customDialog: BaseDialog? = null

    fun showLoading(useAnim: Boolean) {
        showLoading(useAnim, -1)
    }

    fun showLoading(useAnim: Boolean, cancelFlag: Int) {
        try {
            if (useAnim) {
                if (customDialog?.isVisible == true) {
                    return
                }
                if (customDialog == null) {
                    customDialog = customLoadingFunc0?.invoke(activity as? BaseActivity<*, *>)
                    if (cancelFlag >= 0) {
                        customDialog?.setCancelable(false)
                    } else {
                        customDialog?.setCancelable(true)
                    }
                } else {
                    if (customDialog!!.isAdded || customDialog!!.isVisible || customDialog!!.isRemoving) {
                        return
                    }
                }
                customDialog?.setCallBack(object : BaseDialog.OnDisMissCallBack {
                    override fun disMiss() {
//                        (mPresenter as? RxPresenter<V>)?.cancelCurrent()
                    }
                })
                if (customDialog?.isVisible == false) {
                    try {
                        customDialog?.show(fragmentManager(), javaClass)
                    } catch (e: Exception) {
                    }
                }
//                    customDialog?.show(activity?.fragmentManager, javaClass)
                return
            }

            if (loading?.isVisible == true) {
                return
            }
            if (loading != null) {
//                if (loading!!.isAdded||loading!!.isVisible||loading!!.isRemoving){
                return
//                }
            }
            loading = loading ?: LoadingDialog()
            if (cancelFlag >= 0) {
                loading?.setCancelable(false)
            } else {
                loading?.setCancelable(true)
            }
            loading?.setCallBack(object : BaseDialog.OnDisMissCallBack {
                override fun disMiss() {
//                    (mPresenter as? RxPresenter<V>)?.cancelCurrent()
                }
            })
            if (loading?.isVisible == false) {
                try {
                    loading?.show(fragmentManager(), javaClass)
                } catch (e: Exception) {
                }
            }

        } catch (e: Exception) {
        }

    }

    private var managerType = 0
    fun setManagerType(@IntRange(from = 0, to = 1) type: Int) {
        managerType = type
    }

    private fun fragmentManager(): androidx.fragment.app.FragmentManager {
        if (managerType == 0)
            return activity!!.supportFragmentManager
        else return childFragmentManager
    }

    override fun disMissLoading() {
        try {
            customDialog?.dismiss(fragmentManager(), javaClass)
        } catch (e: Exception) {
        }
        try {
            if (loading == null) return
            loading?.dismiss(fragmentManager(), javaClass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun reload() {

    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun initPresenter(): P
    protected abstract fun configView()
    protected abstract fun initData()


}