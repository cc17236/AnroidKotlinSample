package com.example.applicationkotlinsample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import android.view.Gravity
import cn.aihuaiedu.school.base.DeviceUtil
import cn.aihuaiedu.school.base.event.UIMessageEvent
import cn.aihuaiedu.school.base.getMetaData
import cn.aihuaiedu.school.base.http.ExistRequestException
import cn.aihuaiedu.school.base.http.HttpManager
import com.huawen.baselibrary.delegate.AppDelegate
import com.huawen.baselibrary.delegate.DatabaseDelegate
import com.huawen.baselibrary.schedule.App
import com.huawen.baselibrary.schedule.host.BaseApplication
import com.huawen.baselibrary.schedule.rxresult2.RxActivityResult
import com.huawen.baselibrary.utils.*
import com.huawen.baselibrary.views.refresh.OnRefreshInterrupter
import com.huawen.baselibrary.views.refresh.SmartStateRefreshLayout
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import me.jessyan.autosize.AutoSizeConfig
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rx.plugins.RxJavaHooks
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.SocketException
import java.util.*


class DisposalApp : BaseApplication() {

    var registrationID: String = ""

    /**
     * kotlin派生对象无法通过继承获得,在此方法中拿到委托对象后需要自己存放在自己的派生对象中,已达到DisposalApp.delegate形式的单例调用
     * 本质上与BaseApplication.delegate没有太大差别,看各人喜好
     */
    override fun canStaticallyDelegate(delegateStatics: AppDelegate) {
        delegate = delegateStatics
    }


    internal var mDeviceID: String? = null

    /**
     * 第一个页面的加载速度往往比application创建完delegate还要快,
     * 所以必须在主线程优先完成一些操蛋的扩展库
     */
    override fun fastextensions() {
//        Cockroach.install(this, BuglyExceptionHandler.getInstance(this))
//        rxPluginErrorHandler()
        fixTimeout()


        val deviceID = DeviceUtil.getDeviceUUID(this)
        mDeviceID = deviceID  //本机唯一ID
        Utils.init(this) //工具集初始化
        LogUtils.Builder().setLogSwitch(BuildConfig.DEBUG).setBorderSwitch(false)
        ToastUtils.setView(R.layout.toast_layout) //设置Toast弹窗的root布局
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)//Toast弹窗全部居中
        SharedPreferencesUtil.init(
            this,
            "prefs",
            Context.MODE_PRIVATE
        )//初始化一个存储sp键值对的表格引用,(类似iOS对一个plist文件进行初始化,然后所有的键值对按需写入)
        RxActivityResult.register(this)//讲app的activity回调交给RxAR托管,所有的生命周期监控已生成响应式回调
        /**
         * 以下是 AndroidAutoSize 可以自定义的参数, {@link AutoSizeConfig} 的每个方法的注释都写的很详细
         * 使用前请一定记得跳进源码，查看方法的注释, 下面的注释只是简单描述!!!
         */
        AutoSizeConfig.getInstance()
            //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启
            .setCustomFragment(true)//是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
            .setLog(false)
        //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
        //AutoSize 会将屏幕总高度减去状态栏高度来做适配, 如果设备上有导航栏还会减去导航栏的高度
        //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏以及导航栏高度
        //                .setUseDeviceSize(true)
        //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
        //                .setBaseOnWidth(false)
        //设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
        //                .setAutoAdaptStrategy(new AutoAdaptStrategy())
        customAdaptForExternal()
    }


    /**
     * 处理 Timeout 异常
     */
    private fun fixTimeout() {
        try {
            val c = Class.forName("java.lang.Daemons")
            val maxField = c.getDeclaredField("MAX_FINALIZE_NANOS")
            maxField.isAccessible = true
            maxField.set(null, Long.MAX_VALUE)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        try {
            val clazz = Class.forName("java.lang.Daemons\$FinalizerWatchdogDaemon")
            val method = clazz.superclass?.getDeclaredMethod("stop")
            method?.isAccessible = true
            val field = clazz.getDeclaredField("INSTANCE")
            field.isAccessible = true
            method?.invoke(field.get(null))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun rxPluginErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            printCause(it, 1)
        }
        RxJavaHooks.setOnError {
            printCause(it, 2)
        }

    }

    private fun printCause(throwable: Throwable?, type: Int) {
        if (throwable == null) return
        var e: Throwable? = null
        if (throwable is UndeliverableException) {
            e = throwable.cause
        }
        if ((throwable is IOException) || (throwable is SocketException) || (throwable is ExistRequestException)) {
            // fine, irrelevant network problem or API that throws on cancellation
            return
        }
        if (throwable is InterruptedException) {
            // fine, some blocking code was interrupted by a dispose call
            return
        }
        if (throwable is IllegalStateException) {
            // that's a bug in RxJava or in a custom operator
            Thread.currentThread().uncaughtExceptionHandler.uncaughtException(
                Thread.currentThread(),
                throwable.let {
                    if (type == 2) {
                        return@let Exception("当前错误可能并非致命异常,请根据实际情况处理 ${it.message}", it)
                    } else {
                        return@let Exception("当前错误有可能为致命异常,请查找bugly日志 ${it.message}", it)
                    }
                })
            return
        }
        if (
            throwable is RuntimeException
            || throwable is LinkageError
            || throwable is ReflectiveOperationException
        ) {
            // that's likely a bug in the application
            Thread.currentThread().uncaughtExceptionHandler.uncaughtException(
                Thread.currentThread(),
                throwable.let {
                    if (type == 2) {
                        return@let Exception("当前错误可能并非致命异常,请根据实际情况处理 ${it.message}", it)
                    } else {
                        return@let Exception("当前错误有可能为致命异常,请查找bugly日志 ${it.message}", it)
                    }
                })
            return
        }
        Debuger.print("Undeliverable exception received, not sure what to do ${e?.message}")
    }

    private fun customAdaptForExternal() {
        /**
         * [ExternalAdaptManager] 是一个管理外部三方库的适配信息和状态的管理类, 详细介绍请看 [ExternalAdaptManager] 的类注释
         */
//        AutoSizeConfig.getInstance().externalAdaptManager

        //加入的 Activity 将会放弃屏幕适配, 一般用于三方库的 Activity, 详情请看方法注释
        //如果不想放弃三方库页面的适配, 请用 addExternalAdaptInfoOfActivity 方法, 建议对三方库页面进行适配, 让自己的 App 更完美一点
        //                .addCancelAdaptOfActivity(DefaultErrorActivity.class)

        //为指定的 Activity 提供自定义适配参数, AndroidAutoSize 将会按照提供的适配参数进行适配, 详情请看方法注释
        //一般用于三方库的 Activity, 因为三方库的设计图尺寸可能和项目自身的设计图尺寸不一致, 所以要想完美适配三方库的页面
        //就需要提供三方库的设计图尺寸, 以及适配的方向 (以宽为基准还是高为基准?)
        //三方库页面的设计图尺寸可能无法获知, 所以如果想让三方库的适配效果达到最好, 只有靠不断的尝试
        //由于 AndroidAutoSize 可以让布局在所有设备上都等比例缩放, 所以只要你在一个设备上测试出了一个最完美的设计图尺寸
        //那这个三方库页面在其他设备上也会呈现出同样的适配效果, 等比例缩放, 所以也就完成了三方库页面的屏幕适配
        //即使在不改三方库源码的情况下也可以完美适配三方库的页面, 这就是 AndroidAutoSize 的优势
        //但前提是三方库页面的布局使用的是 dp 和 sp, 如果布局全部使用的 px, 那 AndroidAutoSize 也将无能为力
        //经过测试 DefaultErrorActivity 的设计图宽度在 380dp - 400dp 显示效果都是比较舒服的
//                .addExternalAdaptInfoOfActivity(DefaultErrorActivity::class.java!!, ExternalAdaptInfo(true, 400f))
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/" + pid + "/cmdline"));
            var processName = reader.readLine();
            if (processName.isNullOrBlank()) {
                processName = processName.trim()
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null;
    }

    /**
     * 读取插件和扩展,子线程执行,阻塞执行
     */
    override fun loadExtensions() {
        //流量标签,严格模式下所有的流量请求如果不设置tag会爆红,然并卵,第三方库还是一样会爆红的,尤其是垃圾友盟
//        TrafficStats.setThreadStatsTag(0xF00D)

//        val builder = Matrix.Builder(this) // build matrix
//        builder.patchListener(TestPluginListener(this)); // add general pluginListener
//        val dynamicConfig = DynamicConfigImplDemo() // dynamic config
//        // init plugin
//        val ioCanaryPlugin = IOCanaryPlugin(IOConfig.Builder().dynamicConfig(dynamicConfig).build())
//        //add to matrix
//        builder.plugin(ioCanaryPlugin)
//        //init matrix
//        Matrix.init(builder.build())
//        // start plugin
//        ioCanaryPlugin.start()

        //百家云初始化
        var domain = ""
        if (BuildConfig.DEBUG) {
            if (Constant.isUAT) domain = "b62343576" else domain = "b62278297"
        } else {
            domain = "aihua"
        }
        if (BuildConfig.DEBUG) {
            CrashReport.initCrashReport(applicationContext, "192d4442dd", true) //腾讯BUG收集
        }
        //友盟社交平台初始化
        try {
            PlatformConfig.setWeixin("wx49f3420df5c54d48", "539872091e5418fb3efadda23cec4b7b")
            PlatformConfig.setSinaWeibo(
                "785373440",
                "45748cdef8f5ef339b425c24fba1fdf5",
                "http://sns.whalecloud.com"
            )
            PlatformConfig.setQQZone("1106803449", "dM1vO7eXGEaW30Wh")
            UMConfigure.init(
                this,
                getMetaData("UMENG_APPKEY"),
                getMetaData("UMENG_CHANNEL"),
                UMConfigure.DEVICE_TYPE_PHONE, ""
            )
            UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        } catch (e: Exception) {
        }
        // }
    }

    /**
     * app初始化需要上下文的地方已完成,可以进行一部分回收了
     */
    override fun disposalContext() {

    }


    /**
     * 当前activity到后台的通知方法
     */
    override fun notifyForeground() {
        super.notifyForeground()
    }

    /**
     *  限制当前app 启动页必须继承SplashHost
     *  返回false则不限制
     *  因application类是子线程初始化插件和扩展,在第一个活动中有可能并没有初始化完成
     */
    override fun necessaryInheritSplashHost(): Boolean {
        return false
    }

    /**
     * 空栈时关闭虚拟机杀死进程
     * 可重写以覆盖默认策略
     */
    override fun emptyStack() {
//        super.emptyStack()
        try {
            MobclickAgent.onKillProcess(this)
        } catch (e: Exception) {
        }

    }


    /**
     * 获取本地软件版本号
     */
    fun getLocalVersion(): Int {
        var localVersion = 0
        try {
            val packageInfo = applicationContext.packageManager
                .getPackageInfo(packageName, 0)
            localVersion = packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return localVersion
    }

    /**
     * 获取本地软件版本号名称
     */
    fun getLocalVersionName(): String {
        var localVersion = ""
        try {
            val packageInfo = applicationContext
                .packageManager
                .getPackageInfo(packageName, 0);
            localVersion = packageInfo.versionName.toLowerCase().replace("v", "")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return localVersion
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Debuger.print("onLowMemory::onLowMemory")
    }

    override fun onTerminate() {
        super.onTerminate()
        Debuger.print("onLowMemory::onTerminate")
    }

    /**
     * 当前activity回到前台的通知方法
     */
    override fun notifyBackground() {
        super.notifyBackground()
        Debuger.print("onLowMemory::onTrimMemory")
    }


    /**
     * app初始化完成
     * 可以进行一部分无用的资源或者io流数据库的关闭操作
     */
    override fun loadingComplete() {
        super.loadingComplete()
        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                threadPolicyBuilder.penaltyDeathOnNetwork()
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build())
//            TrafficStats.setThreadStatsTag()
        }
    }


//    override fun initDataBase(): DatabaseDelegate? {
//        return AppDataBaseHelper.getInstance(this)
//    }


    /**
     * 退出登录
     */
    fun exitLogin() {
////        //如果登录页不存在于栈中
//        val login = delegate?.queryStack(LoginActivity::class.java)
//        if (login == null) {
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("kickUp", true)
//            startActivity(intent)
//        }
//        cleanLoginInfo()
//        (delegate as? App)?.cleanStackWithoutInstance(
//            LoginActivity::class.java,
//            HomeActivity::class.java
//        )
    }

    fun isLoginLose(): Boolean {
//        if (getCurrentUser() == null) {
//            cleanLoginInfo()
//            return true
//        }
        return false
    }

    /**
     * 打开登录页面
     * @param kickUp 是否是被踢下线的
     */
    @Synchronized
    fun openLogin(kickUp: Boolean = false) {
//        val login = delegate?.queryStack(LoginActivity::class.java)
//        if (login == null) {
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
//            if (kickUp)
//                intent.putExtra("kickUp", true)
//            startActivity(intent)
//        }
//        delegate?.cleanStackWithoutInstance(LoginActivity::class.java, HomeActivity::class.java)
//        cleanLoginInfo()
    }


    fun cleanLoginInfo() {
//        currentUser = null
//        SharedPreferencesUtil.instance?.remove("token")
//        SharedPreferencesUtil.instance?.remove("mobile")
//        HttpManager.getManager()?.cleanCache()
//        dbDelegate?.cleanUser()
//        SharedPreferencesUtil.instance?.putBoolean("isFirstShowPop", false)
//        SharedPreferencesUtil.instance?.putInt("signDay",-1)

    }

    /**
     * 获得栈中最顶层的Activity
     *
     * @param context
     * @return
     */
    fun getTopActivity(): String? {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val runningTaskInfos = manager.getRunningTasks(1)
        if (!(runningTaskInfos?.isEmpty() != false)) {
            return (runningTaskInfos.get(0).topActivity?.className)
        } else
            return null
    }

    /**
     * 退出进程,杀死程序
     */
    override fun exitProcess() {
        try {
            MobclickAgent.onKillProcess(this)
        } catch (e: Exception) {
        }

//        DownloadService.myBinder?.removeRef()
//        DownloadService.myBinder = null
        (delegate as? App)?.cleanStack()
//        CrashReport.closeBugly()
//        (delegate as? App)?.finishAffinityStack(LoginActivity::class.java)
//        (delegate as? App)?.finishAffinityStack(SplashActivity::class.java)
//        (delegate as? App)?.finishAffinityStack(HomeActivity::class.java)
        SharedPreferencesUtil.instance?.commit()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

//    fun setCurrentUser(model: LoginModel) {
//        updateLoginInfo(model)
//    }
//
    fun getCurrentUser(){
//        if (currentUser == null) {
//            currentUser = dbDelegate?.getLoginUser()
//            if (currentUser != null) {
////                CrashReport.setUserId("${currentUser!!.mobile}-${currentUser!!.extraInfo?.customerInfo?.customerName}")
//            }
//        }
//        return currentUser
    }
//
//
//    private var currentUser: LoginModel? = null


//    @Synchronized
//    fun updateLoginInfo(data: LoginModel) {
//        cleanLoginInfo()
//        currentUser = data
//        SharedPreferencesUtil.instance?.putString("token", data.auth_token!!)
//        SharedPreferencesUtil.instance?.putString("mobile", data.mobile!!)
//        dbDelegate?.updateLoginUser(data)
////        CrashReport.setUserId("${data.mobile}-${data.extraInfo?.customerInfo?.customerName ?: ""}")
//    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }


    companion object {
        public val mContext = getBaseApp()
        private const val REFRESH = "GLOBAL_REFRESH"

        /**
         * SmartRefreshLayout 的全局刷新头部和尾部
         */
        init {
            //static 代码段可以防止内存泄露
            //设置全局的Header构建器
            SmartStateRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(R.color.transparent)//全局设置主题颜色
                val header =
                    ClassicsHeader(context).setTimeFormat(DynamicTimeFormat("更新于 %s"))//指定为经典Header，默认是 贝塞尔雷达Header
//                val header = ClassicsGifHeader(context)//水鸭动态图头部
                DynamicTimeFormat("更新于 %s").calendar
                refreshInterrupt(layout)
                return@DefaultRefreshHeaderCreator header
            })
//             SmartStateRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, delete_layout ->
//                delete_layout.setPrimaryColorsId(R.color.transparent)//全局设置主题颜色
//                DynamicTimeFormat("更新于 %s").calendar
//                refreshInterrupt(delete_layout)
//                val header = ClassicsHeader(context).setTimeFormat(DynamicTimeFormat("更新于 %s"))//指定为经典Header，默认是 贝塞尔雷达Header
//                header.setPrimaryColor(ContextCompat.getColor(header.context, R.color.transparent))
//                header.setAccentColor(ContextCompat.getColor(header.context, R.color.black))
//                return@DefaultRefreshHeaderCreator header
//            })
            //设置全局的Footer构建器
            SmartStateRefreshLayout.setDefaultRefreshFooterCreator(DefaultRefreshFooterCreator { context, layout ->
                if (layout?.refreshHeader == null) {
                    refreshInterrupt(layout)
                }
                //指定为经典Footer，默认是 BallPulseFooter
                val footer = ClassicsFooter(context).setDrawableSize(20f)
                return@DefaultRefreshFooterCreator footer
            })
        }

        /**
         * 拦截刷新事件,标记最后刷新时间,以页面唯一标识符(页面的名称,一个页面对应一个唯一标识,多个相同页面只会保留一个)存储在sp中
         * 次处的sp(plist)单独维护一个单文件,不受其他键值对影响
         */
        private fun refreshInterrupt(layout: RefreshLayout) {
            if (layout is SmartStateRefreshLayout) {
                val utils =
                    SharedPreferencesUtil.outer(unsafeDelegate()!!, REFRESH, Context.MODE_PRIVATE)
                val componentName = layout.getUniqueComponentNameWithFragment()
                val time = utils?.getLong(componentName + REFRESH)
//                Debuger.print("componentName", "$componentName time $time")
                if (time != null && time > 0) {
                    layout.doAsync {
                        uiThread {
                            //如果是经典头部(默认不是了,已经被全局替换掉了),则设置最后刷新时间,模仿iOS的@{MJRefresh}
                            (it.refreshHeader as? ClassicsHeader)?.setLastUpdateTime(Date(time))
                        }
                    }
                }
                layout.setOnRefreshInterrupter(object : OnRefreshInterrupter {
                    override fun onRefresh(date: Date) {
                        (layout!!.refreshHeader as? ClassicsHeader)?.setLastUpdateTime(date)
                        utils?.putLong(componentName + REFRESH, date.time)
                    }

                    override fun onLoadMore(date: Date) {

                    }
                })
            }
        }


        /**
         * @param unsafeTrigger 不安全触发器
         * 一般情况下不会生效,需要设置好特定的安全启动 Splash页面的启动页中调用delegate 才会触发不安全选项,一旦触发,直接崩溃
         */
        private var unsafeTrigger = false
        var delegate: AppDelegate? = null
            get() {
                /**
                 * 在Splash中调用delegate的判断
                 */
                if (!unsafeTrigger)
                    illegalDelegateCaller()
                return field
            }


        /**
         * 数据库委托对象
         */
//        var dbDelegate: AppDataBaseHelper? = null
//            get() {
//                if (field == null) {
//                    field = delegate?.getDbDelegate() as? AppDataBaseHelper
//                }
//                return field
//            }

        var app: DisposalApp? = null
            get() {
                if (field == null) {
                    field = delegate?.getApp()
                }
                return field
            }

        /**
         * 委托对象不安全的调用,不用不行的情况下使用
         * 随时会爆炸
         */
        fun unsafeDelegate(): DisposalApp? {
            unsafeTrigger = true
            val appUnsafe = app
            unsafeTrigger = false
            return appUnsafe
        }
    }


}