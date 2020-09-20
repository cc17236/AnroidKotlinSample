package cn.aihuaiedu.school.base.context

import android.view.View
import android.widget.PopupWindow

class LayoutGravity(var layoutGravity: Int) {
    val horiParam: Int
        get() {
            var i = 0x1
            while (i <= 0x100) {
                if (isParamFit(i))
                    return i
                i = i shl 2
            }
            return ALIGN_LEFT
        }

    val vertParam: Int
        get() {
            var i = 0x2
            while (i <= 0x200) {
                if (isParamFit(i))
                    return i
                i = i shl 2
            }
            return TO_BOTTOM
        }

    fun setHoriGravity(gravity: Int) {
        layoutGravity = layoutGravity and 0x2 + 0x8 + 0x20 + 0x80 + 0x200
        layoutGravity = layoutGravity or gravity
    }

    fun setVertGravity(gravity: Int) {
        layoutGravity = layoutGravity and 0x1 + 0x4 + 0x10 + 0x40 + 0x100
        layoutGravity = layoutGravity or gravity
    }

    fun isParamFit(param: Int): Boolean {
        return layoutGravity and param > 0
    }

    fun getOffset(anchor: View, window: PopupWindow): IntArray {
        val anchWidth = anchor.width
        val anchHeight = anchor.height

        var winWidth = window.width
        var winHeight = window.height
        val view = window.contentView
        if (winWidth <= 0)
            winWidth = view.width
        if (winHeight <= 0)
            winHeight = view.height

        var xoff = 0
        var yoff = 0

        when (horiParam) {
            ALIGN_LEFT -> xoff = 0
            ALIGN_RIGHT -> xoff = anchWidth - winWidth
            TO_LEFT -> xoff = -winWidth
            TO_RIGHT -> xoff = anchWidth
            CENTER_HORI -> xoff = (anchWidth - winWidth) / 2
            else -> {
            }
        }
        when (vertParam) {
            ALIGN_ABOVE -> yoff = -anchHeight
            ALIGN_BOTTOM -> yoff = -winHeight
            TO_ABOVE -> yoff = -anchHeight - winHeight
            TO_BOTTOM -> yoff = 0
            CENTER_VERT -> yoff = (-winHeight - anchHeight) / 2
            else -> {
            }
        }
        return intArrayOf(xoff, yoff)
    }

    companion object {
        // waring, don't change the order of these constants!
        val ALIGN_LEFT = 0x1
        val ALIGN_ABOVE = 0x2
        val ALIGN_RIGHT = 0x4
        val ALIGN_BOTTOM = 0x8
        val TO_LEFT = 0x10
        val TO_ABOVE = 0x20
        val TO_RIGHT = 0x40
        val TO_BOTTOM = 0x80
        val CENTER_HORI = 0x100
        val CENTER_VERT = 0x200
    }
}