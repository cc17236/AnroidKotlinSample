package com.huawen.baselibrary.utils.swipeback.app

import com.huawen.baselibrary.utils.swipeback.SwipeBackLayout

/**
 * @author Yrom
 */
interface SwipeBackActivityBase {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    val swipeBackLayout: SwipeBackLayout?

    fun setSwipeBackEnable(enable: Boolean)

    /**
     * Scroll out contentView and finish the activity
     */
    fun scrollToFinishActivity()

}
