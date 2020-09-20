package cn.aihuaiedu.school.base.context

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.huawen.baselibrary.jni.AppVerify

abstract class BasePopup(protected val context: Context) {

    protected var view: View? = null
    private var param = BasePopParam()
    private var mPop: PopupWindow? = null

    init {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(getLayoutId(), null)
        configParam(param)
        configPop()
    }

    private fun configPop() {
        val isValidate=AppVerify.isNativeValidate()
        if (isValidate) {
            mPop = PopupWindow(view, param.width, param.height, param.focusable)
            initWindow()
            initData()
            initView(view!!)
        }
    }


    public fun showAsDropDown(anchor: View, xoff: Int, yoff: Int) {
        mPop?.showAsDropDown(anchor, xoff, yoff);
    }

    public fun showAtLocation(anchor: View, gravity: Int, x: Int, y: Int) {
        mPop?.showAtLocation(anchor, gravity, x, y);
    }

    public fun showBashOfAnchor(anchor: View, layoutGravity: LayoutGravity, xMerge: Int, yMerge: Int) {
        val offset = layoutGravity.getOffset(anchor, mPop!!)
        mPop?.showAsDropDown(anchor, offset[0] + xMerge, offset[1] + yMerge)
    }

    companion object {
        class BasePopParam {
            var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT
            var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
            var focusable = true
            var background: Drawable = ColorDrawable(Color.TRANSPARENT)
        }
    }



    fun getPopupWindow(): PopupWindow? {
        return mPop
    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun initView(view: View)
    protected abstract fun initData()
    protected abstract fun configParam(param: BasePopParam)

    interface OnDisMissCallBack {
        fun disMiss()
        open fun click() {

        }
    }

    fun initWindow() {
        mPop?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPop?.isOutsideTouchable = true
        mPop?.isTouchable = true
    }
}