package cn.aihuaiedu.school.utils

import com.huawen.baselibrary.utils.ToastUtils
import java.util.regex.Pattern

class StringUtil {
    companion object {
        fun verify(s: String, text: String): Boolean {
            if (s.isNullOrEmpty()) {
                ToastUtils.showShort(text)
                return false
            } else {
                return true
            }
        }

        /*
     * 写一个功能实现校验
     *两个明确:
     * 明确返回值类型:boolean
     * 明确参数列表:String qq
     */
        fun checkQQ(qq: String): Boolean {
            var flag = true
            //校验长度
            if (qq.length >= 5 && qq.length <= 14) {
                //0不开头
                if (!qq.startsWith("0")) {
                    //必须是数字
                    val chs = qq.toCharArray()
                    for (i in chs.indices) {
                        val c = chs[i]
                        if (!Character.isDigit(c)) {
                            flag = false
                            break
                        }

                    }
                } else {
                    flag = false
                }
            } else {
                flag = false
            }
            return flag
        }


        fun isPhone(phone: String): Boolean {
            val regex =
                "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
            if (phone.length != 11) {

                return false
            } else {
                val p = Pattern.compile(regex)
                val m = p.matcher(phone)
                val isMatch = m.matches()

                return isMatch
            }

        }
    }


}

