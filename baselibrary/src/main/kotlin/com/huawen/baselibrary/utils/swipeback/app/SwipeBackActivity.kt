package com.huawen.baselibrary.utils.swipeback.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.huawen.baselibrary.utils.swipeback.SwipeBackLayout
import com.huawen.baselibrary.utils.swipeback.Utils


open class SwipeBackActivity : AppCompatActivity(), SwipeBackActivityBase {
    private var mHelper: SwipeBackActivityHelper? = null

    override val swipeBackLayout: SwipeBackLayout
        get() {
            return mHelper!!.swipeBackLayout!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper!!.onActivityCreate()
        setSwipeBackEnable(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper!!.onPostCreate()
    }

    override fun <T : View> findViewById(id: Int): T? {
        val v=super.findViewById<T?>(id)
        return if (v == null && mHelper != null) mHelper!!.findViewById(id) as? T else v
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout!!.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout!!.scrollToFinishActivity()
    }
}
