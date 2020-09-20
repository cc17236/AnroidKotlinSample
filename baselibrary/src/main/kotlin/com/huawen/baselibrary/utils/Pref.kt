/*
 * Copyright (C) 2017 Ricky.yao https://github.com/vihuela
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package com.huawen.baselibrary.utils

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaType

//apply for Paper，use delegate properties
class Pref<T>(val key: String?, val default: T) {

    operator fun getValue(any: Any, property: KProperty<*>): T {
        val javaType = property.returnType.javaType
        val sp = SharedPreferencesUtil.instance ?: return default
        var id: Any? = null
        when (javaType) {
            is ParameterizedType -> {
                println("parameterized type: $javaType")
                when ((any::class.qualifiedName ?: "")
                    .replace("java.lang.", "")
                    .replace("kotlin.", "").toLowerCase()
                    ) {
                    "int", "integer" -> {
                        id = sp.getInt(key ?: property.name, default as Int)
                    }
                    "long" -> {
                        id = sp.getLong(key ?: property.name, default as Long)
                    }
                    "float" -> {
                        id = sp.getFloat(key ?: property.name, default as Float)
                    }
                    "char" -> {
                        id = sp.getInt(key ?: property.name, (default as Char).toInt()).toChar()
                    }
                    "short" -> {
                        id = sp.getInt(key ?: property.name, (default as Short).toInt()).toShort()
                    }
                    "boolean" -> {
                        id = sp.getBoolean(key ?: property.name, default as Boolean)
                    }
                    "double" -> {
                        id = sp.getFloat(key ?: property.name, (default as Double).toFloat()).toDouble()
                    }
                }
            }
            is Class<*> -> {
                when ((any::class.qualifiedName ?: "")
                    .replace("java.lang.", "")
                    .replace("kotlin.Collections.", "")
                    .replace("kotlin.", "")
                    .replace("java.util.", "")
                    ) {
                    "String" -> {
                        id= sp.getString(key ?: property.name, default as String)
                    }
                    "Set" -> {
                        id= sp.getStringSet(key ?: property.name, default as Set<String>)
                    }
                    else -> {
                        id= sp.getObject(key ?: property.name, any::class.java)?:default
                    }
                }
            }
            else -> {
                throw RuntimeException("这是不受支持的类型,只允许基本类型和对象,受不了,告辞")
            }
        }
        if (id==null)return default
        return id as T
    }

    operator fun setValue(any: Any, property: KProperty<*>, value: T) {
        val javaType = property.returnType.javaType
        val sp = SharedPreferencesUtil.instance ?: return
        when (javaType) {
            is ParameterizedType -> {
                println("parameterized type: $javaType")
                when ((any::class.qualifiedName ?: "")
                    .replace("java.lang.", "")
                    .replace("kotlin.", "").toLowerCase()
                    ) {
                    "int", "integer" -> {
                        sp.putInt(key ?: property.name, value as Int)
                    }
                    "long" -> {
                        sp.putLong(key ?: property.name, value as Long)
                    }
                    "float" -> {
                        sp.putFloat(key ?: property.name, value as Float)
                    }
                    "char" -> {
                        sp.putInt(key ?: property.name, (value as Char).toInt())
                    }
                    "short" -> {
                        sp.putInt(key ?: property.name, (value as Short).toInt())
                    }
                    "boolean" -> {
                        sp.putBoolean(key ?: property.name, value as Boolean)
                    }
                    "double" -> {
                        sp.putFloat(key ?: property.name, (value as Double).toFloat())
                    }
                }
            }
            is Class<*> -> {
                when ((any::class.qualifiedName ?: "")
                    .replace("java.lang.", "")
                    .replace("kotlin.Collections.", "")
                    .replace("kotlin.", "")
                    .replace("java.util.", "")
                    ) {
                    "String" -> {
                        sp.putString(key ?: property.name, value as String)
                    }
                    "Set" -> {
                        sp.putStringSet(key ?: property.name, value as Set<String>)
                    }
                    else -> {
                        sp.putObject(key ?: property.name, value as Any)
                    }
                }
            }
            else -> {
                throw RuntimeException("这是不受支持的类型,只允许基本类型和对象,受不了,告辞")
            }
        }
    }
}

//使用基本类型之外的model，记得继承IUoocNoProguard！！
inline fun <reified AnyClz, T> AnyClz.pref(default: T, key: String? = null) = Pref(key, default)