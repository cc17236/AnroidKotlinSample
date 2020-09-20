package com.huawen.baselibrary.views.banners.loader

import android.content.Context
import android.view.View

import java.io.Serializable


interface ImageLoaderInterface<T : View> : Serializable {

    fun displayImage(context: Context, path: Any?, imageView: T?)

    fun createImageView(context: Context): T
}
