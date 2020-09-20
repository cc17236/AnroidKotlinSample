package com.huawen.baselibrary.adapter

import android.util.SparseIntArray
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import com.huawen.baselibrary.adapter.entity.IExpandable
import com.huawen.baselibrary.adapter.entity.SectionMultiEntity

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
abstract class BaseSectionMultiItemQuickAdapter<T : SectionMultiEntity<*>, K : BaseViewHolder>
/**
 * Same as QuickAdapter#QuickAdapter(Context,int) but with
 * some initialization data.
 *
 * @param sectionHeadResId The section head layout id for each item
 * @param data             A new list is created out of this one to avoid mutable list
 */
    (protected var mSectionHeadResId: Int, data: MutableList<T>) : BaseQuickAdapter<T, K>(data) {

    /**
     * layouts indexed with their types
     */
    private var layouts: SparseIntArray? = null

    override fun getDefItemViewType(position: Int): Int {
        val item = mData?.get(position)
        return if (item != null) {
            // check the item type include header or not
            if (item.isHeader) SECTION_HEADER_VIEW else item.itemType()
        } else DEFAULT_VIEW_TYPE
    }

    protected fun setDefaultViewTypeLayout(@LayoutRes layoutResId: Int) {
        addItemType(DEFAULT_VIEW_TYPE, layoutResId)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): K {
        // add this to check viewType of section
        return if (viewType == SECTION_HEADER_VIEW) createBaseViewHolder(
            getItemView(
                mSectionHeadResId,
                parent
            )
        ) else createBaseViewHolder(parent, getLayoutId(viewType))

    }

    private fun getLayoutId(viewType: Int): Int {
        return layouts!!.get(viewType, TYPE_NOT_FOUND)
    }

    /**
     * collect layout types you need
     *
     * @param type             The key of layout type
     * @param layoutResId      The layoutResId of layout type
     */
    protected fun addItemType(type: Int, @LayoutRes layoutResId: Int) {
        if (layouts == null) {
            layouts = SparseIntArray()
        }
        layouts!!.put(type, layoutResId)
    }

    override fun isFixedViewType(type: Int): Boolean {
        return super.isFixedViewType(type) || type == SECTION_HEADER_VIEW
    }

    override fun onBindViewHolder(holder: K, position: Int) {
        when (holder.itemViewType) {
            SECTION_HEADER_VIEW -> {
                setFullSpan(holder)
                convertHead(holder, getItem(position - headerLayoutCount))
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }

    protected abstract fun convertHead(helper: K, item: T?)

    override fun remove(@IntRange(from = 0L) position: Int) {
        if (mData == null || position < 0
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

    companion object {

        private val DEFAULT_VIEW_TYPE = -0xff
        val TYPE_NOT_FOUND = -404
        protected val SECTION_HEADER_VIEW = 0x00000444
    }
}


