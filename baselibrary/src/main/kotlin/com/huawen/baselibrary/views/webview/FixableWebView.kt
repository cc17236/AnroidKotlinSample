package com.huawen.baselibrary.views.webview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.annotation.RequiresApi

open class FixableWebView : WebView, FixScroller {

    private var mOnScrollChangeListener: OnFixScrollChangeListener? = null


    private var downX = 0f
    private var downY = 0f

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, privateBrowsing: Boolean) : super(
        context,
        attrs,
        defStyleAttr,
        privateBrowsing
    ) {
    }


    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        val content = computeVerticalScrollRange().toFloat()
        val now = (height + scrollY).toFloat()
        if (mOnScrollChangeListener != null)
            if (Math.abs(content - now) < 1) {
                if (scrollY == 0) {//webview不超过屏幕高度
                    mOnScrollChangeListener!!.onPageTop()
                } else
                //处于底端
                    mOnScrollChangeListener!!.onPageEnd()
            } else if (scrollY == 0) {
                //处于顶端
                mOnScrollChangeListener!!.onPageTop()
            } else {
                mOnScrollChangeListener!!.onScrollChanged()
            }
        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    /**
     * 触屏事件
     *
     * @param event
     * @return
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //        if (!canScrollVertically(1) && !canScrollVertically(-1)) {
        var action = ""
        //在触发时回去到起始坐标
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (scrollY == 0) {
                    if (mOnScrollChangeListener != null)
                        mOnScrollChangeListener!!.onPageTop()
                }
                //将按下时的坐标存储
                downX = x
                downY = y
            }
            MotionEvent.ACTION_UP -> {
                downX = 0f
                downY = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                //获取到距离差
                val dx = x - downX
                val dy = y - downY
                //防止是按下也判断
                if (Math.abs(dx) > 8 && Math.abs(dy) > 8) {
                    //通过距离差判断方向
                    val orientation = getOrientation(dx, dy).toChar()
                    when (orientation) {
                        'r' -> action = "右"
                        'l' -> action = "左"
                        't' -> action = "上"
                        'b' -> action = "下"
                    }
                    if (action == "下") {
                        if (mOnScrollChangeListener != null)
                            mOnScrollChangeListener!!.onPageTop()
                    }
                }
            }
        }
        //        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * 根据距离差判断 滑动方向
     *
     * @param dx X轴的距离差
     * @param dy Y轴的距离差
     * @return 滑动的方向
     */
    private fun getOrientation(dx: Float, dy: Float): Int {
        return if (Math.abs(dx) > Math.abs(dy)) {
            //X轴移动
            (if (dx > 0) 'r' else 'l').toInt()
        } else {
            //Y轴移动
            (if (dy > 0) 'b' else 't').toInt()
        }
    }


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //        // webview的高度
        //        float webcontent = getContentHeight() * getScale();
        //        // 当前webview的高度
        //        float webnow = getHeight() + getScrollY();
        //        if (mOnScrollChangeListener != null)
        //            if (Math.abs(webcontent - webnow) < 1) {
        //                if (getScrollY() == 0) {//webview不超过屏幕高度
        //                    mOnScrollChangeListener.onPageTop();
        //                    return;
        //                }
        //                //处于底端
        //                mOnScrollChangeListener.onPageEnd();
        //            } else if (getScrollY() == 0) {
        //                //处于顶端
        //                mOnScrollChangeListener.onPageTop();
        //            } else {
        //                mOnScrollChangeListener.onScrollChanged();
        //            }
    }

    override fun setOnScrollChangeListener(listener: OnFixScrollChangeListener) {
        this.mOnScrollChangeListener = listener
    }


}