package com.huawen.baselibrary.adapter.util

import android.view.MotionEvent

object TouchEventUtil {

    fun getTouchAction(actionId: Int): String {
        var actionName = "Unknow:id=$actionId"
        when (actionId) {
            MotionEvent.ACTION_DOWN -> actionName = "ACTION_DOWN"
            MotionEvent.ACTION_MOVE -> actionName = "ACTION_MOVE"
            MotionEvent.ACTION_UP -> actionName = "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> actionName = "ACTION_CANCEL"
            MotionEvent.ACTION_OUTSIDE -> actionName = "ACTION_OUTSIDE"
        }
        return actionName
    }

}
