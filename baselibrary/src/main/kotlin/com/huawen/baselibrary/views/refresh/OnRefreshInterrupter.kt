package com.huawen.baselibrary.views.refresh

import java.util.*

interface OnRefreshInterrupter {
    fun onRefresh(date: Date)
    fun onLoadMore(date: Date)
}