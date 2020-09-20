package com.huawen.baselibrary.adapter.provider

import android.content.Context

import com.huawen.baselibrary.adapter.BaseViewHolder

/**
 * https://github.com/chaychan
 * @author ChayChan
 * @description: The base class of ItemProvider
 * @date 2018/3/21  10:41
 */

abstract class BaseItemProvider<T, V : BaseViewHolder> {

    var mContext: Context? = null
    var mData: List<T>? = null

    //子类须重写该方法返回viewType
    //Rewrite this method to return viewType
    abstract fun viewType(): Int

    //子类须重写该方法返回layout
    //Rewrite this method to return layout
    abstract fun layout(): Int

    abstract fun convert(helper: V, data: T, position: Int)

    //子类若想实现条目点击事件则重写该方法
    //Subclasses override this method if you want to implement an item click event
    fun onClick(helper: V, data: T, position: Int) {}

    //子类若想实现条目长按事件则重写该方法
    //Subclasses override this method if you want to implement an item long press event
    fun onLongClick(helper: V, data: T, position: Int): Boolean {
        return false
    }
}
