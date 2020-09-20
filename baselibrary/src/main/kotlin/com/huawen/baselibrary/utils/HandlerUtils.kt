package com.huawen.baselibrary.utils

import android.os.Handler
import android.os.Message

import java.lang.ref.WeakReference

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 16/11/01
 * desc  : Handler相关工具类
</pre> *
 */
class HandlerUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    class HandlerHolder
    /**
     * 使用必读：推荐在Activity或者Activity内部持有类中实现该接口，不要使用匿名类，可能会被GC
     *
     * @param listener 收到消息回调接口
     */
    (listener: Handler.Callback) : Handler() {
        internal var mListenerWeakReference: WeakReference<Handler.Callback>? = null

        init {
            mListenerWeakReference = WeakReference(listener)
        }

        override fun handleMessage(msg: Message) {
            if (mListenerWeakReference != null && mListenerWeakReference!!.get() != null) {
                mListenerWeakReference!!.get()?.handleMessage(msg)
            }
        }
    }
}
