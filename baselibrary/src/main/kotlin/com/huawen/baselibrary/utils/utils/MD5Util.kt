package com.huawen.baselibrary.utils.utils


import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest

/**
 * Created by zhangliangming on 2017/8/4.
 */
object MD5Util {

    /**
     * 获取文件的md5
     *
     * @param file
     * @return
     * @throws Exception
     */
    fun getFileMd5(file: File): String {
        var value = ""
        var `in`: FileInputStream? = null
        try {
            `in` = FileInputStream(file)
            val byteBuffer = `in`.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(byteBuffer)
            val bi = BigInteger(1, md5.digest())
            value = bi.toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != `in`) {
                try {
                    `in`.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return value

    }

}
