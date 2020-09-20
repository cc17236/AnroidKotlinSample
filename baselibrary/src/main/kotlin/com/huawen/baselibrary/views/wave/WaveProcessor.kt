package com.huawen.baselibrary.views.wave

import android.graphics.*

class WaveProcessor : WaveDelegate {
    private var level = 0
    private val boxRect = Rect()
    private val normalStep = 30
    private var stepByStep = normalStep
    private var totallyStep = 4
    private val map = hashMapOf<Int, Int>()
    private val mPaint = Paint()

    init {
        mPaint.color = Color.BLUE
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 5f
        mPaint.isAntiAlias = true
    }

    override fun start() {
        level = 0
        stepByStep = normalStep
        map.clear()
        map[0] = normalStep
    }

    override fun end() {
        level = 0
        stepByStep = normalStep
        map.clear()
    }

    override fun draw(canvas: Canvas) {
        for (i in 0 until level) {
            val gap = (map[i] ?: 0)
            val startXOrigin = boxRect.left - gap
            var startY = boxRect.top - gap
            val endXOrigin = boxRect.right + gap
            var endY = boxRect.bottom + gap
            val width = endXOrigin - startXOrigin
            val height = endY - startY

            val startX=startXOrigin+startXOrigin*0.2f
            val endX=endXOrigin-endXOrigin*0.2f
            if (height >= width) {
                val v = ((height - width) / 2)+(width*0.05f).toInt()
                endY -= v
                startY += v
            }
            val rectF = RectF(startXOrigin.toFloat(), startY.toFloat(), endXOrigin.toFloat(), endY.toFloat())
            var rx = (endX - startX) //resolveCorner((endX - startX).toFloat(), i)
            rx=(rx*0.65f)
            val ry = rx*0.85//endY - startY //resolveCorner((endY - startY).toFloat(), i)
            mPaint.color = resolveColorLevel(i)
            canvas.drawRoundRect(rectF, rx.toFloat(), ry.toFloat(), mPaint)
        }
    }

    private fun resolveCorner(range: Float, level: Int): Float {
        val fraction = (totallyStep - level.toFloat()) / totallyStep.toFloat()
        val rlt = (range * 0.7f) * (20f * fraction)
        return rlt
    }


    private fun resolveColorLevel(level: Int): Int {
        val fraction = (totallyStep - level.toFloat()) / totallyStep.toFloat()
        val color = changeAlpha(Color.parseColor("#FF5BE2"), fraction)
        return color
    }

    fun changeAlpha(color: Int, fraction: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = (Color.alpha(color) * fraction).toInt()
        return Color.argb(alpha, red, green, blue)
    }

    override fun postRefresh() = Unit
    fun setLevel(count: Int, left: Int, top: Int, right: Int, bottom: Int) {
        if (count > totallyStep) return
        level = count
        if (count > 0) {
            stepByStep += (normalStep + count * 10)
            map[count] = stepByStep
        } else {
            map.clear()
            stepByStep = normalStep
            map[count] = stepByStep
        }
        boxRect.setEmpty()
        boxRect.set(left, top, right, bottom)
    }

}