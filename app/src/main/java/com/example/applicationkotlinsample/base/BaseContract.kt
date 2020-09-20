package cn.aihuaiedu.school.base

import android.content.Context
import androidx.annotation.LayoutRes
import com.huawen.baselibrary.schedule.rxlifecycle2.LifecycleTransformer

/**
 * 对应mvp中的contract契约接口
 */

interface BaseContract {
    interface BasePresenter<in T> {

        /*该方法可以获取到View实例对象*/
        fun attachView(view: T)

        /*释放View对象的引用，gc才能回收View*/
        fun detachView()

        /*网络改变*/
        fun networkChange()

        fun executeQueue() {

        }

        fun bindLifeCycle(lifecycle: LifecycleTransformer<Any>){

        }
    }

    interface BaseView {
        fun showLoading()
        fun disMissLoading()

        fun showError(e: Throwable)

        fun showError(errMsg: String?)

        fun showError(@LayoutRes errMsg: Int)

        fun complete()
        fun getContext():Context?
    }
}
