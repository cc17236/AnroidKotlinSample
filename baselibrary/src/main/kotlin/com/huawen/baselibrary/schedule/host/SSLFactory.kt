package com.huawen.baselibrary.schedule.host

import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

/**
 * @作者: #Administrator #
 *@日期: #2018/11/13 #
 *@时间: #2018年11月13日 13:58 #
 *@File:Kotlin File
 */
object SSLFactory {
    fun createSSLSocketFactory(): SSLSocketFactory? {
        var ssfFactory: SSLSocketFactory? = null;
        try {
            val sc = SSLContext.getInstance("SSL")
                sc.init(null, Array<TrustManager>(1) { TrustAllCerts() }, SecureRandom())
//            sc.init(null, null, SecureRandom())
            ssfFactory = sc.socketFactory
        } catch (e: Exception) {
        }

        return ssfFactory
    }
}