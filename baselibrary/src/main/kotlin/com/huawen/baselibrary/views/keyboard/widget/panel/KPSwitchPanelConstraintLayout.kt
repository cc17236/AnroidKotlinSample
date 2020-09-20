package com.huawen.baselibrary.views.keyboard.widget.panel

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.huawen.baselibrary.views.keyboard.IPanelConflictLayout
import com.huawen.baselibrary.views.keyboard.IPanelHeightTarget
import com.huawen.baselibrary.views.keyboard.handler.KPSwitchPanelLayoutHandler

class KPSwitchPanelConstraintLayout : ConstraintLayout, IPanelHeightTarget, IPanelConflictLayout {

    override var height: Int? = 0

    private var panelLayoutHandler: KPSwitchPanelLayoutHandler? = null

    override val isKeyboardShowing: Boolean
        get() = panelLayoutHandler?.isKeyboardShowing ?: false

    override val isVisible: Boolean
        get() = panelLayoutHandler?.isVisible ?: false

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        panelLayoutHandler = KPSwitchPanelLayoutHandler(this, attrs)
    }

    override fun setVisibility(visibility: Int) {
        if (panelLayoutHandler?.filterSetVisibility(visibility) == true) {
            return
        }
        super.setVisibility(visibility)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val processedMeasureWHSpec = panelLayoutHandler?.processOnMeasure(widthMeasureSpec,
                heightMeasureSpec) ?: intArrayOf(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(processedMeasureWHSpec[0], processedMeasureWHSpec[1])
    }

    override fun handleShow() {
        super.setVisibility(View.VISIBLE)
    }

    override fun handleHide() {
        panelLayoutHandler?.handleHide()
    }

    override fun setIgnoreRecommendHeight(isIgnoreRecommendHeight: Boolean) {
        panelLayoutHandler?.setIgnoreRecommendHeight(isIgnoreRecommendHeight)
    }

    override fun refreshHeight(panelHeight: Int) {
        panelLayoutHandler?.resetToRecommendPanelHeight(panelHeight)
    }

    override fun onKeyboardShowing(showing: Boolean) {
        panelLayoutHandler?.isKeyboardShowing = (showing)
    }
}