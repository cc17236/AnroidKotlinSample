package com.huawen.baselibrary.utils

import java.util.regex.Pattern
import kotlin.experimental.and

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/16
 * desc  : 字符串相关工具类
</pre> *
 */
class StringUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 判断字符串是否为null或长度为0
         *
         * @param s 待校验字符串
         * @return `true`: 空<br></br> `false`: 不为空
         */
        fun isEmpty(s: CharSequence?): Boolean {
            return s == null || s.length == 0 || s.equals("null")
        }

        /**
         * 判断字符串是否为null或全为空格
         *
         * @param s 待校验字符串
         * @return `true`: null或全空格<br></br> `false`: 不为null且不全空格
         */
        fun isTrimEmpty(s: String?): Boolean {
            return s == null || s.trim { it <= ' ' }.length == 0
        }

        /**
         * 判断字符串是否为null或全为空白字符
         *
         * @param s 待校验字符串
         * @return `true`: null或全空白字符<br></br> `false`: 不为null且不全空白字符
         */
        fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        /**
         * 判断两字符串是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        fun equals(a: CharSequence?, b: CharSequence?): Boolean {
            if (a === b) return true
            var length: Int=0
            if (a != null && b != null && ({length = a.length;length}()) == b.length) {
                if (a is String && b is String) {
                    return a == b
                } else {
                    for (i in 0 until length) {
                        if (a[i] != b[i]) return false
                    }
                    return true
                }
            }
            return false
        }

        /**
         * 判断两字符串忽略大小写是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        fun equalsIgnoreCase(a: String?, b: String?): Boolean {
            return a?.equals(b!!, ignoreCase = true) ?: (b == null)
        }

        /**
         * null转为长度为0的字符串
         *
         * @param s 待转字符串
         * @return s为null转为长度为0字符串，否则不改变
         */
        fun null2Length0(s: String?): String {
            return s ?: ""
        }

        /**
         * 返回字符串长度
         *
         * @param s 字符串
         * @return null返回0，其他返回自身长度
         */
        fun length(s: CharSequence?): Int {
            return s?.length ?: 0
        }

        /**
         * 首字母大写
         *
         * @param s 待转字符串
         * @return 首字母大写字符串
         */
        fun upperFirstLetter(s: String): String? {
            return if (isEmpty(s) || !Character.isLowerCase(s[0])) s else (s[0].toInt() - 32).toChar().toString() + s.substring(1)
        }

        /**
         * 首字母小写
         *
         * @param s 待转字符串
         * @return 首字母小写字符串
         */
        fun lowerFirstLetter(s: String): String? {
            return if (isEmpty(s) || !Character.isUpperCase(s[0])) s else (s[0].toInt() + 32).toChar().toString() + s.substring(1)
        }

        /**
         * 反转字符串
         *
         * @param s 待反转字符串
         * @return 反转字符串
         */
        fun reverse(s: String): String {
            val len = length(s)
            if (len <= 1) return s
            val mid = len shr 1
            val chars = s.toCharArray()
            var c: Char
            for (i in 0 until mid) {
                c = chars[i]
                chars[i] = chars[len - i - 1]
                chars[len - i - 1] = c
            }
            return String(chars)
        }

        /**
         * 转化为半角字符
         *
         * @param s 待转字符串
         * @return 半角字符串
         */
        fun toDBC(s: String): String? {
            if (isEmpty(s)) return s
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i].toInt() == 12288) {
                    chars[i] = ' '
                } else if (65281 <= chars[i].toInt() && chars[i].toInt() <= 65374) {
                    chars[i] = (chars[i].toInt() - 65248).toChar()
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 转化为全角字符
         *
         * @param s 待转字符串
         * @return 全角字符串
         */
        fun toSBC(s: String): String? {
            if (isEmpty(s)) return s
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i] == ' ') {
                    chars[i] = 12288.toChar()
                } else if (33 <= chars[i].toInt() && chars[i].toInt() <= 126) {
                    chars[i] = (chars[i].toInt() + 65248).toChar()
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }
        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        /**
         * byteArr转hexString
         *
         * 例如：
         * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
         *
         * @param bytes 字节数组
         * @return 16进制大写字符串
         */
        public fun bytes2HexString(bytes: ByteArray?): String? {
            if (bytes == null) return null
            val len = bytes.size
            if (len <= 0) return null
            val ret = CharArray(len shl 1)
            var i = 0
            var j = 0
            while (i < len) {
                val b= bytes[i]
                ret[j++] =hexDigits[b.toInt().ushr(4) and 0x0f]
                ret[j++] =hexDigits[(b and  0x0f).toInt()]
                i++
            }
            return String(ret)
        }
        /**
         * 去掉文本中的html标签
         *
         * @param inputString
         * @return
         */
        fun html2Text(inputString: String): String? {
            if (inputString.isNullOrEmpty()) {
                return null
            }
            var htmlStr = inputString
            var textStr = ""
            val p_script: java.util.regex.Pattern
            val m_script: java.util.regex.Matcher
            val p_style: java.util.regex.Pattern
            val m_style: java.util.regex.Matcher
            val p_html: java.util.regex.Pattern
            val m_html: java.util.regex.Matcher

            val p_html1: java.util.regex.Pattern
            val m_html1: java.util.regex.Matcher

            try {
                val regEx_script =
                    "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>" // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
                // }
                val regEx_style =
                    "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>" // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
                // }
                val regEx_html = "<[^>]+>" // 定义HTML标签的正则表达式
                val regEx_html1 = "<[^>]+"
                p_script = Pattern.compile(
                    regEx_script,
                    Pattern.CASE_INSENSITIVE
                )
                m_script = p_script.matcher(htmlStr)
                htmlStr = m_script.replaceAll("") // 过滤script标签

                p_style = Pattern
                    .compile(regEx_style, Pattern.CASE_INSENSITIVE)
                m_style = p_style.matcher(htmlStr)
                htmlStr = m_style.replaceAll("") // 过滤style标签

                p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE)
                m_html = p_html.matcher(htmlStr)
                htmlStr = m_html.replaceAll("") // 过滤html标签

                p_html1 = Pattern
                    .compile(regEx_html1, Pattern.CASE_INSENSITIVE)
                m_html1 = p_html1.matcher(htmlStr)
                htmlStr = m_html1.replaceAll("") // 过滤html标签

                textStr = htmlStr

                // 替换&amp;nbsp;
                textStr = textStr.replace("&amp;".toRegex(), "").replace("nbsp;".toRegex(), "")

            } catch (e: Exception) {
                System.err.println("Html2Text: " + e.message)
            }

            return textStr// 返回文本字符串
        }
    }


}