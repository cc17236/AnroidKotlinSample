package com.huawen.baselibrary.utils.utils

import android.content.Context
import android.graphics.Typeface

/**
 * 加载字体
 *
 * @author zhangliangming
 */
class FontUtil(context: Context) {

    init {
        typeFace = Typeface.createFromAsset(context.assets,
                "fonts/iconfont.ttf")
    }

    fun getTypeFace(): Typeface {
        return typeFace!!
    }

    companion object {
        /**
         * 字体
         */
        private var typeFace: Typeface?=null

        private var _FontUtil: FontUtil? = null

        fun getInstance(context: Context): FontUtil {
            if (_FontUtil == null) {
                _FontUtil = FontUtil(context)
            }
            return _FontUtil!!
        }
    }

}
