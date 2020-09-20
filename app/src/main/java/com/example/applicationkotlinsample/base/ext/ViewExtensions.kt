package cn.aihuaiedu.school.base.ext

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Xml
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import org.jetbrains.anko.dip
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader


/**
 * @作者: #Administrator #
 *@日期: #2018/5/9 #
 *@时间: #2018年05月09日 11:34 #
 *@File:Kotlin File
 */

inline fun EditText.changeLine(isProhibit: Boolean) {
    if (isProhibit) {
        setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener event.getKeyCode() == KeyEvent.KEYCODE_ENTER
        }
    }
}

inline fun EditText.forbidPrefix(prefix: String) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if (text.isNullOrEmpty()) {
            } else {
                //禁止开头输入0
                if (text.startsWith(prefix)) {
                    s?.replace(0, 1, "")
                    return
                }
                //超出金额
                var m = 0f
                try {
                    m = text.toFloat()
                } catch (e: Exception) {
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    })
}

inline fun EditText.setScrollTouchFixable() {
    setOnTouchListener { v, event ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.parent.requestDisallowInterceptTouchEvent(false)
        }
        return@setOnTouchListener false
    }
}

inline fun WebView.forbidLongClick() {
    setOnLongClickListener { true }
}

inline fun RecyclerView.itemDecoration(top: Int = 0, left: Int = 0, bottom: Int = 0, right: Int = 0) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = dip(top)
            outRect.right = dip(right)
            outRect.left = dip(left)
            outRect.bottom = dip(bottom)
        }
    })
}

fun String.html(): CharSequence {
    val sequence = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
    var start = 0
    var end = sequence.length
    while (start < end && Character.isWhitespace(sequence[start])) {
        start++
    }
    while (end > start && Character.isWhitespace(sequence[end - 1])) {
        end--
    }
    return sequence.subSequence(start, end)
}

fun String.htmlToPlainText(): String {
    val text = Jsoup.parse(this).text()
    return text.replace("   ", "&nbsp;&nbsp;&nbsp;").replace("  ", "&nbsp;&nbsp;")
}

fun String?.trimSpace(holder: String): String {
    if (TextUtils.isEmpty(this)) return holder
    val tr = this!!.trim().replace(" ", "")
        .replace("\n", "")
        .replace("\t", "")
    if (tr.isBlank()) return holder
    return this
}


@Deprecated(message = "系统限制,无法使用运行时生成,此方法废弃")
fun Context.createAttribute(attribute: String): AttributeSet? {
    val attributes = "<attribute xmlns:android=\"http://schemas.android.com/apk/res/android\"  xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
            "        xmlns:tools=\"http://schemas.android.com/tools\" $attribute />"
    var factory: XmlPullParserFactory? = null
    try {
        factory = XmlPullParserFactory.newInstance()
        factory.isValidating = true
        factory!!.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(StringReader(attributes))
        parser.next()
        return Xml.asAttributeSet(parser)
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun ViewPager.sliding(isSliding: Boolean) {
    setOnTouchListener { v, event ->
        return@setOnTouchListener isSliding
    }


}

