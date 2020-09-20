package com.huawen.baselibrary.utils

import android.graphics.Rect
import android.view.View

object WindowFrameCompat {
    fun parse(view: View?, r: Rect) {
        view?.getWindowVisibleDisplayFrame(r)
        if (Math.abs(r.top) == 10000) {
            r.setEmpty()
            view?.getGlobalVisibleRect(r)
        }
    }

}
