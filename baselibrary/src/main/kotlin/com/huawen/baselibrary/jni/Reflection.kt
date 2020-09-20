package com.huawen.baselibrary.jni

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build

import java.lang.reflect.Method

/**
 * @author weishu
 * @date 2018/6/7.
 */

object Reflection {

    private val UNKNOWN = -9999

    private val ERROR_SET_APPLICATION_FAILED = -20

    private var unsealed = UNKNOWN

    init {
        System.loadLibrary("free-reflection")
    }

    private external fun unsealNative(targetSdkVersion: Int): Int

    fun unseal(context: Context?): Int {
        if (Build.VERSION.SDK_INT < 28) {
            // Below Android P, ignore
            return 0
        }

        if (context == null) {
            return -10
        }

        val applicationInfo = context.applicationInfo
        val targetSdkVersion = applicationInfo.targetSdkVersion

        synchronized(Reflection::class.java) {
            if (unsealed == UNKNOWN) {
                unsealed = unsealNative(targetSdkVersion)
                if (unsealed >= 0) {
                    try {
                        @SuppressLint("PrivateApi") val setHiddenApiEnforcementPolicy = ApplicationInfo::class.java
                            .getDeclaredMethod("setHiddenApiEnforcementPolicy", Int::class.javaPrimitiveType!!)
                        setHiddenApiEnforcementPolicy.invoke(applicationInfo, 0)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        unsealed = ERROR_SET_APPLICATION_FAILED
                    }

                }
            }
        }
        return unsealed
    }
}
