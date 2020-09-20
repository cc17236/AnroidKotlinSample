/*
 * Copyright (C) 2015-2017 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawen.baselibrary.views.keyboard.util

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by Jacksgong on 9/1/15.
 *
 *
 * For save the keyboard height.
 */
internal object KeyBoardSharedPreferences {

    private val FILE_NAME = "keyboard.common"

    private val KEY_KEYBOARD_HEIGHT = "sp.key.keyboard.height"

    @Volatile
    private var sp: SharedPreferences? = null

    fun save(context: Context, keyboardHeight: Int): Boolean {
        return with(context)!!.edit()
                .putInt(KEY_KEYBOARD_HEIGHT, keyboardHeight)
                .commit()
    }

    private fun with(context: Context): SharedPreferences? {
        if (sp == null) {
            synchronized(KeyBoardSharedPreferences::class.java) {
                if (sp == null) {
                    sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                }
            }
        }

        return sp
    }

    operator fun get(context: Context, defaultHeight: Int): Int {
        return with(context)!!.getInt(KEY_KEYBOARD_HEIGHT, defaultHeight)
    }

}
