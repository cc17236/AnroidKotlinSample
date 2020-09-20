package com.huawen.baselibrary.utils.swipeback.app

import android.app.Activity
import com.huawen.baselibrary.utils.swipeback.SwipeBackLayout
import com.huawen.baselibrary.utils.swipeback.Utils

import java.lang.ref.WeakReference

/**
 * Created by laysionqet on 2018/4/24.
 */
class SwipeBackListenerActivityAdapter(activity: Activity) : SwipeBackLayout.SwipeListenerEx {
    private val mActivity: WeakReference<Activity>

    init {
        mActivity = WeakReference(activity)
    }

    override fun onScrollStateChange(state: Int, scrollPercent: Float) {

    }

    override fun onEdgeTouch(edgeFlag: Int) {
        val activity = mActivity.get()
        if (null != activity) {
            Utils.convertActivityToTranslucent(activity)
        }
    }

    override fun onScrollOverThreshold() {

    }

    override fun onContentViewSwipedBack() {
        val activity = mActivity.get()
        if (null != activity && !activity.isFinishing) {
            activity.finish()
            activity.overridePendingTransition(0, 0)
        }
    }
}
