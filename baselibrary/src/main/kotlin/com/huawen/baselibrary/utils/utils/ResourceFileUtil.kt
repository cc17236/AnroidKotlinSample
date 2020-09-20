package com.huawen.baselibrary.utils.utils

import android.content.Context
import android.os.Build
import android.os.Environment


import com.huawen.baselibrary.utils.Debuger

import java.io.File

/**
 * @Description: 资源文件处理类
 * @Author: zhangliangming
 * @Date: 2017/7/16 13:48
 * @Version:
 */
object ResourceFileUtil {
    /**
     * 文件的基本路径
     */
    private var baseFilePath: String? = null

    /**
     * 获取资源文件的完整路径
     *
     * @param context
     * @param tempFilePath 文件的临时路径
     * @return
     */
    fun getFilePath(context: Context, tempFilePath: String, fileName: String?=null): String {
        var fileName__ = fileName

        if (baseFilePath == null) {
            val storageInfos = StorageListUtil.listAvaliableStorage(context)
            for (i in storageInfos.indices) {
                val temp = storageInfos[i]
                Debuger.print("isRemoveable"+temp?.isRemoveable)
                if (!temp.isRemoveable) {
                    baseFilePath = temp.path
                    break
                }
            }
            if (baseFilePath.isNullOrBlank()){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1&&OSUtils.romType== OSUtils.ROM.EMUI) {
//                    baseFilePath=context.cacheDir.absolutePath+"/tempSong"//
//                }else{
                    baseFilePath=Environment.getExternalStorageDirectory().absolutePath+"/tempSong"//
//                }
            }
        }

        //
        if (fileName__ == null) {
            fileName__ = ""
        }

        //
        val filePath = baseFilePath + File.separator + tempFilePath + File.separator + fileName__

        val file = File(filePath)
        if (!fileName__.isNullOrBlank()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
        } else {
            if (!file.exists()) {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.mkdirs()
            }
        }

        return filePath
    }
}
