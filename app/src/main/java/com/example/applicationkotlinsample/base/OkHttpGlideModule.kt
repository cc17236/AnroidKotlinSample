package com.example.applicationkotlinsample.base

import android.annotation.SuppressLint
import android.content.Context
import android.net.TrafficStats
import androidx.bug.DelegatingSocketFactory
import android.util.Log
import cn.aihuaiedu.school.base.http.fixTls
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.example.applicationkotlinsample.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.net.Proxy
import java.net.Socket
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@GlideModule
class OkHttpGlideModule : AppGlideModule() {
    private var okHttpClient: OkHttpClient
    private val debugMode = true

    init {
        val THREAD_ID = 10001
        val socketFactory = object : DelegatingSocketFactory(SocketFactory.getDefault()) {
            override fun configureSocket(socket: Socket): Socket {
                TrafficStats.setThreadStatsTag(THREAD_ID)
                TrafficStats.tagSocket(socket)
                return socket
            }
        }
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (!debugMode) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        } else {
            if (BuildConfig.DEBUG) {//开发模式中记录整个body的日志
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {//开发模式中记录基本的一些日志，如状态值返回200
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            }
        }
        //定制OkHttp
        val httpClientBuilder = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .socketFactory(socketFactory)
            .fixTls()
            .retryOnConnectionFailure(true)
            .hostnameVerifier(object :HostnameVerifier{
                @SuppressLint("BadHostnameVerifier")
                override fun verify(p0: String?, p1: SSLSession?): Boolean {
                    return true
                }
            })
        //请求头设置
        httpClientBuilder.interceptors().add(httpLoggingInterceptor)
        httpClientBuilder.interceptors().add(HeaderInterceptor())
        okHttpClient = httpClientBuilder
            .build()

    }

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val userAgent = System.getProperty("http.agent")?:""
            val request = chain.request()
                .newBuilder()
                .addHeader("User-Agent", userAgent)
                .build()
            return chain.proceed(request)
        }

    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val memoryCacheSizeBytes = 1024 * 1024 * 40 // 40mb
        builder.setLogLevel(if (debugMode) Log.DEBUG else Log.ERROR)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
//                .disallowHardwareConfig()
        )
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
    }


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
//                .register(ByteBufferRewinder.Factory())
//                .append(File::class.java, ByteBuffer::class.java, ByteBufferFileLoader.Factory())
//                .append(File::class.java, InputStream::class.java, FileLoader.StreamFactory())
//                .append(File::class.java, File::class.java, FileDecoder())
//                .append(File::class.java, ParcelFileDescriptor::class.java, FileLoader.FileDescriptorFactory())
//                .append(String::class.java, InputStream::class.java, DataUrlLoader.StreamFactory())
//                .append(String::class.java, InputStream::class.java, StringLoader.StreamFactory())
//                .append(String::class.java, ParcelFileDescriptor::class.java, StringLoader.FileDescriptorFactory())
//                .append(Uri::class.java, InputStream::class.java, HttpUriLoader.Factory())
//                .append(Uri::class.java, InputStream::class.java, AssetUriLoader.StreamFactory(context.assets))
//                .append(Uri::class.java, ParcelFileDescriptor::class.java, AssetUriLoader.FileDescriptorFactory(context.assets))
//                .append(Uri::class.java, InputStream::class.java, MediaStoreImageThumbLoader.Factory(context))
//                .append(Uri::class.java, InputStream::class.java, MediaStoreVideoThumbLoader.Factory(context))
//                .append(Uri::class.java, InputStream::class.java, UriLoader.StreamFactory(context.contentResolver))
//                .append(Uri::class.java, ParcelFileDescriptor::class.java, UriLoader.FileDescriptorFactory(context.contentResolver))
//                .append(Uri::class.java, InputStream::class.java, UrlUriLoader.StreamFactory())
//                .append(URL::class.java, InputStream::class.java, UrlLoader.StreamFactory())
//                .append(Uri::class.java, File::class.java, MediaStoreFileLoader.Factory(context))
//                .append(GlideUrl::class.java, InputStream::class.java, HttpGlideUrlLoader.Factory())
//                .append(ByteArray::class.java, ByteBuffer::class.java, ByteArrayLoader.ByteBufferFactory())
//                .append(ByteArray::class.java, InputStream::class.java, ByteArrayLoader.StreamFactory())
//                .register(Bitmap::class.java, ByteArray::class.java, BitmapBytesTranscoder())
//                .register(GifDrawable::class.java, ByteArray::class.java, GifDrawableBytesTranscoder())
        super.registerComponents(context, glide, registry)
    }


}