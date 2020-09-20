package cn.aihuaiedu.school.base.expandable.adapter

import android.view.View
import cn.aihuaiedu.school.base.expandable.dao.ContextDAO
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder


abstract class ExpandableAdapterModel<T>(private val listener: (ExpandableAdapterModel<T>) -> Unit) :
    EpoxyModelWithHolder<ExpandableAdapterModel<T>.ViewAllAdapterEpoxyHolder<T>>() {

    var subItems: MutableList<ExpandableAdapterModel<*>>? = null
    var mContextDAO: ContextDAO? = null
    var isExpanded: Boolean = false


    val parentModel: ExpandableAdapterModel<*>?
        get() {
            return mContextDAO?.mParent?.get()?.itemData as? ExpandableAdapterModel<*>
        }
    val index: Int
        get() {
            return mContextDAO?.index ?: 0
        }

    val level: Int
        get() {
            return mContextDAO?.groupLevel ?: 0
        }


    override fun bind(holder: ViewAllAdapterEpoxyHolder<T>) {
        super.bind(holder)
        holder.mItemView?.setOnClickListener {
            listener.invoke(this)
        }
    }

    abstract inner class ViewAllAdapterEpoxyHolder<T> : EpoxyHolder() {
        internal var mItemView: View? = null
        override fun bindView(itemView: View) {
            mItemView = itemView
        }

        abstract fun bind(item: T, isSelected: Boolean)
    }
}