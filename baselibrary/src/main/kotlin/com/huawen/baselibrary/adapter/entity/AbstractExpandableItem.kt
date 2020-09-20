package com.huawen.baselibrary.adapter.entity

import java.util.ArrayList

/**
 *
 * A helper to implement expandable item.
 *
 * if you don't want to extent a class, you can also implement the interface IExpandable
 * Created by luoxw on 2016/8/9.
 */
abstract class AbstractExpandableItem<T> : IExpandable<T> {
    override var isExpanded = false
    protected var mSubItems: MutableList<T>? = null

    override var subItems: MutableList<T>?
        get() = mSubItems
        set(list) {
            mSubItems = list
        }

    fun hasSubItem(): Boolean {
        return mSubItems != null && mSubItems!!.size > 0
    }

    fun getSubItem(position: Int): T? {
        return if (hasSubItem() && position < mSubItems!!.size) {
            mSubItems!![position]
        } else {
            null
        }
    }

    fun getSubItemPosition(subItem: T): Int {
        return if (mSubItems != null) mSubItems!!.indexOf(subItem) else -1
    }

    fun addSubItem(subItem: T) {
        if (mSubItems == null) {
            mSubItems = ArrayList()
        }
        mSubItems!!.add(subItem)
    }

    fun addSubItem(position: Int, subItem: T) {
        if (mSubItems != null && position >= 0 && position < mSubItems!!.size) {
            mSubItems!!.add(position, subItem)
        } else {
            addSubItem(subItem)
        }
    }

    operator fun contains(subItem: T): Boolean {
        return mSubItems != null && mSubItems!!.contains(subItem)
    }

    fun removeSubItem(subItem: T): Boolean {
        return mSubItems != null && mSubItems!!.remove(subItem)
    }

    fun removeSubItem(position: Int): Boolean {
        if (mSubItems != null && position >= 0 && position < mSubItems!!.size) {
            mSubItems!!.removeAt(position)
            return true
        }
        return false
    }

}
