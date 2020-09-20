package cn.aihuaiedu.school.base.context

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.huawen.baselibrary.adapter.BaseQuickAdapter
import com.huawen.baselibrary.utils.isDoubleClick
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * @作者: #Administrator #
 *@日期: #2018/5/9 #
 *@时间: #2018年05月09日 11:34 #
 *@File:Kotlin File
 */
internal inline fun BaseQuickAdapter<*, *>.findFirstHeader(): View? {
    if (headerLayoutCount > 0)
        return headerLayout?.getChildAt(0)
    return null
}

internal inline fun BaseQuickAdapter<*, *>.findFirstHeaderOrInsert(ctx: Context, id: Int): View? {
    val that = this
    return (findFirstHeader()
        ?: LayoutInflater.from(ctx).inflate(id, null).apply { that.addHeaderView(this) })
}

inline fun Any.getJson(context: Context, fileName: String): String {
    val stringBuilder = StringBuilder()
    try {
        val assetManager = context.assets
        val bf = BufferedReader(
            InputStreamReader(
                assetManager.open(fileName)
            )
        )
        var line: String? = null

        while ({ line = bf.readLine();line }() != null) {
            stringBuilder.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

inline fun Any.getDateForString(date: String): List<Int> {
    val dates = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val list = ArrayList<Int>()
    list.add(Integer.parseInt(dates[0]))
    list.add(Integer.parseInt(dates[1]))
    list.add(Integer.parseInt(dates[2]))
    return list
}


inline fun BaseQuickAdapter<*, *>.setOnItemSingleClickListener(crossinline listener: (BaseQuickAdapter<*, *>, View, Int) -> Unit) {
    setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
        if (isDoubleClick())
            return@setOnItemClickListener
        listener.invoke(adapter, view, position)
    }
}