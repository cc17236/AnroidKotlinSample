package com.huawen.baselibrary.views.refresh

internal interface SmartDelegate {
    fun getInterrupter(): OnRefreshInterrupter?
}