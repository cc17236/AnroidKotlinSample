package com.huawen.baselibrary.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : SP相关工具类
</pre> *
 */
class SPUtils
/**
 * SPUtils构造函数
 *
 * 在Application中初始化
 *
 * @param spName spName
 */
(spName: String) {

    private val sp: SharedPreferences
    private val editor: SharedPreferences.Editor

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    val all: Map<String, *>
        get() = sp.all

    init {
        sp = Utils.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE)
        editor = sp.edit()
        editor.apply()
    }

    /**
     * SP中写入String
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: String?) {
        editor.putString(key, value).apply()
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String? = null): String? {
        return sp.getString(key, defaultValue)
    }

    /**
     * SP中写入int
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = -1): Int {
        return sp.getInt(key, defaultValue)
    }

    /**
     * SP中写入long
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return sp.getLong(key, defaultValue)
    }

    /**
     * SP中写入float
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmOverloads
    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return sp.getFloat(key, defaultValue)
    }

    /**
     * SP中写入boolean
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    /**
     * SP中写入String集合
     *
     * @param key    键
     * @param values 值
     */
    fun put(key: String, values: Set<String>?) {
        editor.putStringSet(key, values).apply()
    }

    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmOverloads
    fun getStringSet(key: String, defaultValue: Set<String>? = null): Set<String>? {
        return sp.getStringSet(key, defaultValue)
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    fun remove(key: String) {
        editor.remove(key).apply()
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    operator fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * SP中清除所有数据
     */
    fun clear() {
        editor.clear().apply()
    }
}
/**
 * SP中读取String
 *
 * @param key 键
 * @return 存在返回对应值，不存在返回默认值`null`
 */
/**
 * SP中读取int
 *
 * @param key 键
 * @return 存在返回对应值，不存在返回默认值-1
 */
/**
 * SP中读取long
 *
 * @param key 键
 * @return 存在返回对应值，不存在返回默认值-1
 */
/**
 * SP中读取float
 *
 * @param key 键
 * @return 存在返回对应值，不存在返回默认值-1
 */
/**
 * SP中读取boolean
 *
 * @param key 键
 * @return 存在返回对应值，不存在返回默认值`false`
 */
/**
 * SP中读取StringSet
 *
 * @param key 键
 * @return 存在返回对应值，不存在返回默认值`null`
 */