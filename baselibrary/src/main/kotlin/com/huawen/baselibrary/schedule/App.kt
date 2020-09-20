package com.huawen.baselibrary.schedule

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.huawen.baselibrary.delegate.AppDelegate
import com.huawen.baselibrary.delegate.DatabaseDelegate
import com.huawen.baselibrary.schedule.host.BaseApplication
import org.jetbrains.anko.doAsync
import java.util.*


open class App : AppDelegate {
    override fun stackSize(): Int {
        return getStack()?.size ?: 0
    }

    override fun cleanOpen(clazz: Class<out Activity>, activity: Activity?) {
        var ctx: Context? = null
        if (activity != null) {
            ctx = activity
        } else
            ctx = getContext()
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (ctx!=null)
        intent.setClass(ctx, clazz)
        ctx?.startActivity(intent)
        (ctx as? Activity)?.overridePendingTransition(0, 0)
    }

    override fun finishStack(clazz: Class<out Activity>) {
        val stack = getStack()
        loop@ for (i in 0 until (stack?.size ?: 0)) {
            val act = stack?.get(i)
            val actName = act?.javaClass?.simpleName?.toString()
            var flag = false
            if (actName?.equals(clazz.simpleName.toString()) == true) {
                flag = true
            }
            if (flag) {
                continue@loop
            }
            act?.finish()
        }
    }

    override fun finishAffinityStack(clazz: Class<out Activity>) {
        val stack = getStack()
        loop@ for (i in 0 until (stack?.size ?: 0)) {
            val act = stack?.get(i)
            val actName = act?.javaClass?.simpleName?.toString()
            var flag = false
            if (actName?.equals(clazz.simpleName.toString()) == true) {
                flag = true
            }
            if (flag) {
                continue@loop
            }
            act?.finishAffinity()
        }
    }

    override fun queryLaunch(packageName: String, activity: Activity?, packageManager: PackageManager?): Boolean {
        val launchIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val mainComponentName = launchIntent?.component
        val currComponentName = activity?.componentName
        if (mainComponentName?.toString().equals(currComponentName?.toString())) {
            return true
        }
        return false
    }

    override fun <T : BaseApplication?> getApp(): T {
        return getContext() as T
    }

    override fun createDelegateBind(
        app: Application,
        fun0: () -> Stack<Activity>,
        dbDelegate: DatabaseDelegate?
    ): BaseApplication? {
        ctx = app
        this.fun0 = fun0
        return ctx as? BaseApplication
    }

    private var ctx: Application? = null
    private var fun0: (() -> Stack<Activity>)? = null
    override fun getContext(): Context? {
        if (ctx is Application) return ctx else return ctx?.applicationContext
    }


    override fun getDbDelegate(): DatabaseDelegate? {
        return (getContext() as? BaseApplication)?.initDataBase()
    }

    override fun killSelf() {

    }

    override fun lowMemoryConfigure() {
    }

    final override fun <T : Activity> queryStack(clazz: Class<T>): T? {
        val stack = getStack()
        loop@ for (i in 0 until (stack?.size ?: 0)) {
            val act: Activity? = stack?.get(i) ?: continue@loop
            if (act?.javaClass?.simpleName?.toString().equals(clazz.simpleName.toString())) {
                return act as T
            }
        }
        return null
    }

    final override fun cleanStack() {
        cleanStackWithoutInstance(null)
    }

    final override fun cleanStackWithoutHashStack(clazz: Class<out Activity>, hashCode: Int) {
        val stack = getStack()
        loop@ for (i in 0 until (stack?.size ?: 0)) {
            val act = stack?.get(i)
            val actName = act?.javaClass?.simpleName?.toString()
            if (actName?.equals(clazz.simpleName.toString()) == true) {
                if (act.hashCode() == hashCode) {
                    continue
                } else {
                    act.finish()
                }
            }
        }
    }


    final override fun cleanStackWithoutInstance(vararg clazz: Class<out Activity>?,retry:Boolean) {
        val stack = getStack()
        val clazzList = clazz as? Array<Class<Activity>?>
        loop@ for (i in 0 until (stack?.size ?: 0)) {
            if (stack?.size?:0<=i){
                if (retry){
                    continue@loop
                }else{
                    doAsync {
                        cleanStackWithoutInstance(*clazz,retry = true)
                    }
                    return
                }
            }
            val act = stack?.getOrNull(i)?:continue@loop
            val actName = act.javaClass.simpleName.toString()
            var flag = false
            if (clazzList != null) {
                clazzList.forEach {
                    if (actName.equals(it?.simpleName.toString()) == true) {
                        flag = true
                        return@forEach
                    }
                }
            }
            if (flag) {
                continue@loop
            }
            act.finish()
        }
    }


    final override fun getStack(): Stack<Activity>? {
        return fun0?.invoke()
    }

    override fun ifRequiredNecessaryInheritSplashHost(): Boolean {
        return getApp<BaseApplication>()?.necessaryInheritSplashHost()
    }

    companion object {
        var delegate: AppDelegate? = null
        fun getAppDelegate(): AppDelegate {
            if (delegate == null) {
                delegate = App()
            }
            return delegate!!
        }
    }
}