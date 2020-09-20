package cn.aihuaiedu.school.base.context

import android.app.Dialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.KeyEvent
import com.example.applicationkotlinsample.R
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.ScreenUtils

/**
 *
 */
class LoadingDialog : BaseDialog() {
    override fun getLayoutId(): Int = R.layout.dialog_loading

    override fun setStyle(): Int = R.style.DialogFragment

    override fun initView() {

    }

    override fun initData() {

    }

    override fun config(dialog: Dialog) {
        dialog.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                Debuger.print("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]")
                callBack?.disMiss()
                return@OnKeyListener false
            }
            false
        })
    }

    override fun initOnStart() {
        dialog?.window?.setGravity(
                Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        val lp = dialog?.window?.attributes
        lp?.width = (ScreenUtils.screenWidth * 0.28f).toInt()
        lp?.height = lp?.width
        lp?.dimAmount = 0f
        dialog?.window?.attributes = lp
        isCancelable = true
    }


}