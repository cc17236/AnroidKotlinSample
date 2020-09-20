package com.huawen.baselibrary.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup


/**
 * @作者: #Administrator #
 *@日期: #2018/9/12 #
 *@时间: #2018年09月12日 11:27 #
 *@File:Kotlin Class
 */
class SwipeProgressView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var paint: Paint

    private var touchSlop = 0
    private var clickSlop = 0

    init {
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        clickSlop = ViewConfiguration.getTapTimeout()
    }

    private var cursor: Float = 0f

    var leftColor: Int = Color.parseColor("#D6ECFF")
    var rightColor: Int = Color.WHITE

    fun setProgress(progress: Float) {
        cursor = progress
        invalidate()
    }

    private val leftRectF = RectF()
    private val rightRectF = RectF()

    private var mWidth = 0
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        leftRectF.left = 0f
        rightRectF.right = 0f
        leftRectF.top = top.toFloat()
        leftRectF.bottom = bottom.toFloat()
        rightRectF.top = top.toFloat()
        rightRectF.bottom = bottom.toFloat()
        mWidth = width
    }

    companion object {
        interface SwipeListener {
            fun onSwipe(progress: Float)
            fun onSwipeBegin()
            fun onSwipeEnd(pos: Float)
        }
    }

    private var lis: SwipeListener? = null
    fun setOnSwipeListener(lis: SwipeListener) {
        this.lis = lis
    }

    private var isEnable = true


    fun setEnable(enable: Boolean) {
        isEnable = enable
    }

    private var downX = 0f
    private var downTime = 0.toLong()

    private var swipeBegin = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                swipeBegin = false
                downX = event.x
                downTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                (parent as? ViewGroup)?.requestDisallowInterceptTouchEvent(false)
                if (swipeBegin) {
                    val progress = ((event.x) / (mWidth.toFloat())) * 100.toFloat()
                    if (progress >= 0 && progress <= 100) {
                        if (x > downX) {//向右滑
                            lis?.onSwipeEnd(progress)
                        } else {//向左滑
                            lis?.onSwipeEnd(progress)
                        }
                    }

                } else {
                    if (clickSlop < System.currentTimeMillis() - downTime) {
                        val progress = ((event.x) / (mWidth.toFloat())) * 100.toFloat()
                        if (progress >= 0 && progress <= 100) {
                            lis?.onSwipeBegin()
                            lis?.onSwipe(progress)
                            lis?.onSwipeEnd(progress)
                        }

                    }
                }
                downX = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                if (isEnable) {
                    if (Math.abs(x - downX) > touchSlop) {
                        (parent as? ViewGroup)?.requestDisallowInterceptTouchEvent(true)
                        if (!swipeBegin) {
                            lis?.onSwipeBegin()
                        }

                        swipeBegin = true
                        val progress = ((event.x) / (mWidth.toFloat())) * 100.toFloat()
                        if (progress >= 0 && progress <= 100) {
                            if (x > downX) {//向右滑
                                setProgress(progress)
                                lis?.onSwipe(progress)
                            } else {//向左滑
                                setProgress(progress)
                                lis?.onSwipe(progress)
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.measuredWidth
        val bottom = top + view.measuredHeight
        //view.isClickable() &&
        return if (y >= top && y <= bottom && x >= left
                && x <= right) {
            true
        } else false
    }

    override fun onDraw(canvas: Canvas?) {

        val center = ((cursor / 100f) * mWidth.toFloat()).toInt()
        leftRectF.right = leftRectF.left + center
        rightRectF.left = rightRectF.right - center
        if (center > 0f) {
            paint.color = leftColor
            canvas?.drawRect(leftRectF, paint)
        }
        paint.color = rightColor
        canvas?.drawRect(rightRectF, paint)

    }


}