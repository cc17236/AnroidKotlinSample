package com.huawen.baselibrary.jni

import android.app.Application
import android.content.Context
import com.huawen.baselibrary.utils.Debuger
import com.lahm.library.EasyProtectorLib
import com.lahm.library.VirtualApkCheckUtil

object AppVerify {

    init {
        System.loadLibrary("JNIEncrypt");
    }
    //Native 方法声明，显然这里需要传递一个Context过去用于获取包管理器以及包名

    /**
     * 检查 打包签名是否 是正确的 防止被二次打包
     *
     * @param con
     * @return 1 : pass ， -1 or  -2 : error.
     */
    external fun checkSignature(context: Any): Int

    external fun isNativeValidate(): Boolean

    /**
     * AES加密
     *
     * @param context
     * @param str
     * @return
     */
    external fun encode(context: Any, str: String): String


    /**
     * AES 解密
     *
     * @param context
     * @param str
     * @return UNSIGNATURE ： sign not pass .
     */
    external fun decode(context: Any, str: String): String

    /**
     * 检测作弊及二次开发
     */
    fun checkCheatSuspect(app: Application) {
        detectingIllegalUse(app) { it, vars ->
            val (emulator, multiVirtual, xposed) = vars
            if (multiVirtual) {
                Debuger.print("检测到应用多开的小婊砸")
                throw AppVerifyException()
            } else if (emulator) {
                Debuger.print("检测到模拟器")
            } else {
                if (it == true) {
                    Debuger.print("检测到可疑用户")
                    throw AppVerifyException()
                } else {
                    if (xposed) {
                        Debuger.print("有作案动机的人")
                    } else {
                        Debuger.print("正常用户")
                    }
                }
            }
        }

        checkSignature(app)


    }

    fun detectingIllegalUse(app: Application, fun0: (suspects: Boolean?, Triple<Boolean, Boolean, Boolean>) -> Unit) {
        try {
            val isRoot_ = EasyProtectorLib.checkIsRoot()
            val emulator_ = EasyProtectorLib.checkIsRunningInEmulator()
            val xposed_ = EasyProtectorLib.checkIsXposedExist()
            val multiVirtual_ = EasyProtectorLib.checkIsUsingMultiVirtualApp()
            VirtualApkCheckUtil.getSingleInstance().checkByPrivateFilePath(app)
            VirtualApkCheckUtil.getSingleInstance().checkByOriginApkPackageName(app)
            VirtualApkCheckUtil.getSingleInstance().checkByHasSameUid()
            VirtualApkCheckUtil.getSingleInstance().checkByMultiApkPackageName()
            fun0.invoke(null, Triple(emulator_, multiVirtual_, xposed_))
            VirtualApkCheckUtil.getSingleInstance().checkByPortListening(app.packageName) {
                //                VirtualApkCheckUtil.getSingleInstance().checkByPortListening(app.packageName,)
                val isRoot = EasyProtectorLib.checkIsRoot()
                val emulator = EasyProtectorLib.checkIsRunningInEmulator()
                val xposed = EasyProtectorLib.checkIsXposedExist()
                val multiVirtual = EasyProtectorLib.checkIsUsingMultiVirtualApp()
                fun0.invoke(true, Triple(emulator, multiVirtual, xposed))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fun0.invoke(null, Triple(false, false, false))
        }

    }



    class AppVerifyException:RuntimeException("检测到应用多开")

}