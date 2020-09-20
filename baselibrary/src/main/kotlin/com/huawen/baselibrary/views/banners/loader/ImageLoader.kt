package com.huawen.baselibrary.views.banners.loader

import android.content.Context
import android.view.View
import android.widget.ImageView


abstract class ImageLoader : ImageLoaderInterface<View> {

    override fun createImageView(context: Context): View {
        return ImageView(context)
    }

}
