package cn.aihuaiedu.school.base

import cn.aihuaiedu.school.base.http.requestApi
import com.example.applicationkotlinsample.base.http.HttpService
import io.reactivex.Observable


/**
 * @作者: #Administrator #
 *@日期: #2018/6/8 #
 *@时间: #2018年06月08日 13:08 #
 *@File:Kotlin Class
 */

class Injection(private val presenter: RxPresenter<*>) {
    companion object {
        const val testobject = "testobject"
    }

    fun injectTag(): String {
        return testobject
    }


    fun <A : Any> request(
        fun0: ((HttpService)?) -> Observable<A>?,
        fun1: (success: Boolean, resp: A?, thr: Throwable?) -> Unit
    ) {
        presenter.requestApi({
            fun0.invoke(it)
        }, object : ObserverImp<A?>() {
            override fun onErr(errCode: Int, str: String) {
                fun1.invoke(false, null, Throwable(str))
            }

            override fun doNext(t: A?) {
                if (t != null) {
                    fun1.invoke(true, t, null)
                } else {
                    fun1.invoke(false, null, Throwable("未知异常"))
                }
            }
        })
    }


}