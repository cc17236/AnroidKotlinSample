package com.huawen.baselibrary.views.wave

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.View

class WaveRadar : View, WaveDelegate {
    private val delegating = DelegatingHelper(this)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private var aspectRatio = 0.4f
    fun setCentralBoxRatio(@FloatRange(from = 0.toDouble(), to = 1.toDouble()) f: Float) {
        aspectRatio = f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minw = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = Math.max(minw, View.MeasureSpec.getSize(widthMeasureSpec))
        val h = View.MeasureSpec.getSize(heightMeasureSpec)
        if (w == 0 || h == 0) return
        setMeasuredDimension(w, h)

        val boxWidth = w * aspectRatio
        val boxHeight = h * aspectRatio

        Log.e("Delegate", "boxWidth==>${boxWidth}   boxHeight===>${boxHeight}")
        delegating.setBoxRect(
            ((w - boxWidth) / 2.toFloat()).toInt(),
            ((h - boxHeight) / 2.toFloat()).toInt(),
            w - ((w - boxWidth) / 2.toFloat()).toInt(),
            h - ((h - boxHeight) / 2.toFloat()).toInt()
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        measure(0, 0)
    }

    override fun start() {
        delegating.start()
    }

    override fun end() {
        delegating.end()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null)
            delegating.draw(canvas)
    }

    override fun postRefresh() {
        postInvalidate()
    }

}