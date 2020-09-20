package cn.aihuaiedu.school.base.expandable.dao

import java.lang.ref.WeakReference

/**
 * Created by klitaviy on 1/12/18-3:31 PM.
 */
open class ContextDAO(val groupLevel:Int, val index:Int, val itemData:Any?=null) {
    var subItems: MutableList<ContextDAO>? = null
    var name: String? = null
    var mParent: WeakReference<ContextDAO>? = null
    var id : Long? = null
}
