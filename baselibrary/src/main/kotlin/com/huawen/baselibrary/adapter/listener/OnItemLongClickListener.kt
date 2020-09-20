package com.huawen.baselibrary.adapter.listener

import android.view.View

import com.huawen.baselibrary.adapter.BaseQuickAdapter

/**
 * create by: allen on 16/8/3.
 */

abstract class OnItemLongClickListener : SimpleClickListener() {
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {

    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        onSimpleItemLongClick(adapter, view, position)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {

    }

    override fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {}

    abstract fun onSimpleItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int)
}
