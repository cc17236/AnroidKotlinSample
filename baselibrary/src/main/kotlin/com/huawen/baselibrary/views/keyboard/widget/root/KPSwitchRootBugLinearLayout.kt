package com.huawen.baselibrary.views.keyboard.widget.root

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.bug.AndroidBugInsetsLinearLayout
import android.util.AttributeSet
import android.view.View
import com.huawen.baselibrary.views.keyboard.handler.KPSwitchRootLayoutHandler

class KPSwitchRootBugLinearLayout : AndroidBugInsetsLinearLayout {

    private var conflictHandler: KPSwitchRootLayoutHandler? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
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