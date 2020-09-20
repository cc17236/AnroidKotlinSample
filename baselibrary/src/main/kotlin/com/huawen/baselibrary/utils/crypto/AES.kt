package com.huawen.baselibrary.utils.crypto

import java.lang.Byte
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * @作者: #Administrator #
 *@日期: #2018/11/5 #
 *@时间: #2018年11月05日 14:18 #
 *@File:Kotlin Class
 */
class AES {
    fun abcd(hexKey:String,hexIV:String){
//        val aKey = ByteConvertor.hexStringToByteArray(hexKey)
//        val aIV = ByteConvertor.hexStringToByteArray(hexIV)
//        val key = SecretKeySpec(aKey, "AES")
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(16 * Byte.SIZE, aIV))
//        cipher.updateAAD(aad)
//        val encrypted = cipher.doFinal(aKey)
    }
}