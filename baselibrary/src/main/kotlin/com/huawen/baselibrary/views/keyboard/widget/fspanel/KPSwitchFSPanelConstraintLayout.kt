package com.huawen.baselibrary.views.keyboard.widget.fspanel

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.util.AttributeSet
import android.view.Window
import com.huawen.baselibrary.utils.ScreenUtils
import com.huawen.baselibrary.utils.utils.ScreenUtil
import com.huawen.baselibrary.views.keyboard.IFSPanelConflictLayout
import com.huawen.baselibrary.views.keyboard.IPanelHeightTarget
import com.huawen.baselibrary.views.keyboard.handler.KPSwitchFSPanelLayoutHandler
import com.huawen.baselibrary.views.keyboard.util.ViewUtil

class KPSwitchFSPanelConstraintLayout : ConstraintLayout, IPanelHeightTarget, IFSPanelConflictLayout {
    override var height: Int? = 0

    private var panelHandler: KPSwitchFSPanelLayoutHandler? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        panelHandler = KPSwitchFSPanelLayoutHandler(this)
    }

    override fun refreshHeight(panelHeight: Int) {
        ViewUtil.refreshHeight(this, panelHeight)
//        val constraintLayout= parent as? ConstraintLayout ?: return
//        val set=ConstraintSet()
//        set.clone(constraintLayout)
//        val sh=ScreenUtils.screenHeight
//        set.setVerticalWeight(id,)
//        set.applyTo(constraintLayout)
    }

    override fun onKeyboardShowing(showing: Boolean) {
        panelHandler?.onKeyboardShowing(showing)
    }


    override fun recordKeyboardStatus(window: Window) {
        panelHandler?.recordKeyboardStatus(window)
    }

}