package com.huawen.baselibrary.views

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/**
 * 预览图片PagerSnapHelper
 * Created by zhouL on 2018/11/13.
 */
class ViewPagerSnapHelper : PagerSnapHelper() {

    /** 监听器  */
    private var mListener: OnPageChangeListener? = null

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        recyclerView?.addOnScrollListener(RecyclerViewPageChangeListenerHelper(this, object : OnPageChangeListener {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mListener?.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mListener?.onScrolled(recyclerView, dx, dy)
            }

            override fun onPageSelected(position: Int) {
                mListener?.onPageSelected(position)
            }
        }))
    }

    /**
     * 设置选中监听器
     * @param listener 监听器
     */
    fun setOnPageChangeListener(listener: OnPageChangeListener) {
        mListener = listener
    }

    interface OnPageChangeListener {
        fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)

        fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)

        fun onPageSelected(position: Int)
    }


    private class RecyclerViewPageChangeListenerHelper(
        private val snapHelper: SnapHelper,
        private val onPageChangeListener: ViewPagerSnapHelper.OnPageChangeListener?
    ) : RecyclerView.OnScrollListener() {
        private var oldPosition = -1//防止同一Position多次触发

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onPageChangeListener?.onScrolled(recyclerView, dx, dy)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            var position = 0
            val layoutManager = recyclerView.layoutManager
            //获取当前选中的itemView
            val view = snapHelper.findSnapView(layoutManager)
            if (view != null) {
                //获取itemView的position
                position = layoutManager!!.getPosition(view)
            }
            if (onPageChangeListener != null) {
                onPageChangeListener.onScrollStateChanged(recyclerView, newState)
                //newState == RecyclerView.SCROLL_STATE_IDLE 当滚动停止时触发防止在滚动过程中不停触发
                if (newState == RecyclerView.SCROLL_STATE_IDLE && oldPosition != position) {
                    oldPosition = position
                    onPageChangeListener.onPageSelected(position)
                }
            }
        }
    }

}