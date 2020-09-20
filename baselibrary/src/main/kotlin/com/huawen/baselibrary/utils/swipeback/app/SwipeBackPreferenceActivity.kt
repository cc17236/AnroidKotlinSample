package com.huawen.baselibrary.utils.swipeback.app

import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.View
import com.huawen.baselibrary.utils.swipeback.SwipeBackLayout

class SwipeBackPreferenceActivity : PreferenceActivity(), SwipeBackActivityBase {
    private var mHelper: SwipeBackActivityHelper? = null

    override val swipeBackLayout: SwipeBackLayout?
        get() = mHelper!!.swipeBackLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper!!.onActivityCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper!!.onPostCreate()
    }

    override fun <T : View> findViewById(id: Int): T? {
        val v = super.findViewById<T?>(id)
        return (if (v == null && mHelper != null) mHelper!!.findViewById(id) else v) as? T
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout!!.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        swipeBackLayout!!.scrollToFinishActivity()
    }
}
