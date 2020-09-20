package com.huawen.baselibrary.utils

import android.text.Editable
import android.text.TextWatcher


/**
 * @作者: #Administrator #
 *@日期: #2018/6/6 #
 *@时间: #2018年06月06日 16:54 #
 *@File:Kotlin Class
 */
abstract class SimpleWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null) {
            onFixTextChanged("", start, before, count)
        } else {
            onFixTextChanged(s, start, before, count)
        }
    }

    abstract fun onFixTextChanged(s: CharSequence, start: Int, before: Int, count: Int)


}