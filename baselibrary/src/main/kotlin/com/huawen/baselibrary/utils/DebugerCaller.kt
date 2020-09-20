package com.huawen.baselibrary.utils

import android.util.Log

/**
 * Created by vicky
 *
 * @Author vicky
 */
object DebugerCaller {
    internal fun getCurrentStackTraceElement(): StackTraceElement {
        if (Thread.currentThread().stackTrace.size<4){
            return Thread.currentThread().stackTrace[2]
        }
        return Thread.currentThread().stackTrace[3]
    }

    fun getCallerStackTraceElement(): StackTraceElement {
        if (Thread.currentThread().stackTrace.size<4){
            return Thread.currentThread().stackTrace[3]
        }
        return Thread.currentThread().stackTrace[4]
    }

    fun printHide4(msg: String, traceElement: StackTraceElement) {
        printHide(Debuger.customTagPrefix, msg, traceElement)
    }


    fun printHide5(tag: String, tr: Throwable?, traceElement: StackTraceElement) {
        if (tr == null) {
            printHideNobody(tag, traceElement)
            return
        }
        printHideFull(tag, "", tr, traceElement)
    }

    fun printHide(tag: String, obj: Any?, traceElement: StackTraceElement) {
        if (obj == null) {
            printHideNoThrow(tag, "空打印", traceElement)
            return
        }
        printHideNoThrow(tag, obj, traceElement)
    }

    fun printHide(tag: String, obj: Any?, tr: Throwable?, traceElement: StackTraceElement) {
        if (obj == null) {
            printHideNoThrow(tag, "空打印", tr, traceElement)
            return
        }
        printHideNoThrow(tag, obj, tr, traceElement)
    }

    fun printHide(msg: String, traceElement: StackTraceElement) {
        printHideNoThrow(Debuger.customTagPrefix, msg, traceElement)
    }

    fun printHideNoThrow(tag: String, obj: Any, tr: Throwable?, traceElement: StackTraceElement) {
        if (tr == null) {
            printHideNoThrow(tag, obj, traceElement)
            return
        }
        printHideFull(tag, obj, tr, traceElement)
    }

    fun printHideNobody(tag: String, traceElement: StackTraceElement) {
        grep(tag, null, null, traceElement)
    }


    fun printHideNoThrow(tag: String, obj: Any, traceElement: StackTraceElement) {
        grep(tag, obj, null, traceElement)
    }

    fun printHideFull(tag: String, obj: Any, tr: Throwable, traceElement: StackTraceElement) {
        grep(tag, obj, tr, traceElement)
    }

    private fun grep(tag: String, obj: Any?, tr: Throwable?, traceElement: StackTraceElement) {
        var logTag = tag
        var logBody = obj?.toString() ?: ""

        logTag += "(方法名:${traceElement.methodName})"

        val taskName = StringBuilder()
        if (!Debuger.hideStackLine){
            taskName.append("(")
                    .append(traceElement.fileName).append(":")
                    .append(traceElement.lineNumber).append(")")
        }

        logBody = taskName.toString() + logBody
        if (traceElement.className.toLowerCase().contains("Play".toLowerCase())
                || traceElement.className.toLowerCase().contains("Controls".toLowerCase())
                || traceElement.className.toLowerCase().contains("Music".toLowerCase())
                || traceElement.className.toLowerCase().contains("Media".toLowerCase())
        ) {
            if (tr == null) {
                i(logBody, logTag)
            } else {
                i(logBody, logTag, tr)
            }
        } else {
            if (tr == null) {
                e(logBody, logTag)
            } else {
                e(logBody, logTag, tr)
            }
        }

    }


    fun d(content: String, tag: String) {
        if (!Debuger.allowD) return
        Log.d(tag, content)
    }

    fun d(content: String, tag: String, tr: Throwable) {
        if (!Debuger.allowD) return
        Log.d(tag, content, tr)
    }

    fun e(content: String, tag: String) {
        if (!Debuger.allowE) return
        Log.e(tag, content)
    }

    fun e(content: String, tag: String, tr: Throwable) {
        if (!Debuger.allowE) return
        Log.e(tag, content, tr)
    }

    fun i(content: String, tag: String) {
        if (!Debuger.allowI) return
        Log.i(tag, content)
    }

    fun i(content: String, tag: String, tr: Throwable) {
        if (!Debuger.allowI) return
        Log.i(tag, content, tr)
    }

    fun v(content: String, tag: String) {
        if (!Debuger.allowV) return
        Log.v(tag, content)
    }

    fun v(content: String, tag: String, tr: Throwable) {
        if (!Debuger.allowV) return
        Log.v(tag, content, tr)
    }

    fun w(content: String, tag: String) {
        if (!Debuger.allowW) return
        Log.w(tag, content)
    }

    fun w(content: String, tag: String, tr: Throwable) {
        if (!Debuger.allowW) return
        Log.w(tag, content, tr)
    }

    fun w(tag: String, tr: Throwable) {
        if (!Debuger.allowW) return
        Log.w(tag, tr)
    }


    fun wtf(content: String, tag: String) {
        if (!Debuger.allowWtf) return
        Log.wtf(tag, content)
    }

    fun wtf(content: String, tag: String, tr: Throwable) {
        if (!Debuger.allowWtf) return
        Log.wtf(tag, content, tr)
    }

    fun wtf(tag: String, tr: Throwable) {
        if (!Debuger.allowWtf) return
        Log.wtf(tag, tr)
    }
}