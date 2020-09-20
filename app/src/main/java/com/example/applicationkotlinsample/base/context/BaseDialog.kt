package cn.aihuaiedu.school.base.context

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.*
import cn.aihuaiedu.school.base.BaseContract
import com.example.applicationkotlinsample.R
import com.huawen.baselibrary.jni.AppVerify
import com.huawen.baselibrary.utils.ScreenUtils
import com.huawen.baselibrary.utils.ToastUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * DialogFragment是google推荐使用的弹窗
 */

abstract class BaseDialog : DialogFragment(), BaseContract.BaseView {

    override fun onStart() {
        super.onStart()
        initOnStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val isValidate = AppVerify.isNativeValidate()
        if (isValidate) {
            return inflater.inflate(getLayoutId(), container, false)
        } else {
            return inflater.inflate(0, container, false)
        }
    }

    private var mBundle: Bundle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isValidate = AppVerify.isNativeValidate()
        if (isValidate) {
            initView()
            initData()
        }
    }

    final fun setArguments(bundle: Bundle): BaseDialog {
        arguments = bundle
        return this
    }

    final internal fun bundle(arg: String? = null): Bundle? {
        val ctx: Bundle?
        if (mBundle == null) {
            mBundle = arguments
        }
        if (arg != null) {
            ctx = mBundle?.getBundle(arg)
        } else {
            ctx = mBundle
        }
        return ctx
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mBundle == null) {
            mBundle = arguments
        }
        outState.putBundle("keyBundle", mBundle)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        val keyBound = savedInstanceState?.getBundle("keyBundle")
        if (keyBound != null)
            mBundle = keyBound

        super.onViewStateRestored(savedInstanceState)
    }

    override fun showLoading() {
        showLoading(false)
    }

    fun showLoading(cancelFlag: Int) {
        showLoading(false, cancelFlag)
    }

    fun showLoading(useAnim: Boolean) {
        showLoading(useAnim, -1)
    }

    override fun complete() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun getContext(): Context? {
        return super.getContext()
    }

    private var customLoadingFunc0: ((BaseDialog?) -> BaseDialog)? = null
    private var customLoadingFunc1: ((BaseDialog) -> Unit)? = null
    private var customDialog: BaseDialog? = null

    fun setCustomDialogStyle(fun0: (BaseDialog?) -> BaseDialog, fun1: (BaseDialog) -> Unit) {
        this.customLoadingFunc0 = fun0
        this.customLoadingFunc1 = fun1
    }

    //提示框
    private var loading: LoadingDialog? = null;

    fun showLoading(useAnim: Boolean, cancelFlag: Int) {
        try {
            if (useAnim) {
                if (customDialog?.isVisible == true) {
                    return
                }
                if (customDialog == null) {
                    customDialog = customLoadingFunc0?.invoke(this)
                    if (cancelFlag >= 0) {
                        customDialog?.setCancelable(false)
                    } else {
                        customDialog?.setCancelable(true)
                    }
                }
                customDialog?.setCallBack(object : OnDisMissCallBack {
                    override fun disMiss() {
//                        (mPresenter as? RxPresenter<V>)?.cancelCurrent()
                    }
                })
                if (customDialog?.isVisible == false)
                    customDialog?.show(childFragmentManager, javaClass)
                return
            }
            if (loading?.isVisible == true) {
                return
            }
            loading = loading ?: LoadingDialog()
            if (cancelFlag >= 0) {
                loading?.setCancelable(false)
            } else {
                loading?.setCancelable(true)
            }
            loading?.setCallBack(object : OnDisMissCallBack {
                override fun disMiss() {
//                    (mPresenter as? RxPresenter<V>)?.cancelCurrent()
                }
            })
            if (loading?.isVisible == false) {
                loading?.show(childFragmentManager, javaClass)
            }
        } catch (e: Exception) {
        }
    }


    override fun disMissLoading() {
        try {
            customDialog?.dismiss(childFragmentManager, javaClass)
            customDialog?.dismiss()
        } catch (e: Exception) {
        }
        customDialog = null
        try {
            loading?.dismiss(childFragmentManager, javaClass)
            loading?.dismiss()
        } catch (e: Exception) {
        }
        loading = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //设置dialog的样式
        val dialog = Dialog(requireContext(), setStyle())
        dialog.setOnCancelListener {
            //When you touch outside of dialog bounds,
            //the dialog gets canceled and this method executes.
            callBack?.disMiss()
            callBack = null
        }
        config(dialog)

        return dialog
    }

    fun addSubscribe(subscription: Disposable) {
        this.subscription = subscription
        if (mCompositeSubscription?.isDisposed == true) {
            mCompositeSubscription = null
        }
        if (mCompositeSubscription == null) {
            mCompositeSubscription = CompositeDisposable()
        }
        mCompositeSubscription!!.add(subscription)
    }

    //观察者订阅管理对象
    private var mCompositeSubscription: CompositeDisposable? = null
    //当前最新的订阅者
    private var subscription: Disposable? = null

    /**
     * 取消所有的订阅 Unsubscribes itself and all inner subscriptions.
     */
    private fun unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription!!.dispose()
        }
    }

    override fun showError(throwable: Throwable) {
        disMissLoading()
        ToastUtils.showShort("${throwable.message}")
    }

    override fun showError(errMsg: String?) {
        disMissLoading()
        ToastUtils.showShort("${errMsg}")
    }

    override fun showError(@StringRes errMsg: Int) {
        disMissLoading()
        ToastUtils.showShort(getString(errMsg))
    }


    fun currentSubscription(): Disposable? {
        return subscription
    }

    fun unsubscribeAllRequest() {
        unSubscribe()
    }

    final fun show(manager: androidx.fragment.app.FragmentManager, source: Class<*>) {
        show(manager, source.simpleName)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callBack?.disMiss()
        callBack = null
        unsubscribeAllRequest()
    }


    final override fun show(manager: FragmentManager, tag: String?) {
        val transaction = manager?.beginTransaction()
        if (!isAdded) {
            transaction?.addToBackStack(tag)
            try {
                show(transaction, tag)
                manager?.executePendingTransactions()
            } catch (e: Exception) {
//                e.printStackTrace()
            }
        } else {
            transaction?.show(this)?.commit()
            manager?.executePendingTransactions()
        }
    }

    open fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, this.javaClass)
    }

    open fun dismiss(fragmentManager: FragmentManager) {
        dismiss(fragmentManager, this.javaClass)
        callBack?.disMiss()
        callBack = null
    }

    final override fun show(transaction: FragmentTransaction, tag: String?): Int {
        val id = super.show(transaction, tag)
        showSelf()
        return id
    }


    open fun showSelf() {

    }

    final fun dismiss(manager: androidx.fragment.app.FragmentManager?, source: Class<*>) {
        val tag = source.simpleName
        val dialogFragment = manager?.findFragmentByTag(tag) as? BaseDialog
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss()
        }
        dismissSelfInternal()
        callBack?.disMiss()
        unsubscribeAllRequest()
    }

    open protected fun dismissSelfInternal() {
        if (this.isAdded)
        dismiss()
    }

    interface OnDisMissCallBack {
        fun disMiss()

        open fun click() {

        }
    }

    internal var callBack: OnDisMissCallBack? = null

    fun setCallBack(callBack: OnDisMissCallBack) {
        this.callBack = callBack
    }

    /**
     * 设置布局
     * @return
     */
    protected abstract fun getLayoutId(): Int

    /**
     * 初始化控件
     * @param view
     */
    protected abstract fun initView()

    /**
     * 初始化数据，在初始化控件之前调用
     */
    protected abstract fun initData()

    /**
     * 设置弹窗的样式
     * @param
     */
    protected open fun setStyle() = R.style.DialogFragment

    /**
     * @param dialog
     */
    protected abstract fun config(dialog: Dialog)

    /**
     * 窗口的基本设置，包括宽高、动画、渐变、是否有navigationBar等
     */
    protected open fun initOnStart() {
        val lp = dialog?.window?.attributes
        lp?.width = ScreenUtils.screenWidth.toInt()
        dialog?.window?.setGravity(
            Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        )

        lp?.dimAmount = 0.5f
        dialog?.window?.attributes = lp
        dialog?.window?.decorView?.setOnClickListener {
            callBack?.disMiss()
            callBack = null
        }
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
    }


}
