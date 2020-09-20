package com.google.android.material.appbar

import android.animation.ValueAnimator
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.google.android.material.animation.AnimationUtils

/**
 * Deprecated the design library has fix the fling problem by the new version
 */
@Deprecated("")
class AppBarFlingFixBehavior : AppBarLayout.Behavior {

    private var mOffsetAnimator: ValueAnimator? = null

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onStartNestedScroll(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: AppBarLayout,
                                     directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        // Return true if we're nested scrolling vertically, and we have scrollable children
        // and the scrolling view is big enough to scroll
        val started = (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
                && child.hasScrollableChildren()
                && parent.height - directTargetChild.height <= child.height)

        if (started && mOffsetAnimator != null) {
            // Cancel any offset animation
            mOffsetAnimator!!.cancel()
        }

        setLastNestedScrollingChildRef(null)
        return started
    }

    private fun setLastNestedScrollingChildRef(o: Any?) {
        try {
            val field = AppBarLayout.Behavior::class.java.getDeclaredField("mLastNestedScrollingChildRef")
            field.isAccessible = true
            field.set(this, o)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onNestedFling(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
                               child: AppBarLayout, target: View, velocityX: Float, velocityY: Float,
                               consumed: Boolean): Boolean {
        var flung = false

        if (!consumed) {
            flung = fling(coordinatorLayout, child, -child.totalScrollRange,
                    0, -velocityY)
        } else {
            if (velocityY < 0) {
                val targetScroll = +child.downNestedPreScrollRange
                animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY)
                flung = true
            } else {
                val targetScroll = -child.upNestedPreScrollRange
                if (topBottomOffsetForScrollingSibling > targetScroll) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY)
                    flung = true
                }
            }
        }

        setWasNestedFlung(flung)
        return flung
    }

    private fun setWasNestedFlung(o: Boolean) {
        try {
            val field = AppBarLayout.Behavior::class.java.getDeclaredField("mWasNestedFlung")
            field.isAccessible = true
            field.set(this, o)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun animateOffsetTo(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
                                child: AppBarLayout, offset: Int, velocity_: Float) {
        var velocity = velocity_
        val distance = Math.abs(topBottomOffsetForScrollingSibling - offset)

        val duration: Int
        velocity = Math.abs(velocity)
        if (velocity > 0) {
            duration = 3 * Math.round(1000 * (distance / velocity))
        } else {
            val distanceRatio = distance.toFloat() / child.height
            duration = ((distanceRatio + 1) * 150).toInt()
        }

        animateOffsetWithDuration(coordinatorLayout, child, offset, duration)
    }

    private fun animateOffsetWithDuration(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
                                          child: AppBarLayout, offset: Int, duration: Int) {
        val currentOffset = topBottomOffsetForScrollingSibling
        if (currentOffset == offset) {
            if (mOffsetAnimator != null && mOffsetAnimator!!.isRunning) {
                mOffsetAnimator!!.cancel()
            }
            return
        }

        if (mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator()
            mOffsetAnimator!!.interpolator = AnimationUtils.DECELERATE_INTERPOLATOR
            mOffsetAnimator!!.addUpdateListener { animator ->
                setHeaderTopBottomOffset(coordinatorLayout, child,
                        animator.animatedValue as Int)
            }
        } else {
            mOffsetAnimator!!.cancel()
        }

        mOffsetAnimator!!.duration = Math.min(duration,
            MAX_OFFSET_ANIMATION_DURATION
        ).toLong()
        mOffsetAnimator!!.setIntValues(currentOffset, offset)
        mOffsetAnimator!!.start()
    }

    @VisibleForTesting
    internal override fun isOffsetAnimatorRunning(): Boolean {
        return mOffsetAnimator != null && mOffsetAnimator!!.isRunning
    }

    companion object {
        private val MAX_OFFSET_ANIMATION_DURATION = 600 // ms
    }

}