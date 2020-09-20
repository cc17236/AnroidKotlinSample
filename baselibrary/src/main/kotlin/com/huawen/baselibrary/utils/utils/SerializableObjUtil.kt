package com.huawen.baselibrary.utils.utils

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Serializable对象处理类
 * Created by zhangliangming on 2017/8/15.
 */

object SerializableObjUtil {

    /**
     * 保存
     *
     * @param filePath
     * @param object
     */
    fun saveObj(filePath: String, `object`: Any) {
        try {
            val outputStream = FileOutputStream(filePath)
            val objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(`object`)

            objectOutputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 读取
     *
     * @param filePath
     * @return
     */
    fun readObj(filePath: String): Any? {
        var fileInputStream: FileInputStream? = null
        var objectInputStream: ObjectInputStream? = null
        try {
            fileInputStream = FileInputStream(filePath)
            objectInputStream = ObjectInputStream(fileInputStream)
            return objectInputStream.readObject()
        } catch (e: Exception) {
//            e.printStackTrace()
        } finally {
            if (objectInputStream != null)
                try {
                    objectInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (fileInputStream != null)
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }
        return null
    }
}
