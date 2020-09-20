package cn.aihuaiedu.school.base.ext

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import cn.aihuaiedu.school.base.BaseAdapter
import cn.aihuaiedu.school.base.setItemClick
import cn.aihuaiedu.school.base.setItemClickNoPadding
import com.example.applicationkotlinsample.utils.GlideUtil
import com.huawen.baselibrary.adapter.BaseQuickAdapter
import com.huawen.baselibrary.adapter.BaseViewHolder
import com.huawen.baselibrary.views.NoPaddingTextView
import org.jetbrains.anko.dip

inline fun BaseViewHolder.setImage(id: Int, url: String) {
    var image: ImageView? = null
    try {
        image = getViewSafe(id)
    } catch (e: Exception) {
    }
    GlideUtil.intoView(image, url, 0f)
}


inline fun BaseViewHolder.setVisibleSafe(viewId: Int, visible: Boolean): BaseViewHolder {
    val view: View? = getViewSafe(viewId)
    view?.visibility = if (visible) View.VISIBLE else View.GONE
    return this
}


inline fun <T : View> BaseViewHolder.getViewSafe(viewId: Int): T? {
    var view: T? = null
    try {
        val v: View? = getView(viewId)
        view = v as? T
    } catch (e: Exception) {
    }
    return view
}

inline fun BaseViewHolder.setTextSafe(@IdRes viewId: Int, value: CharSequence?): BaseViewHolder {
    try {
        val view = getViewSafe<TextView>(viewId)
        view?.text = value ?: ""
    } catch (e: Exception) {
        try {
            val view = getViewSafe<NoPaddingTextView>(viewId)
            view?.getTextView()?.text = value ?: ""
        } catch (e: Exception) {
        }
    }
    return this
}


inline fun BaseViewHolder.setEnable(@IdRes viewId: Int, isEnable: Boolean): BaseViewHolder {
    try {
        val view = getViewSafe<View>(viewId)
        view?.isEnabled = isEnable
    } catch (e: Exception) {
    }
    return this
}

inline fun BaseViewHolder.setClickable(@IdRes viewId: Int, isEnable: Boolean): BaseViewHolder {
    try {
        val view = getViewSafe<View>(viewId)
        view?.isClickable = isEnable
    } catch (e: Exception) {
    }
    return this
}

inline fun BaseViewHolder.setFouceable(@IdRes viewId: Int, isEnable: Boolean): BaseViewHolder {
    try {
        val view = getViewSafe<View>(viewId)
        view?.isFocusable = isEnable
    } catch (e: Exception) {
    }
    return this
}

inline fun BaseViewHolder.setTextColorSafe(@IdRes viewId: Int, @ColorInt textColor: Int): BaseViewHolder {
    try {
        val view = getViewSafe<TextView>(viewId)
        view?.setTextColor(textColor)
    } catch (e: Exception) {
    }
    return this
}


inline fun BaseViewHolder.setSelected(@IdRes viewId: Int, value: Boolean): BaseViewHolder {
    try {
        val view = getViewSafe<View>(viewId)
        view?.isSelected = value
    } catch (e: Exception) {
    }
    return this
}


inline fun BaseViewHolder.setRoundedImage(id: Int, url: String, redis: Float) {
    var image: ImageView? = null
    try {
        image = getViewSafe(id)
    } catch (e: Exception) {
    }
    GlideUtil.intoView(image, url, redis)
}

inline fun BaseViewHolder.setCircleImage(id: Int, url: String) {
    var image: ImageView? = null
    try {
        image = getViewSafe(id)
    } catch (e: Exception) {
    }
    GlideUtil.intoCircleView(image, url)
}

/**
 * 最好用这个
 */
/**
 * 适配中item的子控件点击监听,反馈子控件的id
 */
inline fun BaseViewHolder.setItemClick(id: Int, adapter: BaseQuickAdapter<*, *>?) {
    adapter?.setItemClick(this, id, adapter?.onItemChildClickListener)
}


inline fun BaseViewHolder.dip(px: Int): Int {
    return itemView?.dip(px) ?: 0
}


/**
 * 适配中item的子控件点击监听,不反馈子控件的id,已整个itemView为反馈
 */
inline fun BaseViewHolder.setItemInvokeClick(id: Int, adapter: BaseQuickAdapter<*, *>?) {
    adapter?.setItemClick(this, id, adapter?.itemClickListener())
}
inline fun BaseViewHolder.setItemInvokeClickWithPadding(id: Int, adapter: BaseQuickAdapter<*, *>?,pad:Int) {
    adapter?.setItemClickNoPadding(this, id, adapter?.itemClickListener(),pad)
}


inline fun <reified A : Any> BaseAdapter<A>.getLastItem(): A? {
    if (itemCount <= 0) return null
    val item = getItem(itemCount - 1) as? A
    return item
}
