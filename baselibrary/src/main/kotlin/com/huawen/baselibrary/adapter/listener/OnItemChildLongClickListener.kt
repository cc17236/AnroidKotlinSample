package com.huawen.baselibrary.adapter.listener

import android.view.View

import com.huawen.baselibrary.adapter.BaseQuickAdapter

/**
 * Created by AllenCoder on 2016/8/03.
 * A convenience class to extend when you only want to OnItemChildLongClickListener for a subset
 * of all the SimpleClickListener. This implements all methods in the
 * [SimpleClickListener]
 */
abstract class OnItemChildLongClickListener : SimpleClickListener() {
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {

    }

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {

    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {

    }

    override fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        onSimpleItemChildLongClick(adapter, view, position)
    }

    abstract fun onSimpleItemChildLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int)
}
