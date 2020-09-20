package cn.aihuaiedu.school.utils

import android.view.View
import cn.aihuaiedu.school.utils.ViewClickDelay.SPACE_TIME
import cn.aihuaiedu.school.utils.ViewClickDelay.hash
import cn.aihuaiedu.school.utils.ViewClickDelay.lastClickTime


object ViewClickDelay {
        var hash: Int = 0
        var lastClickTime: Long = 0
        var SPACE_TIME: Long = 2000
    }

    infix fun View.clickDelay(clickAction: () -> Unit) {
        this.setOnClickListener {
            if (this.hashCode() != hash) {
                hash = this.hashCode()
                lastClickTime = System.currentTimeMillis()
                clickAction()
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > SPACE_TIME) {
                    lastClickTime = System.currentTimeMillis()
                    clickAction()
                }
            }
        }
    }
