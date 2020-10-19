package cn.aihuaiedu.school.base

import android.net.ParseException
import android.os.Build
import android.system.ErrnoException
import android.util.MalformedJsonException
import cn.aihuaiedu.school.base.entity.*
import cn.aihuaiedu.school.base.gson.ApiException
import cn.aihuaiedu.school.base.gson.HttpErrorHandler
import com.example.applicationkotlinsample.DisposalApp
import com.google.gson.JsonParseException
import com.huawen.baselibrary.adapter.entity.MultiItemEntity
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.ToastUtils
import io.reactivex.subscribers.DisposableSubscriber
import org.json.JSONException
import org.reactivestreams.Subscription
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicReference


abstract class ObserverImp<T> : DisposableSubscriber<T> {
    internal val atomicReference = AtomicReference<Subscription>()

    var shouldApiHandler = false
    private var errorHandler: HttpErrorHandler? = null

    constructor() : super()
    constructor(shouldApiHandler: Boolean, errorHandler: HttpErrorHandler? = null) : super() {
        this.shouldApiHandler = shouldApiHandler
        this.errorHandler = errorHandler
    }


    override fun onComplete() {
    }


    override fun onError(err: Throwable) {
        var e = err
        var throwable = e
        //获取最根源的异常
        while (throwable.cause != null) {
            if (e is HttpException) {
                break
            }
            e = throwable
            throwable = throwable.cause!!
        }


        if (e is HttpException) {
            e?.printStackTrace()
            when (e.code()) {
                UNAUTHORIZED -> doOnErr(UNAUTHORIZED, "连接失败")
                FORBIDDEN -> doOnErr(FORBIDDEN, "服务器错误")          //权限错误，需要实现
                NOT_FOUND -> doOnErr(NOT_FOUND, "连接失败")
                REQUEST_TIMEOUT -> doOnErr(REQUEST_TIMEOUT, "连接失败")
                GATEWAY_TIMEOUT -> doOnErr(GATEWAY_TIMEOUT, "连接失败")
                INTERNAL_SERVER_ERROR -> doOnErr(INTERNAL_SERVER_ERROR, "连接失败")
                BAD_GATEWAY -> doOnErr(BAD_GATEWAY, "连接失败")
                SERVICE_UNAVAILABLE -> doOnErr(SERVICE_UNAVAILABLE, "连接失败")
                IGNORED_REQUEST -> {//请求被拦截,忽略的请求
                    return
                }
                else -> doOnErr(ERR_CODE_NET, "连接失败")
            }
        } else if (e is SocketTimeoutException) {
            doOnErr(GATEWAY_TIMEOUT, "请求超时")
        } else if (e is UnknownHostException) {
            e?.printStackTrace()
            doOnErr(ERR_CODE_NET, "网络连接失败")
        } else if (e is SecurityException) {
            e?.printStackTrace()
            doOnErr(ERR_CODE_NET, "未开启网络权限")
        } else if (e is JsonParseException || e is JSONException || e is ParseException || e is MalformedJsonException) {  //解析数据错误
            e?.printStackTrace()
//            doOnErr(ERR_CODE_PARSE, "解析错误")
        } else if (e is ConnectException) {//连接网络错误
            e?.printStackTrace()
            doOnErr(ERR_CODE_NET, "连接失败")
        } else if (e is ApiException) {
            if (e.isTokenExpried) {
                if (errorHandler != null) {
                    errorHandler?.tokenExpired()
                    return
                }
                DisposalApp.app?.cleanLoginInfo()
//                Debuger.print("踢下线")
//                DisposalApp.app?.outLogin(e.message)
                //处理token失效对应的逻辑
                doOnErr(ERR_CODE_TOKEN_EXPRIED, e.message ?: "token过期")
                DisposalApp.app?.openLogin()
                return
            } else {
                doOnErr(e.code(), e.message ?: "服务端异常")
            }
        } else if (e is EOFException) {
            e?.printStackTrace()
            doOnErr(ERR_CODE_EOF, "服务端异常EOF")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && e is ErrnoException) {
                e?.printStackTrace()
                doOnErr(ERR_CODE_NET, "网络连接失败")
            } else {
                if (e.message?.contains("socket is close") == true) {
                    unShowingError()
                } else {
                    e?.printStackTrace()
                    doOnErr(ERR_CODE_UNKNOWN, "网络连接失败")
                }
            }
        }
    }

    override fun onNext(t: T) {
        if (t is Pair<*, *>) {
            doNext(t)
        } else if (t is List<*>) {
            doNext(t)
        } else {
            var base: MultiItemEntity? = t as? MultiItemEntity
            var data2: T? = null

            if (base == null) {
                val body = t as? BaseBody
                if (body != null) {
                    base = body
                }
            } else {
                if (base is BaseInfo<*>) {
                    var baseinfo: BaseInfo<T>? = t as? BaseInfo<T>

                    if (baseinfo != null && baseinfo.errorCode.equals("0") ) {
                        if (baseinfo.data != null)
                            doNext(baseinfo.data as T)
                        else {
                            doNext(baseinfo as T)
                        }
                    } else if (baseinfo != null && !baseinfo.errorCode.equals("0")
                    ) {
                        if (baseinfo?.errorCode.equals("-1001") ) {//登录过期
                            DisposalApp.app?.cleanLoginInfo()
                            DisposalApp.app?.openLogin()
                        }
                        baseinfo.errorMsg?.let { doOnErr(ERR_CODE_LOGIC, it) }
                    } else {
                        doOnErr(ERR_CODE_LOGIC, "请求失败")
                    }
                    return
                } else if (base is ListBaseInfo<*>) {
                    if (base != null && base.errorCode.equals("0")) {
                        doNext(base as T)
                    } else if (base != null && !base.errorCode.equals("0") ) {
                        if (base?.errorCode.equals("-1001") ) {//登录过期
                            DisposalApp.app?.cleanLoginInfo()
                            DisposalApp.app?.openLogin()
                        }
                        base.errorMsg?.let { doOnErr(ERR_CODE_LOGIC, it) }
                    } else {
                        doOnErr(ERR_CODE_LOGIC, "请求失败")
                    }
                    return
                }
            }

            if (base?.shouldIgnoreStateCheck() == true) {
                if (base is BaseStruct<*>) {
                    if (base.state()) {
                        doNext(base as T)
                    } else {
                        doOnErr(ERR_CODE_LOGIC, base.errorMsg ?: "逻辑错误,转换失败")
                    }
                } else {
                    doNext(base as T)
                }
                return
            }
            if (base is BaseStruct<*> && base.state() && ({
                    data2 = base.getData() as? T
                    data2
//                }() != null)) {
                }() != null)) {
                doNext(data2!!)
            } else {//网络接口内部逻辑出错
                if (base is BaseStruct<*>) {
                    doOnErr(ERR_CODE_LOGIC, base.errorMsg ?: "逻辑错误,转换失败")
                } else {
                    doOnErr(ERR_CODE_LOGIC, "逻辑错误,转换失败")
                }
            }
        }
    }

    private final fun doOnErr(code: Int, errMsg: String) {
        Debuger.print("errCode=$code errMsg=$errMsg")
        onErr(code, errMsg)
    }

    open fun unShowingError() {

    }

    /**
     * 出错回调
     * @param errCode
     * @param str
     */
    protected abstract fun onErr(errCode: Int, errMsg: String)

    /**
     * 在已经实现了接口业务逻辑出错判断后开始进行后面的流程
     * @see .onNext
     * @param t
     */
    protected abstract fun doNext(model: T)

    companion object {
        //对应HTTP的状态码
        internal const val UNAUTHORIZED = 401
        internal const val FORBIDDEN = 403
        internal const val NOT_FOUND = 404
        internal const val REQUEST_TIMEOUT = 408
        internal const val INTERNAL_SERVER_ERROR = 500
        internal const val BAD_GATEWAY = 502
        internal const val SERVICE_UNAVAILABLE = 503
        internal const val GATEWAY_TIMEOUT = 504
        internal const val ERR_CODE_NET = 110
        internal const val ERR_CODE_UNKNOWN = 111
        internal const val ERR_CODE_LOGIC = 112
        internal const val ERR_CODE_PARSE = 113
        internal const val ERR_CODE_EOF = 114
        internal const val ERR_CODE_TOKEN_EXPRIED = 115
        internal const val CODE_SUCCESS = 0
        internal const val TOKEN_EXPRIED = 501
        internal const val IGNORED_REQUEST = 1008611
    }

    inline fun <reified T> new(): T {
        val clz = T::class.java
        val mCreate = clz.getDeclaredConstructor()
        mCreate.isAccessible = true
        return mCreate.newInstance()
    }
}
