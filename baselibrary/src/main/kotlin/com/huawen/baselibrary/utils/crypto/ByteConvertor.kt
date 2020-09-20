package com.huawen.baselibrary.utils.crypto

import kotlin.experimental.and


/**
 * @作者: #Administrator #
 *@日期: #2018/11/5 #
 *@时间: #2018年11月05日 14:20 #
 *@File:Kotlin Class
 */
object ByteConvertor {
        @JvmStatic
        fun main(args: Array<String>) {
            val key_s = "09348179d466baa4ab2980ef998ef89efa"
            val key_b = hexStringToByteArray(key_s)
            val key_s_round = byteArrayToHexString(key_b)

            if (key_s == key_s_round)
                println("Round the loop conversion works!")
            else
                println("Sorry! still more to make it work")
        }

        //Converting a string of hex character to bytes
        fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }

        //Converting a bytes array to string of hex character
        fun byteArrayToHexString(b: ByteArray): String {
            val len = b.size
            var data = String()

            for (i in 0 until len) {
                data += Integer.toHexString((b[i]).toInt() shr 4 and 0xf)
                data += Integer.toHexString(b[i].toInt() and (0xf).toInt())
            }
            return data
        }

}