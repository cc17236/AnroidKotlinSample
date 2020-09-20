package com.huawen.baselibrary.utils.swipeback.app

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.huawen.baselibrary.R
import com.huawen.baselibrary.utils.swipeback.SwipeBackLayout


/**
 * @author Yrom
 */
class SwipeBackActivityHelper(private val mActivity: Activity) {

    var swipeBackLayout: SwipeBackLayout? = null
        private set

    fun onActivityCreate() {
        mActivity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mActivity.window.decorView.setBackgroundDrawable(null)
        swipeBackLayout = LayoutInflater.from(mActivity).inflate(
            R.layout.swipeback_layout, null
        ) as SwipeBackLayout
    }

    fun onPostCreate() {
        swipeBackLayout!!.attachToActivity(mActivity)
    }

    fun findViewById(id: Int): View? {
        return if (swipeBackLayout != null) {
            swipeBackLayout!!.findViewById(id)
        } else null
    }
}
