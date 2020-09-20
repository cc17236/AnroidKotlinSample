package com.huawen.baselibrary.schedule.host

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.WindowManager
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.huawen.baselibrary.delegate.AppDelegate
import com.huawen.baselibrary.delegate.BaseApp
import com.huawen.baselibrary.delegate.DatabaseDelegate
import com.huawen.baselibrary.excpetion.WrongScheduleTriggerException
import com.huawen.baselibrary.jni.AppVerify
import com.huawen.baselibrary.schedule.App
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.ProcessUtil
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess

/**
 * Created by vicky
 *
 * @Author vicky
 */
abstract class BaseApplication : MultiDexApplication(), BaseApp {

    companion object {
        private var unsafeTrigger = false
        fun getDelegate(): AppDelegate {
            if (!unsafeTrigger)
                illegalDelegateCaller()
            return App.getAppDelegate()
        }

        /**
         * 在Splash中调用delegate的判断
         */
        final fun illegalDelegateCaller() {
            val app = App.delegate?.getApp<BaseApplication>()
            if (app != null && app.necessaryInheritSplashHost() && app.inSplashHost()) {
                throw WrongScheduleTriggerException("在Splash中请勿调用delegate")
            }
        }

        fun unsafeDelegate(): BaseApplication? {
            unsafeTrigger = true
            val appUnsafe = getApp<BaseApplication>()
            unsafeTrigger = false
            return appUnsafe
        }

        fun <T : BaseApplication> getApp(): T {
            return getDelegate().getApp()
        }

        fun getBaseApp(): BaseApplication {
            return getApp()
        }
    }

    private var actStack = Stack<Activity>()
    private var actDisplay: WeakReference<Activity?>? = null

    private var isBackground: Boolean = false


    fun currentActivity(): Activity? {
        if (actDisplay != null && actDisplay!!.get() != null) {
            val display = actDisplay!!.get()
            return display
        }
        return null
    }

    @SuppressLint("MissingSuperCall", "CheckResult")
    final override fun onCreate() {
        if (ProcessUtil.judgeMainProcess(this)) {
            AppVerify.checkCheatSuspect(this)
            val delegateStatics = registerDelegateRef()
            doAsync {
                Flowable.create(FlowableOnSubscribe<AppDelegate> {
                    it.onNext(delegateStatics)
                    it.onComplete()
                }, BackpressureStrategy.BUFFER)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        val app = it?.getApp<BaseApplication>()
                        if ((app is BaseApplication)) {
                            val baseApp = (app as BaseApplication)
                            val screenOffFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                            baseApp.registerReceiver(object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    if (baseApp.isBackground) {
                                        baseApp.isBackground = false
                                        baseApp.notifyForeground()
                                    }
                                }
                            }, screenOffFilter)
                            try {
                                app.loadExtensions()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        val app = it?.getApp<BaseApplication>()
                        if ((app is BaseApplication)) {
                            app.disposalContext()
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .doOnComplete {
                        //todo somthing
                    }
                    .subscribe {
                        val app = it?.getApp<BaseApplication>()
                        if (!(app is BaseApplication)) return@subscribe
                        app.loadingComplete()
                    }
            }
        }

    }

    private fun registerDelegateRef(): AppDelegate {
        val delegateStatics = App.getAppDelegate()
        canStaticallyDelegate(delegateStatics)
        doAsync {
            delegateStatics.createDelegateBind(this@BaseApplication, {
                getAppStack()
            }, initDataBase())
            fastextensions()
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityResumed(activity: Activity?) {
                if (isBackground) {
                    isBackground = false
                    notifyForeground()
                }
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                actStack.remove(activity)
                if (actStack.size == 0) {
                    emptyStack()
                } else if (actStack.size == 1) {
                    val isLaunch = getDelegate().queryLaunch(packageName, actStack[0], packageManager)
                    if (isLaunch && !(activity is BaseSplashActivityHost)) {
                        inSplashHost = true
                        if (delegateStatics?.ifRequiredNecessaryInheritSplashHost() ?: false) {
                            throw WrongScheduleTriggerException("LaunchActivity is not extends BaseSplashActivityHost")
                        }
                    } else if (isLaunch) {
                        inSplashHost = true
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0+ 打开硬件加速
                        activity?.window?.setFlags(
                            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        )
                    }
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } catch (e: Exception) {
                }

                if (actStack.size == 0) {
                    actStack.add(activity)
                    actDisplay = WeakReference(activity)
                    val isLaunch = getDelegate().queryLaunch(packageName, activity, packageManager)
//                    Debuger.print("isLaunch==>" + isLaunch)
                    if (isLaunch && !(activity is BaseSplashActivityHost)) {
                        inSplashHost = true
                        if (delegateStatics != null && delegateStatics.ifRequiredNecessaryInheritSplashHost() ?: false) {
                            throw IllegalArgumentException("LaunchActivity is not extends BaseSplashActivityHost")
                        }
                    } else if (isLaunch) {
                        inSplashHost = true
                    }
                    return
                }
                inSplashHost = false
                actStack.add(activity)
                actDisplay = WeakReference(activity)
            }
        })
        return delegateStatics
    }

    /**
     * 第一个页面的加载速度往往比application创建完delegate还要快,
     * 所以必须在主线程优先完成一些操蛋的扩展库
     */
    open protected fun fastextensions() {

    }

    /**
     * 空栈时关闭虚拟机杀死进程
     * 可在子类中重写以覆盖默认策略
     */
    open fun emptyStack() {
        Debuger.print("程序退出")
        funExit?.invoke()
        exitProcess(0)
    }

    abstract fun exitProcess()

    private var inSplashHost = false
    override fun inSplashHost() = inSplashHost

    final override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true
            notifyBackground()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }

    /**
     * 当前activity回到前台的通知方法
     */
    open fun notifyForeground() {

    }

    /**
     * 当前activity到后台的通知方法
     */
    open fun notifyBackground() {

    }

    /**
     * kotlin派生对象无法通过继承获得,在此方法中拿到委托对象后需要自己存放在自己的派生对象中,已达到App.getInstance()形式的单例调用
     */
    abstract fun canStaticallyDelegate(delegateStatics: AppDelegate)

    /**
     * Activity的活动栈
     */
    final override fun getAppStack(): Stack<Activity> {
        return actStack
    }

    /**
     * 读取插件和扩展,子线程执行,阻塞执行
     */
    abstract fun loadExtensions()

    /**
     * app初始化需要上下文的地方已完成,可以进行一部分回收了
     */
    abstract fun disposalContext()

    /**
     * app初始化完成
     * 可以进行一部分无用的资源或者io流数据库的关闭操作
     */
    open fun loadingComplete() {

    }

    /**
     * 初始化数据库
     */
    open fun initDataBase(): DatabaseDelegate? {
        return null
    }

    /**
     *  限制当前app 启动页必须继承SplashHost
     *  返回false则不限制
     *  因application类是子线程初始化插件和扩展,在第一个活动中有可能并没有初始化完成
     */
    open fun necessaryInheritSplashHost(): Boolean {
        return false
    }

    private var funExit: (() -> Unit)? = null
    fun setOnExitListener(funExit: () -> Unit) {
        this.funExit = funExit
    }
}