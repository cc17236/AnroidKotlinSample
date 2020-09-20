package com.huawen.baselibrary.utils

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.SoundPool
import android.net.Uri
import androidx.annotation.IntDef
import androidx.annotation.RawRes

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.HashMap
import android.content.Context.VIBRATOR_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.os.Vibrator
import android.app.Activity
import android.app.Application
import android.app.Service
import android.media.AudioAttributes
import android.os.VibrationEffect
import com.huawen.baselibrary.schedule.host.BaseApplication


/**
 * <pre>
 * author: Chestnut
 * blog  : http://www.jianshu.com/u/a0206b5f4526
 * time  : 2017/6/22 17:24
 * desc  :  封装了SoundPool
 * thanks To:   http://flycatdeng.iteye.com/blog/2120043
 * http://www.2cto.com/kf/201408/325318.html
 * https://developer.android.com/reference/android/media/SoundPool.html
 * dependent on:
 * update log:
 * 1.  2017年6月28日10:39:46
 * 1）修复了当play指定的RingtoneName为空的时候，触发的一个bug
 * 2）增加了一个默认的铃声，当找不到系统的默认铃声时候，会默认加载一个我们提供的一个默认铃声
</pre> *
 */
class SoundPoolHelper @JvmOverloads constructor(private var maxStream: Int, @TYPE streamType: Int = TYPE_ALARM) {

    /*变量*/
    private val soundPool: SoundPool?
    private var NOW_RINGTONE_TYPE = RingtoneManager.TYPE_NOTIFICATION
    private val ringtoneIds: MutableMap<String, Int>

    @IntDef(TYPE_MUSIC, TYPE_ALARM, TYPE_RING)
    @Retention(RetentionPolicy.SOURCE)
    annotation class TYPE

    @IntDef(RING_TYPE_MUSIC, RING_TYPE_ALARM, RING_TYPE_RING)
    @Retention(RetentionPolicy.SOURCE)
    annotation class RING_TYPE


    init {
        soundPool = SoundPool(maxStream, streamType, 1)
        ringtoneIds = HashMap()
    }

    /**
     * 设置RingtoneType，这只是关系到加载哪一个默认音频
     * 需要在load之前调用
     *
     * @param ringtoneType ringtoneType
     * @return this
     */
    fun setRingtoneType(@RING_TYPE ringtoneType: Int): SoundPoolHelper {
        NOW_RINGTONE_TYPE = ringtoneType
        return this
    }

    /**
     * 加载音频资源
     *
     * @param context 上下文
     * @param resId   资源ID
     * @return this
     */
    fun load(context: Context, ringtoneName: String, @RawRes resId: Int): SoundPoolHelper {
        if (maxStream == 0)
            return this
        maxStream--
        ringtoneIds[ringtoneName] = soundPool!!.load(context, resId, 1)
        return this
    }

    /**
     * 加载默认的铃声
     * @param context 上下文
     * @return this
     */
    //    public SoundPoolHelper loadDefault(Context context) {
    //        Uri uri = getSystemDefaultRingtoneUri(context);
    //        if (uri==null)
    //            load(context,"default", R.raw.reminder);
    //        else
    //            load(context,"default",ConvertUtils.uri2Path(context,uri));
    //        return this;
    //    }

    /**
     * 加载铃声
     *
     * @param context      上下文
     * @param ringtoneName 自定义铃声名称
     * @param ringtonePath 铃声路径
     * @return this
     */
    fun load(context: Context, ringtoneName: String, ringtonePath: String): SoundPoolHelper {
        if (maxStream == 0)
            return this
        maxStream--
        ringtoneIds[ringtoneName] = soundPool!!.load(ringtonePath, 1)
        return this
    }

    /**
     * int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) ：
     * 1)该方法的第一个参数指定播放哪个声音；
     * 2) leftVolume 、
     * 3) rightVolume 指定左、右的音量：
     * 4) priority 指定播放声音的优先级，数值越大，优先级越高；
     * 5) loop 指定是否循环， 0 为不循环， -1 为循环；
     * 6) rate 指定播放的比率，数值可从 0.5 到 2 ， 1 为正常比率。
     */

    fun play(ringtoneName: String, isLoop: Boolean) {
        if (ringtoneIds.containsKey(ringtoneName)) {
            soundPool!!.play(ringtoneIds[ringtoneName]!!, 1f, 1f, 1, if (isLoop) -1 else 0, 1f)
            vibrate(50)
        }
    }

    //震动milliseconds毫秒
    fun vibrate(milliseconds: Long) {
        val vib = BaseApplication.getApp<BaseApplication>().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

        vib.vibrate(longArrayOf(150), -1)

//        val attrBuilder =  AudioAttributes.Builder()
//        //设置音频流的合适属性
//        attrBuilder.setLegacyStreamType(AudioManager.STREAM_ALARM)
////        VibrationEffect.createOneShot(milliseconds,VibrationEffect.DEFAULT_AMPLITUDE)
//        vib.vibrate(milliseconds, attrBuilder.build())
    }

    //以pattern[]方式震动
    fun vibrate(pattern: LongArray, repeat: Int) {
        val vib = BaseApplication.getApp<BaseApplication>().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(pattern, repeat)
    }

    //取消震动
    fun virateCancle() {
        val vib = BaseApplication.getApp<BaseApplication>().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.cancel()
    }


    fun playDefault() {
        play("default", false)
    }

    /**
     * 释放资源
     */
    fun release() {
        soundPool?.release()
    }

    /**
     * 获取系统默认铃声的Uri
     *
     * @param context 上下文
     * @return uri
     */
    private fun getSystemDefaultRingtoneUri(context: Context): Uri? {
        try {
            return RingtoneManager.getActualDefaultRingtoneUri(context, NOW_RINGTONE_TYPE)
        } catch (e: Exception) {
            return null
        }

    }

    companion object {

        /*常量*/
        const val TYPE_MUSIC = AudioManager.STREAM_MUSIC
        const val TYPE_ALARM = AudioManager.STREAM_ALARM
        const val TYPE_RING = AudioManager.STREAM_RING

        const val RING_TYPE_MUSIC = RingtoneManager.TYPE_ALARM
        const val RING_TYPE_ALARM = RingtoneManager.TYPE_NOTIFICATION
        const val RING_TYPE_RING = RingtoneManager.TYPE_RINGTONE
    }
}