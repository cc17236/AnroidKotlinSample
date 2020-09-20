package com.huawen.baselibrary.jni


import android.content.Context
import android.content.pm.PackageManager
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.StringUtils
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory

object SignatureTool {

    private val PKGNAME = "com.aihuaiedu.xxxx"


    /* 1.如果包名不存在 就会报错  return -1
    System.err:     android.content.pm.PackageManager$NameNotFoundException: [配置包名]
    System.err:     at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:137)

     2.如果配置包名不是当前进程的包名 那么，也不会报错。
        这点我觉得奇怪 居然可以取得别人的包的keystore的hash
    */
    fun getSignatureHash(context: Context): Int {

        try {
            val packageInfo = context.packageManager.getPackageInfo(PKGNAME, PackageManager.GET_SIGNATURES)

            val signs = packageInfo.signatures
            val sign = signs[0]
            val value = sign.hashCode()
            Debuger.print("${value}")
            return value
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    fun getSignatureSha1(context: Context): String? {

        try {
            val packageInfo = context.packageManager.getPackageInfo(PKGNAME, PackageManager.GET_SIGNATURES)

            val signs = packageInfo.signatures
            val sign = signs[0]
            sign.toByteArray()
            val bis = ByteArrayInputStream(sign.toByteArray())
            val arr = MessageDigest.getInstance("SHA1")
                .digest(CertificateFactory.getInstance("X.509").generateCertificate(bis).encoded)
            val value = StringUtils.bytes2HexString(arr)
            Debuger.print("${value}")
            return value
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}
