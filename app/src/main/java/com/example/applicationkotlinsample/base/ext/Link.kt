package cn.aihuaiedu.school.base.ext

import cn.aihuaiedu.school.base.RefreshController
import com.example.applicationkotlinsample.DisposalApp
import com.huawen.baselibrary.utils.Debuger
import okhttp3.MultipartBody

//fun makeCustomerIdMap(): HashMap<String, String> {
//    val usr = DisposalApp.app?.getCurrentUser()
//    return hashMapOf<String, String>().also {
//        if (usr != null)
////            it.putAll(arrayOf(Pair("customerId", "${DisposalApp.app?.getCurrentUser()?.customerId ?: 0}")))
////        Debuger.print("customerId = ${DisposalApp.app?.getCurrentUser()?.customerId ?: 0}")
//    }.let {
//        if (it.size == 0) {
//            it.put("empty", "")
//        }
//        it
//    }
//}

//fun makeCustomerIdOrNull(): Long? {
////    return DisposalApp.app?.getCurrentUser()?.customerId
//}


fun <K, V> makeCustomerIdMap(vararg pairs: Pair<K, V>, controller: RefreshController? = null): HashMap<K, V> {
    return hashMapOf<K, V>().also {
        val info = DisposalApp.app?.getCurrentUser()
//        it.putAll(arrayOf(Pair("customerId", "${info?.customerId ?: 0}") as Pair<K, V>))
        it.putAll(pairs)

        if (controller != null) {
            val isRefresh = controller.isRefreshFixable()
            val pageIndex = if (isRefresh) 1 else (controller.pageIndex) + 1
            val pageSize = controller.pageSize
            it.put("pageNum" as K, pageIndex as V)
            it.put("pageSize" as K, pageSize as V)
        }


    }.let {
        if (it.size == 0) {
            it.put("empty" as K, "" as V)
            if (controller != null) {
                val isRefresh = controller.isRefreshFixable()
                val pageIndex = if (isRefresh) 1 else (controller.pageIndex) + 1
                val pageSize = controller.pageSize
                it.put("pageNum" as K, pageIndex as V)
                it.put("pageSize" as K, pageSize as V)
            }
        }
        it
    }
}
fun <K, V> makeCustomerFrom0IdMap(vararg pairs: Pair<K, V>, controller: RefreshController? = null): HashMap<K, V> {
    return hashMapOf<K, V>().also {
        val info = DisposalApp.app?.getCurrentUser()
//        it.putAll(arrayOf(Pair("customerId", "${info?.customerId ?: 0}") as Pair<K, V>))
        it.putAll(pairs)

        if (controller != null) {
            val isRefresh = controller.isRefreshFixable()
            val pageIndex = if (isRefresh) 0 else (controller.pageIndex) + 1
            val pageSize = controller.pageSize
            it.put("pageNum" as K, pageIndex as V)
            it.put("pageSize" as K, pageSize as V)
        }


    }.let {
        if (it.size == 0) {
            it.put("empty" as K, "" as V)
            if (controller != null) {
                val isRefresh = controller.isRefreshFixable()
                val pageIndex = if (isRefresh) 1 else (controller.pageIndex) + 1
                val pageSize = controller.pageSize
                it.put("pageNum" as K, pageIndex as V)
                it.put("pageSize" as K, pageSize as V)
            }
        }
        it
    }
}

//fun makeCustomerIdFormDataPart(builder: MultipartBody.Builder) {
//    val usr = DisposalApp.app?.getCurrentUser()
//    if (usr != null)
////        builder.addFormDataPart("customerId", "${usr.customerId}")
//}
