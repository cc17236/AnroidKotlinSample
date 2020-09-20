package com.huawen.baselibrary.utils.utils

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * <pre>
 * author : Senh Linsh
 * github : https://github.com/SenhLinsh
 * date   : 2017/11/13
 * desc   : 工具类: 字符串相关
 * API  : 判空, 编码 等
</pre> *
 */
object StringUtils {

    /**
     * 判断字符串是否为空
     *
     * @param string 指定字符串
     * @return null 或 空字符串返回 true, 否则返回 false
     */
    fun isEmpty(string: CharSequence?): Boolean {
        return string == null || string.length == 0
    }

    /**
     * 判断字符串是否不为空
     *
     * @param string 指定字符串
     * @return 不为 null 且 长度大于 0 返回 true, 否则返回 false
     */
    fun notEmpty(string: CharSequence?): Boolean {
        return string != null && string.length > 0
    }

    /**
     * 判断所有的字符串是否都为空
     *
     * @param strings 指定字符串数组获或可变参数
     * @return 所有字符串都为空返回 true, 否则放回 false
     */
    fun isAllEmpty(vararg strings: CharSequence): Boolean {
        if (strings == null) return true
        for (charSequence in strings) {
            if (!isEmpty(charSequence)) return false
        }
        return true
    }

    /**
     * 判断所有的字符串是否都不为空
     *
     * @param strings 指定字符串数组获或可变参数
     * @return 所有字符串都不为空返回 true, 否则放回 false
     */
    fun isAllNotEmpty(vararg strings: CharSequence): Boolean {
        if (strings == null) return false
        for (charSequence in strings) {
            if (isEmpty(charSequence)) return false
        }
        return true
    }

    /**
     * 判断所有的字符串是否不都为空
     *
     * @param strings 指定字符串数组获或可变参数
     * @return 所有字符串不都为空返回 true, 否则返回 false
     */
    fun isNotAllEmpty(vararg strings: CharSequence): Boolean {
        return !isAllEmpty(*strings)
    }

    /**
     * 判断字符串是否为空或空格
     *
     * @param string 指定字符串
     * @return null 或空字符串或空格字符串返回true, 否则返回 false
     */
    fun isTrimEmpty(string: String?): Boolean {
        return string == null || string.trim { it <= ' ' }.length == 0
    }

    /**
     * 判断字符串是否为空或空白
     *
     * @param string 指定字符串
     * @return null 或空白字符串返回true, 否则返回 false
     */
    fun isBlank(string: String?): Boolean {
        if (string == null) return true
        var i = 0
        val len = string.length
        while (i < len) {
            if (!Character.isWhitespace(string[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * 判断两个字符串是否相同
     *
     * @param a 作为对比的字符串
     * @param b 作为对比的字符串
     * @return 是否相同
     */
    fun isEquals(a: String?, b: String): Boolean {
        return a === b || a != null && a == b
    }

    /**
     * 判断两个字符串是否不同
     *
     * @param a 作为对比的字符串
     * @param b 作为对比的字符串
     * @return 是否不同
     */
    fun notEquals(a: String, b: String): Boolean {
        return !isEquals(a, b)
    }

    /**
     * null 转 空字符串
     *
     * @param obj 对象
     * @return 将 null 对象返回空字符串(""), 其他对象调用 toString() 返回的字符串
     */
    fun nullStrToEmpty(obj: Any?): String {
        return if (obj == null) "" else obj as? String ?: obj.toString()
    }

    /**
     * 将字符串进行 UTF-8 编码
     *
     * @param string 指定字符串
     * @return 编码后的字符串
     */
    fun utf8Encode(string: String): String? {
        if (!isEmpty(string) && string.toByteArray().size != string.length) {
            try {
                return URLEncoder.encode(string, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException("UnsupportedEncodingException occurred. ", e)
            }

        }
        return string
    }

    /**
     * 将字符串进行 UTF-8 编码
     *
     * @param string        指定字符串
     * @param defaultReturn 编码失败返回的字符串
     * @return 编码后的字符串
     */
    fun utf8Encode(string: String, defaultReturn: String): String? {
        if (!isEmpty(string) && string.toByteArray().size != string.length) {
            try {
                return URLEncoder.encode(string, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                return defaultReturn
            }

        }
        return string
    }

    /**
     * 判断字符串中是否存在中文汉字
     *
     * @param string 指定字符串
     * @return 是否存在
     */
    fun hasChineseChar(string: String): Boolean {
        var temp = false
        val p = Pattern.compile("[\u4e00-\u9fa5]")
        val m = p.matcher(string)
        if (m.find()) {
            temp = true
        }
        return temp
    }


    /**
     * 格式化字符串, 用参数进行替换, 例子: format("I am {arg1}, {arg2}", arg1, arg2);
     *
     * @param format 需要格式化的字符串
     * @param args   格式化参数
     * @return 格式化后的字符串
     */
    fun format(format: String, vararg args: Any): String {
        var format = format
        for (arg in args) {
            format = format.replaceFirst("\\{[^\\}]+\\}".toRegex(), arg.toString())
        }
        return format
    }
}
