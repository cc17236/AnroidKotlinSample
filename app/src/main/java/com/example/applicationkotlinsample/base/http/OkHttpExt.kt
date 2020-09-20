@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package cn.aihuaiedu.school.base.http

import android.os.Build
import com.huawen.baselibrary.schedule.host.SSLFactory
import com.huawen.baselibrary.schedule.host.TrustAllCerts
import com.huawen.baselibrary.utils.Debuger
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import javax.net.ssl.SSLContext


inline fun OkHttpClient.Builder.fixTls(): OkHttpClient.Builder {
    if (Build.VERSION.SDK_INT in 16..21) {
        try {
            val sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, null)
            this.sslSocketFactory(SSLFactory.createSSLSocketFactory()!!, TrustAllCerts())
            val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()
            val specs = ArrayList<ConnectionSpec>()
            specs.add(cs)
            specs.add(ConnectionSpec.COMPATIBLE_TLS)
            specs.add(ConnectionSpec.CLEARTEXT)
            this.connectionSpecs(specs)
        } catch (exc: Exception) {
            Debuger.print("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
        }
    }
    return this
}