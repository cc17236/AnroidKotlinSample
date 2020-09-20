package cn.aihuaiedu.school.base.gson

import cn.aihuaiedu.school.base.ObserverImp
import com.google.gson.annotations.SerializedName

//HttpStatus.java
class HttpStatus {
    @SerializedName("code")
    val code: Int = 0
    @SerializedName("msg")
    val msg: String? = null

    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    val isCodeInvalid: Boolean
        get() = code != ObserverImp.CODE_SUCCESS
}
