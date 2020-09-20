package com.huawen.baselibrary.utils

import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by vicky on 2018.02.01.
 *
 * @Author vicky
 * @Date 2018年02月01日  18:16:26
 * @ClassName 排序工具,rxjava线程调度
 */
object RxSorter {


    /**
     * 成功或失败都会调用fun1  lambda函数
     * 需要自己做空指针处理
     */
    fun <T> sort(observable: Observable<List<T>?>, fun0: (T, T) -> Int, fun1: (List<T>?) -> Unit) {
        observable
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Debuger.print("doOnNext ${it?.size}")
                sort(it, fun0, fun1)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnError {
                isMainThread()
                fun1(null)
            }.subscribe()
    }

    /**
     * 成功或失败都会调用fun1  lambda函数
     * 需要自己做空指针处理
     */
    fun <T> sort(list: List<T>?, fun0: (T, T) -> Int, fun1: (List<T>?) -> Unit) {
        Observable.fromIterable(list)
            .toSortedList { t1, t2 ->
                Int
                isMainThread("toSortedList")
                if (t1 == null && t2 == null) {
                    return@toSortedList 0
                } else {
                    if (t1 == null) {
                        return@toSortedList 0.compareTo(1)
                    } else if (t2 == null) {
                        return@toSortedList 1.compareTo(0)
                    } else
                        return@toSortedList fun0(t1, t2)
                }
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<T>?> {
                override fun onSuccess(value: List<T>) {
                    fun1(value)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    fun1(null)
                }
            })
    }
}