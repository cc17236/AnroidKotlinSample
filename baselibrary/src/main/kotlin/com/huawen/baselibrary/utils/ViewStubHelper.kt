package com.huawen.baselibrary.utils

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.ViewStub
import com.huawen.baselibrary.findViewSafety

object ViewStubHelper {
    fun inflate(stub: ViewStub?, layout: Int) {
        if (stub != null && stub.parent != null) {
            stub.layoutResource = layout
            stub.inflate()
        }
    }

    /**
     * ViewStub会丢失LayoutParams属性,直接inflate有可能会丢失宽高属性
     */
    @SuppressLint("ResourceType")
    fun inflate(stub: ViewStub?, layout: Int, lp: ViewGroup.LayoutParams? = null, targetId: Int? = null) {
        if (lp == null) {
            inflate(stub, layout)
            return
        }
        if (stub != null && (stub.parent != null)) {
            val temp = stub.inflatedId
            val parent = stub.parent as ViewGroup
            stub.inflatedId = 0x1921991
            stub.layoutResource = layout
            stub.inflate()
            val v = parent.findViewSafety(0x1921991)
            v?.layoutParams = lp
            if (targetId != null) {
                v?.id = targetId
//                v?.setBackgroundColor(Color.BLACK)
            } else
                v?.id = temp
        }else if (stub!=null){
            val temp = stub.inflatedId
            if (targetId != null) {
                stub.inflatedId = targetId
            } else
                stub.inflatedId = temp
            stub.layoutResource = layout
            stub.inflate()
        }
    }

    fun isInflated(stub: ViewStub?): Boolean {
        if (stub != null && stub.parent != null) return false
        return true
    }

    fun restoreStub(viewSafe: ViewGroup?, restoreStub: ViewGroup?): Boolean {
        if (viewSafe != null && viewSafe.childCount >= 0) {
            if (viewSafe.childCount > 0)
                viewSafe.removeViewAt(0)
            if (restoreStub != null) {
                viewSafe.addView(restoreStub, 0)
                return true
            }
        }
        return false
    }
}