@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package cn.aihuaiedu.school.base.ext

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.NonNull
import androidx.bug.DelegatingSocketFactory
import cn.aihuaiedu.school.base.http.fixTls
import com.ReflectUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.applicationkotlinsample.BuildConfig
import com.example.applicationkotlinsample.DisposalApp
import com.example.applicationkotlinsample.utils.GlideUtil
import com.huawen.baselibrary.utils.Debuger
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import java.io.*
import java.net.Proxy
import java.net.Socket
import java.net.URLConnection
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier


/**
 * @作者: #Administrator #
 *@日期: #2018/5/9 #
 *@时间: #2018年05月09日 12:03 #
 *@File:Kotlin File
 */

inline fun WebView.setNormalConfig() {
    setNormalConfig(null)
}


inline fun WebView.destroyRef() {
    loadUrl("about:blank")
    val wvc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        webViewClient as? OkhttpWebClient
    } else {
        null
    }
    if (wvc != null) {
        wvc.destroyRef()
    }
    val parent = parent
    if (parent != null) {
        (parent as? ViewGroup)?.removeView(this)
    }
    removeAllViews()
    destroy()
}

@SuppressLint("SetJavaScriptEnabled")
inline fun <A : WebViewListener> WebView.setNormalConfig(listener: A?): WebView {
    forbidLongClick()
    isHorizontalScrollBarEnabled = false;//水平不显示
    isVerticalScrollBarEnabled = false; //垂直不显示

    setBackgroundColor(0)
    val ws = settings
    //自适应屏幕
    ws.useWideViewPort = true
    //自动加载图片
    ws.loadsImagesAutomatically = true
    // 网页内容的宽度是否可大于WebView控件的宽度
    ws.loadWithOverviewMode = true
    // 保存表单数据
    ws.defaultTextEncodingName = "utf-8"
    ws.saveFormData = true
    // 是否应该支持使用其屏幕缩放控件和手势缩放
    ws.setSupportZoom(false)

    ws.builtInZoomControls = false
    ws.displayZoomControls = false
    // 启动应用缓存
    ws.setAppCacheEnabled(true)
    // 设置缓存模式
    ws.cacheMode = WebSettings.LOAD_DEFAULT
    // 缩放比例 1
    setInitialScale(100)

    // 告诉WebView启用JavaScript执行。默认的是false。
    ws.javaScriptEnabled = true
    //  页面加载好以后，再放开图片
    ws.blockNetworkImage = false
    // 使用localStorage则必须打开
    ws.domStorageEnabled = true


    scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY;
    isScrollbarFadingEnabled = false

    // 排版适应屏幕
//    ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
//    ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
//    ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
//    ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
    } else {
//        ws.setRenderPriority(WebSettings.RenderPriority.HIGH)
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
    }

    // WebView是否支持多个窗口。
    ws.setSupportMultipleWindows(false)

    ws.allowContentAccess = true
    ws.allowFileAccess = true
    ws.allowFileAccessFromFileURLs = true

    // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
    ws.textZoom = 100
    /*
    todo 需要兼容vuejs的页面
    ws.defaultFixedFontSize = dip(16)
     ws.defaultFontSize =dip(16)*/

    ws.defaultFixedFontSize = 16
    ws.defaultFontSize = 16

    // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
    webViewClient = OkhttpWebClient(this, listener)

    val pos = arrayOf(false)
    webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            listener?.webLoadingProgress(newProgress)
            // 增加Javascript异常监控
//            CrashReport.setJavascriptMonitor(view, true)
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                if (pos[0] == false) {
                    listener?.webContentFinished()
                    pos[0] = true
                }
            } else {
                pos[0] = false
            }
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            listener?.receiveTitle(title)
        }


    }
    return this
}

class OkhttpWebClient(private var webView: WebView?, private var listener: WebViewListener?) : WebViewClient() {
    var okHttpClient: OkHttpClient? = null
    fun destroyRef() {
        okHttpClient = null
        listener = null
        webView = null
    }

    init {
        val THREAD_ID = 10004
        val socketFactory = object : DelegatingSocketFactory(SocketFactory.getDefault()) {
            override fun configureSocket(socket: Socket): Socket {
                TrafficStats.setThreadStatsTag(THREAD_ID)
                TrafficStats.tagSocket(socket)
                return socket
            }
        }
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (true) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        } else {
            if (BuildConfig.DEBUG) {//开发模式中记录整个body的日志
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {//开发模式中记录基本的一些日志，如状态值返回200
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            }
        }
        val noPool = ConnectionPool(0, 1, TimeUnit.SECONDS)
        val cacheFile = File(DisposalApp.unsafeDelegate()?.cacheDir, "web/cache/")
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        //定制OkHttp
        val httpClientBuilder = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .socketFactory(socketFactory)
            .fixTls()
            .connectionPool(noPool)
            // no timeouts, we don't support them yet
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .hostnameVerifier(HostnameVerifier { hostname, session -> true })
            .cache(Cache(cacheFile, 1024 * 1024 * 100))
        //请求头设置
        httpClientBuilder.interceptors().add(httpLoggingInterceptor)

        okHttpClient = httpClientBuilder.build()

    }

    private fun getUserAgent(context: Context?): String {
        var userAgent = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context)
            } catch (e: Exception) {
                userAgent = System.getProperty("http.agent") ?: ""
            }
        } else {
            userAgent = System.getProperty("http.agent") ?: ""
        }
        val sb = StringBuffer();
        for (i in 0 until userAgent.length) {
            val c = userAgent.get(i)
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.toInt()))
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        try {
            if (url.startsWith("baiduboxapp://")) {
                return true
            }
        } catch (e: Exception) {
            return false
        }
        if (listener == null) return true
        return listener?.shouldOverLoad(view, url) ?: false
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url_ = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request?.url?.toString()
        } else {
            webView?.url.toString()
        }
        if (urlShouldBeHandledByWebView(url_)) {
            return super.shouldInterceptRequest(view, request)
        }
        return handleRequestViaOkHttp(url_!!)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val url_ = url
        if (urlShouldBeHandledByWebView(url_)) {
            return super.shouldInterceptRequest(view, url_)
        }
        return handleRequestViaOkHttp(url_!!)
    }

    fun urlShouldBeHandledByWebView(url: String?): Boolean {
        // file: Resolve requests to local files such as files from cache folder via WebView itself
//        try {
//            URL(url)
//        }catch (e:Exception){
//            if (e is MalformedURLException){
//                return true
//            }
//        }

        return TextUtils.isEmpty(url) || (url?.startsWith("file:")
            ?: false) || url?.contains("data:text/html;charset=utf-8;base64") ?: false
    }


    @NonNull
    fun handleRequestViaOkHttp(@NonNull url: String): WebResourceResponse? {
        try {
            if (url.endsWith("/favicon.ico")) {
                return WebResourceResponse("image/png", null, null)
            }
            if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif")) {
//                val resp = webResourceResponsePlanA(webView?.context, url)
                val resp = webResourceResponsePlanB(webView?.context, url)
                if (resp != null) {
                    return resp
                }
            }
            val userAgent = getUserAgent(webView?.context)
            val okReq = Request.Builder().url(url)
                .addHeader("User-Agent", userAgent)
                .cacheControl(CacheControl.Builder().noCache().build())
                .build()
            val startMillis = System.currentTimeMillis()
            val okResp = okHttpClient?.newCall(okReq)?.execute()
            val dtMillis = System.currentTimeMillis() - startMillis
            Debuger.print("Got response: " + okResp + " after " + dtMillis + "ms")

            val contentTypeValue = okResp?.header("Content-Type")
            if (contentTypeValue != null) {
                if (contentTypeValue.contains("charset=")) {
                    if (contentTypeValue.indexOf("charset=") > 0) {
                        try {
                            val contentTypeAndEncoding =
                                contentTypeValue.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (contentTypeAndEncoding.size < 2) {
                                val bs = okResp.body!!.byteStream()
                                val baos = ByteArrayOutputStream()
                                val buffer = ByteArray(1024)
                                var len = 0
                                while ({ len = bs.read(buffer);len }() > -1) {
                                    baos.write(buffer, 0, len)
                                }
                                baos.flush()
                                val stream = ByteArrayInputStream(baos.toByteArray())
                                okResp?.body?.close()
                                baos.close()
                                val res = WebResourceResponse(
                                    "text/html", // You can set something other as default content-type
                                    "utf-8",  // Again, you can set another encoding as default
                                    stream
                                )
                                try {
                                    ReflectUtil.setFiled(WebResourceResponse::class.java, "mStatusCode", res, 200)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                return res
                            }
                            val contentType = contentTypeAndEncoding[0]
                            val charset =
                                contentTypeAndEncoding[1].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                            val bs = okResp.body!!.byteStream()
                            val baos = ByteArrayOutputStream()
                            val buffer = ByteArray(1024)
                            var len = 0
                            while ({ len = bs.read(buffer);len }() > -1) {
                                baos.write(buffer, 0, len)
                            }
                            baos.flush()
                            val stream = ByteArrayInputStream(baos.toByteArray())
                            okResp?.body?.close()
                            baos.close()
                            return WebResourceResponse(contentType, charset, stream)
                        } catch (e: Exception) {
                            return null
                        }
                    } else {
                        try {
                            val bs = okResp.body!!.byteStream()
                            val baos = ByteArrayOutputStream()
                            val buffer = ByteArray(1024)
                            var len = 0
                            while ({ len = bs.read(buffer);len }() > -1) {
                                baos.write(buffer, 0, len)
                            }
                            baos.flush()
                            val stream = ByteArrayInputStream(baos.toByteArray())
                            okResp?.body?.close()
                            baos.close()
                            val res = WebResourceResponse(
                                "text/html", // You can set something other as default content-type
                                "utf-8",  // Again, you can set another encoding as default
                                stream
                            )
                            try {
                                ReflectUtil.setFiled(WebResourceResponse::class.java, "mStatusCode", res, 200)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            return res
                        } catch (e2: Exception) {
                            return null
                        }
                    }
                } else {
                    try {
                        val bs = okResp.body!!.byteStream()
                        val baos = ByteArrayOutputStream()
                        val buffer = ByteArray(1024)
                        var len = 0
                        while ({ len = bs.read(buffer);len }() > -1) {
                            baos.write(buffer, 0, len)
                        }
                        baos.flush()
                        val stream = ByteArrayInputStream(baos.toByteArray())
                        okResp?.body?.close()
                        baos.close()
                        val res = WebResourceResponse(
                            "text/html", // You can set something other as default content-type
                            "utf-8",  // Again, you can set another encoding as default
                            stream
                        )
                        try {
                            ReflectUtil.setFiled(WebResourceResponse::class.java, "mStatusCode", res, 200)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return res
                    } catch (e2: Exception) {
                        return null
                    }
                }
            } else {
                try {
                    val bs = okResp?.body?.byteStream()
                    val baos = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    var len = 0
                    while ({ len = bs?.read(buffer) ?: -1;len }() > -1) {
                        baos.write(buffer, 0, len)
                    }
                    baos.flush()
                    val stream = ByteArrayInputStream(baos.toByteArray())
                    okResp?.body?.close()
                    baos.close()
                    val res = WebResourceResponse(
                        "text/html", // You can set something other as default content-type
                        "utf-8",  // Again, you can set another encoding as default
                        stream
                    )
                    try {
                        ReflectUtil.setFiled(WebResourceResponse::class.java, "mStatusCode", res, 200)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return res
                } catch (e2: Exception) {
                    return null
                }
            }
        } catch (e: Exception) {
            if (e.message?.contains("java.lang.IllegalArgumentException: unexpected url") == true) {
                return null
            }
            e.printStackTrace()
            Debuger.print("Failed to load request: $url", e)
            try {
//                val errorPage = IOUtils.toString(view.context.assets.open("request_failed.html"), "UTF-8")
//                        .replace("__ERROR_MESSAGE__", e.message)
                val respStream = ByteArrayInputStream("${e.message}".toByteArray(charset("UTF-8")))
                return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    WebResourceResponse("text/html", "UTF-8", 500, "FAIL", null, respStream)
                } else {
                    WebResourceResponse("text/html", "UTF-8", respStream)
                }
            } catch (ex: IOException) {
                Debuger.print("Loading error page from assets failed", ex) // should never happen
                return null
            }
            // return response for bad request
        }
    }

    private fun webResourceResponsePlanB(context: Context?, url: String): WebResourceResponse? {
        val glide = GlideUtil.assertManager(context, true)
        if (glide != null) {
            val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .sizeMultiplier(0.1f)
                .dontAnimate()
            val resource = glide
                .`as`(InputStream::class.java)
                .apply(options)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get(5, TimeUnit.SECONDS)
            val stream = FileInputStream(resource)
            val mimeType = URLConnection.guessContentTypeFromStream(stream)
            return WebResourceResponse(mimeType, null, stream)
        } else {
            return null
        }
    }

    private fun webResourceResponsePlanA(context: Context?, url: String): WebResourceResponse? {
        val glide = GlideUtil.assertManager(context, true)
        val glide2 = GlideUtil.assertManager(context, true)
        if (glide != null && glide2 != null) {
            val array = arrayOf<ByteArray?>(null)

            val submit = glide
                .`as`(InputStream::class.java)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate()
                )
                .thumbnail(0.1f)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)


            val futureTask = glide2
                .`as`(InputStream::class.java)
                .load(url)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                )
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            doAsync {
                val resource = futureTask.get(10, TimeUnit.SECONDS)
                if (resource != null) {
                    try {
                        val targetStream = FileInputStream(resource)
                        val baos = ByteArrayOutputStream()
                        val buffer = ByteArray(1024)
                        var len = 0
                        while ({ len = targetStream.read(buffer);len }() > -1) {
                            baos.write(buffer, 0, len)
                        }
                        baos.flush()
                        val arr = baos.toByteArray()
                        if (array[0] == null) {
                            try {
                                array[0] = arr
                                submit.cancel(true)
                            } catch (e: Exception) {
                            }
                        }
                        webView?.post {
                            webView?.postUrl(url, arr)
                        }
                        targetStream.close()
                        baos.close()
                    } catch (e: Exception) {
                    }
                }
            }
            var byteArray = byteArrayOf()
            try {
                val resource = submit.get(7, TimeUnit.SECONDS)
                val stream = FileInputStream(resource)
                val baos = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len = 0
                while ({ len = stream.read(buffer);len }() > -1) {
                    baos.write(buffer, 0, len)
                }
                baos.flush()
                byteArray = baos.toByteArray()
            } catch (e: Exception) {
                if (array[0] != null) {
                    val size = array[0]!!.size
                    byteArray = ByteArray(size)
                    System.arraycopy(array[0] ?: "", 0, byteArray, 0, size)
                }
            }
            val stream1 = ByteArrayInputStream(byteArray)
            val resp1 = WebResourceResponse("application/octet-stream", null, stream1)
            array[0] = byteArray
            return resp1
        } else {
            return null
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        listener?.error(Throwable(""))
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        listener?.error(Throwable(""))
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        listener?.error(Throwable(""))
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
//            listener?.webContentFinished()
//            parent?.requestLayout()
    }


}

abstract class OnWebViewListenerImpl : WebViewListener {
    override fun receiveTitle(title: String) {

    }

    override fun webLoadingProgress(progress: Int) {

    }

    override fun shouldOverLoad(webView: WebView, url: String): Boolean {
        return false
    }

    override fun webContentFinished() {

    }

    override fun error(error: Throwable) {

    }
}

interface WebViewListener {
    fun receiveTitle(title: String)
    fun error(error: Throwable) {}

    fun shouldOverLoad(webView: WebView, url: String): Boolean

    fun webLoadingProgress(progress: Int)

    fun webContentFinished()

}

inline fun WebView.loadStringData(data: String?) {
    post {
        loadDataWithBaseURL(null, data, "text/html", "UTF-8", null)
    }
}

inline fun WebView.loadCacheStringData(data: String?) {
    post {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info.isAvailable) {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            settings.cacheMode = WebSettings.LOAD_CACHE_ONLY//不使用网络，只加载缓存
        }
        loadDataWithBaseURL(null, data, "text/html", "UTF-8", null)
    }
}
