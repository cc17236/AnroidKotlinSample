package com.huawen.baselibrary.utils.utils

import android.app.Activity
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * 加载对话框帮助类
 */
class HelperUtil(private val mActivity: Activity) {
    internal var progressDialog: ProgressDialog? = null

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isLoading) {
                val text = msg.obj as String
                progressDialog!!.setMessage(text)
            }
        }
    }

    /**
     * 加载对话框是否存
     *
     * @return 存在就返回true，不存在则返回发false
     */
    val isLoading: Boolean
        get() = progressDialog != null && progressDialog!!.isShowing

    /**
     * 显示加载对话
     *
     * @param str 对话框上的提示信
     */
    fun showLoading(str: String) {
        if (progressDialog == null) {
            // 先判断是否为null，可避免重复创建
            progressDialog = ProgressDialog(mActivity)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog!!.setMessage(str)
        }
        if (!progressDialog!!.isShowing) {
            progressDialog!!.show()
        }
    }

    /**
     * 刷新提示信息
     *
     * @param text
     */
    fun refreshLoadingText(text: String) {
        if (isLoading) {
            val msg = Message()
            msg.obj = text
            mHandler.sendMessage(msg)
        }
    }

    /**
     * 关闭加载对话
     */
    fun hideLoading() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

}
