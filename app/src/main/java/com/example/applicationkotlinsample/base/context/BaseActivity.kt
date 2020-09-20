package cn.aihuaiedu.school.base.context

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import cn.aihuaiedu.school.base.BaseContract
import cn.aihuaiedu.school.base.RxPresenter
import com.example.applicationkotlinsample.base.background.BackgroundLibrary
import com.githang.statusbar.StatusBarCompat
import com.huawen.baselibrary.BuildConfig
import com.huawen.baselibrary.jni.AppVerify
import com.huawen.baselibrary.schedule.BaseRxActivityHost
import com.huawen.baselibrary.schedule.rxlifecycle2.android.ActivityEvent
import com.huawen.baselibrary.utils.DebugerCaller
import com.huawen.baselibrary.utils.ToastUtils
import com.notch.NotchCompat
import com.umeng.analytics.MobclickAgent
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.bundleOf
import java.util.concurrent.TimeUnit

/**
 * 所有包含业务的Activity基类
 */
abstract class BaseActivity<in V : BaseContract.BaseView, P : BaseContract.BasePresenter<V>> : BaseRxActivityHost(),
    BaseContract.BaseView {
    //网络逻辑处理实例
    protected var mPresenter: P? = null
    //提示框
    private var loading: LoadingDialog? = null;

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

    //用于倒计时
    var countDownDisposable: Disposable? = null

    fun countDownTask(count: Int = 0, end: (() -> Unit)? = null, duration: (text: Int) -> Unit) {
        //先初始化
        countDownDisposable?.dispose()
        countDownDisposable = null
//        val count = (TOTAL_TIME).toInt()

        var floawable: Flowable<Long>
        if (count > 0) {
            floawable = Flowable.intervalRange(0, count.toLong(), 0, 1, TimeUnit.SECONDS)//设定倒计时次数
        } else {
            floawable = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
        }

        countDownDisposable = floawable.map(Function<Long, Int> {
            return@Function it.toInt()
        })
            .subscribeOn(Schedulers.computation())
            // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
            .subscribe({
                duration.invoke(it)
            }, {
                it?.printStackTrace()
            }, {
                end?.invoke()
                countDownDisposable?.dispose()
                countDownDisposable = null
            })
    }


    private var mHasCutout = false

    fun hasCutoutScreen(): Boolean {
        return mHasCutout
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(bundleOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val isValidate = AppVerify.isNativeValidate()
        //使用布局框架
        BackgroundLibrary.inject(this)
        //设置默认状态栏颜色
        setStatusBar(this)
        super.onCreate(null)
        if (isValidate) {
            mPresenter = initPresenter()
            mPresenter!!.attachView(this as V)
            try {
                setContentView(getLayoutId())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (NotchCompat.hasDisplayCutout(window)) {
                    mHasCutout = true
                    hasCutout()
                }
            } catch (e: Exception) {
            }
            addCreateSubscribe(
                Observable.create(ObservableOnSubscribe<Unit> {
                    doConfig()
                    it.onNext(Unit)
                })?.compose(bindToLifecycle())
                    ?.subscribe({
                        initData()
                    }, {
                        it.printStackTrace()
                        showError(it)
                    })
            )
        }
        EventBus.getDefault().register(this)
    }

    open fun setStatusBar(activity:Activity) {
        StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT, true)
    }


    open fun trimInstance() {

    }

    open protected fun doConfig() {
        configView()
    }

    open protected fun hasCutout() {
//        NotchCompat.immersiveDisplayCutout(window)
    }


    override fun getContext(): Context? {
        return this
    }

    override fun onDestroy() {
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

    private var createSubscription: CompositeDisposable? = null
    final private fun addCreateSubscribe(subscription: Disposable?) {
        if (subscription == null) return
        if (createSubscription == null) {
            createSubscription = CompositeDisposable()
        }
        createSubscription!!.add(subscription)
    }

    private fun umengEnd() {
        if (flag) {
            flag = false
            try {
                if (!isContainsFragment()) {
                    MobclickAgent.onPageEnd("Activity${javaClass.simpleName}")
                }
                MobclickAgent.onPause(this)
            } catch (e: Exception) {
            }

        }
    }


    private var flag = false
    private fun umengStart() {
        if (!flag) {
            flag = true
            try {
                if (!isContainsFragment()) {
                    MobclickAgent.onPageStart("Activity${javaClass.simpleName}")
                }
                MobclickAgent.onResume(this)
            } catch (e: Exception) {
            }

        }
    }


    override fun onPause() {
        super.onPause()
        umengEnd()
    }

    override fun onResume() {
        super.onResume()
        umengStart()
    }

    override fun finish() {
        super.finish()
        umengEnd()
    }

    override fun onBackPressed() {
//        if (fragmentManager.backStackEntryCount > 0) {
//            fragmentManager.popBackStack()
//        } else {

        super.onBackPressed()
//        }
        umengEnd()
    }


    open fun isContainsFragment(): Boolean {
        return false
    }


    final override fun networkChange() {
        mPresenter!!.networkChange()
    }

    fun findviewSafety(id: Int): View? {
        var view: View? = null
        try {
            view = findview(id)
        } catch (e: Exception) {
        }
        return view
    }

    fun setOnclick(vararg ids: Int, click: (View) -> Unit) {
        ids.forEach {
            findview(it)?.setOnClickListener {
                click.invoke(it)
            }
        }
    }

    fun findview(id: Int): View? {
        val t = findViewById<View>(id)
        return t
    }

    inline fun <reified T : View> find(id: Int): T? {
        var t: T? = null
        try {
            t = findViewById(id)
        } catch (e: Exception) {
        }
        return t
    }

    //级联查找
    inline fun <reified T : View> findNestedView(parent: Int, vararg child: Int): T? {
        var t: View? = find(parent)
        loop@ for (i in 0 until child.size) {
            try {
                t = t?.findViewById(child[i])
                if (t != null) {
                    return@loop
                }
            } catch (e: Exception) {
            }

        }
        return t as? T
    }

    /**
     * 显示加载中弹窗
     */
    override fun showLoading() {
        showLoading(false)
    }

    private var customLoadingFunc0: ((BaseActivity<*, *>?) -> BaseDialog)? = null
    private var customLoadingFunc1: ((BaseDialog) -> Unit)? = null
    private var customDialog: BaseDialog? = null

    fun setCustomDialogStyle(fun0: (BaseActivity<*, *>?) -> BaseDialog, fun1: (BaseDialog) -> Unit) {
        this.customLoadingFunc0 = fun0
        this.customLoadingFunc1 = fun1
    }

    fun showLoading(cancelFlag: Int) {
        showLoading(false, cancelFlag)
    }

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
                    customDialog = customLoadingFunc0?.invoke(this)
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
                        customDialog?.show(supportFragmentManager, javaClass)
                    } catch (e: Exception) {
                    }
                }
                return
            }
            if (loading?.isVisible == true) {
                return
            }
            if (loading != null) {
//                if (loading!!.isAdded || loading!!.isVisible || loading!!.isRemoving) {
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
//                        disMissLoading()
//                    (mPresenter as? RxPresenter<V>)?.cancelCurrent()
                }
            })
            if (loading?.isVisible == false) {
                try {
                    loading?.show(supportFragmentManager, javaClass)
                } catch (e: Exception) {
                }
            }

        } catch (e: Exception) {
        }
    }


    override fun disMissLoading() {
        try {
            customDialog?.dismiss(supportFragmentManager, javaClass)
            customDialog?.dismiss()
        } catch (e: Exception) {
        }
        customDialog = null
        try {
            loading?.dismiss(supportFragmentManager, javaClass)
            loading?.dismiss()
        } catch (e: Exception) {
        }
        loading = null
    }

    @SuppressLint("MissingSuperCall")
    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            onActivityResult(data, requestCode, resultCode)
        } catch (e: Exception) {
        }
    }


//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(Emojix.wrap(newBase))
//    }

    /**
     * 类型检查data非空,但实际上会返回空,避免写错而崩溃,屏蔽掉系统方法
     */
    open fun onActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.onActivityResult(requestCode, resultCode, data)
    }

//    @Subscribe
//    open fun onEventMainThread(event: BusEvent) {}

    protected abstract fun getLayoutId(): Int
    protected abstract fun initPresenter(): P
    protected abstract fun configView()
    protected abstract fun initData()

}