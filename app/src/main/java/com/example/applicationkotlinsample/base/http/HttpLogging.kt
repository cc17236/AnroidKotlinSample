package cn.aihuaiedu.school.base.http

import android.util.Log
import com.example.applicationkotlinsample.base.http.JsonUtil
import okhttp3.logging.HttpLoggingInterceptor

class HttpLogger : HttpLoggingInterceptor.Logger {
    private val mMessage = StringBuilder()

    companion object {
        const val TAG = "HttpLogger"
    }

    override fun log(message: String) {
        var msg = message
        // 请求或者响应开始
//        if (msg.startsWith("--> POST")) {
//            mMessage.setLength(0)
//        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        var hasJson=false
        if ((msg.startsWith("{") && msg.endsWith("}"))
            || (msg.startsWith("[") && msg.endsWith("]"))
        ) {
            msg = JsonUtil.formatJson(JsonUtil.decodeUnicode(msg))
            hasJson=true
        }
        // 响应结束，打印整条日志
        Log.e("OkHttp","$msg".replace("\n","").replace("\t",""))
    }
}