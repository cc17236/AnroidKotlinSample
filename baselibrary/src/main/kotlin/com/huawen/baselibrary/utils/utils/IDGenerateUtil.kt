package com.huawen.baselibrary.utils.utils

import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Administrator id自动生成器
 */
object IDGenerateUtil {
    private val integer = AtomicInteger(0)

    val id: String
        get() {
            val time = System.currentTimeMillis()
            val str = StringBuilder(20)
            str.append(time)
            val intValue = integer.getAndIncrement()
            if (integer.get() >= 10000) {
                integer.set(0)
            }
            if (intValue < 10) {
                str.append("000")
            } else if (intValue < 100) {
                str.append("00")
            } else if (intValue < 1000) {
                str.append("0")
            }
            str.append(intValue)
            return str.toString()
        }

    fun getId(length: Int): String {
        val time = System.currentTimeMillis()
        val str = StringBuilder(length)
        str.append(time)
        val intValue = integer.getAndIncrement()
        if (integer.get() >= 10000) {
            integer.set(0)
        }
        if (intValue < 10) {
            str.append("000")
        } else if (intValue < 100) {
            str.append("00")
        } else if (intValue < 1000) {
            str.append("0")
        }
        str.append(intValue)
        return str.toString()
    }

    fun getId(key: String): String {
        val time = System.currentTimeMillis()
        val str = StringBuilder(18)
        str.append(time)
        val intValue = integer.getAndIncrement()
        if (integer.get() >= 10000) {
            integer.set(0)
        }
        if (intValue < 10) {
            str.append("000")
        } else if (intValue < 100) {
            str.append("00")
        } else if (intValue < 1000) {
            str.append("0")
        }
        str.append(intValue)
        return key + str.toString()
    }

    fun getId(key: String, length: Int): String {
        val time = System.currentTimeMillis()
        val str = StringBuilder(length)
        str.append(time)
        val intValue = integer.getAndIncrement()
        if (integer.get() >= 10000) {
            integer.set(0)
        }
        if (intValue < 10) {
            str.append("000")
        } else if (intValue < 100) {
            str.append("00")
        } else if (intValue < 1000) {
            str.append("0")
        }
        str.append(intValue)
        return key + str.toString()
    }
}