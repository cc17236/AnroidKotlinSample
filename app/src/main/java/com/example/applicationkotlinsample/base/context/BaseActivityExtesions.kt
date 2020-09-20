package cn.aihuaiedu.school.base.context

import android.view.View
import androidx.annotation.IdRes
import org.jetbrains.anko.dip
import org.jetbrains.anko.px2dip

/**
 * @作者: #Administrator #
 *@日期: #2018/5/7 #
 *@时间: #2018年05月07日 12:49 #
 *@File:Kotlin File
 */
internal fun BaseActivity<*, *>.doOnBackPressedEvent(@IdRes id: Int, clickLis: ((View) -> Boolean)? = null) {
    find<View>(id)?.setOnClickListener {
        if (clickLis !== null) {
            val rlt=clickLis.invoke(it)
            if (rlt){
                onBackPressed()
            }
        } else
            onBackPressed()
    }
}

internal fun BaseActivity<*, *>.doOnBackPressedEvent(view: View?, clickLis: ((View) -> Boolean)? = null) {
    view?.setOnClickListener {
        if (clickLis !== null) {
            val rlt=clickLis.invoke(it)
            if (rlt){
                onBackPressed()
            }
        } else
            onBackPressed()
    }
}

internal inline fun BaseActivity<*, *>.doOnRightPressedEvent(@IdRes id: Int, crossinline fun0: (View) -> Unit) {
    find<View>(id)?.setOnClickListener {
        fun0(it)
    }
}

internal inline fun BaseFragment<*, *>.doOnRightPressedEvent(@IdRes id: Int, crossinline fun0: (View) -> Unit) {
    find<View>(id)?.setOnClickListener {
        fun0(it)
    }
}

internal inline fun BaseFragment<*, *>?.px2dip(px: Int): Int {
    if (this?.activity != null) {
        return activity!!.px2dip(px).toInt()
    } else {
        return px
    }
}

internal inline fun BaseFragment<*, *>?.px2dip(px: Float): Int {
    if (this?.activity != null) {
        return activity!!.px2dip(px.toInt()).toInt()
    } else {
        return px.toInt()
    }
}

internal inline fun BaseFragment<*, *>?.dip(dp: Int): Int {
    if (this?.activity != null) {
        return activity!!.dip(dp)
    } else {
        return dp
    }
}

internal inline fun BaseFragment<*, *>?.dip(dp: Float): Int {
    if (this?.activity != null) {
        return activity!!.dip(dp)
    } else {
        return dp.toInt()
    }
}


