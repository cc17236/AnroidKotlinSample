package com.huawen.baselibrary.views

import android.content.Context
import android.graphics.PointF
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import android.util.DisplayMetrics

/**
 * 控制滑动速度的LinearLayoutManager
 */
class ScrollSpeedLinearLayoutManger(private val contxt: Context) : androidx.recyclerview.widget.LinearLayoutManager(contxt) {
    private var MILLISECONDS_PER_INCH = 0.03f
    init {
        setSpeedFast()
    }

    override fun smoothScrollToPosition(recyclerView: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State?, position: Int) {
        val linearSmoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@ScrollSpeedLinearLayoutManger
                        .computeScrollVectorForPosition(targetPosition)
            }

            //This returns the milliseconds it takes to
            //scroll one pixel.
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.density
                //返回滑动一个pixel需要多少毫秒
            }

        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }


    fun setSpeedSlow() {
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = contxt.resources.displayMetrics.density * 0.3f
    }

    fun setSpeedFast() {
        MILLISECONDS_PER_INCH = contxt.resources.displayMetrics.density * 0.01f
    }
}