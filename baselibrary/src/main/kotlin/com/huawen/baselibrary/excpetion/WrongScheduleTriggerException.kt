package com.huawen.baselibrary.excpetion


/**
 * @作者: #Administrator #
 *@日期: #2018/4/29 #
 *@时间: #2018年04月29日 12:12 #
 *@File:Kotlin Class
 */
class WrongScheduleTriggerException : Exception {
    constructor(message: String?) : super(formatMessage(message))

    private companion object {
        private fun formatMessage(string: String?): String {
            var formatString = ""
            formatString = "调度触发器异常:"+(string ?: "null exception")
            return formatString
        }
    }

}