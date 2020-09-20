package com.huawen.baselibrary.alias

import android.net.Uri
import com.huawen.baselibrary.views.urldecode

typealias NSString = String
typealias NSInteger = Long
typealias CGFloat = Float
typealias block = () -> Unit


/**
 * 获取短文件名,不带扩展名
 * @param fileName
 * @return
 */
fun String.stringByDeletingPathExtension(): String? {
    return if (this.isNotEmpty() && this.lastIndexOf(".") > -1) {
        this.substring(0, this.lastIndexOf("."))
    } else this
}

/**
 * 获取扩展名,不带点
 * @param fileName
 * @return
 */
fun String.pathExtension(): String {
    return if (this.isNotEmpty() && this.lastIndexOf(".") > -1) {
        this.substring(this.lastIndexOf(".")).replace(".","")
    } else ""
}



fun String.fileName(): String {
    val self = urldecode()
    val start = self.lastIndexOf("/")
    val end = self.lastIndexOf(".")
    if (start != -1 && end != -1) {
        return self.substring(start + 1, end)
    } else {
        return ""
    }
}


fun String?.safeToInt() =
    if (isNullOrBlank()) {
        0
    } else {
        var arg = 0
        try {
            arg = this!!.toInt()
        } catch (e: Exception) {
        }
        arg
    }

fun String.lastPathComponent(): String {
    try {
        val uri = Uri.parse(this)
        val lastPath = uri.lastPathSegment
        if (lastPath != null)
            return lastPath
    } catch (e: Exception) {
    }
    return ""
}
