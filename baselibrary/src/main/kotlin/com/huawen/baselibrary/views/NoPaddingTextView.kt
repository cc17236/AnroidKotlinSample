package com.huawen.baselibrary.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class NoPaddingTextView : LinearLayout {
    private var textView: MyTextViewInner? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null && defStyleAttr > 0) {
            textView = MyTextViewInner(context, attrs, defStyleAttr)
        } else if (attrs != null) {
            textView = MyTextViewInner(context, attrs)
        } else {
            textView = MyTextViewInner(context)
        }
        this.setPadding(0, 0, 0, 0)
        addView(textView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = measuredWidth
        width = width + textView!!.paddingLeft + textView!!.paddingRight
        setMeasuredDimension(width, measuredHeight)
    }

    fun getTextView(): TextView? {
        return textView
    }

    inner class MyTextViewInner : CustomTextView {
        //设置是否remove间距，true为remove
        private val noDefaultPadding = true
        private var fontMetricsInt: Paint.FontMetricsInt? = null
        private var minRect: Rect? = null

        constructor(context: Context) : super(context) {}

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

        constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            if (fontMetricsInt == null) {
                //fontMetricsInt包含的是text文字四条线的 距离，
                //此四条线距离也是以text文字baseline为基准的
                fontMetricsInt = Paint.FontMetricsInt()
            }
            paint.getFontMetricsInt(fontMetricsInt)
            if (minRect == null) {
                //minRect用来获取文字实际显示的时候的左上角和右下角  坐标
                //该坐标是以text文字baseline为基准的
                minRect = Rect()
            }
            paint.getTextBounds(text.toString(), 0, text.length, minRect)
            val lp = this.layoutParams as ViewGroup.MarginLayoutParams
            lp.topMargin = -(fontMetricsInt!!.bottom - minRect!!.bottom) + (fontMetricsInt!!.top - minRect!!.top)
            lp.rightMargin = -(minRect!!.left + (measuredWidth - minRect!!.right))
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        override fun onDraw(canvas: Canvas) {
            if (noDefaultPadding) {
                if (fontMetricsInt == null) {
                    //fontMetricsInt包含的是text文字四条线的 距离，
                    //此四条线距离也是以text文字baseline为基准的
                    fontMetricsInt = Paint.FontMetricsInt()
                }
                paint.getFontMetricsInt(fontMetricsInt)
                if (minRect == null) {
                    //minRect用来获取文字实际显示的时候的左上角和右下角  坐标
                    //该坐标是以text文字baseline为基准的
                    minRect = Rect()
                }
                paint.getTextBounds(text.toString(), 0, text.length, minRect)
                canvas.translate((-minRect!!.left).toFloat(), (fontMetricsInt!!.bottom - minRect!!.bottom).toFloat())
            }
            super.onDraw(canvas)
        }

        override fun setText(text: CharSequence, type: TextView.BufferType) {
            super.setText(text, type)
            this.requestLayout()
        }
    }
}
