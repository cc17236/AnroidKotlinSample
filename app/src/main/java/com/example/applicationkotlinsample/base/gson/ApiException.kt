package cn.aihuaiedu.school.base.gson

import cn.aihuaiedu.school.base.ObserverImp

//ApiException.java
class ApiException(private val mErrorCode: Int, errorMessage: String, private val resp: String) : RuntimeException(errorMessage) {

    /**
     * 判断是否是token失效
     *
     * @return 失效返回true, 否则返回false;
     */
    val isTokenExpried: Boolean
        get() = mErrorCode == ObserverImp.TOKEN_EXPRIED

    internal fun code(): Int {
        return mErrorCode
    }

    internal fun resp(): String {
        return resp
    }
}
