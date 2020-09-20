package com.huawen.baselibrary.utils.utils

import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * 动画工具类 最近修改时间2013年12月10日
 */
object AniUtil {

    private val START = 0

    private val STOP = 1

    private val aniHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.obj !is AnimationDrawable) {
                return
            }
            val ani = msg.obj as AnimationDrawable
            if (msg.what == START) {
                ani.start()
            } else {
                ani.stop()
            }
        }
    }

    /**
     * 开始动画
     *
     * @param ani
     */
    fun startAnimation(ani: AnimationDrawable) {
        postAnimationMessage(ani, START)
    }

    /**
     * 停止动画
     *
     * @param ani
     */
    fun stopAnimation(ani: AnimationDrawable) {
        postAnimationMessage(ani, STOP)
    }

    /**
     * 发送动画消息
     *
     * @param what
     * @param ani
     */
    private fun postAnimationMessage(ani: AnimationDrawable,
                                     what: Int) {
        aniHandler.postDelayed({
            val msg = Message.obtain()
            msg.what = what
            msg.obj = ani
            aniHandler.sendMessage(msg)
        }, 5)
    }
}
