package cn.aihuaiedu.school.utils

class DateUtil {
    companion object {
        /**
        * ms :为毫秒值
        * @author 2jingo
        * @time 2019/12/11 16:44
        */
        fun parseTime(ms: Long): String {
            val hour = ms / 1000 / 60 / 60
            val min = ms / 1000 / 60 % 60
            return "${hour}时${min}分"
        }

        /**
        * i：单位秒
        * @author 2jingo
        * @time 2019/12/11 16:44
        */
        fun formatLongToTimeStr(i: Int): String {
            val min = i / 60 % 60
            val second = i % 60

            return "${min}分${second}秒"
        }


    }

}