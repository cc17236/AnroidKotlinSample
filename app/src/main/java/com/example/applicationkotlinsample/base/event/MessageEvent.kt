package cn.aihuaiedu.school.base.event

import cn.aihuaiedu.school.base.RxPresenter

open class MessageEvent {
    var clazz: List<Class<out RxPresenter<*>>>? = null
    var signleClazz: Class<out RxPresenter<*>>? = null
    var i: Int? = null
    var message: String? = null
    var obj: Any? = null

    constructor(clazz: List<Class<out RxPresenter<*>>>, i: Int?, message: String?, obj: Any?) {
        this.clazz = clazz
        this.i = i
        this.message = message
        this.obj = obj
    }

    constructor(clazz: Class<out RxPresenter<*>>, i: Int?, message: String?, obj: Any?) {
        this.signleClazz = clazz
        this.i = i
        this.message = message
        this.obj = obj
    }


}

class UIMessageEvent : MessageEvent {
    constructor(clazz: List<Class<out RxPresenter<*>>>, i: Int) : super(clazz, i, null, null)
    constructor(vararg clazz: Class<out RxPresenter<*>>, i: Int) : super(clazz.toList(), i, null, null)
    constructor(clazz: Class<out RxPresenter<*>>, i: Int) : super(clazz, i, null, null)
    constructor(clazz: Class<out RxPresenter<*>>, i: Int, message: String?) : super(clazz, i, message, null)
    constructor(clazz: Class<out RxPresenter<*>>, i: Int, message: String?, obj: Any?) : super(clazz, i, message, obj)
}

class IOMessageEvent : MessageEvent {
    constructor(clazz: List<Class<out RxPresenter<*>>>, i: Int) : super(clazz, i, null, null)
    constructor(vararg clazz: Class<out RxPresenter<*>>, i: Int) : super(clazz.toList(), i, null, null)
    constructor(clazz: Class<out RxPresenter<*>>, i: Int) : super(clazz, i, null, null)
    constructor(clazz: Class<out RxPresenter<*>>, i: Int, message: String?) : super(clazz, i, message, null)
    constructor(clazz: Class<out RxPresenter<*>>, i: Int, message: String?, obj: Any?) : super(clazz, i, message, obj)
}