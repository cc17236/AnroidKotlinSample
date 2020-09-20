package com.huawen.baselibrary.adapter

import android.view.ViewGroup

import com.huawen.baselibrary.adapter.entity.SectionEntity

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
abstract class BaseSectionQuickAdapter<T : SectionEntity<*>, K : BaseViewHolder>
/**
 * Same as QuickAdapter#QuickAdapter(Context,int) but with
 * some initialization data.
 *
 * @param sectionHeadResId The section head layout id for each item
 * @param layoutResId      The layout resource id of each item.
 * @param data             A new list is created out of this one to avoid mutable list
 */
    (layoutResId: Int, protected var mSectionHeadResId: Int, data: MutableList<T>) :
    BaseQuickAdapter<T, K>(layoutResId, data) {

    override fun getDefItemViewType(position: Int): Int {
        return if (mData!![position].isHeader) SECTION_HEADER_VIEW else 0
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): K {
        return if (viewType == SECTION_HEADER_VIEW) createBaseViewHolder(
            getItemView(
                mSectionHeadResId,
                parent
            )
        ) else super.onCreateDefViewHolder(parent, viewType)

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

    companion object {
        protected val SECTION_HEADER_VIEW = 0x00000444
    }

}
