package com.huawen.baselibrary.views

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import java.net.URLDecoder

fun Collection<*>?.isNotEmptys(): Boolean {
    return this != null && !this.isEmpty()
}

fun String.urldecode(): String {
    val decode = URLDecoder.decode(this, "UTF-8")
    if (!decode.equals(this)) {
        return decode
    }
    return this
}


fun Drawable.setTintColor(@ColorInt color: Int) {
    setColorFilter(color, PorterDuff.Mode.MULTIPLY)
}


fun RecyclerView.removeAllItemDecorations() {
    val field = RecyclerView::class.java.getDeclaredField("mItemDecorations")
    field.isAccessible = true
    val lists = field.get(this) as? ArrayList<RecyclerView.ItemDecoration>
    lists?.clear()
    invalidateItemDecorations()
}

fun EditText.removeAllTextWatcher() {
    try {
        val field = TextView::class.java.getDeclaredField("mListeners")
        field.isAccessible = true
        val objs = field.get(this)
        val m = objs.javaClass.getDeclaredMethod("clear")
        m.isAccessible = true
        m.invoke(objs)
    } catch (e: Exception) {
    }
}






