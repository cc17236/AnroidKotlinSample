package cn.aihuaiedu.school.base.http

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

class ScheduleObserverTransformer<T>
/**
 * 私有的构造函数
 */
private constructor() : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }



    /**
     *内部单例
     */
    private object TransformerHolder {
        private val instance: ScheduleObserverTransformer<Any>? = null
        fun getInstance(): ScheduleObserverTransformer<Any> {
            return instance ?: ScheduleObserverTransformer()
        }
    }

    companion object {
        val  instance: ScheduleObserverTransformer<Any>
            get(){
                return TransformerHolder.getInstance()
            }
    }
}


class ScheduleFlowableTransformer<T>
/**
 * 私有的构造函数
 */
private constructor() : FlowableTransformer<T, T> {
    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     *内部单例
     */
    private object TransformerHolder {
        private val instance: ScheduleFlowableTransformer<Any>? = null
        fun getInstance(): ScheduleFlowableTransformer<Any> {
            return instance ?: ScheduleFlowableTransformer()
        }
    }

    companion object {
        val instance: ScheduleFlowableTransformer<Any>
            get() = TransformerHolder.getInstance()
    }
}




