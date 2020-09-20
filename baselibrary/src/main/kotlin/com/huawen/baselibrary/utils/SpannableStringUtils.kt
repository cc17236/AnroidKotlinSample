package com.huawen.baselibrary.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import android.text.Layout
import android.text.Layout.Alignment
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.*
import android.util.Log
import java.lang.ref.WeakReference

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 16/12/13
 * desc  : SpannableString相关工具类
</pre> *
 */
class SpannableStringUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    @IntDef(ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER, ALIGN_TOP)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Align

    class Builder {

        private val defaultValue = 0x12000000

        private var text: CharSequence? = null
        private var flag: Int = 0
        @ColorInt
        private var foregroundColor: Int = 0
        @ColorInt
        private var backgroundColor: Int = 0
        @ColorInt
        private var quoteColor: Int = 0
        private var stripeWidth: Int = 0
        private var quoteGapWidth: Int = 0
        private var isLeadingMargin: Boolean = false
        private var first: Int = 0
        private var rest: Int = 0
        private var margin: Int = 0
        private var isBullet: Boolean = false
        private var bulletColor: Int = 0
        private var bulletRadius: Int = 0
        private var bulletGapWidth: Int = 0
        private var fontSize: Int = 0
        private var fontSizeIsDp: Boolean = false
        private var proportion: Float = 0.toFloat()
        private var xProportion: Float = 0.toFloat()
        private var isStrikethrough: Boolean = false
        private var isUnderline: Boolean = false
        private var isSuperscript: Boolean = false
        private var isSubscript: Boolean = false
        private var isBold: Boolean = false
        private var isItalic: Boolean = false
        private var isBoldItalic: Boolean = false
        private var fontFamily: String? = null
        private var typeface: Typeface? = null
        private var alignment: Alignment? = null
        private var imageIsBitmap: Boolean = false
        private var bitmap: Bitmap? = null
        private var imageIsDrawable: Boolean = false
        private var drawable: Drawable? = null
        private var imageIsUri: Boolean = false
        private var uri: Uri? = null
        private var imageIsResourceId: Boolean = false
        @DrawableRes
        private var resourceId: Int = 0
        @Align
        internal var align: Int = 0

        private var clickSpan: ClickableSpan? = null
        private var url: String? = null

        private var isBlur: Boolean = false
        private var blurRadius: Float = 0.toFloat()
        private var style: BlurMaskFilter.Blur? = null

        private val mBuilder: SpannableStringBuilder

        init {
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            foregroundColor = defaultValue
            backgroundColor = defaultValue
            quoteColor = defaultValue
            margin = -1
            fontSize = -1
            proportion = -1f
            xProportion = -1f
            align = ALIGN_BOTTOM
            mBuilder = SpannableStringBuilder()
        }

        /**
         * 设置标识
         *
         * @param flag
         *  * [Spanned.SPAN_INCLUSIVE_EXCLUSIVE]
         *  * [Spanned.SPAN_INCLUSIVE_INCLUSIVE]
         *  * [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
         *  * [Spanned.SPAN_EXCLUSIVE_INCLUSIVE]
         *
         * @return [Builder]
         */
        fun setFlag(flag: Int): Builder {
            this.flag = flag
            return this
        }

        /**
         * 设置前景色
         *
         * @param color 前景色
         * @return [Builder]
         */
        fun setForegroundColor(@ColorInt color: Int): Builder {
            this.foregroundColor = color
            return this
        }

        /**
         * 设置背景色
         *
         * @param color 背景色
         * @return [Builder]
         */
        fun setBackgroundColor(@ColorInt color: Int): Builder {
            this.backgroundColor = color
            return this
        }

        /**
         * 设置引用线的颜色
         *
         * @param color 引用线的颜色
         * @return [Builder]
         */
        fun setQuoteColor(@ColorInt color: Int): Builder {
            this.quoteColor = color
            this.stripeWidth = 2
            this.quoteGapWidth = 2
            return this
        }

        /**
         * 设置引用线的颜色
         *
         * @param color         引用线的颜色
         * @param stripeWidth   引用线线宽
         * @param quoteGapWidth 引用线和文字间距
         * @return [Builder]
         */
        fun setQuoteColor(@ColorInt color: Int, stripeWidth: Int, quoteGapWidth: Int): Builder {
            this.quoteColor = color
            this.stripeWidth = stripeWidth
            this.quoteGapWidth = quoteGapWidth
            return this
        }

        /**
         * 设置缩进
         *
         * @param first 首行缩进
         * @param rest  剩余行缩进
         * @return [Builder]
         */
        fun setLeadingMargin(first: Int, rest: Int): Builder {
            this.first = first
            this.rest = rest
            isLeadingMargin = true
            return this
        }

        /**
         * 设置间距
         *
         * @param margin 间距
         * @return [Builder]
         */
        fun setMargin(margin: Int): Builder {
            this.margin = margin
            this.text = " " + this.text!!
            return this
        }

        /**
         * 设置列表标记
         *
         * @param gapWidth 列表标记和文字间距离
         * @return [Builder]
         */
        fun setBullet(@ColorInt gapWidth: Int): Builder {
            this.bulletColor = 0
            this.bulletRadius = 3
            this.bulletGapWidth = gapWidth
            isBullet = true
            return this
        }

        /**
         * 设置列表标记
         *
         * @param color    列表标记的颜色
         * @param radius   列表标记颜色
         * @param gapWidth 列表标记和文字间距离
         * @return [Builder]
         */
        fun setBullet(@ColorInt color: Int, radius: Int, gapWidth: Int): Builder {
            this.bulletColor = color
            this.bulletRadius = radius
            this.bulletGapWidth = gapWidth
            isBullet = true
            return this
        }

        /**
         * 设置字体尺寸
         *
         * @param size 尺寸
         * @return [Builder]
         */
        fun setFontSize(size: Int): Builder {
            this.fontSize = size
            this.fontSizeIsDp = false
            return this
        }

        /**
         * 设置字体尺寸
         *
         * @param size 尺寸
         * @param isDp 是否使用dip
         * @return [Builder]
         */
        fun setFontSize(size: Int, isDp: Boolean): Builder {
            this.fontSize = size
            this.fontSizeIsDp = isDp
            return this
        }

        /**
         * 设置字体比例
         *
         * @param proportion 比例
         * @return [Builder]
         */
        fun setFontProportion(proportion: Float): Builder {
            this.proportion = proportion
            return this
        }

        /**
         * 设置字体横向比例
         *
         * @param proportion 比例
         * @return [Builder]
         */
        fun setFontXProportion(proportion: Float): Builder {
            this.xProportion = proportion
            return this
        }

        /**
         * 设置删除线
         *
         * @return [Builder]
         */
        fun setStrikethrough(): Builder {
            this.isStrikethrough = true
            return this
        }

        /**
         * 设置下划线
         *
         * @return [Builder]
         */
        fun setUnderline(): Builder {
            this.isUnderline = true
            return this
        }

        /**
         * 设置上标
         *
         * @return [Builder]
         */
        fun setSuperscript(): Builder {
            this.isSuperscript = true
            return this
        }

        /**
         * 设置下标
         *
         * @return [Builder]
         */
        fun setSubscript(): Builder {
            this.isSubscript = true
            return this
        }

        /**
         * 设置粗体
         *
         * @return [Builder]
         */
        fun setBold(): Builder {
            isBold = true
            return this
        }

        /**
         * 设置斜体
         *
         * @return [Builder]
         */
        fun setItalic(): Builder {
            isItalic = true
            return this
        }

        /**
         * 设置粗斜体
         *
         * @return [Builder]
         */
        fun setBoldItalic(): Builder {
            isBoldItalic = true
            return this
        }

        /**
         * 设置字体系列
         *
         * @param fontFamily 字体系列
         *
         *  * monospace
         *  * serif
         *  * sans-serif
         *
         * @return [Builder]
         */
        fun setFontFamily(fontFamily: String): Builder {
            this.fontFamily = fontFamily
            return this
        }

        /**
         * 设置字体
         *
         * @param typeface 字体
         * @return [Builder]
         */
        fun setTypeface(typeface: Typeface): Builder {
            this.typeface = typeface
            return this
        }

        /**
         * 设置对齐
         *
         * @param alignment 对其方式
         *
         *  * [Alignment.ALIGN_NORMAL]正常
         *  * [Alignment.ALIGN_OPPOSITE]相反
         *  * [Alignment.ALIGN_CENTER]居中
         *
         * @return [Builder]
         */
        fun setAlign(alignment: Alignment): Builder {
            this.alignment = alignment
            return this
        }

        /**
         * 设置图片
         *
         * @param bitmap 图片位图
         * @param align  对齐
         *
         *  * [Align.ALIGN_TOP]顶部对齐
         *  * [Align.ALIGN_CENTER]居中对齐
         *  * [Align.ALIGN_BASELINE]基线对齐
         *  * [Align.ALIGN_BOTTOM]底部对齐
         *
         * @return [Builder]
         */
        @JvmOverloads
        fun setBitmap(bitmap: Bitmap, @Align align: Int = ALIGN_CENTER): Builder {
            this.bitmap = bitmap
            this.align = align
            this.text = " " + this.text!!
            imageIsBitmap = true
            return this
        }

        /**
         * 设置图片
         *
         * @param drawable 图片资源
         * @param align    对齐
         *
         *  * [Align.ALIGN_TOP]顶部对齐
         *  * [Align.ALIGN_CENTER]居中对齐
         *  * [Align.ALIGN_BASELINE]基线对齐
         *  * [Align.ALIGN_BOTTOM]底部对齐
         *
         * @return [Builder]
         */
        @JvmOverloads
        fun setDrawable(drawable: Drawable, @Align align: Int = ALIGN_CENTER): Builder {
            this.drawable = drawable
            this.align = align
            this.text = " " + this.text!!
            imageIsDrawable = true
            return this
        }

        /**
         * 设置图片
         *
         * @param uri 图片uri
         * @return [Builder]
         */
        fun setUri(uri: Uri): Builder {
            setUri(uri, ALIGN_BOTTOM)
            return this
        }

        /**
         * 设置图片
         *
         * @param uri   图片uri
         * @param align 对齐
         *
         *  * [Align.ALIGN_TOP]顶部对齐
         *  * [Align.ALIGN_CENTER]居中对齐
         *  * [Align.ALIGN_BASELINE]基线对齐
         *  * [Align.ALIGN_BOTTOM]底部对齐
         *
         * @return [Builder]
         */
        fun setUri(uri: Uri, @Align align: Int): Builder {
            this.uri = uri
            this.align = align
            this.text = " " + this.text!!
            imageIsUri = true
            return this
        }

        /**
         * 设置图片
         *
         * @param resourceId 图片资源id
         * @param align      对齐
         * @return [Builder]
         */
        @JvmOverloads
        fun setResourceId(@DrawableRes resourceId: Int, @Align align: Int = ALIGN_CENTER): Builder {
            this.resourceId = resourceId
            this.align = align
            this.text = " " + this.text!!
            imageIsResourceId = true
            return this
        }

        /**
         * 设置点击事件
         *
         * 需添加view.setMovementMethod(LinkMovementMethod.getInstance())
         *
         * @param clickSpan 点击事件
         * @return [Builder]
         */
        fun setClickSpan(clickSpan: ClickableSpan): Builder {
            this.clickSpan = clickSpan
            return this
        }

        /**
         * 设置超链接
         *
         * 需添加view.setMovementMethod(LinkMovementMethod.getInstance())
         *
         * @param url 超链接
         * @return [Builder]
         */
        fun setUrl(url: String): Builder {
            this.url = url
            return this
        }

        /**
         * 设置模糊
         *
         * 尚存bug，其他地方存在相同的字体的话，相同字体出现在之前的话那么就不会模糊，出现在之后的话那会一起模糊
         *
         * 推荐还是把所有字体都模糊这样使用
         *
         * @param radius 模糊半径（需大于0）
         * @param style  模糊样式
         *  * [BlurMaskFilter.Blur.NORMAL]
         *  * [BlurMaskFilter.Blur.SOLID]
         *  * [BlurMaskFilter.Blur.OUTER]
         *  * [BlurMaskFilter.Blur.INNER]
         *
         * @return [Builder]
         */
        fun setBlur(radius: Float, style: BlurMaskFilter.Blur): Builder {
            this.blurRadius = radius
            this.style = style
            this.isBlur = true
            return this
        }

        /**
         * 追加样式一行字符串
         *
         * @param text 样式字符串文本
         * @return [Builder]
         */
        fun appendLine(text: CharSequence): Builder {
            return append(text.toString() + LINE_SEPARATOR)
        }

        /**
         * 追加样式字符串
         *
         * @param text 样式字符串文本
         * @return [Builder]
         */
        fun append(text: CharSequence): Builder {
            setSpan()
            this.text = text
            return this
        }

        /**
         * 创建样式字符串
         *
         * @return 样式字符串
         */
        fun create(): SpannableStringBuilder {
            setSpan()
            return mBuilder
        }

        /**
         * 设置样式
         */
        private fun setSpan() {
            if (text == null || text!!.length == 0) return
            val start = mBuilder.length
            mBuilder.append(this.text)
            val end = mBuilder.length
            if (backgroundColor != defaultValue) {
                mBuilder.setSpan(BackgroundColorSpan(backgroundColor), start, end, flag)
                backgroundColor = defaultValue
            }
            if (foregroundColor != defaultValue) {
                mBuilder.setSpan(ForegroundColorSpan(foregroundColor), start, end, flag)
                foregroundColor = defaultValue
            }
            if (isLeadingMargin) {
                mBuilder.setSpan(LeadingMarginSpan.Standard(first, rest), start, end, flag)
                isLeadingMargin = false
            }
            if (margin != -1) {
                mBuilder.setSpan(MarginSpan(margin), start, end, flag)
                margin = -1
            }
            if (quoteColor != defaultValue) {
                mBuilder.setSpan(CustomQuoteSpan(quoteColor, stripeWidth, quoteGapWidth), start, end, flag)
                quoteColor = defaultValue
            }
            if (isBullet) {
                mBuilder.setSpan(CustomBulletSpan(bulletColor, bulletRadius, bulletGapWidth), start, end, flag)
                isBullet = false
            }
            if (fontSize != -1) {
                mBuilder.setSpan(AbsoluteSizeSpan(fontSize, fontSizeIsDp), start, end, flag)
                fontSize = -1
                fontSizeIsDp = false
            }
            if (proportion != -1f) {
                mBuilder.setSpan(RelativeSizeSpan(proportion), start, end, flag)
                proportion = -1f
            }
            if (xProportion != -1f) {
                mBuilder.setSpan(ScaleXSpan(xProportion), start, end, flag)
                xProportion = -1f
            }
            if (isStrikethrough) {
                mBuilder.setSpan(StrikethroughSpan(), start, end, flag)
                isStrikethrough = false
            }
            if (isUnderline) {
                mBuilder.setSpan(UnderlineSpan(), start, end, flag)
                isUnderline = false
            }
            if (isSuperscript) {
                mBuilder.setSpan(SuperscriptSpan(), start, end, flag)
                isSuperscript = false
            }
            if (isSubscript) {
                mBuilder.setSpan(SubscriptSpan(), start, end, flag)
                isSubscript = false
            }
            if (isBold) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
                isBold = false
            }
            if (isItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
                isItalic = false
            }
            if (isBoldItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
                isBoldItalic = false
            }
            if (fontFamily != null) {
                mBuilder.setSpan(TypefaceSpan(fontFamily), start, end, flag)
                fontFamily = null
            }
            if (typeface != null) {
                mBuilder.setSpan(CustomTypefaceSpan(typeface!!), start, end, flag)
                typeface = null
            }
            if (alignment != null) {
                mBuilder.setSpan(AlignmentSpan.Standard(alignment!!), start, end, flag)
                alignment = null
            }
            if (imageIsBitmap || imageIsDrawable || imageIsUri || imageIsResourceId) {
                if (imageIsBitmap) {
                    mBuilder.setSpan(CustomImageSpan(Utils.getContext(), bitmap!!, align), start, end, flag)
                    bitmap = null
                    imageIsBitmap = false
                } else if (imageIsDrawable) {
                    mBuilder.setSpan(CustomImageSpan(drawable!!, align), start, end, flag)
                    drawable = null
                    imageIsDrawable = false
                } else if (imageIsUri) {
                    mBuilder.setSpan(CustomImageSpan(Utils.getContext(), uri!!, align), start, end, flag)
                    uri = null
                    imageIsUri = false
                } else {
                    mBuilder.setSpan(CustomImageSpan(Utils.getContext(), resourceId, align), start, end, flag)
                    resourceId = 0
                    imageIsResourceId = false
                }
            }
            if (clickSpan != null) {
                mBuilder.setSpan(clickSpan, start, end, flag)
                clickSpan = null
            }
            if (url != null) {
                mBuilder.setSpan(URLSpan(url), start, end, flag)
                url = null
            }
            if (isBlur) {
                mBuilder.setSpan(MaskFilterSpan(BlurMaskFilter(blurRadius, style)), start, end, flag)
                isBlur = false
            }
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        }
    }
    /**
     * 设置图片
     *
     * @param bitmap 图片位图
     * @return [Builder]
     */
    /**
     * 设置图片
     *
     * @param drawable 图片资源
     * @return [Builder]
     */
    /**
     * 设置图片
     *
     * @param resourceId 图片资源id
     * @return [Builder]
     */

    internal class MarginSpan internal constructor(private val margin: Int) : ReplacementSpan() {

        override fun getSize(paint: Paint, text: CharSequence, @IntRange(from = 0) start: Int, @IntRange(from = 0) end: Int, fm: Paint.FontMetricsInt?): Int {
            var text = text
            text = " "
            return margin
        }

        override fun draw(canvas: Canvas, text: CharSequence, @IntRange(from = 0) start: Int, @IntRange(from = 0) end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {

        }
    }

    internal class CustomQuoteSpan internal constructor(@param:ColorInt private val color: Int, private val stripeWidth: Int, private val gapWidth: Int) : LeadingMarginSpan {

        override fun getLeadingMargin(first: Boolean): Int {
            return stripeWidth + gapWidth
        }

        override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                       top: Int, baseline: Int, bottom: Int,
                                       text: CharSequence, start: Int, end: Int,
                                       first: Boolean, layout: Layout) {
            val style = p.style
            val color = p.color

            p.style = Paint.Style.FILL
            p.color = this.color

            c.drawRect(x.toFloat(), top.toFloat(), (x + dir * stripeWidth).toFloat(), bottom.toFloat(), p)

            p.style = style
            p.color = color
        }
    }

    internal class CustomBulletSpan internal constructor(private val color: Int, private val radius: Int, private val gapWidth: Int) : LeadingMarginSpan {

        override fun getLeadingMargin(first: Boolean): Int {
            return 2 * radius + gapWidth
        }

        override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                       top: Int, baseline: Int, bottom: Int,
                                       text: CharSequence, start: Int, end: Int,
                                       first: Boolean, l: Layout) {
            if ((text as Spanned).getSpanStart(this) == start) {
                val style = p.style
                var oldColor = 0
                oldColor = p.color
                p.color = color
                p.style = Paint.Style.FILL
                if (c.isHardwareAccelerated) {
                    if (sBulletPath == null) {
                        sBulletPath = Path()
                        // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                        sBulletPath!!.addCircle(0.0f, 0.0f, radius.toFloat(), Path.Direction.CW)
                    }
                    c.save()
                    c.translate((x + dir * radius).toFloat(), (top + bottom) / 2.0f)
                    c.drawPath(sBulletPath!!, p)
                    c.restore()
                } else {
                    c.drawCircle((x + dir * radius).toFloat(), (top + bottom) / 2.0f, radius.toFloat(), p)
                }
                p.color = oldColor
                p.style = style
            }
        }

        companion object {

            private var sBulletPath: Path? = null
        }
    }

    @SuppressLint("ParcelCreator")
    internal class CustomTypefaceSpan internal constructor(private val newType: Typeface) : TypefaceSpan("") {

        override fun updateDrawState(textPaint: TextPaint) {
            apply(textPaint, newType)
        }

        override fun updateMeasureState(paint: TextPaint) {
            apply(paint, newType)
        }

        private fun apply(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            if (old == null) {
                oldStyle = 0
            } else {
                oldStyle = old.style
            }

            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }

            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }

            paint.typeface = tf
        }
    }

    internal class CustomImageSpan : CustomDynamicDrawableSpan {
        private var mDrawable: Drawable? = null
        private var mContentUri: Uri? = null
        private var mResourceId: Int? = null
        private var mContext: Context? = null

        override var drawable: Drawable? = null
            get() {
                var drawable: Drawable? = null
                if (mDrawable != null) {
                    drawable = mDrawable
                } else if (mContentUri != null) {
                    var bitmap: Bitmap? = null
                    try {
                        val `is` = mContext!!.contentResolver.openInputStream(
                                mContentUri!!)
                        bitmap = BitmapFactory.decodeStream(`is`)
                        drawable = BitmapDrawable(mContext!!.resources, bitmap)
                        drawable.setBounds(0, 0, drawable.intrinsicWidth,
                                drawable.intrinsicHeight)
                        `is`?.close()
                    } catch (e: Exception) {
                        Log.e("sms", "Failed to loaded content $mContentUri", e)
                    }

                } else {
                    try {
                        drawable = ContextCompat.getDrawable(mContext!!, mResourceId ?: 0)
                        drawable!!.setBounds(0, 0, drawable.intrinsicWidth,
                                drawable.intrinsicHeight)
                    } catch (e: Exception) {
                        Log.e("sms", "Unable to find resource: $mResourceId")
                    }

                }
                return drawable!!
            }

        constructor(context: Context?, b: Bitmap, verticalAlignment: Int) : super(verticalAlignment) {
            mContext = context
            mDrawable = if (context != null)
                BitmapDrawable(context.resources, b)
            else
                BitmapDrawable(b)
            val width = mDrawable!!.intrinsicWidth
            val height = mDrawable!!.intrinsicHeight
            mDrawable!!.setBounds(0, 0, if (width > 0) width else 0, if (height > 0) height else 0)
        }

        constructor(d: Drawable, verticalAlignment: Int) : super(verticalAlignment) {
            mDrawable = d
            mDrawable!!.setBounds(0, 0, mDrawable!!.intrinsicWidth,
                    mDrawable!!.intrinsicHeight)
        }

        constructor(context: Context, uri: Uri, verticalAlignment: Int) : super(verticalAlignment) {
            mContext = context
            mContentUri = uri
        }

        constructor(context: Context, @DrawableRes resourceId: Int, verticalAlignment: Int) : super(verticalAlignment) {
            mContext = context
            mResourceId = resourceId
        }
    }

    internal abstract class CustomDynamicDrawableSpan : ReplacementSpan {

        val mVerticalAlignment: Int

        abstract var drawable: Drawable?

        private var cachedDrawable: Drawable? = null
            get() {
                val wr = mDrawableRef
                var d: Drawable? = null
                if (wr != null)
                    d = wr.get()
                if (d == null) {
                    d = drawable
                    if (d != null)
                        mDrawableRef = WeakReference(d)
                }
                return drawable
            }
        private var mDrawableRef: WeakReference<Drawable>? = null

        constructor() {
            mVerticalAlignment = ALIGN_BOTTOM
        }

        constructor(verticalAlignment: Int) {
            mVerticalAlignment = verticalAlignment
        }

        override fun getSize(paint: Paint, text: CharSequence,
                             start: Int, end: Int,
                             fm: Paint.FontMetricsInt?): Int {
            val d = cachedDrawable
            val rect = d?.bounds
            val fontHeight = (paint.fontMetrics.descent - paint.fontMetrics.ascent).toInt()
            if (fm != null) { // this is the fucking code which I waste 3 days
                if (rect!=null&&rect.height() > fontHeight) {
                    if (mVerticalAlignment == ALIGN_TOP) {
                        fm.descent += rect.height() - fontHeight
                    } else if (mVerticalAlignment == ALIGN_CENTER) {
                        fm.ascent -= (rect.height() - fontHeight) / 2
                        fm.descent += (rect.height() - fontHeight) / 2
                    } else if (mVerticalAlignment == ALIGN_BASELINE) {
                        fm.ascent -= rect.height() - fontHeight + fm.descent
                    } else {
                        fm.ascent -= rect.height() - fontHeight
                    }
                }
            }

            return rect?.right?:0
        }

        override fun draw(canvas: Canvas, text: CharSequence,
                          start: Int, end: Int, x: Float,
                          top: Int, y: Int, bottom: Int, paint: Paint) {
            val d = cachedDrawable
            val rect = d?.bounds
            canvas.save()
            val fontHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
            var transY = bottom - (rect?.bottom?:0)
            if (rect!=null&&rect.height() < fontHeight) { // this is the fucking code which I waste 3 days
                if (mVerticalAlignment == ALIGN_BASELINE) {
                    transY -= paint.fontMetricsInt.descent
                } else if (mVerticalAlignment == ALIGN_CENTER) {
                    transY -= ((fontHeight - rect.height()) / 2).toInt()
                } else if (mVerticalAlignment == ALIGN_TOP) {
                    transY -= (fontHeight - rect.height()).toInt()
                }
            } else {
                if (mVerticalAlignment == ALIGN_BASELINE) {
                    transY -= paint.fontMetricsInt.descent
                }
            }
            canvas.translate(x, transY.toFloat())
            d?.draw(canvas)
            canvas.restore()
        }

        companion object {

            val ALIGN_BOTTOM = 0

            val ALIGN_BASELINE = 1

            val ALIGN_CENTER = 2

            val ALIGN_TOP = 3
        }
    }

    companion object {

        const val ALIGN_BOTTOM = 0

        const val ALIGN_BASELINE = 1

        const val ALIGN_CENTER = 2

        const val ALIGN_TOP = 3

        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }
}