package cn.aihuaiedu.school.base

import android.view.View
import androidx.annotation.LayoutRes
import com.huawen.baselibrary.adapter.BaseQuickAdapter
import com.huawen.baselibrary.adapter.BaseViewHolder
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.isDoubleClick

/**
 * @作者: #Administrator #
 *@日期: #2018/5/4 #
 *@时间: #2018年05月04日 12:05 #
 *@File:Kotlin Class
 */
abstract class BaseAdapter<T> : BaseQuickAdapter<T, BaseViewHolder> {
    constructor(@LayoutRes res: Int, data: MutableList<T>?) : super(res, data) {
    }

    override final fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val idsSet = arrayListOf<Int>()
            try {
                payloads.forEach {
                    if (it is Int) {
                        idsSet.add(it)
                    } else {
                        if (it is ArrayList<*>) {
                            it.forEach {
                                if (it is Int)
                                    idsSet.add(it.toString().toInt())
                            }
                        } else if (it is Int) {
                            idsSet.add(it.toString().toInt())
                        }
                    }
                }
            } catch (e: Exception) {
            }

            var item: T? = null
            try {
                val pos = position - headerLayoutCount
                item = getItem(pos)
            } catch (e: Exception) {
            }
            onPartialUpdate(holder, item, idsSet)
        }
    }


    public final fun notifyIdsSetChange(vararg ids: Int) {
        ids.asList()
        val idsList = arrayListOf<Int>()
        try {
            idsList.addAll(ids.asList())
        } catch (e: Exception) {
        }
        if (idsList.isEmpty()) {
            idsList.add(0)
        }
        notifyItemRangeChanged(0, itemCount, idsList)
    }

    public final fun notifyIdsItemSetChange(position: Int, vararg ids: Int) {
        val idsList = mutableListOf<Int>()
        try {
            idsList.addAll(ids.asList())
        } catch (e: Exception) {
        }
        if (idsList.isEmpty()) {
            idsList.add(0)
        }
        val pos = position + headerLayoutCount
        notifyItemChanged(pos, idsList)
    }


    open protected fun onPartialUpdate(helper: BaseViewHolder, item: T?, ids: MutableList<Int>) {
    }


    constructor(data: MutableList<T>?) : super(0, data) {
    }

    constructor(@LayoutRes res: Int) : super(res, null) {
    }
}

inline fun BaseQuickAdapter<*, *>.setItemClickNoPadding(
    helper: BaseViewHolder?,
    id: Int,
    listener: BaseQuickAdapter.OnItemClickListener?,
    pad: Int
) {
    var image: View? = null
    try {
        image = helper?.getView(id)
    } catch (e: Exception) {
    }
    image?.setOnClickListener {
        var pos = helper?.adapterPosition ?: 0
        pos += pad
        Debuger.print("setItemClick${pos}")
        if (isDoubleClick()) {
            return@setOnClickListener
        }
        listener?.onItemClick(this, helper?.itemView!!, pos)
    }
}

inline fun BaseQuickAdapter<*, *>.setItemClick(
    helper: BaseViewHolder?,
    id: Int,
    listener: BaseQuickAdapter.OnItemClickListener?
) {
    var image: View? = null
    try {
        image = helper?.getView(id)
    } catch (e: Exception) {
    }
    image?.setOnClickListener {
        var pos = helper?.adapterPosition ?: 0
        if (headerLayoutCount > 0) {
            pos += 1
        }
        Debuger.print("setItemClick${pos}")
        if (isDoubleClick()) {
            return@setOnClickListener
        }
        listener?.onItemClick(this, helper?.itemView!!, pos)
    }
}

inline fun BaseQuickAdapter<*, *>.setItemClick(
    helper: BaseViewHolder?,
    id: Int,
    listener: BaseQuickAdapter.OnItemChildClickListener?
) {
    var image: View? = null
    try {
        image = helper?.getView(id)
    } catch (e: Exception) {
    }
    image?.setOnClickListener {
        var pos = helper?.adapterPosition ?: 0
        Debuger.print("setItemClick${pos}")
        listener?.onItemChildClick(this, it, pos)
    }
}

inline fun <T> BaseAdapter<T>.setDataUnChange(index: Int, data: T) {
    val datas = this.data
    if (datas?.isNotEmpty() == true && datas.size <= index) return
    datas!!.set(index, data)
    notifyIdsItemSetChange(index, 0)
}

inline fun BaseQuickAdapter<*, *>.setOnItemChildClickListener(
    vararg id: Int,
    crossinline listener: (baseAdapter: BaseQuickAdapter<*, *>?, id: Int, view: View, position: Int) -> Unit
) {
    onItemChildClickListener = object : BaseQuickAdapter.OnItemChildClickListener {
        override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
            Debuger.print("setOnItemChildClickListener${position}  ${id.size}")
            if (id.contains(view.id) || id.isEmpty()) {
                var pos = position
                if (adapter.headerLayoutCount ?: 0 > 0) {
                    pos -= 1
                }
                if (isDoubleClick()) {
                    return
                }
                listener.invoke(adapter, view.id, view, pos)
            }
        }

    }
}

inline fun BaseQuickAdapter<*, *>.setOnItemChildClickListener(
    doubleClickInterrupt: Boolean = true,
    crossinline listener: (baseAdapter: BaseQuickAdapter<*, *>?, id: Int, view: View, position: Int) -> Unit
) {
    onItemChildClickListener = object : BaseQuickAdapter.OnItemChildClickListener {
        override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
            var pos = position
            if (adapter.headerLayoutCount ?: 0 > 0) {
                pos -= 1
            }
            if (doubleClickInterrupt)
                if (isDoubleClick()) {
                    return
                }
            Debuger.print("setItemClick${pos}  posi:${position}")
            listener.invoke(adapter, view.id, view, pos)
        }
    }
}

