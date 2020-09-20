package com.huawen.baselibrary.adapter.util

import android.util.SparseArray

import com.huawen.baselibrary.adapter.provider.BaseItemProvider

/**
 * https://github.com/chaychan
 * @author ChayChan
 * @date 2018/3/21  11:04
 */

class ProviderDelegate {

    val itemProviders = SparseArray<BaseItemProvider<*, *>>()

    fun registerProvider(provider: BaseItemProvider<*, *>?) {
        if (provider == null) {
            throw ItemProviderException("ItemProvider can not be null")
        }

        val viewType = provider.viewType()

        if (itemProviders.get(viewType) == null) {
            itemProviders.put(viewType, provider)
        }
    }

}
