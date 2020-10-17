package cn.aihuaiedu.school.base.http

import android.annotation.SuppressLint
import android.net.ParseException
import android.net.TrafficStats
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.util.MalformedJsonException
import cn.aihuaiedu.school.base.DeviceUtil
import cn.aihuaiedu.school.base.ObserverImp
import cn.aihuaiedu.school.base.entity.MultipartBean
import cn.aihuaiedu.school.base.getMetaData
import cn.aihuaiedu.school.base.gson.ApiException
import cn.aihuaiedu.school.base.gson.CustomGsonConverterFactory
import com.ReflectUtil
import com.example.applicationkotlinsample.BuildConfig
import com.example.applicationkotlinsample.Constant
import com.example.applicationkotlinsample.DisposalApp
import com.example.applicationkotlinsample.base.http.HttpService
import com.example.applicationkotlinsample.base.http.JsonUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.huawen.baselibrary.jni.AppVerify
import com.huawen.baselibrary.utils.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull;
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.collections.forEachWithIndex
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.net.*
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HostnameVerifier


/**
 * 网络请求设置
 */

/**
 * @author VickyLeu
 * @Email yu6564172@gmail.com
 *
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HttpManager : InterceptorHelper {

    fun cacheParamWrap(map: HashMap<String, Any>, name: String, fun0: () -> String?): HashMap<String, Any> {
        if (cacheParamWrap.containsKey(name)) {
            val v = cacheParamWrap.get(name) ?: ""
            if (v == null || v.equals("null") || v.equals("")) return map
            map.put(name, v)
        } else {
            val v = fun0.invoke()
            if (v == null || v.equals("null") || v.equals("")) return map
            cacheParamWrap.put(name, v)
            map.put(name, v)
        }
        return map
    }

    /**
     * 缓存拦截器
     */
    class CacheInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (!NetworkUtils.isAvailableByPing) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            val response = chain.proceed(request)
            if (NetworkUtils.isAvailableByPing) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build()
            } else {
                // 无网络时，设置超时为4周
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build()
            }
            try {
                return chain.proceed(request)
            } catch (e: Exception) {
                if (
                    e is UnknownHostException
                    || e is SocketTimeoutException
                    || e is HttpException
                    || e is SecurityException
                    || e is JsonParseException
                    || e is ParseException
                    || e is MalformedJsonException
                    || e is JSONException
                    || e is ConnectException
                    || e is EOFException
                ) {
                    throw Exception(e)
                } else
                    throw UnknownHostException("${e.message}")
            }
        }
    }

    fun cleanCache() {
        cacheHeaderWrap.clear()
        cacheParamWrap.clear()
    }


    fun createHeader(): HeadInterceptor {
        return HeadInterceptor { request: Request.Builder, name: String, fun0: () -> String? ->
            cacheHeaderWrap(request, name, fun0)
        }
    }

    fun createParams(): CommonParameterInterceptor {
        return CommonParameterInterceptor { map: HashMap<String, Any>, name: String, fun0: () -> String? ->
            cacheParamWrap(map, name, fun0)
        }
    }

    private val cacheHeaderWrap = hashMapOf<String, String?>()
    private val cacheParamWrap = hashMapOf<String, String?>()


    fun cleanHeaderCache() {
        cacheHeaderWrap?.clear()
        cacheParamWrap?.clear()
    }


    fun cacheHeaderWrap(request: Request.Builder, name: String, fun0: () -> String?): Request.Builder {
        if (cacheHeaderWrap.containsKey(name)) {
            val v = cacheHeaderWrap.get(name) ?: ""
            request.header(name, v)
        } else {
            val v = fun0.invoke()
            if (v != null) {
                cacheHeaderWrap.put(name, v)
                request.header(name, v)
            }
        }
        return request
    }

    var mClient: OkHttpClient? = null
    private fun setClient(client: OkHttpClient?) {
        mClient = client
    }


    override fun getClient(): OkHttpClient? {
        return mClient
    }


    class DynamicConnectTimeout : XInterceptor {
        override fun process(newBuilder: Request?, request: Request): Request? {
            return newBuilder?.newBuilder()?.build()
        }

        private var mRetrofit: Retrofit? = null

        constructor(retrofit: Retrofit?) {
            setRetrofit(retrofit)
        }

        override fun intercept(helper: InterceptorHelper?, chain: Interceptor.Chain): Pair<Exception?, Request?> {
            if (mRetrofit == null) {
                return Pair(null, chain.request())
            }
            val oldRequest = chain.request()
            setDynamicConnectTimeout(oldRequest, mRetrofit!!, "api/v1/S_003")
            val newRequest = oldRequest.newBuilder()
                .method(oldRequest.method, oldRequest.body)
                .url(oldRequest.url)
                .build()
            return Pair(null, newRequest)
        }

        /**
         * 根据所需接口、进行动态设置网络超时时间
         * @param oldRequest
         * @param retrofit
         */
        private fun setDynamicConnectTimeout(oldRequest: Request, retrofit: Retrofit, vararg url: String) {
            //动态设置超时时间
            var questUrl = oldRequest.url.encodedPath
            if (questUrl.length > 1) {
                if (questUrl.substring(0, 1).equals("/")) {
                    questUrl = questUrl.substring(1, questUrl.length)
                }
            }
            try {
                //1、private final okhttp3.Call.Factory callFactory;   Retrofit 的源码 构造方法中
                val callFactoryField = retrofit.javaClass.getDeclaredField("callFactory")
                callFactoryField.isAccessible = true
                //2、callFactory = new OkHttpClient();   Retrofit 的源码 build()中
                val client = callFactoryField.get(retrofit) as OkHttpClient
                //3、OkHttpClient(Builder builder)     OkHttpClient 的源码 构造方法中
                val connectTimeoutField = client.javaClass.getDeclaredField("connectTimeoutMillis")
                connectTimeoutField.isAccessible = true
                //4、根据所需要的时间进行动态设置超时时间
                if (url.contains(questUrl)) {
                    connectTimeoutField.setInt(client, DEFAULT_TIMEOUT * 1000)
                } else {
                    connectTimeoutField.setInt(client, CONNECT_SHORT_TIMEOUT * 1000)
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }

        fun setRetrofit(retrofit: Retrofit?) {
            mRetrofit = retrofit
        }
    }

    /**
     * 设置头拦截器
     */
    class HeadInterceptor(val fun0: (request: Request.Builder, name: String, fun0: () -> String?) -> Request.Builder) :
        XInterceptor {
        override fun process(newBuilder: Request?, request: Request): Request? {
            val headers = mergeHeader(request, newBuilder)
            return newBuilder?.newBuilder()?.headers(headers)?.build()
        }

        private fun filteredPath(helper: InterceptorHelper?, request: Request): Boolean {
            val client = helper?.getClient()
            val cacheQueue = client?.dispatcher?.runningCalls() ?: return false
            val count = cacheQueue.size
            if (count < 2) return false
            var duplicate = false
            try {
                val currentIdx = reverseIndex(cacheQueue as MutableList<Call>, request)
                if (currentIdx == 0) return false
                val lastIdx = currentIdx
                val lastTwiceIdx = currentIdx - 1
                val last = cacheQueue.get(lastIdx)
                val l = cacheQueue.get(lastTwiceIdx)
                val url = last.request()?.url?.toUri()?.toASCIIString()
                val url2 = l.request()?.url?.toUri()?.toASCIIString()
                if (!TextUtils.isEmpty(url) && url.equals(url2)) {
                    duplicate = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return duplicate
        }

        private fun reverseIndex(cacheQueue: MutableList<Call>, request: Request): Int {
            cacheQueue.forEachReversedWithIndex { i, call ->
                if (call.request().url.toUri().toASCIIString().equals(request.url.toUri().toASCIIString())) {
                    return i
                }
            }

            return cacheQueue.size - 1
        }

        @SuppressLint("ObsoleteSdkInt", "HardwareIds")
        override fun intercept(helper: InterceptorHelper?, chain: Interceptor.Chain): Pair<Exception?, Request?> {
            val original = chain.request()
            val builder = original.newBuilder()
            val modifiedUrl = original.url.newBuilder()
            var request: Request? = null
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                fun0(builder, "Connection") {
                    "close"
                }
                fun0(builder, "Accept-Encoding") {
                    "identity"
                }
            }
            fun0(builder, "platform") {
                return@fun0 "Android"
            }.also {
                fun0(builder, "os-version") {
                    return@fun0 SystemUtil.systemVersion
                }
            }.also {
                fun0(builder, "model") {
                    return@fun0 "${SystemUtil.deviceBrand} ${SystemUtil.systemModel}"
                }
            }.also {
                fun0(builder, "app-version") {
                    return@fun0 DisposalApp.app?.getLocalVersionName() ?: "1.0.0"
                }
            }.also {
                fun0(builder, "requestAim") {
                    return@fun0 "aihuaSchool"
                }
            }.also {
                fun0(builder, "imei") {
                    return@fun0 DeviceUtil.uniquePsuedoID()
                }
            }.also {
                fun0(builder, "channel") {
                    return@fun0 DisposalApp.app?.getMetaData("UMENG_CHANNEL") ?: ""
                }
            }.also {
                fun0(builder, "Accept") {
                    return@fun0 "application/json"
                }
            }.also {
                fun0(builder, "User-Agent") {
                    return@fun0 "adultedu/${DisposalApp.app?.getLocalVersionName()
                        ?: "1.0.0"} (${SystemUtil.systemModel}; Android ${SystemUtil.systemVersion};Scale/2.00)"
                }
            }.also {
                val url = modifiedUrl.build().toString()
                if (url.contains("api/login/login") ||
                    url.contains("api/wechatLogin/login") ||
                    url.contains("api/app/index/versionInfoAndLaunchScreen") ||
                    url.contains("api/login/verifycode")
                ) {
                } else {
                    fun0(builder, "Authorization") {
                        val token = SharedPreferencesUtil.instance?.getString("token", "")
                        if (token.isNullOrBlank()) return@fun0 null
                        return@fun0 token
                    }
                }
            }.also {
                val url = modifiedUrl.build().toString()
                if (url.contains("api/login/login") ||
                    url.contains("api/wechatLogin/login") ||
                    url.contains("api/app/index/versionInfoAndLaunchScreen") ||
                    url.contains("api/login/verifycode")
                ) {
                } else {
                    fun0(builder, "clientKey") {
                        val token = SharedPreferencesUtil.instance?.getString("mobile", "")
                        if (token.isNullOrBlank()) return@fun0 null
                        return@fun0 token
                    }
                }
            }.also {
                request = it.method(original.method, original.body)
                    .build()
            }
            if (request == null) {
                request = original.newBuilder().method(original.method, original.body).build()
            }
            val path = request!!.url.encodedPath//.uri().toASCIIString()
            //todo 使用队列装载当前需要限制速度的请求，请求完成后延迟删除队列请求，队列中存在的请求不在发送
            when (path) {
                "/api/app/customer/info",
                "/api/app/index/index",
                "/api/wechat/index/app",
                "/api/app/courseSchedule/daily"
                -> {

                }
                else -> {
                    val isDuplicateRequest: Boolean = filteredPath(helper, request!!)
                    if (isDuplicateRequest) {
                        if (!path.equals("/api/v1/M_019"))
                            Debuger.print("重复请求")
                        return Pair(ExistRequestException(), null)
                    }
                }
            }
            return Pair(null, request)
        }
    }

    /**
     * 设置公共参数拦截器
     */
    class CommonParameterInterceptor(val fun0: (map: HashMap<String, Any>, name: String, fun0: () -> String?) -> HashMap<String, Any>) :
        XInterceptor {
        override fun process(newBuilder: Request?, request: Request): Request? {
            val headers = mergeHeader(request, newBuilder)
            var builder = newBuilder?.newBuilder()
            val body = request.body
            if (body != null) {
                if (request.method == "GET") {
                    builder = builder?.get()
                } else if (request.method == "POST") {
                    builder = builder?.post(body)
                }
            }
            builder = builder?.headers(headers)
            return builder?.build()
        }

        /**
         * 拦截器
         */
        override fun intercept(helper: InterceptorHelper?, chain: Interceptor.Chain): Pair<Exception?, Request?> {
            var originalRequest = chain.request()
            if (originalRequest.method.toUpperCase() == "GET") {

            } else {
                val body = originalRequest.body
                val newparamMap = hashMapOf<String, Any>()
                var newRequestBody: RequestBody? = null
                val modifiedUrl = originalRequest.url.newBuilder()
                val timeStamp=System.currentTimeMillis()
                newparamMap.put("timeStamp",timeStamp)
                newparamMap.put("sign", EncryptUtils.encryptMD5ToString((timeStamp as? String) + Constant.SIGN_KEY)?.toUpperCase()
                    ?: "")
                val newHeader=hashMapOf<String, Any>()
                newHeader.put("appType","3")
                val paramMap=hashMapOf<String, Any>()

                if (body is FormBody) {
                    // 从 formBody 中拿到请求参数，放入 formMap 中
                    for (i in 0 until body.size) {
                        newparamMap.put(body.name(i), body.value(i))
                        Debuger.print("表单数据", "key:${body.name(i)}  value:${body.value(i)}")
                    }
                    paramMap.put("header",newHeader)
                    paramMap.put("body",newparamMap)
                    newRequestBody = setCommonParam(3, paramMap, modifiedUrl)
                } else if (body is MultipartBody) {
                    for (i in 0 until body.size) {
                        val part = body.part(i)
                        paramMap.put(i.toString(), part)
                    }
                    newRequestBody = setCommonParam(1, paramMap, modifiedUrl)
                } else if (body is MultipartBean) {
//                    val json = body.toJson()
//                    for (i in 0 until body.size) {
//                        val part = body.part(i)
//                        newparamMap.put(i.toString(), part)
//                    }
                    paramMap.put("header",newHeader)
                    paramMap.put("body",newparamMap)
                    newRequestBody = setCommonParam(2, paramMap, modifiedUrl)
                } else if (body != null) {
                    if(originalRequest.url.toString().contains("?")) {
                        var urlContext = originalRequest.url.toString().split("?")[1]
                        var urla=urlContext.split("&");
                        for (a in urla) {
                            newparamMap.put(a.split("=")[0], a.split("=")[1])
                            Debuger.print("表单数据", "key:${a.split("=")[0]}  value:${a.split("=")[1]}")
                        }
                    }
                    paramMap.put("header",newHeader)
                    paramMap.put("body",newparamMap)
                    newRequestBody = setCommonParam(3, paramMap, modifiedUrl)
                }

                if (newRequestBody != null) {
                    var request = originalRequest.newBuilder().post(newRequestBody)
                        .build()
                    TrafficStats.setThreadStatsTag(0xF00D)
                    if (Build.VERSION.SDK_INT > 13) {
                        request = request.newBuilder()
//                            .addHeader("Connection", "close")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Accept-Encoding", "identity")
                            .build()
                    }
//                    if(!DisposalApp.app?.getCurrentUser()?.auth_token.isNullOrEmpty()){
//                        request=request.newBuilder().addHeader("authtoken",DisposalApp.app?.getCurrentUser()?.auth_token.toString()).build()
//                    }
                    return Pair(null, request)
                }
            }

            TrafficStats.setThreadStatsTag(0xF00D)
            if (Build.VERSION.SDK_INT > 13) {
                originalRequest = originalRequest.newBuilder()
//                    .addHeader("Connection", "close")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept-Encoding", "identity")
                    .build()
//                if(!DisposalApp.app?.getCurrentUser()?.auth_token.isNullOrEmpty()){
//                    originalRequest = originalRequest.newBuilder().addHeader("authtoken",DisposalApp.app?.getCurrentUser()?.auth_token.toString()).build()
//                }
            }
            return Pair(null, originalRequest)
        }
        //拼接请求头
        protected fun setCommonParam(
            type: Int,
            paramMap: Any,//HashMap<String, Any>,
            modifiedUrl: HttpUrl.Builder
        ): RequestBody? {
            var requestBody: RequestBody? = null
//            fun0(paramMap, "version") {
//                "V${DisposalApp.app?.getLocalVersionName()?.replace("V", "")?.replace("v", "")
//                    ?: ""}"
//            }
            when (type) {
//                0 -> {
//                    val map = paramMap as? HashMap<String, Any>
//                    val builder = FormBody.Builder()
//                    map?.toList()?.forEach { p: Pair<String, Any> ->
//                        builder.add(p.first, p.second.toString())
//                    }
//                    requestBody = builder.build()
//                }
                1 -> {
                    val map = paramMap as? HashMap<String, Any>
                    val builder = MultipartBody.Builder()
                    map?.toList()?.forEach { p: Pair<String, Any> ->
                        if (p.second is String) {
                            builder.addFormDataPart(p.first, p.second.toString())
                        } else {
                            builder.addPart(p.second as MultipartBody.Part)
                        }
                    }
                    requestBody = builder.setType(MultipartBody.FORM).build()
                }
//                2 -> {
//                    requestBody =
//                        (paramMap as String).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//                }
                3 -> {

                    var paramMapString= JsonUtil.object2String(paramMap as java.util.HashMap<String, java.util.HashMap<Any, Any>>?)
                    Debuger.print("paramMapString", "value:${paramMapString}")
                    paramMapString=if(Constant.TEXT_ITYPE_CODING)Base64.encodeToString(paramMapString.toByteArray(),Base64.NO_WRAP) else paramMapString
                    Debuger.print("paramMapStringBase64", "value:${paramMapString}")
                    requestBody =paramMapString.toRequestBody(Constant.BASE_TEXT_ITYPE_URL.toMediaTypeOrNull())
                   // requestBody = paramMap as RequestBody
                }
            }
            // 将 formMap 转化为 json 然后 AES 加密
//            val gson = Gson()
//            val jsonParams = gson.toJson(formMap)
////                val encryptParams = AESCryptUtils.encrypt(jsonParams.getBytes(CHARSET), AppConstant.getAESKey());
//////                // 重新修改 body 的内容
//            body = FormBody.Builder().add(FORM_NAME, jsonParams).build()
            return requestBody
        }
    }


    companion object {
        private val CONNECT_LONG_TIMEOUT = 10
        private val CONNECT_SHORT_TIMEOUT = 100
        //网络请求延迟时间，默认为10秒
        private val DEFAULT_TIMEOUT = CONNECT_LONG_TIMEOUT
        private var client: OkHttpClient? = null
        private var retrofit: Retrofit? = null
        private var httpService: HttpService? = null


        private val mWorkingMappingService = hashMapOf<String, Any>()

        private var httpManager: HttpManager? = null
            get() {
                if (field == null) {
                    field = HttpManager()
                }
                return field
            }

        fun getManager(): HttpManager? {
            return httpManager
        }

        private fun mergeHeader(request: Request, newBuilder: Request?): Headers {
            val headers = request.headers
            val oldHeaders = newBuilder?.headers
            val map: HashMap<String, String> = hashMapOf<String, String>()
            if (oldHeaders != null)
                for (i in 0 until oldHeaders.size) {
                    val key = oldHeaders.name(i)
                    val value = oldHeaders.value(i)
                    map.put(key, value)
                }
            for (i in 0 until headers.size) {
                val key = headers.name(i)
                val value = headers.value(i)
                map.put(key, value)
            }
            val builderNew = Headers.Builder()
            map.forEach {
                builderNew.add(it.key, it.value)
            }
            return builderNew.build()
        }

        private fun cipher(str: String): ByteArray {
            val value = URLEncoder.encode(
                Base64.encodeToString(AppVerify.encode(this, str).toByteArray(), Base64.DEFAULT),
                "utf-8"
            )
            val out = ByteArrayOutputStream()
            try {
                val gzip = GZIPOutputStream(out)
                gzip.write(value.toByteArray(Charset.forName("utf-8")))
                gzip.close()
            } catch (e: IOException) {
                LogUtils.d("gzip compress error.[$e]")
            }
            return out.toByteArray()
        }

        /**
         * 获取httpService实例
         * @return
         */
        fun <T : Any> getWorkHttpService(clazz: Class<T>): T {
            var host: String? = null
            if (!baseUrlInit) {
                try {
                    host = ReflectUtil.getStaticClassField(
                        clazz, "Companion",
                        "BASE_URL"
                    ) as String
                } catch (e: Exception) {
                    Debuger.print(e)
                    host = ""
                    e.printStackTrace()
                }
//                val httpUrl = HttpUrl.parse(host)
//                ReflectUtil.updateFinalModifiers(retrofit, retrofit.javaClass.getDeclaredField("baseUrl"), httpUrl)
//                Debuger.print(host)
                baseUrlInit = true
            }
            val retrofit = getWorkRetrofit(host)
            val httpService = retrofit.create(clazz) as T
            return httpService
        }

        fun <T : Any> getDefWorkService(clazz: Class<T>): T {
            var host: String? = null
            if (!baseUrlInit) {
                try {
                    host = ReflectUtil.getStaticClassField(
                        clazz, "Companion",
                        "BASE_URL"
                    ) as String
                } catch (e: Exception) {
                    Debuger.print(e)
                    host = ""
                    e.printStackTrace()
                }
//                val httpUrl = HttpUrl.parse(host)
//                ReflectUtil.updateFinalModifiers(retrofit, retrofit.javaClass.getDeclaredField("baseUrl"), httpUrl)
//                Debuger.print(host)
                baseUrlInit = true
            }
            val retrofit = getWorkRetrofit(host)
            val httpService = retrofit.create(clazz) as T
            return httpService
        }

        /**
         * 默认调用的这个
         */
        fun getProjectWorkHttpService(): HttpService {
            if (httpService == null) {
                synchronized(this) {
                    if (httpService == null) {
                        httpService = getWorkHttpService(HttpService::class.java)
                    }
                }
            }
            return httpService!!
        }


        fun <T : Any> getDefineWorkingService(
            clazz: Class<T>,
            useParameterNormalizer: Boolean = false,
            cleanShareState: Boolean = false
        ): T {
            var theService: T? = null
            synchronized(this) {
                val key = clazz.simpleName
                val service = mWorkingMappingService[key] as? T
                if (service != null && !cleanShareState) {
                    theService = service
                } else {
                    val service_: T = getSingleWorkHttpService(clazz, useParameterNormalizer)!!
                    theService = service_
                    mWorkingMappingService[key] = service_
                }
            }
            return theService!!
        }


        fun <T : Any> getMutableBaseUrlHttpService(
            clazz: Class<T>,
            url: String? = null,
            fun0: (() -> String?)? = null,
            useParameterNormalizer: Boolean = false
        ): T? {
            val retrofit = getSingleWorkRetrofit(url, useParameterNormalizer)
            val httpService = retrofit.create(clazz) as T
            if (fun0 != null) {
                val host = fun0.invoke()
                Debuger.print(host)

                val httpUrl = host?.toHttpUrlOrNull()
                ReflectUtil.updateFinalModifiers(retrofit, retrofit.javaClass.getDeclaredField("baseUrl"), httpUrl)
            }
            return httpService
        }

        fun <T : Any> getSingleWorkHttpService(clazz: Class<T>, useParameterNormalizer: Boolean = true): T? {
            var host: String
            try {
                host = ReflectUtil.getStaticClassField(
                    clazz, "Companion",
                    "BASE_URL"
                ) as String
            } catch (e: Exception) {
                Debuger.print(e)
                host = ""
                e.printStackTrace()
            }
            Debuger.print(host)

            val retrofit = getSingleWorkRetrofit(host, useParameterNormalizer)
            val httpService = retrofit.create(clazz) as T

//            val httpUrl = HttpUrl.parse(host)
//            ReflectUtil.updateFinalModifiers(retrofit, retrofit.javaClass.getDeclaredField("baseUrl"), httpUrl)
            return httpService
        }


        private var baseUrlInit = false
        private fun getSingleWorkRetrofit(url: String? = null, useParameterNormalizer: Boolean = true): Retrofit {
            var retrofit: Retrofit? = null
            synchronized(HttpManager::class.java) {
                val (dynamicInterceptor, gson, okhttp) = initOkhttp(useParameterNormalizer)
                // 获取retrofit的实例
                retrofit = Retrofit.Builder()
                    .baseUrl(
                        if (TextUtils.isEmpty(url)) "http://www.aihuagrp.com/" else {
                            "$url"
                        }
                    )
                    .client(okhttp)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(CustomGsonConverterFactory.create(gson))
                    .build()
                dynamicInterceptor?.setRetrofit(retrofit)
            }
            return retrofit!!
        }

        /**
         * 初始化OKhttp
         */
        private fun initOkhttp(useParameterNormalizer: Boolean = true): Triple<DynamicConnectTimeout?, Gson, OkHttpClient> {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriDeserializer())
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(String::class.java, StringDeserializer())
                .registerTypeAdapter(Int::class.java, IntDeserializer())
                .registerTypeAdapter(Float::class.java, FloatDeserializer())
                .registerTypeAdapter(Double::class.java, DoubleDeserializer())
                .registerTypeAdapter(Long::class.java, LongDeserializer())
                .registerTypeAdapter(Short::class.java, ShortDeserializer())
                .registerTypeAdapterFactory(NullStringToEmptyAdapterFactory())
                .create()


            if (client == null || !useParameterNormalizer) {
                val (interceptorWrap, httpManager) = if (useParameterNormalizer) {
                    Pair(InterceptorWrap(httpManager), httpManager)
                } else {
                    val mgr = HttpManager()
                    Pair(InterceptorWrap(mgr), mgr)
                }
                var builder = OkHttpClient().newBuilder()
                    .retryOnConnectionFailure(false)

                if (useParameterNormalizer) {
                    val headers = httpManager?.createHeader()
                    val params = httpManager?.createParams()
                    //设置 请求的缓存的大小跟位置
                    val cacheFile = File(DisposalApp.unsafeDelegate()?.cacheDir, "cache")
                    //50Mb 缓存的大小
                    val cache = Cache(cacheFile, (1024 * 1024 * 50).toLong())
                    if (headers != null) {
                        interceptorWrap.addQueueEntity(headers)
                    }
                    if (params != null) {
                        interceptorWrap.addQueueEntity(params)
                    }
                    if (BuildConfig.DEBUG) {//开发模式中记录整个body的日志
                        //添加一个log拦截器,打印log
                        val httpLoggingInterceptor = HttpLoggingInterceptor()
                        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        builder = builder.addInterceptor(httpLoggingInterceptor) //添加日志拦截器,所有的请求响应度看到
                    }
                } else {
                    if (BuildConfig.DEBUG) {//开发模式中记录整个body的日志
                        //添加一个log拦截器,打印log
                        val httpLogger = HttpLogger()
                        val httpLoggingInterceptor = HttpLoggingInterceptor(httpLogger)
                        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        builder = builder.addInterceptor(httpLoggingInterceptor) //添加日志拦截器,所有的请求响应度看到
                    }
                }
                val dynamicInterceptor = DynamicConnectTimeout(null)
                interceptorWrap.addQueueEntity(dynamicInterceptor)
                builder = builder.addInterceptor(interceptorWrap)

                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                if (useParameterNormalizer) {
                    if (BuildConfig.DEBUG) {
                        client =
                            builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .fixTls()
                                .hostnameVerifier(HostnameVerifier { hostname, session -> true })
                                .build()
                    } else {
                        client =
                            builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                                .proxy(Proxy.NO_PROXY)
                                .fixTls()
                                .hostnameVerifier(HostnameVerifier { hostname, session -> true })
                                .build()
                    }
                    httpManager?.setClient(client)
                    return Triple(dynamicInterceptor, gson, client!!)
                } else {
                    val client = (if (BuildConfig.DEBUG) {
                        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                            .fixTls()
                            .hostnameVerifier(HostnameVerifier { hostname, session -> true })

                    } else {
                        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                            .proxy(Proxy.NO_PROXY)
                            .fixTls()
                            .hostnameVerifier(HostnameVerifier { hostname, session -> true })
                    }).let {
                        it
                    }
                        .apply {
                            interceptorWrap.addQueueEntity(object : XInterceptor {
                                override fun intercept(
                                    helper: InterceptorHelper?,
                                    chain: Interceptor.Chain
                                ): Pair<Exception?, Request?> {
                                    val oldRequest = chain.request()
                                    return Pair(null, oldRequest)
                                }

                                override fun process(newBuilder: Request?, request: Request): Request? {

                                    val headers = request.headers.newBuilder().removeAll("User-Agent")

                                        .build()
                                    return newBuilder?.newBuilder()?.headers(headers)?.build()
                                }
                            })
                        }.build()
                    httpManager?.setClient(client)
                    return Triple(dynamicInterceptor, gson, client)
                }
            } else {
                return Triple(null, gson, client!!)
            }
        }

        /**
         * 构建拦截器
         */
        private class InterceptorWrap(val helper: InterceptorHelper?) : Interceptor {

            private var interceptorQueue = arrayListOf<XInterceptor>()
            override fun intercept(chain: Interceptor.Chain): Response {
                var requestFinalizer: Request? = null
                interceptorQueue.forEachWithIndex { i, interceptor ->
                    val (exception, request) = interceptor.intercept(helper, chain)
                    if (exception != null) {
                        when (exception) {
                            is ExistRequestException -> {
                                Debuger.print("拦截请求")
                                //重复请求拦截
                                val rlt = Response.Builder()
                                    .code(ObserverImp.IGNORED_REQUEST) //Simply put whatever value you want to designate to aborted request.
                                    .message("重复请求")
                                    .body(ResponseBody.create(null, ""))
                                    .protocol(Protocol.HTTP_1_0)
                                    .request(chain.request())
                                    .build()
                                return rlt
                            }
                        }
                        throw exception
                    } else if (request != null) {
                        requestFinalizer = interceptor.process(requestFinalizer, request)
                        if (requestFinalizer == null) {
                            requestFinalizer = request
                        }
                    }
                }
                try {
                    if (requestFinalizer == null) {
                        throw ApiException(500, "请求失败", "请求不成功")
                    }
                    var response:Response= chain.proceed(requestFinalizer!!)
                    return response
                } catch (e: Exception) {
                    if (
                        e is UnknownHostException
                        || e is SocketTimeoutException
                        || e is HttpException
                        || e is SecurityException
                        || e is JsonParseException
                        || e is ParseException
                        || e is MalformedJsonException
                        || e is JSONException
                        || e is ConnectException
                        || e is EOFException
                    ) {
                        throw e
                    } else {
                        e.printStackTrace()
                        throw UnknownHostException("${e.message}")
                    }
                }
            }

            fun addQueueEntity(interceptor: XInterceptor) {
                interceptorQueue.add(interceptor)
            }

        }

        private interface XInterceptor {
            fun intercept(helper: InterceptorHelper?, chain: Interceptor.Chain): Pair<Exception?, Request?>
            fun process(newBuilder: Request?, request: Request): Request?
        }

        /**
         * 网络请求改造
         */
        private fun getWorkRetrofit(url: String? = null): Retrofit {
            if (retrofit == null) {
                baseUrlInit = false
                synchronized(HttpManager::class.java) {
                    if (retrofit == null) {
                        val (dynamicInterceptor, gson, okhttp) = initOkhttp()
                        // 获取retrofit的实例
//
                        //HttpService.BASE_URL
                        httpManager?.setClient(okhttp)
                        // 获取retrofit的实例
                        retrofit = Retrofit.Builder()
                            .baseUrl(url)
                            .client(okhttp)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(CustomGsonConverterFactory.create(gson))
                            .build()
                        dynamicInterceptor?.setRetrofit(retrofit)
                    }
                }
            }
            return retrofit!!
        }
    }


}


interface InterceptorHelper {
    fun getClient(): OkHttpClient?
}