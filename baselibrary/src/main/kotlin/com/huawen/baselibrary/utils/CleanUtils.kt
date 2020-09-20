package com.huawen.baselibrary.utils

import java.io.File
import java.math.BigDecimal


/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/27
 * desc  : 清除相关工具类
</pre> *
 */
class CleanUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        fun getTotalCacheSizeString(): String {
            var cacheSize: Double = 0.toDouble()
            if (SDCardUtils.isSDCardEnable) {
                cacheSize += getFolderSize(Utils.getContext().externalCacheDir)
            }
            cacheSize += getFolderSize(Utils.getContext().cacheDir)
            cacheSize += getFolderSize(Utils.getContext().filesDir)
            return getFormatSize(cacheSize)
        }

        fun getTotalCacheSize(): Double {
            var cacheSize: Double = 0.toDouble()
            if (SDCardUtils.isSDCardEnable) {
                cacheSize += getFolderSize(Utils.getContext().externalCacheDir)
            }
            cacheSize += getFolderSize(Utils.getContext().cacheDir)
            cacheSize += getFolderSize(Utils.getContext().filesDir)
            return cacheSize
        }

        // 获取文件
        //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，
        // 一般放一些长时间保存的数据
        //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，
        // 一般存放临时缓存数据
        @Throws(Exception::class)
        fun getFolderSize(file: File?): Double {
            var size: Double = 0.toDouble()
            if (file==null)return 0.0
            try {
                val fileList = file.listFiles()
                for (i in fileList.indices) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory) {
                        size = size + getFolderSize(fileList[i])
                    } else {
                        size = size + fileList[i].length()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return size
        }

        /**
         * 格式化单位
         * @param size
         */
        fun getFormatSize(size: Double): String {
            val kiloByte = size / 1024
            if (kiloByte < 1) {
                return size.toString() + "Byte"
            }

            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "KB"
            }

            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(java.lang.Double.toString(megaByte))
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "MB"
            }

            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "GB"
            }
            val result4 = BigDecimal(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
        }


        /**
         * 清除内部缓存
         *
         * /data/data/com.xxx.xxx/cache
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanInternalCache(): Boolean {
            return FileUtils.deleteFilesInDir(Utils.getContext().cacheDir)
        }

        /**
         * 清除内部文件
         *
         * /data/data/com.xxx.xxx/files
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanInternalFiles(): Boolean {
            return FileUtils.deleteFilesInDir(Utils.getContext().filesDir)
        }

        /**
         * 清除内部数据库
         *
         * /data/data/com.xxx.xxx/databases
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanInternalDbs(): Boolean {
            return FileUtils.deleteFilesInDir(Utils.getContext().filesDir.parent + File.separator + "databases")
        }

        /**
         * 根据名称清除数据库
         *
         * /data/data/com.xxx.xxx/databases/dbName
         *
         * @param dbName  数据库名称
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanInternalDbByName(dbName: String): Boolean {
            return Utils.getContext().deleteDatabase(dbName)
        }

        /**
         * 清除内部SP
         *
         * /data/data/com.xxx.xxx/shared_prefs
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanInternalSP(): Boolean {
            return FileUtils.deleteFilesInDir(Utils.getContext().filesDir.parent + File.separator + "shared_prefs")
        }

        /**
         * 清除外部缓存
         *
         * /storage/emulated/0/android/data/com.xxx.xxx/cache
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanExternalCache(): Boolean {
            return SDCardUtils.isSDCardEnable && FileUtils.deleteFilesInDir(Utils.getContext().externalCacheDir)
        }

        /**
         * 清除自定义目录下的文件
         *
         * @param dirPath 目录路径
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanCustomCache(dirPath: String): Boolean {
            return FileUtils.deleteFilesInDir(dirPath)
        }

        /**
         * 清除自定义目录下的文件
         *
         * @param dir 目录
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        fun cleanCustomCache(dir: File?): Boolean {
            return FileUtils.deleteFilesInDir(dir)
        }
    }
}
