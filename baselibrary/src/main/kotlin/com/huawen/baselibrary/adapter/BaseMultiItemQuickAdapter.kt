package com.huawen.baselibrary.adapter

import android.util.SparseIntArray
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import com.huawen.baselibrary.adapter.entity.IExpandable
import com.huawen.baselibrary.adapter.entity.MultiItemEntity

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
abstract class BaseMultiItemQuickAdapter<T : MultiItemEntity, K : BaseViewHolder>
/**
 * Same as QuickAdapter#QuickAdapter(Context,int) but with
 * some initialization data.
 *
 * @param data A new list is created out of this one to avoid mutable list
 */
    (data: MutableList<T>?) : BaseQuickAdapter<T, K>(data) {

    /**
     * layouts indexed with their types
     */
    private var layouts: SparseIntArray? = null

    override fun getDefItemViewType(position: Int): Int {
        val item = mData?.get(position)
        return item?.itemType() ?: DEFAULT_VIEW_TYPE
    }

    protected fun setDefaultViewTypeLayout(@LayoutRes layoutResId: Int) {
        addItemType(DEFAULT_VIEW_TYPE, layoutResId)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): K {
        return createBaseViewHolder(parent, getLayoutId(viewType))
    }

    private fun getLayoutId(viewType: Int): Int {
        return layouts!!.get(viewType, TYPE_NOT_FOUND)
    }

    protected fun addItemType(type: Int, @LayoutRes layoutResId: Int) {
        if (layouts == null) {
            layouts = SparseIntArray()
        }
        layouts!!.put(type, layoutResId)
    }


    override fun remove(@IntRange(from = 0L) position: Int) {
        if (mData == null
            || position < 0
            || position >= mData!!.size
        )
            return

        val entity = mData!![position]
        if (entity is IExpandable<*>) {
            removeAllChild(entity as IExpandable<*>, position)
        }
        removeDataFromParent(entity)
        super.remove(position)
    }

    /**
     * 移除父控件时，若父控件处于展开状态，则先移除其所有的子控件
     *
     * @param parent         父控件实体
     * @param parentPosition 父控件位置
     */
    protected fun removeAllChild(parent: IExpandable<*>, parentPosition: Int) {
        if (parent.isExpanded) {
            val chidChilds = parent.subItems
            if (chidChilds == null || chidChilds.size == 0) return

            val childSize = chidChilds.size
            for (i in 0 until childSize) {
                remove(parentPosition + 1)
            }
        }
    }

    /**
     * 移除子控件时，移除父控件实体类中相关子控件数据，避免关闭后再次展开数据重现
     *
     * @param child 子控件实体
     */
    protected fun removeDataFromParent(child: T) {
        val position = getParentPosition(child)
        if (position >= 0) {
            val parent = mData!![position] as? IExpandable<*>
            parent?.subItems?.remove(child)
        }
    }

    /**
     * 该方法用于 IExpandable 树形列表。
     * 如果不存在 Parent，则 return -1。
     *
     * @param position 所处列表的位置
     * @return 父 position 在数据列表中的位置
     */
    fun getParentPositionInAll(position: Int): Int {
        val data = data
        if (data != null && data.isNotEmpty()) {
            val multiItemEntity = getItem(position)
            if (isExpandable(multiItemEntity)) {
                val IExpandable = multiItemEntity as IExpandable<*>?
                for (i in position - 1 downTo 0) {
                    val entity = data[i]
                    if (isExpandable(entity) && IExpandable!!.level > (entity as IExpandable<*>).level) {
                        return i
                    }
                }
            } else {
                for (i in position - 1 downTo 0) {
                    val entity = data[i]
                    if (isExpandable(entity)) {
                        return i
                    }
                }
            }
        }
        return -1
    }

    override fun isExpandable(item: T?): Boolean {
        return item != null && item is IExpandable<*>
    }

    companion object {
        private val DEFAULT_VIEW_TYPE = -0xff
        val TYPE_NOT_FOUND = -404
    }
}


