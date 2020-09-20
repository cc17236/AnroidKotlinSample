package com.huawen.baselibrary.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View

object ViewFindHelper {
    fun getView(context: Context?, stub: Int): View {
        return LayoutInflater.from(context).inflate(stub, null)
    }
}