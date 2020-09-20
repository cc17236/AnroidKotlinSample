package com.huawen.baselibrary.adapter.entity

/**
 * implement the interface if the item is expandable
 * Created by luoxw on 2016/8/8.
 */
interface IExpandable<T> {
    var isExpanded: Boolean
    val subItems: MutableList<T>?

    /**
     * Get the level of this item. The level start from 0.
     * If you don't care about the level, just return a negative.
     */
    val level: Int
}
