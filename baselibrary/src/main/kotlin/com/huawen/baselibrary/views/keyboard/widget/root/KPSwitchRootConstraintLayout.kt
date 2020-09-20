package com.huawen.baselibrary.views.keyboard.widget.root

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.huawen.baselibrary.views.keyboard.handler.KPSwitchRootLayoutHandler

class KPSwitchRootConstraintLayout : ConstraintLayout {
    private var conflictHandler: KPSwitchRootLayoutHandler? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        conflictHandler = KPSwitchRootLayoutHandler(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        conflictHandler?.handleBeforeMeasure(View.MeasureSpec.getSize(widthMeasureSpec),
                View.MeasureSpec.getSize(heightMeasureSpec))
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}