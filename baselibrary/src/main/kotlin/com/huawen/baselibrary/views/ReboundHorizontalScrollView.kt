package com.huawen.baselibrary.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.HorizontalScrollView


/**
 * Created by liwenfei on 2015/12/23.
 */
class ReboundHorizontalScrollView : HorizontalScrollView {
    /**
     * 经过延迟后移动的实际距离
     */
    private var offset = 0
    /**
     * ScrollView的子View， 也是ScrollView的唯一一个子View
     */
    private var contentView: View? = null
    /**
     * 用于记录正常的布局位置
     */
    private val originalRect = Rect()
    /**
     * 手指按下时记录是否可以继续右拉
     */
    private var canPullRight = false
    /**
     * 手指按下时记录是否可以继续左拉
     */
    private var canPullLeft = false
    /**
     * 手指按下时的X值, 用于在移动时计算移动距离
     * 如果按下时不能左拉和右拉， 会在手指移动时更新为当前手指的X值
     */
    private var startX: Float = 0.toFloat()
    /**
     * 在手指滑动的过程中记录是否移动了布局
     */
    private var isMoved = false
    /**
     * 是否触发左侧的事件
     */
    private var isTriggerLeft = false
    /**
     * 是否触发右侧的事件
     */
    private var isTriggerRight = false
    /**
     * 回弹事件
     */
    private var mOnReboundListener: OnReboundListener? = null

    /**
     * 是否滚动到了顶部
     *
     * @return
     */
    private val isCanPullRight: Boolean
        get() = scrollX == 0 || contentView!!.width < width + scrollX

    /**
     * 是否滚动到了底部
     *
     * @return
     */
    private val isCanPullLeft: Boolean
        get() = contentView!!.width <= width + scrollX

    private var function1: ((destinyX: Int, idle: Boolean) -> Unit)? = null


    fun isfinishScroll(): Boolean {
        var isfinish = false
        val scrollview = HorizontalScrollView::class.java
        try {
            //获取Scrollview里的OverScroller这个字段
            val scrollField = scrollview.getDeclaredField("mScroller")
            scrollField.isAccessible = true
            //获取到Scrollview里OverScroller的成员变量值
            val scroller = scrollField.get(this)
            //获取scroller的类类型
            val overscroller = scrollField.type
            //获取到OverScroller中isFinished（）方法
            val finishField = overscroller.getMethod("isFinished")
            finishField.isAccessible = true
            //调用isFinished（）方法
            isfinish = finishField.invoke(scroller) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isfinish
    }


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * 当View中所有的子控件均被映射成xml后触发
     */
    override fun onFinishInflate() {
        if (childCount > 0) {
            contentView = getChildAt(0)
        }
        super.onFinishInflate()
    }

    /**
     * 是ViewGroup中子View的布局方法
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (null == contentView)
            return
        //HorizontalScrollView中的唯一子控件的位置信息, 这个位置信息在整个控件的生命周期中保持不变
        originalRect.set(contentView!!.left, contentView!!.top, contentView!!.right,
                contentView!!.bottom)
    }


    private var mCurrentlyTouching: Boolean = false
    private var mCurrentlyFling: Boolean = false

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: ReboundHorizontalScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
        fun onEndScroll(x: Int)
    }


    init {
        scrollViewListener = object : ScrollViewListener {
            override fun onScrollChanged(scrollView: ReboundHorizontalScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
                var cpX = x
                if (cpX < 0) {
                    cpX = 0
                } else if (cpX > computeScrollRange()) {
                    cpX = computeScrollRange()
                }
                var value = cpX - paddingLeft
                if (value < 0) value = 0
                function1?.invoke(value, false)
            }

            override fun onEndScroll(x: Int) {
                var cpX = x
                if (cpX < 0) {
                    cpX = 0
                } else if (cpX > computeScrollRange()) {
                    cpX = computeScrollRange()
                }
                var value = cpX - paddingLeft
                if (value < 0) value = 0
                function1?.invoke(value, true)
            }

        }
    }


    private val scrollViewListener: ScrollViewListener? = null

    override fun fling(velocityY: Int) {
        super.fling(velocityY)
        mCurrentlyFling = true
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        scrollViewListener?.onScrollChanged(this, x, y, oldx, oldy)
        if (Math.abs(x - oldx) < 2 || x >= measuredWidth || x == 0) {
            if (!mCurrentlyTouching) {
                if (scrollViewListener != null) {
                    Log.d("SCROLL WITH LISTENER", "-- OnEndScroll")
                    scrollViewListener.onEndScroll(x)
                }
            }
            mCurrentlyFling = false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mCurrentlyTouching = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrentlyTouching = false
                if (!mCurrentlyFling) {
                    if (scrollViewListener != null) {
                        Log.d("SCROLL WITH LISTENER", "-- OnEndScroll")
                        scrollViewListener.onEndScroll(ev.x.toInt())
                    }
                }
            }
            else -> {
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mCurrentlyTouching = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrentlyTouching = false
                if (!mCurrentlyFling) {
                    if (scrollViewListener != null) {
                        Log.d("SCROLL WITH LISTENER", "-- OnEndScroll")
                        scrollViewListener.onEndScroll(ev.x.toInt())
                    }
                }
            }

            else -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }


    /**
     * 在触摸事件中, 处理左拉和右拉的逻辑
     *
     * @param ev
     * @return
     */
//    private var isIdle = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (contentView == null) {
            return super.dispatchTouchEvent(ev)
        }
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
//                isIdle = true
                //判断是否可以右拉和左拉
                canPullRight = isCanPullRight
                canPullLeft = isCanPullLeft
                //记录按下时的X值
                startX = ev.x
            }

            MotionEvent.ACTION_UP -> {
                if (!isMoved)
                    return super.dispatchTouchEvent(ev)
                // 开启动画
                val anim = TranslateAnimation(contentView!!.left.toFloat(), originalRect.left.toFloat(), 0f, 0f)
                anim.duration = ANIM_TIME.toLong()
                contentView!!.startAnimation(anim)

                // 设置回到正常的布局位置
                contentView!!.layout(originalRect.left, originalRect.top,
                        originalRect.right, originalRect.bottom)

                if (isTriggerLeft) {
                    if (offset.toFloat() / width > 0.3) {
                        if (null != mOnReboundListener) {
                            mOnReboundListener!!.OnLeftRebound()
                        }
                    }
                }

                if (isTriggerRight) {
                    if (offset < 0 && Math.abs(offset.toFloat() / width) > 0.3) { //绝对值
                        if (null != mOnReboundListener) {
                            mOnReboundListener!!.OnRightRebound()
                        }
                    }
                }

                //将标志位设回false
                canPullRight = false
                canPullLeft = false
                isMoved = false
                isTriggerLeft = false
                isTriggerRight = false
            }

            MotionEvent.ACTION_MOVE -> {
                //在移动的过程中， 既没有滚动到可以右拉的程度， 也没有滚动到可以左拉的程度
                if (!canPullLeft && !canPullRight) {

                    startX = ev.x
                    canPullRight = isCanPullRight
                    canPullLeft = isCanPullLeft
                    isTriggerLeft = false
                    isTriggerRight = false
//                    isIdle = false
//                    var value = computeHorizontalScrollOffset() - paddingLeft
//                    if (value < 0) value = 0
//                    function1?.invoke(value, false)
                    return super.dispatchTouchEvent(ev)
                }
                //计算手指移动的距离
                val nowX = ev.x
                val deltaX = (nowX - startX).toInt()
                //是否应该移动布局
                val shouldMove = (canPullLeft && deltaX < 0  //可以左拉， 并且手指向左移动

                        || canPullRight && deltaX > 0    //可以右拉， 并且手指向右移动

                        || canPullLeft && canPullRight) //既可以上拉也可以下拉（这种情况出现在ScrollView包裹的控件比ScrollView还小）
                if (shouldMove) {
//                    isIdle=true
                    //计算偏移量
                    offset = (deltaX * MOVE_FACTOR).toInt()

                    //随着手指的移动而移动布局
                    contentView!!.layout(originalRect.left + offset, originalRect.top,
                            originalRect.right + offset, originalRect.bottom)
                    isMoved = true  //记录移动了布局
                    //
                    if (canPullLeft && !canPullRight) {
                        isTriggerRight = true
                    }

                    if (canPullRight && !canPullLeft) {
                        isTriggerLeft = true
                    }

                    if (canPullRight && canPullRight) {
                        if (offset > 0) {
                            isTriggerLeft = true
                        } else {
                            isTriggerRight = true
                        }
                    }
                }
//                else{
//                    isIdle = false
//                    var value = computeHorizontalScrollOffset() - paddingLeft
//                    if (value < 0) value = 0
//                    function1?.invoke(value, false)
//                }
            }

            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun computeScrollOffset(): Int {
        return computeHorizontalScrollOffset()
    }

    fun computeScrollRange(): Int {
        return computeHorizontalScrollRange()
    }

    fun setOnReboundListtener(listener: OnReboundListener) {
        mOnReboundListener = listener
    }

    fun setOnScrollListener(function: (destinyX: Int, idle: Boolean) -> Unit) {
        function1 = function
    }

    interface OnReboundListener {
        fun OnLeftRebound()

        fun OnRightRebound()
    }

    companion object {

        private val TAG = ReboundHorizontalScrollView::class.java.simpleName

        /**
         * 目的是达到一个延迟的效果
         */
        private val MOVE_FACTOR = 0.5f
        /**
         * 松开手指后, 界面回到正常位置需要的动画时间
         */
        private val ANIM_TIME = 300
    }
}
