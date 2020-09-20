package com.huawen.baselibrary.views

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * @作者: #Administrator #
 *@日期: #2018/5/7 #
 *@时间: #2018年05月07日 11:38 #
 *@File:Kotlin File
 */
fun TextView.drawableRes(@DrawableRes left: Int = 0, @DrawableRes top: Int = 0,
                         @DrawableRes right: Int = 0, @DrawableRes bottom: Int = 0) {
    var drawableLeft: Drawable? = null
    var drawableTop: Drawable? = null
    var drawableRight: Drawable? = null
    var drawableBottom: Drawable? = null
    if (left != 0) {
        drawableLeft = ContextCompat.getDrawable(context, left)
        drawableLeft?.setBounds(0, 0, drawableLeft.intrinsicWidth, (drawableLeft.minimumHeight))
    }
    if (top != 0) {
        drawableTop = ContextCompat.getDrawable(context, top)
        drawableTop?.setBounds(0, 0, drawableTop.intrinsicWidth, (drawableTop.minimumHeight))
    }
    if (right != 0) {
        drawableRight = ContextCompat.getDrawable(context, right)
        drawableRight?.setBounds(0, 0, drawableRight.intrinsicWidth, (drawableRight.minimumHeight))
    }
    if (bottom != 0) {
        drawableBottom = ContextCompat.getDrawable(context, bottom)
        drawableBottom?.setBounds(0, 0, drawableBottom.intrinsicWidth, (drawableBottom.minimumHeight))
    }
    this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
    invalidate()
}


fun TextView.getDrawable(left: Boolean = false, top: Boolean = false,
                         right: Boolean = false, bottom: Boolean = false): Drawable? {
    val cds = compoundDrawables
    if (left) {
        return cds.get(0)
    }
    if (top) {
        return cds.get(1)
    }
    if (right) {
        return cds.get(2)
    }
    if (bottom) {
        return cds.get(3)
    }
    return null
}


fun TextView.setDrawable(left: Drawable? = null, top: Drawable? = null,
                         right: Drawable? = null, bottom: Drawable? = null) {
    val cds = compoundDrawables
    if (left != null) {
        left.setBounds(0, 0, left.intrinsicWidth, (left.minimumHeight))
        cds[0] = left
    }
    if (top != null) {
        top.setBounds(0, 0, top.intrinsicWidth, (top.minimumHeight))
        cds[1] = top
    }
    if (right != null) {
        right.setBounds(0, 0, right.intrinsicWidth, (right.minimumHeight))
        cds[2] = right
    }
    if (bottom != null) {
        bottom.setBounds(0, 0, bottom.intrinsicWidth, (bottom.minimumHeight))
        cds[3] = bottom
    }
    this.setCompoundDrawablesWithIntrinsicBounds(cds[0], cds[1], cds[2], cds[3])
    invalidate()
}


fun TextView.layoutParamsMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0,cleanLast:Boolean=true) {
    val lp = layoutParams
    if (lp is LinearLayout.LayoutParams) {
        if (cleanLast){
            lp.leftMargin=left
        }else{
            lp.leftMargin += left
        }
        if (cleanLast){
            lp.topMargin=top
        }else{
            lp.topMargin += top
        }
        if (cleanLast){
            lp.rightMargin=right
        }else{
            lp.rightMargin += right
        }
        if (cleanLast){
            lp.bottomMargin=bottom
        }else{
            lp.bottomMargin += bottom
        }
    } else if (lp is RelativeLayout.LayoutParams) {
        if (cleanLast){
            lp.leftMargin=left
        }else{
            lp.leftMargin += left
        }
        if (cleanLast){
            lp.topMargin=top
        }else{
            lp.topMargin += top
        }
        if (cleanLast){
            lp.rightMargin=right
        }else{
            lp.rightMargin += right
        }
        if (cleanLast){
            lp.bottomMargin=bottom
        }else{
            lp.bottomMargin += bottom
        }
    } else if (lp is FrameLayout.LayoutParams) {
        if (cleanLast){
            lp.leftMargin=left
        }else{
            lp.leftMargin += left
        }
        if (cleanLast){
            lp.topMargin=top
        }else{
            lp.topMargin += top
        }
        if (cleanLast){
            lp.rightMargin=right
        }else{
            lp.rightMargin += right
        }
        if (cleanLast){
            lp.bottomMargin=bottom
        }else{
            lp.bottomMargin += bottom
        }
    } else if (lp is ConstraintLayout.LayoutParams) {
        if (cleanLast){
            lp.leftMargin=left
        }else{
            lp.leftMargin += left
        }
        if (cleanLast){
            lp.topMargin=top
        }else{
            lp.topMargin += top
        }
        if (cleanLast){
            lp.rightMargin=right
        }else{
            lp.rightMargin += right
        }
        if (cleanLast){
            lp.bottomMargin=bottom
        }else{
            lp.bottomMargin += bottom
        }
    }

}
