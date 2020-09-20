package com.huawen.baselibrary.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/04/30
 * desc  : 缓存相关工具类
</pre> *
 */
class CacheUtils private constructor(cacheDir: File, maxSize: Long, maxCount: Int) {
    private val mCacheManager: CacheManager

    init {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw RuntimeException("can't make dirs in " + cacheDir.absolutePath)
        }
        mCacheManager = CacheManager(cacheDir, maxSize, maxCount)
    }

    /**
     * 缓存中写入字节数组
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    @JvmOverloads
    fun puta(key: String, value: ByteArray?, saveTime: Int = -1) {
        var value = value
        if (saveTime >= 0) value = CacheHelper.newByteArrayWithTime(saveTime, value!!)
        val file = mCacheManager.newFile(key)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            out.write(value!!)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(out)
            mCacheManager.put(file)
        }
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key 键
     * @return 字节数组
     */
    fun getBytes(key: String): ByteArray? {
        val file = mCacheManager.getFile(key)
        if (!file.exists()) return null
        var fc: FileChannel? = null
        try {
            fc = RandomAccessFile(file, "r").channel
            val size = fc!!.size().toInt()
            val byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, size.toLong()).load()
            val data = ByteArray(size)
            byteBuffer.get(data, 0, size)
            if (!CacheHelper.isDue(data)) {
                return CacheHelper.getDataWithoutDueTime(data)
            } else {
                mCacheManager.remove(key)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(fc)
        }
        return null
    }

    /**
     * 缓存中写入String
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    @JvmOverloads
    fun put(key: String, value: String, saveTime: Int = -1) {
        put(key, value.toByteArray(), saveTime)
    }

    /**
     * 缓存中读取String
     *
     * @param key 键
     * @return String
     */
    fun getString(key: String): String? {
        val bytes = getBytes(key) ?: return null
        return String(bytes)
    }

    /**
     * 缓存中写入JSONObject
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    @JvmOverloads
    fun put(key: String, value: JSONObject, saveTime: Int = -1) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 缓存中读取JSONObject
     *
     * @param key 键
     * @return JSONObject
     */
    fun getJSONObject(key: String): JSONObject? {
        val json = getString(key)
        try {
            return JSONObject(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 缓存中写入JSONArray
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    @JvmOverloads
    fun put(key: String, value: JSONArray, saveTime: Int = -1) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 缓存中读取JSONArray
     *
     * @param key 键
     * @return JSONArray
     */
    fun getJSONArray(key: String): JSONArray? {
        val JSONString = getString(key)
        try {
            return JSONArray(JSONString)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 缓存中写入Serializable
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    @JvmOverloads
    fun put(key: String, value: Serializable, saveTime: Int = -1) {
        val baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            oos = ObjectOutputStream(ByteArrayOutputStream())
            oos.writeObject(value)
            val data = baos?.toByteArray()
            puta(key, data, saveTime)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(oos)
        }
    }

    /**
     * 缓存中读取Serializable
     *
     * @param key 键
     * @return Serializable
     */
    fun getObject(key: String): Any? {
        val bytes = getBytes(key) ?: return null
        var ois: ObjectInputStream? = null
        try {
            ois = ObjectInputStream(ByteArrayInputStream(bytes))
            return ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(ois)
        }
    }

    /**
     * 缓存中写入bitmap
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    @JvmOverloads
    fun put(key: String, value: Bitmap, saveTime: Int = -1) {
        puta(key, CacheHelper.bitmap2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取bitmap
     *
     * @param key 键
     * @return bitmap
     */
    fun getBitmap(key: String): Bitmap? {
        val bytes = getBytes(key) ?: return null
        return CacheHelper.bytes2Bitmap(bytes)
    }

    ///////////////////////////////////////////////////////////////////////////
    // drawable 数据 读写
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入drawable
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Drawable) {
        puta(key, CacheHelper.drawable2Bytes(value))
    }

    /**
     * 缓存中写入drawable
     *
     * @param key      键
     * @param value    值
     * @param saveTime 保存时长，单位：秒
     */
    fun put(key: String, value: Drawable, saveTime: Int) {
        puta(key, CacheHelper.drawable2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取drawable
     *
     * @param key 键
     * @return bitmap
     */
    fun getDrawable(key: String): Drawable? {
        val bytes = getBytes(key) ?: return null
        return CacheHelper.bytes2Drawable(bytes)
    }

    /**
     * 获取缓存文件
     *
     * @param key 键
     * @return 缓存文件
     */
    fun getCacheFile(key: String): File? {
        val file = mCacheManager.newFile(key)
        return if (file.exists()) file else null
    }

    /**
     * 移除某个key
     *
     * @param key 键
     * @return 是否移除成功
     */
    fun remove(key: String): Boolean {
        return mCacheManager.remove(key)
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        mCacheManager.clear()
    }

    private inner class CacheManager internal constructor(private val cacheDir: File, private val sizeLimit: Long, private val countLimit: Int) {
        private val cacheSize: AtomicLong
        private val cacheCount: AtomicInteger
        private val lastUsageDates = Collections.synchronizedMap(HashMap<File, Long>())

        init {
            cacheSize = AtomicLong()
            cacheCount = AtomicInteger()
            calculateCacheSizeAndCacheCount()
        }

        /**
         * 计算 cacheSize和cacheCount
         */
        private fun calculateCacheSizeAndCacheCount() {
            Thread(Runnable {
                var size = 0
                var count = 0
                val cachedFiles = cacheDir.listFiles()
                if (cachedFiles != null) {
                    for (cachedFile in cachedFiles) {
                        size += cachedFile.length().toInt()
                        count += 1
                        lastUsageDates[cachedFile] = cachedFile.lastModified()
                    }
                    cacheSize.set(size.toLong())
                    cacheCount.set(count)
                }
            }).start()
        }

        internal fun newFile(key: String): File {
            return File(cacheDir, key.hashCode().toString() + "")
        }

        internal fun getFile(key: String): File {
            val file = newFile(key)
            val currentTime = System.currentTimeMillis()
            file.setLastModified(currentTime)
            lastUsageDates[file] = currentTime
            return file
        }

        internal fun put(file: File) {
            var curCacheCount = cacheCount.get()
            while (curCacheCount + 1 > countLimit) {
                val freedSize = removeOldest()
                cacheSize.addAndGet(-freedSize)
                curCacheCount = cacheCount.addAndGet(-1)
            }
            cacheCount.addAndGet(1)
            val valueSize = file.length()
            var curCacheSize = cacheSize.get()
            while (curCacheSize + valueSize > sizeLimit) {
                val freedSize = removeOldest()
                curCacheSize = cacheSize.addAndGet(-freedSize)
            }
            cacheSize.addAndGet(valueSize)
            val millis = System.currentTimeMillis()
            file.setLastModified(millis)
            lastUsageDates[file] = millis
        }

        internal fun remove(key: String): Boolean {
            val file = getFile(key)
            if (file.delete()) {
                cacheSize.addAndGet(-file.length())
                cacheCount.addAndGet(-1)
                return true
            }
            return false
        }

        internal fun clear() {
            lastUsageDates.clear()
            cacheSize.set(0)
            cacheCount.set(0)
            val files = cacheDir.listFiles() ?: return
            for (file in files) file.delete()
        }

        /**
         * 移除旧的文件
         *
         * @return 移除的字节数
         */
        private fun removeOldest(): Long {
            if (lastUsageDates.isEmpty()) return 0
            var oldestUsage: Long? = null
            var oldestFile: File? = null
            val entries = lastUsageDates.entries
            synchronized(lastUsageDates) {
                for ((key, lastValueUsage) in entries) {
                    if (oldestFile == null) {
                        oldestFile = key
                        oldestUsage = lastValueUsage
                    } else {
                        if (lastValueUsage < oldestUsage ?: 0) {
                            oldestUsage = lastValueUsage
                            oldestFile = key
                        }
                    }
                }
            }
            val fileSize = oldestFile!!.length()
            if (oldestFile!!.delete()) {
                lastUsageDates.remove(oldestFile)
            }
            return fileSize
        }
    }

    /**
     * 缓存帮助类
     */
    private object CacheHelper {

        internal val timeInfoLen = 17

        private fun newStringWithTime(second: Int, strInfo: String): String {
            return createDueTime(second) + strInfo
        }

        internal fun newByteArrayWithTime(second: Int, data: ByteArray): ByteArray {
            val time = createDueTime(second).toByteArray()
            val content = ByteArray(time.size + data.size)
            System.arraycopy(time, 0, content, 0, time.size)
            System.arraycopy(data, 0, content, time.size, data.size)
            return content
        }

        /**
         * 创建过期时间
         *
         * @param second 秒
         * @return _$millis$_
         */
        private fun createDueTime(second: Int): String {
            return String.format(Locale.getDefault(), "_$%013d\$_", System.currentTimeMillis() + second * 1000)
        }

        private fun isDue(data: String): Boolean {
            return isDue(data.toByteArray())
        }

        internal fun isDue(data: ByteArray): Boolean {
            val millis = getDueTime(data)
            return millis != (-1).toLong() && System.currentTimeMillis() > millis
        }

        private fun getDueTime(data: ByteArray): Long {
            if (hasTimeInfo(data)) {
                val millis = String(copyOfRange(data, 2, 15))
                try {
                    return java.lang.Long.parseLong(millis)
                } catch (e: NumberFormatException) {
                    return -1
                }

            }
            return -1
        }

        private fun getDataWithoutDueTime(data: String): String {
            var data = data
            if (hasTimeInfo(data.toByteArray())) {
                data = data.substring(timeInfoLen)
            }
            return data
        }

        internal fun getDataWithoutDueTime(data: ByteArray): ByteArray {
            return if (hasTimeInfo(data)) {
                copyOfRange(data, timeInfoLen, data.size)
            } else data
        }

        private fun copyOfRange(original: ByteArray, from: Int, to: Int): ByteArray {
            val newLength = to - from
            if (newLength < 0) throw IllegalArgumentException(from.toString() + " > " + to)
            val copy = ByteArray(newLength)
            System.arraycopy(original, from, copy, 0, Math.min(original.size - from, newLength))
            return copy
        }

        private fun hasTimeInfo(data: ByteArray?): Boolean {
            return (data != null
                    && data.size >= timeInfoLen
                    && data[0] == '_'.toByte()
                    && data[1] == '$'.toByte()
                    && data[15] == '_'.toByte()
                    && data[16] == '$'.toByte())
        }


        /**
         * bitmap转byteArr
         *
         * @param bitmap bitmap对象
         * @return 字节数组
         */
        internal fun bitmap2Bytes(bitmap: Bitmap?): ByteArray? {
            if (bitmap == null) return null
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }

        /**
         * byteArr转bitmap
         *
         * @param bytes 字节数组
         * @return bitmap
         */
        internal fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
            return if (bytes == null || bytes.size == 0) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * drawable转bitmap
         *
         * @param drawable drawable对象
         * @return bitmap
         */
        private fun drawable2Bitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap
            if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * bitmap转drawable
         *
         * @param bitmap bitmap对象
         * @return drawable
         */
        private fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
            return if (bitmap == null) null else BitmapDrawable(Utils.getContext().resources, bitmap)
        }

        /**
         * drawable转byteArr
         *
         * @param drawable drawable对象
         * @return 字节数组
         */
        internal fun drawable2Bytes(drawable: Drawable?): ByteArray? {
            return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable))
        }

        /**
         * byteArr转drawable
         *
         * @param bytes 字节数组
         * @return drawable
         */
        internal fun bytes2Drawable(bytes: ByteArray?): Drawable? {
            return if (bytes == null) null else bitmap2Drawable(bytes2Bitmap(bytes))
        }
    }

    companion object {

        private val LINE_SEP = System.getProperty("line.separator")
        private val DEFAULT_MAX_SIZE = 104857600 // 100Mb
        private val DEFAULT_MAX_COUNT = Integer.MAX_VALUE

        private val sCacheMap = HashMap<String, CacheUtils>()

        /**
         * 获取缓存实例
         *
         * 在/data/data/com.xxx.xxx/cache/cacheUtils目录
         *
         * 最大缓存100M
         *
         * 缓存个数不限
         *
         * @return [CacheUtils]
         */
        val instance: CacheUtils
            get() = getInstance("cacheUtils", DEFAULT_MAX_SIZE.toLong(), DEFAULT_MAX_COUNT)

        /**
         * 获取缓存实例
         *
         * 在/data/data/com.xxx.xxx/cache/cacheUtils目录
         *
         * @param maxSize  缓存大小，单位字节
         * @param maxCount 缓存个数
         * @return [CacheUtils]
         */
        fun getInstance(maxSize: Long, maxCount: Int): CacheUtils {
            return getInstance("cacheUtils", maxSize, maxCount)
        }

        /**
         * 获取缓存实例
         *
         * 在/data/data/com.xxx.xxx/cache/cacheName目录
         *
         * @param cacheName 缓存目录名
         * @param maxSize   缓存大小，单位字节
         * @param maxCount  缓存个数
         * @return [CacheUtils]
         */
        @JvmOverloads
        fun getInstance(cacheName: String, maxSize: Long = DEFAULT_MAX_SIZE.toLong(), maxCount: Int = DEFAULT_MAX_COUNT): CacheUtils {
            val file = File(Utils.getContext().cacheDir, cacheName)
            return getInstance(file, maxSize, maxCount)
        }

        /**
         * 获取缓存实例
         *
         * 在cacheDir目录
         *
         * @param cacheDir 缓存目录
         * @param maxSize  缓存大小，单位字节
         * @param maxCount 缓存个数
         * @return [CacheUtils]
         */
        @JvmOverloads
        fun getInstance(cacheDir: File, maxSize: Long = DEFAULT_MAX_SIZE.toLong(), maxCount: Int = DEFAULT_MAX_COUNT): CacheUtils {
            val cacheKey = cacheDir.absoluteFile.toString() + "_" + Process.myPid()
            var cache: CacheUtils? = sCacheMap[cacheKey]
            if (cache == null) {
                cache = CacheUtils(cacheDir, maxSize, maxCount)
                sCacheMap[cacheKey] = cache
            }
            return cache
        }
    }
}
/**
 * 获取缓存实例
 *
 * 在/data/data/com.xxx.xxx/cache/cacheName目录
 *
 * 最大缓存100M
 *
 * 缓存个数不限
 *
 * @param cacheName 缓存目录名
 * @return [CacheUtils]
 */
/**
 * 获取缓存实例
 *
 * 在cacheDir目录
 *
 * 最大缓存100M
 *
 * 缓存个数不限
 *
 * @param cacheDir 缓存目录
 * @return [CacheUtils]
 *////////////////////////////////////////////////////////////////////////////
// byte 读写
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入字节数组
 *
 * @param key   键
 * @param value 值
 *////////////////////////////////////////////////////////////////////////////
// String 读写
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入String
 *
 * @param key   键
 * @param value 值
 *////////////////////////////////////////////////////////////////////////////
// JSONObject 读写
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入JSONObject
 *
 * @param key   键
 * @param value 值
 *////////////////////////////////////////////////////////////////////////////
// JSONArray 读写
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入JSONArray
 *
 * @param key   键
 * @param value 值
 *////////////////////////////////////////////////////////////////////////////
// Serializable 读写
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入Serializable
 *
 * @param key   键
 * @param value 值
 *////////////////////////////////////////////////////////////////////////////
// bitmap 数据 读写
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入bitmap
 *
 * @param key   键
 * @param value 值
 */
