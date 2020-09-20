package com.huawen.baselibrary.adapter.listener

import android.os.Build
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.huawen.baselibrary.adapter.BaseQuickAdapter
import com.huawen.baselibrary.adapter.BaseQuickAdapter.Companion.EMPTY_VIEW
import com.huawen.baselibrary.adapter.BaseQuickAdapter.Companion.FOOTER_VIEW
import com.huawen.baselibrary.adapter.BaseQuickAdapter.Companion.HEADER_VIEW
import com.huawen.baselibrary.adapter.BaseQuickAdapter.Companion.LOADING_VIEW
import com.huawen.baselibrary.adapter.BaseViewHolder


/**
 * Created by AllenCoder on 2016/8/03.
 *
 *
 * This can be useful for applications that wish to implement various forms of click and longclick and childView click
 * manipulation of item views within the RecyclerView. SimpleClickListener may intercept
 * a touch interaction already in progress even if the SimpleClickListener is already handling that
 * gesture stream itself for the purposes of scrolling.
 *
 * @see RecyclerView.OnItemTouchListener
 */
abstract class SimpleClickListener : RecyclerView.OnItemTouchListener {

    private var mGestureDetector: GestureDetectorCompat? = null
    private var recyclerView: RecyclerView? = null
    protected var baseQuickAdapter: BaseQuickAdapter<*, *>? = null
    private var mIsPrepressed = false
    private var mIsShowPress = false
    private var mPressedView: View? = null

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (recyclerView == null) {
            this.recyclerView = rv
            this.baseQuickAdapter = recyclerView!!.adapter as BaseQuickAdapter<*, *>?
            mGestureDetector =
                GestureDetectorCompat(recyclerView!!.context, ItemTouchHelperGestureListener(recyclerView!!))
        } else if (recyclerView !== rv) {
            this.recyclerView = rv
            this.baseQuickAdapter = recyclerView!!.adapter as BaseQuickAdapter<*, *>?
            mGestureDetector =
                GestureDetectorCompat(recyclerView!!.context, ItemTouchHelperGestureListener(recyclerView!!))
        }
        if (!mGestureDetector!!.onTouchEvent(e) && e.actionMasked == MotionEvent.ACTION_UP && mIsShowPress) {
            if (mPressedView != null) {
                val vh = recyclerView!!.getChildViewHolder(mPressedView!!) as? BaseViewHolder
                if (vh == null || !isHeaderOrFooterView(vh.itemViewType)) {
                    mPressedView!!.isPressed = false
                }
            }
            mIsShowPress = false
            mIsPrepressed = false
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        mGestureDetector!!.onTouchEvent(e)
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    private inner class ItemTouchHelperGestureListener internal constructor(private val recyclerView: RecyclerView) :
        GestureDetector.OnGestureListener {

        override fun onDown(e: MotionEvent): Boolean {
            mIsPrepressed = true
            mPressedView = recyclerView.findChildViewUnder(e.x, e.y)
            return false
        }

        override fun onShowPress(e: MotionEvent) {
            if (mIsPrepressed && mPressedView != null) {
                //                mPressedView.setPressed(true);
                mIsShowPress = true
            }
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (mIsPrepressed && mPressedView != null) {
                if (recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    return false
                }
                val pressedView = mPressedView
                val vh = recyclerView.getChildViewHolder(pressedView!!) as BaseViewHolder

                if (isHeaderOrFooterPosition(vh.layoutPosition)) {
                    return false
                }
                val childClickViewIds = vh.childClickViewIds
                val nestViewIds = vh.nestViews
                if (childClickViewIds != null && childClickViewIds.size > 0) {
                    for (childClickViewId in childClickViewIds) {
                        val childView = pressedView.findViewById<View>(childClickViewId)
                        if (childView != null) {
                            if (inRangeOfView(childView, e) && childView.isEnabled) {
                                if (nestViewIds != null && nestViewIds.contains(childClickViewId)) {
                                    return false
                                }
                                setPressViewHotSpot(e, childView)
                                childView.isPressed = true
                                onItemChildClick(
                                    baseQuickAdapter,
                                    childView,
                                    vh.layoutPosition - baseQuickAdapter!!.headerLayoutCount
                                )
                                resetPressedView(childView)
                                return true
                            } else {
                                childView.isPressed = false
                            }
                        }
                    }
                    setPressViewHotSpot(e, pressedView)
                    mPressedView!!.isPressed = true
                    for (childClickViewId in childClickViewIds) {
                        val childView = pressedView.findViewById<View>(childClickViewId)
                        if (childView != null) {
                            childView.isPressed = false
                        }
                    }
                    onItemClick(baseQuickAdapter, pressedView, vh.layoutPosition - baseQuickAdapter!!.headerLayoutCount)
                } else {
                    setPressViewHotSpot(e, pressedView)
                    mPressedView!!.isPressed = true
                    if (childClickViewIds != null && childClickViewIds.size > 0) {
                        for (childClickViewId in childClickViewIds) {
                            val childView = pressedView.findViewById<View>(childClickViewId!!)
                            if (childView != null) {
                                childView.isPressed = false
                            }
                        }
                    }
                    onItemClick(baseQuickAdapter, pressedView, vh.layoutPosition - baseQuickAdapter!!.headerLayoutCount)
                }
                resetPressedView(pressedView)

            }
            return true
        }

        private fun resetPressedView(pressedView: View?) {
            pressedView?.postDelayed({
                if (pressedView != null) {
                    pressedView.isPressed = false
                }
            }, 50)

            mIsPrepressed = false
            mPressedView = null
        }

        override fun onLongPress(e: MotionEvent) {
            var isChildLongClick = false
            if (recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                return
            }
            if (mIsPrepressed && mPressedView != null) {
                mPressedView!!.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                val vh = recyclerView.getChildViewHolder(mPressedView!!) as BaseViewHolder
                if (!isHeaderOrFooterPosition(vh.layoutPosition)) {
                    val longClickViewIds = vh.itemChildLongClickViewIds
                    val nestViewIds = vh.nestViews
                    if (longClickViewIds != null && longClickViewIds.size > 0) {
                        for (longClickViewId in longClickViewIds) {
                            val childView = mPressedView!!.findViewById<View>(longClickViewId)
                            if (inRangeOfView(childView, e) && childView.isEnabled) {
                                if (nestViewIds != null && nestViewIds.contains(longClickViewId)) {
                                    isChildLongClick = true
                                    break
                                }
                                setPressViewHotSpot(e, childView)
                                onItemChildLongClick(
                                    baseQuickAdapter,
                                    childView,
                                    vh.layoutPosition - baseQuickAdapter!!.headerLayoutCount
                                )
                                childView.isPressed = true
                                mIsShowPress = true
                                isChildLongClick = true
                                break
                            }
                        }
                    }
                    if (!isChildLongClick) {
                        onItemLongClick(
                            baseQuickAdapter,
                            mPressedView!!,
                            vh.layoutPosition - baseQuickAdapter!!.headerLayoutCount
                        )
                        setPressViewHotSpot(e, mPressedView)
                        mPressedView!!.isPressed = true
                        if (longClickViewIds != null) {
                            for (longClickViewId in longClickViewIds) {
                                val childView = mPressedView!!.findViewById<View>(longClickViewId)
                                if (childView != null) {
                                    childView.isPressed = false
                                }
                            }
                        }
                        mIsShowPress = true
                    }
                }
            }
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    }

    private fun setPressViewHotSpot(e: MotionEvent, mPressedView: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * when   click   Outside the region  ,mPressedView is null
             */
            if (mPressedView != null && mPressedView.background != null) {
                mPressedView.background.setHotspot(e.rawX, e.y - mPressedView.y)
            }
        }
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param view     The view within the AdapterView that was clicked (this
     * will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    abstract fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int)

    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param view     The view whihin the AbsListView that was clicked
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    abstract fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int)

    abstract fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int)

    abstract fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int)

    fun inRangeOfView(view: View?, ev: MotionEvent): Boolean {
        val location = IntArray(2)
        if (view == null || !view.isShown) {
            return false
        }
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        return if (ev.rawX < x
            || ev.rawX > x + view.width
            || ev.rawY < y
            || ev.rawY > y + view.height
        ) {
            false
        } else true
    }

    private fun isHeaderOrFooterPosition(position: Int): Boolean {
        /**
         * have a headview and EMPTY_VIEW FOOTER_VIEW LOADING_VIEW
         */
        if (baseQuickAdapter == null) {
            if (recyclerView != null) {
                baseQuickAdapter = recyclerView!!.adapter as BaseQuickAdapter<*, *>?
            } else {
                return false
            }
        }
        val type = baseQuickAdapter!!.getItemViewType(position)
        return type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW
    }

    private fun isHeaderOrFooterView(type: Int): Boolean {
        return type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW
    }

    companion object {
        var TAG = "SimpleClickListener"
    }
}


