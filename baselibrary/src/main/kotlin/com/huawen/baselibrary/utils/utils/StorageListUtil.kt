package com.huawen.baselibrary.utils.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.storage.StorageManager

import com.huawen.baselibrary.utils.model.StorageInfo

import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.ArrayList


/**
 * 获取手机可用的外置内存卡和内置的内存卡
 *
 * @author Administrator
 */
class StorageListUtil(private val mActivity: Activity?) {
    private var mStorageManager: StorageManager? = null
    private var mMethodGetPaths: Method? = null

    val volumePaths: Array<String>?
        get() {
            var paths: Array<String>? = null
            try {
                paths = mMethodGetPaths!!.invoke(mStorageManager) as Array<String>
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            return paths
        }

    init {
        if (mActivity != null) {
            mStorageManager = mActivity
                    .getSystemService(Activity.STORAGE_SERVICE) as StorageManager
            try {
                mMethodGetPaths = mStorageManager!!.javaClass.getMethod(
                        "getVolumePaths")
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        @SuppressLint("NewApi")
        fun listAvaliableStorage(context: Context): List<StorageInfo> {
            val storagges = ArrayList<StorageInfo>()
            val storageManager = context
                    .getSystemService(Context.STORAGE_SERVICE) as StorageManager
            try {
                val paramClasses = arrayOf<Class<*>>()
                val getVolumeList = StorageManager::class.java.getMethod(
                        "getVolumeList", *paramClasses)
                getVolumeList.isAccessible = true
                val params = arrayOf<Any>()
                val invokes = getVolumeList.invoke(storageManager,
                        *params) as Array<Any>
                if (invokes != null) {
                    var info: StorageInfo? = null
                    for (i in invokes.indices) {
                        val obj = invokes[i]
                        val getPath = obj.javaClass.getMethod("getPath"
                        )
                        val path = getPath.invoke(obj) as String
                        info = StorageInfo(path)
                        val file = File(info.path)
                        if (file.exists() && file.isDirectory
                                && file.canWrite()) {
                            val isRemovable = obj.javaClass.getMethod(
                                    "isRemovable")
                            var state: String? = null
                            try {
                                val getVolumeState = StorageManager::class.java
                                        .getMethod("getVolumeState", String::class.java)
                                state = getVolumeState.invoke(
                                        storageManager, info.path) as String
                                info.state = state
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            if (info.isMounted) {
                                info.isRemoveable = (isRemovable.invoke(obj) as Boolean)
                                storagges.add(info)
                            }
                        }
                    }
                }
            } catch (e1: NoSuchMethodException) {
                e1.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            storagges.trimToSize()

            return storagges
        }
    }
}