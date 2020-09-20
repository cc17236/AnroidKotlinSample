package com.huawen.baselibrary.adapter.entity

import androidx.room.Ignore

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
interface MultiItemEntity {
    @Ignore
    fun itemType(): Int = 0
    @Ignore
    fun shouldIgnoreStateCheck() = false

    fun fieldExclusion(fieldName:String):Boolean{
        return false
    }
}
