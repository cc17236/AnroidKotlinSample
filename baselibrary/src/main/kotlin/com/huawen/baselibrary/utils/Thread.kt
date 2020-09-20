package com.huawen.baselibrary.utils

import android.os.Looper
/**
 * Created by vicky
 *
 * @Author vicky
 */
inline fun Any.isMainThread(tag:String?=""): Boolean {
    val isMainThread = Looper.getMainLooper() == Looper.myLooper()
    Debuger.print("${tag?:""}当前线程是${if (isMainThread) "主" else "子"}线程")
    return isMainThread
}
