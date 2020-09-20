package com.huawen.baselibrary.adapter.animation

import android.animation.Animator
import android.view.View

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
interface BaseAnimation {
    fun getAnimators(view: View): Array<out Animator>
}
