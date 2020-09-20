package com.huawen.baselibrary.utils.model

import java.io.Serializable

class StorageInfo(
        /**
         * 路径
         */
        var path: String) : Serializable {
    /**
     * 挂载状态
     */
    var state: String? = null
    /**
     * 是否移除
     */
    var isRemoveable: Boolean = false

    val isMounted: Boolean
        get() = "mounted" == state

    override fun toString(): String {
        return "StorageInfo{" +
                "path='" + path + '\''.toString() +
                ", state='" + state + '\''.toString() +
                ", isRemoveable=" + isRemoveable +
                '}'.toString()
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
    }
}