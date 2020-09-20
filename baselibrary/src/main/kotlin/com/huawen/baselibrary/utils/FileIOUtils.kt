package com.huawen.baselibrary.utils


import com.huawen.baselibrary.utils.constant.MemoryConstants

import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.ArrayList

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/05/24
 * desc  : 文件读写相关工具类
</pre> *
 */
class FileIOUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        private val LINE_SEP = System.getProperty("line.separator")

        /**
         * 将输入流写入文件
         *
         * @param filePath 路径
         * @param is       输入流
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromIS(filePath: String, `is`: InputStream): Boolean {
            return writeFileFromIS(FileUtils.getFileByPath(filePath), `is`, false)
        }

        /**
         * 将输入流写入文件
         *
         * @param filePath 路径
         * @param is       输入流
         * @param append   是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromIS(filePath: String, `is`: InputStream, append: Boolean): Boolean {
            return writeFileFromIS(FileUtils.getFileByPath(filePath), `is`, append)
        }

        /**
         * 将输入流写入文件
         *
         * @param file   文件
         * @param is     输入流
         * @param append 是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        @JvmOverloads
        fun writeFileFromIS(file: File?, `is`: InputStream?, append: Boolean = false): Boolean {
            if (!FileUtils.createOrExistsFile(file) || `is` == null) return false
            var os: OutputStream? = null
            try {
                os = BufferedOutputStream(FileOutputStream(file!!, append))
                val data = ByteArray(1024)
                var len: Int=-1
                while ({len = `is`.read(data, 0, 1024);len}() != -1) {
                    os.write(data, 0, len)
                }
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                CloseUtils.closeIO(`is`, os)
            }
        }

        /**
         * 将字节数组写入文件
         *
         * @param filePath 文件路径
         * @param bytes    字节数组
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByStream(filePath: String, bytes: ByteArray): Boolean {
            return writeFileFromBytesByStream(FileUtils.getFileByPath(filePath), bytes, false)
        }

        /**
         * 将字节数组写入文件
         *
         * @param filePath 文件路径
         * @param bytes    字节数组
         * @param append   是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByStream(filePath: String, bytes: ByteArray, append: Boolean): Boolean {
            return writeFileFromBytesByStream(FileUtils.getFileByPath(filePath), bytes, append)
        }

        /**
         * 将字节数组写入文件
         *
         * @param file   文件
         * @param bytes  字节数组
         * @param append 是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        @JvmOverloads
        fun writeFileFromBytesByStream(file: File?, bytes: ByteArray?, append: Boolean = false): Boolean {
            if (bytes == null || !FileUtils.createOrExistsFile(file)) return false
            var bos: BufferedOutputStream? = null
            try {
                bos = BufferedOutputStream(FileOutputStream(file!!, append))
                bos.write(bytes)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                CloseUtils.closeIO(bos)
            }
        }

        /**
         * 将字节数组写入文件
         *
         * @param filePath 文件路径
         * @param bytes    字节数组
         * @param isForce  是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByChannel(filePath: String, bytes: ByteArray, isForce: Boolean): Boolean {
            return writeFileFromBytesByChannel(FileUtils.getFileByPath(filePath), bytes, false, isForce)
        }

        /**
         * 将字节数组写入文件
         *
         * @param filePath 文件路径
         * @param bytes    字节数组
         * @param append   是否追加在文件末
         * @param isForce  是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByChannel(filePath: String, bytes: ByteArray, append: Boolean, isForce: Boolean): Boolean {
            return writeFileFromBytesByChannel(FileUtils.getFileByPath(filePath), bytes, append, isForce)
        }

        /**
         * 将字节数组写入文件
         *
         * @param file    文件
         * @param bytes   字节数组
         * @param isForce 是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByChannel(file: File, bytes: ByteArray, isForce: Boolean): Boolean {
            return writeFileFromBytesByChannel(file, bytes, false, isForce)
        }

        /**
         * 将字节数组写入文件
         *
         * @param file    文件
         * @param bytes   字节数组
         * @param append  是否追加在文件末
         * @param isForce 是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByChannel(file: File?, bytes: ByteArray?, append: Boolean, isForce: Boolean): Boolean {
            if (bytes == null) return false
            if (!append && !FileUtils.createFileByDeleteOldFile(file)) return false
            var fc: FileChannel? = null
            try {
                fc = RandomAccessFile(file, "rw").channel
                fc!!.position(fc.size())
                fc.write(ByteBuffer.wrap(bytes))
                if (isForce) fc.force(true)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                CloseUtils.closeIO(fc)
            }
        }

        /**
         * 将字节数组写入文件
         *
         * @param filePath 文件路径
         * @param bytes    字节数组
         * @param isForce  是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByMap(filePath: String, bytes: ByteArray, isForce: Boolean): Boolean {
            return writeFileFromBytesByMap(filePath, bytes, false, isForce)
        }

        /**
         * 将字节数组写入文件
         *
         * @param filePath 文件路径
         * @param bytes    字节数组
         * @param append   是否追加在文件末
         * @param isForce  是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByMap(filePath: String, bytes: ByteArray, append: Boolean, isForce: Boolean): Boolean {
            return writeFileFromBytesByMap(FileUtils.getFileByPath(filePath), bytes, append, isForce)
        }

        /**
         * 将字节数组写入文件
         *
         * @param file    文件
         * @param bytes   字节数组
         * @param isForce 是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByMap(file: File, bytes: ByteArray, isForce: Boolean): Boolean {
            return writeFileFromBytesByMap(file, bytes, false, isForce)
        }

        /**
         * 将字节数组写入文件
         *
         * @param file    文件
         * @param bytes   字节数组
         * @param append  是否追加在文件末
         * @param isForce 是否写入文件
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromBytesByMap(file: File?, bytes: ByteArray?, append: Boolean, isForce: Boolean): Boolean {
            if (bytes == null || !FileUtils.createOrExistsFile(file)) return false
            if (!append && !FileUtils.createFileByDeleteOldFile(file)) return false
            var fc: FileChannel? = null
            try {
                fc = RandomAccessFile(file, "rw").channel
                val mbb = fc!!.map(FileChannel.MapMode.READ_WRITE, fc.size(), bytes.size.toLong())
                mbb.put(bytes)
                if (isForce) mbb.force()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                CloseUtils.closeIO(fc)
            }
        }

        /**
         * 将字符串写入文件
         *
         * @param filePath 文件路径
         * @param content  写入内容
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromString(filePath: String, content: String): Boolean {
            return writeFileFromString(FileUtils.getFileByPath(filePath), content, false)
        }

        /**
         * 将字符串写入文件
         *
         * @param filePath 文件路径
         * @param content  写入内容
         * @param append   是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromString(filePath: String, content: String, append: Boolean): Boolean {
            return writeFileFromString(FileUtils.getFileByPath(filePath), content, append)
        }

        /**
         * 将字符串写入文件
         *
         * @param file    文件
         * @param content 写入内容
         * @param append  是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        @JvmOverloads
        fun writeFileFromString(file: File?, content: String?, append: Boolean = false): Boolean {
            if (file == null || content == null) return false
            if (!FileUtils.createOrExistsFile(file)) return false
            var bw: BufferedWriter? = null
            try {
                bw = BufferedWriter(FileWriter(file, append))
                bw.write(content)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                CloseUtils.closeIO(bw)
            }
        }

        ///////////////////////////////////////////////////////////////////////////
        // the divide line of write and read
        ///////////////////////////////////////////////////////////////////////////

        /**
         * 读取文件到字符串链表中
         *
         * @param filePath 文件路径
         * @return 字符串链表中
         */
        fun readFile2List(filePath: String): List<String>? {
            return readFile2List(FileUtils.getFileByPath(filePath), null)
        }

        /**
         * 读取文件到字符串链表中
         *
         * @param filePath    文件路径
         * @param charsetName 编码格式
         * @return 字符串链表中
         */
        fun readFile2List(filePath: String, charsetName: String): List<String>? {
            return readFile2List(FileUtils.getFileByPath(filePath), charsetName)
        }

        /**
         * 读取文件到字符串链表中
         *
         * @param file        文件
         * @param charsetName 编码格式
         * @return 字符串链表中
         */
        fun readFile2List(file: File?, charsetName: String?): List<String>? {
            return readFile2List(file, 0, 0x7FFFFFFF, charsetName)
        }

        /**
         * 读取文件到字符串链表中
         *
         * @param filePath 文件路径
         * @param st       需要读取的开始行数
         * @param end      需要读取的结束行数
         * @return 字符串链表中
         */
        fun readFile2List(filePath: String, st: Int, end: Int): List<String>? {
            return readFile2List(FileUtils.getFileByPath(filePath), st, end, null)
        }

        /**
         * 读取文件到字符串链表中
         *
         * @param filePath    文件路径
         * @param st          需要读取的开始行数
         * @param end         需要读取的结束行数
         * @param charsetName 编码格式
         * @return 字符串链表中
         */
        fun readFile2List(filePath: String, st: Int, end: Int, charsetName: String): List<String>? {
            return readFile2List(FileUtils.getFileByPath(filePath), st, end, charsetName)
        }

        /**
         * 读取文件到字符串链表中
         *
         * @param file        文件
         * @param st          需要读取的开始行数
         * @param end         需要读取的结束行数
         * @param charsetName 编码格式
         * @return 字符串链表中
         */
        @JvmOverloads
        fun readFile2List(file: File?, st: Int = 0, end: Int = 0x7FFFFFFF, charsetName: String? = null): List<String>? {
            if (!FileUtils.isFileExists(file)) return null
            if (st > end) return null
            var reader: BufferedReader? = null
            try {
                var line: String?=null
                var curLine = 1
                val list = ArrayList<String>()
                if (isSpace(charsetName)) {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file!!)))
                } else {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file!!), charsetName!!))
                }
                while ({line = reader.readLine();line}() != null) {
                    if (curLine > end) break
                    if (st <= curLine && curLine <= end) list.add(line!!)
                    ++curLine
                }
                return list
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(reader)
            }
        }

        /**
         * 读取文件到字符串中
         *
         * @param filePath 文件路径
         * @return 字符串
         */
        fun readFile2String(filePath: String): String? {
            return readFile2String(FileUtils.getFileByPath(filePath), null)
        }

        /**
         * 读取文件到字符串中
         *
         * @param filePath    文件路径
         * @param charsetName 编码格式
         * @return 字符串
         */
        fun readFile2String(filePath: String, charsetName: String): String? {
            return readFile2String(FileUtils.getFileByPath(filePath), charsetName)
        }

        /**
         * 读取文件到字符串中
         *
         * @param file        文件
         * @param charsetName 编码格式
         * @return 字符串
         */
        @JvmOverloads
        fun readFile2String(file: File?, charsetName: String? = null): String? {
            if (!FileUtils.isFileExists(file)) return null
            var reader: BufferedReader? = null
            try {
                val sb = StringBuilder()
                if (isSpace(charsetName)) {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file!!)))
                } else {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file!!), charsetName!!))
                }
                var line: String?=null
                while ({line = reader.readLine();line}() != null) {
                    sb.append(line).append(LINE_SEP)
                }
                // delete the last line separator
                return sb.delete(sb.length - (LINE_SEP?.length?:0), sb.length).toString()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(reader)
            }
        }

        /**
         * 读取文件到字节数组中
         *
         * @param filePath 文件路径
         * @return 字符数组
         */
        fun readFile2BytesByStream(filePath: String): ByteArray? {
            return readFile2BytesByStream(FileUtils.getFileByPath(filePath))
        }

        /**
         * 读取文件到字节数组中
         *
         * @param file 文件
         * @return 字符数组
         */
        fun readFile2BytesByStream(file: File?): ByteArray? {
            if (!FileUtils.isFileExists(file)) return null
            var fis: FileInputStream? = null
            var os: ByteArrayOutputStream? = null
            try {
                fis = FileInputStream(file!!)
                os = ByteArrayOutputStream()
                val b = ByteArray(MemoryConstants.KB)
                var len: Int=-1
                while ({len = fis.read(b, 0, MemoryConstants.KB);len}()!= -1) {
                    os.write(b, 0, len)
                }
                return os.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(fis, os)
            }
        }

        /**
         * 读取文件到字节数组中
         *
         * @param filePath 文件路径
         * @return 字符数组
         */
        fun readFile2BytesByChannel(filePath: String): ByteArray? {
            return readFile2BytesByChannel(FileUtils.getFileByPath(filePath))
        }

        /**
         * 读取文件到字节数组中
         *
         * @param file 文件
         * @return 字符数组
         */
        fun readFile2BytesByChannel(file: File?): ByteArray? {
            if (!FileUtils.isFileExists(file)) return null
            var fc: FileChannel? = null
            try {
                fc = RandomAccessFile(file, "r").channel
                val byteBuffer = ByteBuffer.allocate(fc!!.size().toInt())
                while (true) {
                    if (fc.read(byteBuffer) <= 0) break
                }
                return byteBuffer.array()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(fc)
            }
        }

        /**
         * 读取文件到字节数组中
         *
         * @param filePath 文件路径
         * @return 字符数组
         */
        fun readFile2BytesByMap(filePath: String): ByteArray? {
            return readFile2BytesByMap(FileUtils.getFileByPath(filePath))
        }

        /**
         * 读取文件到字节数组中
         *
         * @param file 文件
         * @return 字符数组
         */
        fun readFile2BytesByMap(file: File?): ByteArray? {
            if (!FileUtils.isFileExists(file)) return null
            var fc: FileChannel? = null
            try {
                fc = RandomAccessFile(file, "r").channel
                val size = fc!!.size().toInt()
                val mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size.toLong()).load()
                val result = ByteArray(size)
                mbb.get(result, 0, size)
                return result
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(fc)
            }
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}
/**
 * 将输入流写入文件
 *
 * @param file 文件
 * @param is   输入流
 * @return `true`: 写入成功<br></br>`false`: 写入失败
 */
/**
 * 将字节数组写入文件
 *
 * @param file  文件
 * @param bytes 字节数组
 * @return `true`: 写入成功<br></br>`false`: 写入失败
 */
/**
 * 将字符串写入文件
 *
 * @param file    文件
 * @param content 写入内容
 * @return `true`: 写入成功<br></br>`false`: 写入失败
 */
/**
 * 读取文件到字符串链表中
 *
 * @param file 文件
 * @return 字符串链表中
 */
/**
 * 读取文件到字符串链表中
 *
 * @param file 文件
 * @param st   需要读取的开始行数
 * @param end  需要读取的结束行数
 * @return 字符串链表中
 */
/**
 * 读取文件到字符串中
 *
 * @param file 文件
 * @return 字符串
 */