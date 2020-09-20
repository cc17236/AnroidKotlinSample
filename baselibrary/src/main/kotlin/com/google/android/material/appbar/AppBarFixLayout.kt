package com.google.android.material.appbar

import android.content.Context
import android.util.AttributeSet

class AppBarFixLayout : AppBarLayout, AppBarLayout.OnOffsetChangedListener {

    var state: State? = null
        private set
    private var onStateChangeListener: OnStateChangeListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (layoutParams !is androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams || parent !is androidx.coordinatorlayout.widget.CoordinatorLayout) {
            throw IllegalStateException(
                    "AppBarFixLayout must be a direct child of CoordinatorLayout.")
        }
        addOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (verticalOffset == 0) {
            if (onStateChangeListener != null && state != State.EXPANDED) {
                onStateChangeListener!!.onStateChange(State.EXPANDED)
            }
            state = State.EXPANDED
        } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
            if (onStateChangeListener != null && state != State.COLLAPSED) {
                onStateChangeListener!!.onStateChange(State.COLLAPSED)
            }
            state = State.COLLAPSED
        } else {
            if (onStateChangeListener != null && state != State.IDLE) {
                onStateChangeListener!!.onStateChange(State.IDLE)
            }
            state = State.IDLE
        }
    }

    fun setOnStateChangeListener(listener: OnStateChangeListener) {
        this.onStateChangeListener = listener
    }

    interface OnStateChangeListener {
        fun onStateChange(toolbarChange: State)
    }

    enum class State {
        COLLAPSED,
        EXPANDED,
        IDLE
    }
}