package cn.aihuaiedu.school.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.huawen.baselibrary.utils.Debuger
import java.io.File


/**
 *
 * @author 2jingo
 * @time 2019/11/16 10:24
 */
object AppUtil {


    /**
     * 读取baseurl
     *
     * @param url
     * @return
     */
    fun getBasUrl(url: String): String {
        var url = url
        var head = ""
        var index = url.indexOf("://")
        if (index != -1) {
            head = url.substring(0, index + 3)
            url = url.substring(index + 3)
        }
        index = url.indexOf("/")
        if (index != -1) {
            url = url.substring(0, index + 1)
        }
        return head + url
    }

    fun isMobileNet(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
        if (activeNetworkInfo == null) return false
        if (activeNetworkInfo!!.getType() == ConnectivityManager.TYPE_MOBILE) {
            Debuger.print("连接的网络是移动数据流量")
            return true
        } else {
            Debuger.print("不是移动数据流量")
            return false
        }
    }


    /**
     * 安装apk
     */
    fun installApk(context: Context, savePath: String) {
        val file = File(context!!.externalCacheDir, "aihua.apk")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //判读版本是否在7.0以上
            Debuger.print("Build.VERSION.SDK_INT >= Build.VERSION_CODES.N")
            val apkUri = FileProvider.getUriForFile(context, "cn.aihuaiedu.school.FileProvider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)

    }


}
