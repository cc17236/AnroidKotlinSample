package cn.aihuaiedu.school.base

import com.huawen.baselibrary.adapter.BaseMultiItemQuickAdapter
import com.huawen.baselibrary.adapter.BaseViewHolder
import com.huawen.baselibrary.adapter.entity.MultiItemEntity


/**
 * @作者: #Administrator #
 *@日期: #2018/5/4 #
 *@时间: #2018年05月04日 12:00 #
 *@File:Kotlin Class
 */
abstract class BaseMultiAdapter<T : MultiItemEntity>(data: MutableList<T>?) : BaseMultiItemQuickAdapter<T, BaseViewHolder>(data) {

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
            holder.itemView.post {
                holder.itemView.post {
                    var item: T? = null
                    try {
                        val pos = position - headerLayoutCount
                        item = getItem(pos)
                    } catch (e: Exception) {
                    }
                    onPartialUpdate(holder, item, idsSet)
                }
            }
        }
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

    public fun setDataUnChange(index: Int, item: T) {
        val datas = this.data
        if (datas?.isNotEmpty() == true && datas!!.size <= index) return
        datas?.set(index, item)
        notifyIdsItemSetChange(index, 0)
    }

    public final fun notifyIdsSetChange(vararg ids: Int) {
        val idsList = mutableListOf<Int>()
        try {
            idsList.addAll(ids.asList())
        } catch (e: Exception) {
        }
        if (idsList.isEmpty()) {
            idsList.add(0)
        }
        notifyItemRangeChanged(0, itemCount, idsList)
    }

    open protected fun onPartialUpdate(helper: BaseViewHolder, item: T?, ids: MutableList<Int>) {
    }

}

