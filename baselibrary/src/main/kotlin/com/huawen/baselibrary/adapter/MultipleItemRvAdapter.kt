package com.huawen.baselibrary.adapter

import android.util.SparseArray
import android.view.View

import com.huawen.baselibrary.adapter.provider.BaseItemProvider
import com.huawen.baselibrary.adapter.util.MultiTypeDelegate
import com.huawen.baselibrary.adapter.util.ProviderDelegate

/**
 * https://github.com/chaychan
 * @author ChayChan
 * @description:
 * When there are multiple entries, avoid too much business logic in convert(),Put the logic of each item in the corresponding ItemProvider
 * 当有多种条目的时候，避免在convert()中做太多的业务逻辑，把逻辑放在对应的ItemProvider中
 * @date 2018/3/21  9:55
 */

abstract class MultipleItemRvAdapter<T, V : BaseViewHolder>(data: MutableList<T>?) : BaseQuickAdapter<T, V>(data) {

    private var mItemProviders: SparseArray<BaseItemProvider<T, V>>? = null
    protected var mProviderDelegate: ProviderDelegate?=null

    /**
     * 用于adapter构造函数完成参数的赋值后调用
     * Called after the assignment of the argument to the adapter constructor
     */
    fun finishInitialize() {
        mProviderDelegate = ProviderDelegate()

        multiTypeDelegate = object : MultiTypeDelegate<T>() {

            override fun getItemType(t: T): Int {
                return getViewType(t)
            }
        }

        registerItemProvider()

        mItemProviders = mProviderDelegate?.itemProviders as SparseArray<BaseItemProvider<T, V>>

        for (i in 0 until mItemProviders!!.size()) {
            val key = mItemProviders!!.keyAt(i)
            val provider = mItemProviders!!.get(key)
            provider.mData = mData as? List<Nothing>
            multiTypeDelegate!!.registerItemType(key, provider.layout())
        }
    }

    protected abstract fun getViewType(t: T): Int

    abstract fun registerItemProvider()

    override fun convert(helper: V, item: T) {
        val itemViewType = helper.itemViewType
        val provider = mItemProviders!!.get(itemViewType)

        provider.mContext = helper.itemView.context

        val position = helper.layoutPosition - headerLayoutCount
        provider.convert(helper, item, position)
        bindClick(helper, item, position, provider as BaseItemProvider<T,V>)
    }

    private fun bindClick(helper: V, item: T, position: Int, provider: BaseItemProvider<T, V>) {
        val clickListener = onItemClickListener_
        val longClickListener = onItemLongClickListener

        if (clickListener != null && longClickListener != null) {
            //如果已经设置了子条目点击监听和子条目长按监听
            // If you have set up a sub-entry click monitor and sub-entries long press listen
            return
        }

        val itemView = helper.itemView

        if (clickListener == null) {
            //如果没有设置点击监听，则回调给itemProvider
            //Callback to itemProvider if no click listener is set
            itemView.setOnClickListener { provider.onClick(helper, item, position) }
        }

        if (longClickListener == null) {
            //如果没有设置长按监听，则回调给itemProvider
            // If you do not set a long press listener, callback to the itemProvider
            itemView.setOnLongClickListener { provider.onLongClick(helper, item, position) }
        }
    }
}
