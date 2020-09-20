package com.huawen.baselibrary.excpetion

import com.ReflectUtil
import android.media.MediaPlayer

/**
 * @作者: #Administrator #
 *@日期: #2018/11/12 #
 *@时间: #2018年11月12日 16:09 #
 *@File:Kotlin File
 */
fun MediaPlayer.isReset(): Boolean {
    try {
        val mDrmObj = ReflectUtil.getFiled(MediaPlayer::class.java, "mDrmObj", this)
        if (mDrmObj != null) {
            return false
        }
    } catch (e: Exception) {
    }
    return true
}