package cn.aihuaiedu.school.utils

import android.content.Context
import android.util.Log
import com.example.applicationkotlinsample.BuildConfig


class LogUtil private constructor(val context: Context) {



    companion object {
        var isDebug = BuildConfig.DEBUG
        private val TAG = "LogUtil"

        // 下面四个是默认tag的函数
        fun i(msg: String) {
            if (isDebug)
                Log.i(TAG, msg)
        }

        fun d(msg: String) {
            if (isDebug)
                Log.d(TAG, msg)
        }

        fun e(msg: String) {
            if (isDebug)
                Log.e(TAG, msg)
        }

        fun v(msg: String) {
            if (isDebug)
                Log.v(TAG, msg)
        }

        // 下面是传入自定义tag的函数
        fun i(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (isDebug)
                Log.d(tag, msg)
        }

        fun e(tag: String, msg: String) {
            if (isDebug)
                Log.e(tag, msg)
        }

        fun v(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }
    }

}
