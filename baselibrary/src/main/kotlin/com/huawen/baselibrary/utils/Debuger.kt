package com.huawen.baselibrary.utils

import com.huawen.baselibrary.BuildConfig
import com.huawen.baselibrary.utils.DebugerCaller.getCallerStackTraceElement
import com.huawen.baselibrary.utils.DebugerCaller.printHide
import com.huawen.baselibrary.utils.DebugerCaller.printHide4
import com.huawen.baselibrary.utils.DebugerCaller.printHide5
import com.huawen.baselibrary.utils.DebugerCaller.printHideNoThrow

/**
 * Created by vicky
 *
 * @Author vicky
 */
object Debuger {
    internal var customTagPrefix = "自定义TAG"
    internal var allowD = BuildConfig.DEBUG
    internal var allowE = BuildConfig.DEBUG
    internal var allowI = BuildConfig.DEBUG
    internal var allowV = BuildConfig.DEBUG
    internal var allowW = BuildConfig.DEBUG
    internal var allowWtf = BuildConfig.DEBUG
    internal var hideStackLine = !BuildConfig.DEBUG

    fun print(obj: Any?) {
        if (obj == null) {
            print(customTagPrefix, getCallerStackTraceElement())
            return
        }
        printHide4(obj.toString(), getCallerStackTraceElement())
    }


    fun print(tag: String?, obj: Any?) {
        if (tag == null) {
            printHide(customTagPrefix, obj, getCallerStackTraceElement())
            return
        }
        printHide(tag, obj, getCallerStackTraceElement())
    }

    fun print(tag: String?, obj: String?) {
        if (tag == null) {
            printHide(customTagPrefix, obj, getCallerStackTraceElement())
            return
        }
        printHide(tag, obj, getCallerStackTraceElement())
    }

    fun print(tag: String?, obj: String?, tr: Throwable?) {
        if (tag == null) {
            printHide(customTagPrefix, obj, tr, getCallerStackTraceElement())
        }
    }


    fun print(msg: String?) {
        if (msg == null) {
            print(customTagPrefix, getCallerStackTraceElement())
            return
        }
        printHide(msg, getCallerStackTraceElement())
    }

    fun print(msg: String?, tr: Throwable?) {
        if (msg == null) {
            printHide5(customTagPrefix, tr, getCallerStackTraceElement())
            return
        } else if (tr == null) {
            printHide5(msg, tr, getCallerStackTraceElement())
            return
        }
        printHideNoThrow(customTagPrefix, msg, tr, getCallerStackTraceElement())
    }

    fun print(tr: Throwable?) {
        if (tr == null) {
            printHide5(customTagPrefix, tr, getCallerStackTraceElement())
            return
        }
        printHideNoThrow(customTagPrefix, tr, getCallerStackTraceElement())
    }


}
