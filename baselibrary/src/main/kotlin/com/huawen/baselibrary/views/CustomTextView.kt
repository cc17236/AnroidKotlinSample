package com.huawen.baselibrary.views

import android.content.Context
import android.util.AttributeSet
import com.huawen.baselibrary.R
import com.huawen.baselibrary.utils.FontCache


open class CustomTextView : androidx.appcompat.widget.AppCompatTextView {

    private var fonts: String? = null

    fun setTextFont(font: String?) {
        fonts = font
        applyCustomFont(context, fonts)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    fun init(context: Context, attrs: AttributeSet?){
        if (attrs==null){
            fonts ="pingfang_sc_regular"
        }else{
            //获取 TypedArray 对象
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0)
            try {
                fonts = typedArray.getString(R.styleable.CustomTextView_textFont)
            } finally {
                typedArray.recycle()
            }
        }
        applyCustomFont(context, fonts)
    }

   open protected fun applyCustomFont(context: Context, text: String?) {
        val customFont = FontCache.getTypeface("${text}.ttf", context) ?: return
        typeface = customFont
    }
}
