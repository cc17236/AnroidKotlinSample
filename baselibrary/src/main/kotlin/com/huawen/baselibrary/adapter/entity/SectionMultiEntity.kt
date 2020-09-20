package com.huawen.baselibrary.adapter.entity

import java.io.Serializable

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
abstract class SectionMultiEntity<T> : Serializable, MultiItemEntity {

    var isHeader: Boolean = false
    var t: T? = null
    var header: String? = null

    constructor(isHeader: Boolean, header: String) {
        this.isHeader = isHeader
        this.header = header
        this.t = null
    }

    constructor(t: T) {
        this.isHeader = false
        this.header = null
        this.t = t
    }
}
