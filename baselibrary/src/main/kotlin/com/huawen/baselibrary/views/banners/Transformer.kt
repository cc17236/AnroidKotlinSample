package com.huawen.baselibrary.views.banners

import androidx.viewpager.widget.ViewPager.PageTransformer

import com.huawen.baselibrary.views.banners.transformer.AccordionTransformer
import com.huawen.baselibrary.views.banners.transformer.BackgroundToForegroundTransformer
import com.huawen.baselibrary.views.banners.transformer.CubeInTransformer
import com.huawen.baselibrary.views.banners.transformer.CubeOutTransformer
import com.huawen.baselibrary.views.banners.transformer.DefaultTransformer
import com.huawen.baselibrary.views.banners.transformer.DepthPageTransformer
import com.huawen.baselibrary.views.banners.transformer.FlipHorizontalTransformer
import com.huawen.baselibrary.views.banners.transformer.FlipVerticalTransformer
import com.huawen.baselibrary.views.banners.transformer.ForegroundToBackgroundTransformer
import com.huawen.baselibrary.views.banners.transformer.RotateDownTransformer
import com.huawen.baselibrary.views.banners.transformer.RotateUpTransformer
import com.huawen.baselibrary.views.banners.transformer.ScaleInOutTransformer
import com.huawen.baselibrary.views.banners.transformer.StackTransformer
import com.huawen.baselibrary.views.banners.transformer.TabletTransformer
import com.huawen.baselibrary.views.banners.transformer.ZoomInTransformer
import com.huawen.baselibrary.views.banners.transformer.ZoomOutSlideTransformer
import com.huawen.baselibrary.views.banners.transformer.ZoomOutTranformer


object Transformer {
    var Default: Class<out PageTransformer> = DefaultTransformer::class.java
    var Accordion: Class<out PageTransformer> = AccordionTransformer::class.java
    var BackgroundToForeground: Class<out PageTransformer> = BackgroundToForegroundTransformer::class.java
    var ForegroundToBackground: Class<out PageTransformer> = ForegroundToBackgroundTransformer::class.java
    var CubeIn: Class<out PageTransformer> = CubeInTransformer::class.java
    var CubeOut: Class<out PageTransformer> = CubeOutTransformer::class.java
    var DepthPage: Class<out PageTransformer> = DepthPageTransformer::class.java
    var FlipHorizontal: Class<out PageTransformer> = FlipHorizontalTransformer::class.java
    var FlipVertical: Class<out PageTransformer> = FlipVerticalTransformer::class.java
    var RotateDown: Class<out PageTransformer> = RotateDownTransformer::class.java
    var RotateUp: Class<out PageTransformer> = RotateUpTransformer::class.java
    var ScaleInOut: Class<out PageTransformer> = ScaleInOutTransformer::class.java
    var Stack: Class<out PageTransformer> = StackTransformer::class.java
    var Tablet: Class<out PageTransformer> = TabletTransformer::class.java
    var ZoomIn: Class<out PageTransformer> = ZoomInTransformer::class.java
    var ZoomOut: Class<out PageTransformer> = ZoomOutTranformer::class.java
    var ZoomOutSlide: Class<out PageTransformer> = ZoomOutSlideTransformer::class.java
}
