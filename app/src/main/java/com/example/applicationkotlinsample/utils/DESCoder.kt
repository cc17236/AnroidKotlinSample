package cn.aihuaiedu.school.utils


import android.util.Base64
import java.io.*
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec


class DESCoder(sKey: String, sIv: String) {
    //test_sssss
    // 密钥
    private var sKey = ""
    private var sIv = ""


    init {
        this.sKey = sKey
        this.sIv = sIv
    }

    /**
     * 加密字符串
     */
    fun encrypto(str: String?): String? {
        var result = str
        if (str != null && str.length > 0) {
            try {
                val encodeByte = symmetricEncrypto(str.toByteArray(ENCODING))
                result = Base64.encodeToString(encodeByte, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result
    }

    /**
     * 解密字符串
     */
    fun decrypto(str: String?): String? {
        var result = str
        if (str != null && str.length > 0) {
            try {
                val encodeByte = Base64.decode(str, Base64.DEFAULT)
                val decoder = symmetricDecrypto(encodeByte)
                result = String(decoder, ENCODING)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result
    }


    /**
     * 加密byte[]
     */
    fun encrypto(str: ByteArray?): ByteArray? {
        var result: ByteArray? = null
        if (str != null && str.size > 0) {
            try {
                val encodeByte = symmetricEncrypto(str)
                result = Base64.encodeToString(encodeByte, Base64.DEFAULT).toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result
    }

    /**
     * 解密byte[]
     */
    fun decrypto(str: ByteArray?): ByteArray? {
        var result: ByteArray? = null
        if (str != null && str.size > 0) {
            try {
                val encodeByte = Base64.decode(String(str, ENCODING), Base64.DEFAULT)
                val decoder = symmetricDecrypto(encodeByte)
                result = String(decoder).toByteArray(ENCODING)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result
    }


    /**
     * 对称加密字节数组并返回
     *
     * @param byteSource 需要加密的数据
     * @return 经过加密的数据
     * @throws Exception
     */
    @Throws(Exception::class)
    fun symmetricEncrypto(byteSource: ByteArray): ByteArray {
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(sKey.toByteArray(Charset.forName("utf-8")))
            val digest=md.digest()
            val dsk = DESKeySpec(digest)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(dsk)
            val iv = IvParameterSpec(sIv.toByteArray(Charset.forName("utf-8")))
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            cipher.init(1, key, iv)
            return cipher.doFinal(byteSource)
        } catch (e: Exception) {
            throw e
        } finally {
        }
    }

    /**
     * 对称解密字节数组并返回
     *
     * @param byteSource 需要解密的数据
     * @return 经过解密的数据
     * @throws Exception
     */
    @Throws(Exception::class)
    fun symmetricDecrypto(byteSource: ByteArray): ByteArray {
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(sKey.toByteArray(Charset.forName("utf-8")))
            val dsk = DESKeySpec(md.digest())
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(dsk)
            val iv = IvParameterSpec(sIv.toByteArray(Charset.forName("utf-8")))
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            cipher.init(2, key, iv)

            return cipher.doFinal(byteSource)
        } catch (e: Exception) {
            throw e
        } finally {

        }
    }


    /**
     * 对文件srcFile进行加密输出到文件distFile
     *
     * @param srcFile  明文文件
     * @param distFile 加密后的文件
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encryptFile(srcFile: String, distFile: String) {

        var `is`: InputStream? = null
        var out: OutputStream? = null
        var cis: CipherInputStream? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(sKey.toByteArray(Charset.forName("utf-8")))
            val dsk = DESKeySpec(md.digest())
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(dsk)
            val iv = IvParameterSpec(sIv.toByteArray(Charset.forName("utf-8")))
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            cipher.init(1, key, iv)
            `is` = FileInputStream(srcFile)
            out = FileOutputStream(distFile)
            cis = CipherInputStream(`is`, cipher)
            val buffer = ByteArray(1024)
            var r: Int = -1
            while (({ r = cis.read(buffer);r }()) > 0) {
                out.write(buffer, 0, r)
            }
        } catch (e: Exception) {
            throw e
        } finally {
            cis!!.close()
            `is`!!.close()
            out!!.close()
        }
    }

    /**
     * 解密文件srcFile到目标文件distFile
     *
     * @param srcFile  密文文件
     * @param distFile 解密后的文件
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decryptFile(srcFile: String, distFile: String) {

        var `is`: InputStream? = null
        var out: OutputStream? = null
        var cos: CipherOutputStream? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(sKey.toByteArray(Charset.forName("utf-8")))
            val dsk = DESKeySpec(md.digest())
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(dsk)
            val iv = IvParameterSpec(sIv.toByteArray(Charset.forName("utf-8")))
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            cipher.init(2, key, iv)
            val buffer = ByteArray(1024)
            `is` = FileInputStream(srcFile)
            out = FileOutputStream(distFile)
            cos = CipherOutputStream(out, cipher)

            var r: Int = -1
            while (({ r = `is`.read(buffer);r }()) >= 0) {
                cos.write(buffer, 0, r)
            }

        } catch (e: Exception) {
            throw e
        } finally {
            cos!!.close()
            `is`!!.close()
            out!!.close()
        }
    }


    /**
     * 对文件进行加密64位编码
     *
     * @param srcFile  源文件
     * @param distFile 目标文件
     */
    fun encoderBase64File(srcFile: String, distFile: String) {
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {
            inputStream = FileInputStream(srcFile)

            out = FileOutputStream(distFile)
            val buffer = ByteArray(1024)
            while (inputStream.read(buffer) > 0) {
                out.write(encrypto(buffer))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                out!!.close()
                inputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }


    }

    /**
     * 对文件进行解密64位解码
     *
     * @param srcFile  源文件
     * @param distFile 目标文件
     */
    fun decodeBase64File(srcFile: String, distFile: String) {
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {
            inputStream = FileInputStream(srcFile)

            out = FileOutputStream(distFile)
            val buffer = ByteArray(1412)

            while (inputStream.read(buffer) > 0) {
                out.write(decrypto(buffer))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                out!!.close()
                inputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        // a weak key

        private val ENCODING = Charsets.UTF_8

        /**
         * 散列算法
         *
         * @param byteSource 需要散列计算的数据
         * @return 经过散列计算的数据
         * @throws Exception
         */
        @Throws(Exception::class)
        fun hashMethod(byteSource: ByteArray): ByteArray {
            try {
                val currentAlgorithm = MessageDigest.getInstance("SHA-1")
                currentAlgorithm.reset()
                currentAlgorithm.update(byteSource)
                return currentAlgorithm.digest()
            } catch (e: Exception) {
                throw e
            }

        }
    }

}  