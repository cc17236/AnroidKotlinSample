package com.huawen.baselibrary.utils.constant

import android.os.Environment
import java.io.File

/**
 * @Description: 资源文件常量
 * @Author: zhangliangming
 * @Date: 2017/7/16 13:40
 * @Version:
 */
object ResourceConstants {

    /**
     * app应用名
     */
    val APPNAME = "Erwin"


    /**
     * 临时目录
     */
    val PATH_TEMP = "ErwinPlayer"

    /**
     * 全局异常日志目录
     */
    val PATH_CRASH = (PATH_TEMP + File.separator
            + "crash")

    val PATH_APK = (PATH_TEMP + File.separator
            + "apk" + File.separator + "download")
    /**
     * Logcat日志目录
     */
    val PATH_LOGCAT = (PATH_TEMP + File.separator
            + "logcat")


    val PATH_AUDIO_CACHE = PATH_TEMP + File.separator + "cacheMap" + File.separator + "map"


    /**
     * 歌曲目录
     */
    val PATH_AUDIO = PATH_TEMP + File.separator + "audio"
    /**
     * 歌曲临时保存路径
     */
    val PATH_AUDIO_TEMP = PATH_AUDIO + File.separator + "temp"
    /**
     * 歌手写真目录
     */
    val PATH_SINGER = (PATH_TEMP + File.separator
            + "singer")

    /**
     * 缓存
     */
    val PATH_CACHE = (PATH_TEMP + File.separator
            + "cache")
    /**
     * 图片缓存
     */
    val PATH_CACHE_IMAGE = (PATH_TEMP + File.separator
            + "cache" + File.separator + "image")
    /**
     * 歌曲缓存
     */
    val PATH_CACHE_AUDIO = (PATH_TEMP + File.separator
            + "cache" + File.separator + "audio")

    /**
     * 序列化对象保存路径
     */
    val PATH_CACHE_SERIALIZABLE = (PATH_TEMP + File.separator
            + "cache" + File.separator + "serializable")


    var AUDIO_RECORD_PATH: String? = null
        private set
        get() {
            if (field == null) {
                field = record_path()
            }
            return field
        }


    private fun record_path(): String? {
        var mRecAudioPath: File? = null
        if (Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + File.separator + "record")// 得到SD卡得路径
            mRecAudioPath = File(path)
            if (!mRecAudioPath.exists()) {
                mRecAudioPath.mkdirs()
            }
        } else {
            val path = (Environment.getDownloadCacheDirectory().toString()
                    + File.separator + "record")// 得到SD卡得路径
            mRecAudioPath = File(path)
            if (!mRecAudioPath.exists()) {
                mRecAudioPath.mkdirs()
            }
        }
        return mRecAudioPath.absolutePath
    }
}
