package com.example.applicationkotlinsample.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.util.Util
import com.example.applicationkotlinsample.R
import com.example.applicationkotlinsample.base.GlideApp
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.getActivityFromView
import com.huawen.baselibrary.views.glide.GlideRoundTransform
import com.huawen.baselibrary.views.glide.GlideWhiteCircleTransform
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult", "ObsoleteSdkInt")
object GlideUtil {
    public fun assertManager(image: ImageView, noNeedMainThread: Boolean = false): RequestManager? {
        try {
            val act = image.getActivityFromView()
            return assertManager(act, noNeedMainThread)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    public fun assertManager(activity: Activity?, noNeedMainThread: Boolean = false): RequestManager? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity?.isDestroyed == true) {
                return null
            }
            if (activity == null) return null
            if (!noNeedMainThread && !Util.isOnMainThread()) {
                Debuger.print("请在主线程加载图片")
                return null
            }
            return assertManager(context = activity, noNeedMainThread = noNeedMainThread)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    public fun assertManager(context: Context?, noNeedMainThread: Boolean = false): RequestManager? {
        try {
            if (context == null) return null
            if (!noNeedMainThread && !Util.isOnMainThread()) {
                Debuger.print("请在主线程加载图片")
                return null
            }
            return GlideApp.with(context)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    public fun assertManager(fragment: Fragment?, noNeedMainThread: Boolean = false): RequestManager? {
        try {
            if (fragment == null || (fragment.activity == null)) return null
            if (!noNeedMainThread && !Util.isOnMainThread()) {
                Debuger.print("请在主线程加载图片")
                return null
            }
            return GlideApp.with(fragment)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    public fun assertManager(fragment: android.app.Fragment?, noNeedMainThread: Boolean = false): RequestManager? {
        try {
            if (fragment == null || (fragment.activity == null)) return null
            if (!noNeedMainThread && !Util.isOnMainThread()) {
                Debuger.print("请在主线程加载图片")
                return null
            }
            return GlideApp.with(fragment)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun intoView(image: ImageView?, srcPath: String?) {
        intoView(image, srcPath, 8f)
    }

    fun intoView(image: ImageView?, srcPath: String?, dontAnim: Boolean = false, measureble: Boolean = false) {
        intoView(image, srcPath, true, 0f, dontAnim, measureble)
    }

    @JvmStatic
    fun intoView(image: ImageView?, srcPath: String?, redis: Float) {
        intoView(image, srcPath, true, redis)
    }

    fun intoView(image: ImageView?, srcPath: String?, dontAnim: Boolean = false, redis: Float) {
        intoView(image, srcPath, true, redis, dontAnim, false, 0)
    }

    fun intoView(image: ImageView?, srcPath: String?, dontAnim: Boolean = false, crop: Boolean, redis: Float) {
        intoView(image, srcPath, crop, redis, dontAnim, false, 0)
    }

    fun intoView(
        image: ImageView?,
        srcPath: String?,
        crop: Boolean,
        redis: Float,
        dontAnim: Boolean = false,
        measureble: Boolean = false, @DrawableRes placeHolder: Int? = 0
    ) {
        try {
            doAsync {
                uiThread {
                    if (image != null) {
                        val requestOptions = RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)

                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                        if (crop) {
                            requestOptions.centerCrop()
                        }
                        if (placeHolder != 0) {
                            requestOptions.placeholder(placeHolder!!)
                            requestOptions.error(placeHolder)
                        } else {
                            requestOptions.fallback(ColorDrawable(Color.parseColor("#DFDFDF")))
                        }
                        if (dontAnim) {
                            requestOptions.dontAnimate()
                        }
                        if (!measureble) {
                            val manager: RequestManager? = assertManager(image) ?: return@uiThread

                            val glide = manager!!
                                .asBitmap()
                                .apply(requestOptions)
                                .load(srcPath)
                            if (image.background is ColorDrawable) {
                                image.background = ColorDrawable(Color.TRANSPARENT)
                            }
                            if (redis <= 0) {
                                doAsync {
                                    uiThread {
                                        if (dontAnim) {
                                            glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                override fun setResource(resource: Bitmap?) {
                                                    image.setImageBitmap(resource)
                                                }
                                            })
                                        } else {
                                            glide.into(image)
                                        }
                                    }
                                }

                            } else {
                                glide.into(object : BitmapImageViewTarget(image) {
                                    override fun setResource(resource: Bitmap?) {
                                        doAsync {
                                            uiThread {
                                                val circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(image.resources, resource)
                                                circularBitmapDrawable.cornerRadius =
                                                    image.dip(redis).toFloat() //设置圆角弧度
                                                image.setImageDrawable(circularBitmapDrawable);
                                            }
                                        }

                                    }
                                })
                            }
                        } else {
                            if (image.width != 0 && image.height != 0) {
                                requestOptions.override(image.width, image.height)
                                val manager: RequestManager? = assertManager(image)
                                    ?: return@uiThread
                                val mgr = manager!!
                                    .asBitmap()

                                val glide = mgr.apply(requestOptions)
                                    .load(srcPath)
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                if (redis <= 0) {
                                    doAsync {
                                        uiThread {
                                            if (dontAnim) {
                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                    override fun setResource(resource: Bitmap?) {
                                                        image.setImageBitmap(resource)
                                                    }
                                                })
                                            } else {
                                                glide.into(image)
                                            }
                                        }
                                    }
                                } else {
                                    glide.into(object : BitmapImageViewTarget(image) {
                                        override fun setResource(resource: Bitmap?) {
                                            doAsync {
                                                uiThread {
                                                    if (redis > 0 && crop){
                                                        image.setImageBitmap(resource)
                                                    }else{
                                                        val circularBitmapDrawable =
                                                            RoundedBitmapDrawableFactory.create(image.resources, resource)
                                                        circularBitmapDrawable.cornerRadius =
                                                            image.dip(redis).toFloat() //设置圆角弧度
                                                        image.setImageDrawable(circularBitmapDrawable);
                                                    }
                                                }
                                            }

                                        }
                                    })
                                }

                            } else {
                                image.viewTreeObserver.addOnGlobalLayoutListener(object :
                                    ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        requestOptions.override(image.width, image.height)
                                        val manager: RequestManager? = assertManager(image)
                                            ?: return
                                        val glide = manager!!
                                            .asBitmap()
                                            .apply(requestOptions)
                                            .load(srcPath)
                                        if (redis <= 0) {
                                            doAsync {
                                                uiThread {
                                                    if (dontAnim) {
                                                        glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                            override fun setResource(resource: Bitmap?) {
                                                                image.setImageBitmap(resource)
                                                            }
                                                        })
                                                    } else {
                                                        glide.into(image)
                                                    }
                                                }
                                            }

                                        } else {
                                            glide.into(object : BitmapImageViewTarget(image) {
                                                override fun setResource(resource: Bitmap?) {
                                                    doAsync {
                                                        uiThread {
                                                            val circularBitmapDrawable =
                                                                RoundedBitmapDrawableFactory.create(
                                                                    image.resources,
                                                                    resource
                                                                )
                                                            circularBitmapDrawable.cornerRadius =
                                                                image.dip(redis).toFloat() //设置圆角弧度
                                                            image.setImageDrawable(circularBitmapDrawable);
                                                        }
                                                    }

                                                }
                                            })
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun intoView(
        image: ImageView?, @DrawableRes srcRes: Int?,
        dontAnim: Boolean = true, @DrawableRes placeHolder: Int? = 0,
        measureble: Boolean = false,
        crop: Boolean? = false,
        redis: Float? = 0f
    ) {
        try {
            if (image != null) {
                doAsync {
                    uiThread {
                        val requestOptions = RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)

                        requestOptions.placeholder(R.mipmap.ic_launcher)
                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                        if (dontAnim)
                            requestOptions.dontAnimate()

                        if (crop == true) {
                            requestOptions.centerCrop()
                        }

                        if (!measureble) {
                            val manager: RequestManager? = assertManager(image) ?: return@uiThread
                            val glide = manager!!
                                .asBitmap()
                                .apply(requestOptions)
                                .load(srcRes)
                            if ((redis ?: 0f) <= 0) {
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                doAsync {
                                    uiThread {
                                        try {
                                            if (dontAnim) {

                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                    override fun setResource(resource: Bitmap?) {
                                                        uiThread {
                                                            image.tag = null
                                                            image.setImageBitmap(resource)
                                                        }
                                                    }
                                                })
                                            } else {
                                                doAsync {
                                                    uiThread {
                                                        glide.into(image)
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            } else {
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                glide.into(object : BitmapImageViewTarget(image) {
                                    override fun setResource(resource: Bitmap?) {
                                        doAsync {
                                            uiThread {
                                                val circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(image.resources, resource)
                                                circularBitmapDrawable.cornerRadius =
                                                    image.dip(redis!!).toFloat() //设置圆角弧度
                                                image.tag = null
                                                image.setImageDrawable(circularBitmapDrawable)
                                            }
                                        }

                                    }
                                })
                            }
                        } else {
                            if (image.width != 0 && image.height != 0) {
                                requestOptions.override(image.width, image.height)
                                val manager: RequestManager? = assertManager(image)
                                    ?: return@uiThread
                                val mgr = manager!!
                                    .asBitmap()
                                if (redis?:0f>0f&&crop==true){
                                    mgr.transform(CenterCrop(),GlideRoundTransform(image.dip(redis!!)))
                                }
                                val glide = mgr
                                    .apply(requestOptions)
                                    .load(srcRes)
                                if ((redis ?: 0f) <= 0) {
                                    if (image.background is ColorDrawable) {
                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                    }
                                    doAsync {
                                        uiThread {
                                            try {
                                                if (dontAnim) {
                                                    glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                        override fun setResource(resource: Bitmap?) {
                                                            image.tag = null
                                                            image.setImageBitmap(resource)
                                                        }
                                                    })
                                                } else {
                                                    glide.into(image)
                                                }
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }

                                } else {
                                    if (image.background is ColorDrawable) {
                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                    }
                                    glide.into(object : BitmapImageViewTarget(image) {
                                        override fun setResource(resource: Bitmap?) {
                                            doAsync {
                                                uiThread {
                                                    val circularBitmapDrawable =
                                                        RoundedBitmapDrawableFactory.create(image.resources, resource)
                                                    circularBitmapDrawable.cornerRadius =
                                                        image.dip(redis!!).toFloat() //设置圆角弧度
                                                    image.tag = null
                                                    image.setImageDrawable(circularBitmapDrawable)
                                                }
                                            }

                                        }
                                    })
                                }

                            } else {
                                image.viewTreeObserver.addOnGlobalLayoutListener(object :
                                    ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        doAsync {
                                            uiThread {
                                                requestOptions.override(image.width, image.height)

                                                val manager: RequestManager? = assertManager(image)
                                                    ?: return@uiThread

                                                val mgr=manager!!
                                                    .asBitmap()
                                                if (redis?:0f>0f&&crop==true){
                                                    mgr.transform(CenterCrop(),GlideRoundTransform(image.dip(redis!!)))
                                                }
                                                val glide = mgr.apply(requestOptions)
                                                    .load(srcRes)
                                                if ((redis ?: 0f) <= 0) {
                                                    if (image.background is ColorDrawable) {
                                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                                    }
                                                    doAsync {
                                                        uiThread {
                                                            try {
                                                                if (dontAnim) {
                                                                    glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                                        override fun setResource(resource: Bitmap?) {
                                                                            image.tag = null
                                                                            image.setImageBitmap(resource)
                                                                        }
                                                                    })
                                                                } else {
                                                                    glide.into(image)
                                                                }
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    }

                                                } else {
                                                    if (image.background is ColorDrawable) {
                                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                                    }
                                                    glide.into(object : BitmapImageViewTarget(image) {
                                                        override fun setResource(resource: Bitmap?) {
                                                            doAsync {
                                                                uiThread {
                                                                    val circularBitmapDrawable =
                                                                        RoundedBitmapDrawableFactory.create(
                                                                            image.resources,
                                                                            resource
                                                                        )
                                                                    circularBitmapDrawable.cornerRadius =
                                                                        image.dip(redis!!).toFloat() //设置圆角弧度
                                                                    image.tag = null
                                                                    image.setImageDrawable(circularBitmapDrawable)
                                                                }
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun intoView(
        image: ImageView?,
        srcRes: File,
        dontAnim: Boolean = true, @DrawableRes placeHolder: Int? = 0,
        measureble: Boolean = false,
        crop: Boolean? = false,
        redis: Float? = 0f
    ) {
        try {
            if (image != null) {
                doAsync {
                    uiThread {
                        val requestOptions = RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)

                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                        if (dontAnim)
                            requestOptions.dontAnimate()
                        if (crop == true) {
                            requestOptions.centerCrop()
                        }

                        if (!measureble) {
                            val manager: RequestManager? = assertManager(image) ?: return@uiThread
                            val mgr = manager!!
                                .asBitmap()
                            if (redis?:0f>0f&&crop==true){
                                mgr.transform(CenterCrop(),GlideRoundTransform(image.dip(redis!!)))
                            }
                            val glide = mgr
                                .apply(requestOptions)
                                .load(srcRes)
                            if ((redis ?: 0f) <= 0) {
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                doAsync {
                                    uiThread {
                                        try {
                                            if (dontAnim) {
                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                    override fun setResource(resource: Bitmap?) {
                                                        image.setImageBitmap(resource)
                                                    }
                                                })
                                            } else {
                                                glide.into(image)
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }

                            } else {
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                glide.into(object : BitmapImageViewTarget(image) {
                                    override fun setResource(resource: Bitmap?) {
                                        doAsync {
                                            uiThread {
                                                val circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(image.resources, resource)
                                                circularBitmapDrawable.cornerRadius =
                                                    image.dip(redis!!).toFloat() //设置圆角弧度
                                                image.setImageDrawable(circularBitmapDrawable);
                                            }
                                        }

                                    }
                                })
                            }
                        } else {
                            if (image.width != 0 && image.height != 0) {
                                requestOptions.override(image.width, image.height)
                                val manager: RequestManager? = assertManager(image)
                                    ?: return@uiThread
                                val mgr = manager!!
                                    .asBitmap()
                                if (redis?:0f>0f&&crop==true){
                                    mgr.transform(CenterCrop(),GlideRoundTransform(image.dip(redis!!)))
                                }
                                val glide = mgr
                                    .apply(requestOptions)
                                    .load(srcRes)
                                if ((redis ?: 0f) <= 0) {
                                    if (image.background is ColorDrawable) {
                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                    }
                                    doAsync {
                                        uiThread {
                                            try {
                                                if (dontAnim) {
                                                    glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                        override fun setResource(resource: Bitmap?) {
                                                            image.setImageBitmap(resource)
                                                        }
                                                    })
                                                } else {
                                                    glide.into(image)
                                                }
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }

                                } else {
                                    if (image.background is ColorDrawable) {
                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                    }
                                    glide.into(object : BitmapImageViewTarget(image) {
                                        override fun setResource(resource: Bitmap?) {
                                            doAsync {
                                                uiThread {
                                                    val circularBitmapDrawable =
                                                        RoundedBitmapDrawableFactory.create(image.resources, resource)
                                                    circularBitmapDrawable.cornerRadius =
                                                        image.dip(redis!!).toFloat() //设置圆角弧度
                                                    image.setImageDrawable(circularBitmapDrawable);
                                                }
                                            }

                                        }
                                    })
                                }

                            } else {
                                image.viewTreeObserver.addOnGlobalLayoutListener(object :
                                    ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        doAsync {
                                            uiThread {
                                                requestOptions.override(image.width, image.height)
                                                val manager: RequestManager? = assertManager(image)
                                                    ?: return@uiThread
                                                val mgr = manager!!
                                                    .asBitmap()
                                                if (redis?:0f>0f&&crop==true){
                                                    mgr.transform(CenterCrop(),GlideRoundTransform(image.dip(redis!!)))
                                                }
                                                val glide = mgr
                                                    .apply(requestOptions)
                                                    .load(srcRes)
                                                if ((redis ?: 0f) <= 0) {
                                                    if (image.background is ColorDrawable) {
                                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                                    }
                                                    doAsync {
                                                        uiThread {
                                                            try {
                                                                if (dontAnim) {
                                                                    glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                                        override fun setResource(resource: Bitmap?) {
                                                                            image.setImageBitmap(resource)
                                                                        }
                                                                    })
                                                                } else {
                                                                    glide.into(image)
                                                                }
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    }

                                                } else {
                                                    if (image.background is ColorDrawable) {
                                                        image.background = ColorDrawable(Color.TRANSPARENT)
                                                    }
                                                    glide.into(object : BitmapImageViewTarget(image) {
                                                        override fun setResource(resource: Bitmap?) {
                                                            doAsync {
                                                                uiThread {
                                                                    val circularBitmapDrawable =
                                                                        RoundedBitmapDrawableFactory.create(
                                                                            image.resources,
                                                                            resource
                                                                        )
                                                                    circularBitmapDrawable.cornerRadius =
                                                                        image.dip(redis!!).toFloat() //设置圆角弧度
                                                                    image.setImageDrawable(circularBitmapDrawable);
                                                                }
                                                            }

                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun intoCircleView(
        image: ImageView?,
        srcPath: String?,
        dontAnim: Boolean = false, @DrawableRes placeHolder: Int? = 0,
        measureble: Boolean = false
    ) {
        intoCircleView(image, srcPath, dontAnim, placeHolder, measureble, false)
    }

    fun intoCircleView(
        image: ImageView?, @DrawableRes srcPath: Int,
        dontAnim: Boolean = false, @DrawableRes placeHolder: Int? = 0,
        measureble: Boolean = false
    ) {
        intoCircleView(image, srcPath, dontAnim, placeHolder, measureble, false)
    }

    fun intoCircleView(
        image: ImageView?,
        srcPath: String?,
        dontAnim: Boolean = false,
        @DrawableRes placeHolder: Int? = 0,
        measureble: Boolean = false,
        withWhiteBorder: Boolean = false,
        borderWidth: Int = 2
    ) {
        try {
            doAsync {
                uiThread {
                    if (image != null) {
                        val requestOptions = RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)
                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                        requestOptions.circleCrop()
                        if (dontAnim) {
                            requestOptions.dontAnimate()
                        }
                        if (withWhiteBorder) {
                            requestOptions.transform(GlideWhiteCircleTransform(image.context, borderWidth, Color.WHITE))
                        }
                        if (placeHolder != 0) {
                            requestOptions.placeholder(placeHolder!!)
                        }

                        if (!measureble) {

                            val manager: RequestManager? = assertManager(image)
                                ?: return@uiThread
                            val mgr = manager!!
                                .asBitmap()
                            mgr.transform(CenterCrop())

                            val glide = mgr
                                .apply(requestOptions)
                                .load(srcPath)
                            if (image.background is ColorDrawable) {
                                image.background = ColorDrawable(Color.TRANSPARENT)
                            }
                            doAsync {
                                uiThread {
                                    try {
                                        if (dontAnim) {
                                            glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                override fun setResource(resource: Bitmap?) {
                                                    image.setImageBitmap(resource)
                                                }
                                            })
                                        } else {
                                            glide.into(image)
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                            }

                        } else {
                            if (image.width != 0 && image.height != 0) {
                                requestOptions.override(image.width, image.height)
                                val manager: RequestManager? = assertManager(image)
                                    ?: return@uiThread
                                val mgr = manager!!
                                    .asBitmap()
                                mgr.transform(CenterCrop())
                                val glide = mgr
                                    .apply(requestOptions)
                                    .load(srcPath)
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                doAsync {
                                    uiThread {
                                        try {
                                            if (dontAnim) {
                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                    override fun setResource(resource: Bitmap?) {
                                                        image.setImageBitmap(resource)
                                                    }
                                                })
                                            } else {
                                                glide.into(image)
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }

                            } else {
                                image.viewTreeObserver.addOnGlobalLayoutListener(object :
                                    ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        doAsync {
                                            uiThread {
                                                requestOptions.override(image.width, image.height)
                                                val manager: RequestManager? = assertManager(image)
                                                    ?: return@uiThread
                                                val glide = manager!!
                                                    .asBitmap()
                                                    .apply(requestOptions)
                                                    .load(srcPath)
                                                if (image.background is ColorDrawable) {
                                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                                }
                                                doAsync {
                                                    uiThread {
                                                        try {
                                                            if (dontAnim) {
                                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                                    override fun setResource(resource: Bitmap?) {
                                                                        image.setImageBitmap(resource)
                                                                    }
                                                                })
                                                            } else {
                                                                glide.into(image)
                                                            }
                                                        } catch (e: Exception) {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun intoCircleView(
        image: ImageView?, @DrawableRes srcPath: Int,
        dontAnim: Boolean = false, @DrawableRes placeHolder: Int? = 0,
        measureble: Boolean = false,
        withWhiteBorder: Boolean = false
    ) {
        try {
            doAsync {
                uiThread {
                    if (image != null) {
                        val requestOptions = RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)
                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                        requestOptions.circleCrop()
                        if (withWhiteBorder) {
                            requestOptions.transform(GlideWhiteCircleTransform(image.context, 2, Color.WHITE))
                        }
                        if (placeHolder != 0) {
                            requestOptions.placeholder(placeHolder!!)
                        }
                        if (dontAnim) {
                            requestOptions.dontAnimate()
                        }
                        if (!measureble) {
                            val manager: RequestManager? = assertManager(image) ?: return@uiThread
                            val glide = manager!!
                                .asBitmap()
                                .apply(requestOptions)
                                .load(srcPath)
                            if (image.background is ColorDrawable) {
                                image.background = ColorDrawable(Color.TRANSPARENT)
                            }
                            doAsync {
                                uiThread {
                                    try {
                                        if (dontAnim) {
                                            glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                override fun setResource(resource: Bitmap?) {
                                                    image.setImageBitmap(resource)
                                                }
                                            })
                                        } else {
                                            glide.into(image)
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                            }

                        } else {
                            if (image.width != 0 && image.height != 0) {
                                requestOptions.override(image.width, image.height)
                                val manager: RequestManager? = assertManager(image)
                                    ?: return@uiThread
                                val glide = manager!!
                                    .asBitmap()
                                    .apply(requestOptions)
                                    .load(srcPath)
                                if (image.background is ColorDrawable) {
                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                }
                                doAsync {
                                    uiThread {
                                        try {
                                            if (dontAnim) {
                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                    override fun setResource(resource: Bitmap?) {
                                                        image.setImageBitmap(resource)
                                                    }
                                                })
                                            } else {
                                                glide.into(image)
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }

                            } else {
                                image.viewTreeObserver.addOnGlobalLayoutListener(object :
                                    ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        doAsync {
                                            uiThread {
                                                requestOptions.override(image.width, image.height)
                                                val manager: RequestManager? = assertManager(image)
                                                    ?: return@uiThread
                                                val glide = manager!!
                                                    .asBitmap()
                                                    .apply(requestOptions)
                                                    .load(srcPath)
                                                if (image.background is ColorDrawable) {
                                                    image.background = ColorDrawable(Color.TRANSPARENT)
                                                }
                                                doAsync {
                                                    uiThread {
                                                        try {
                                                            if (dontAnim) {
                                                                glide.into(object : ImageViewTarget<Bitmap>(image) {
                                                                    override fun setResource(resource: Bitmap?) {
                                                                        image.setImageBitmap(resource)
                                                                    }
                                                                })
                                                            } else {
                                                                glide.into(image)
                                                            }
                                                        } catch (e: Exception) {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @MainThread
    fun loadBitmap(context: Context, imgPath: String?): Bitmap? {
        try {
            if (imgPath.isNullOrBlank()) return null
            val requestOptions = RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL)

            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            return Observable.create<Bitmap?> {
                val manager: RequestManager? = assertManager(context)
                if (manager == null) return@create
                val glide = manager
                    .asBitmap()
                    .apply(requestOptions)
                    .load(imgPath)
                    .into(object : BitmapImageViewTarget(ImageView(context)) {
                        override fun setResource(resource: Bitmap?) {
                            if (resource == null) {
                                throw Exception("下载失败")
                            } else
                                it.onNext(resource)
                            it.onComplete()
                        }
                    })
            }.observeOn(Schedulers.io())
                .timeout(3, TimeUnit.SECONDS)
                .blockingFirst()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * @param repeat 重复次数,默认0,无限重复
     */
    fun intoGifView(imageView: ImageView?, @DrawableRes animRes: Int) {//, @IntRange(from = 0) repeat: Int? = 0
        try {
            doAsync {
                uiThread {
                    if (imageView == null) return@uiThread
                    var animationDrawable = imageView.background as? AnimationDrawable
                    if (animationDrawable == null) {
                        imageView.setBackgroundResource(animRes)
                        animationDrawable = imageView.background as? AnimationDrawable
                    }
                    animationDrawable?.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun intoGifImageView(
        image: ImageView?, @DrawableRes animRes: Int,
        circleCrop: Boolean = true,
        dontAnim: Boolean = true,
        repeat: Int = 0,
        completeBlock: (() -> Unit)? = null,
        width: Int = 0,
        height: Int = 0
    ) {
        try {
            doAsync {
                uiThread {
                    if (image == null) return@uiThread
                    val requestOptions = RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL)

                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                    if (circleCrop) {
                        requestOptions.circleCrop()
                    }
                    val manager: RequestManager? = assertManager(image) ?: return@uiThread
                    val glide = manager!!.asGif()
                        .apply(requestOptions)
                        .load(animRes)
                    if (repeat != 0) {
                        repeatBlock(glide, repeat, completeBlock)
                    }
                    doAsync {
                        uiThread {
                            try {
                                glide.into(image)
//                                if (dontAnim) {
//                                    glide.into(object : ImageViewTarget<GifDrawable>(image) {
//                                        override fun setResource(resource: GifDrawable?) {
//                                            image.setImageDrawable(resource)
//                                        }
//                                    })
//                                } else {
//                                    glide.into(image)
//                                }
                            } catch (e: Exception) {
                            }
                        }
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun resizeBlock(
        context: Context,
        glide: RequestBuilder<GifDrawable>,
        builder: RequestManager,
        width: Int,
        height: Int
    ) {
//        val resource = glide.submit().get()
//
//        val outputFile = builder.getCacheFile2(context, R.drawable.gif_beging)
//
//        ReEncodingGifResourceEncoder(context, Glide.get(context).bitmapPool)
//                .encode(GifDrawableResource(resource), outputFile, RequestOptions.fitCenterTransform().override(width, height).options)
    }

    @SuppressLint("CheckResult")
    private fun repeatBlock(glide: RequestBuilder<GifDrawable>, repeat: Int, completeBlock: (() -> Unit)?) {
        try {
            glide.listener(object : RequestListener<GifDrawable> {
                override fun onResourceReady(
                    resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {
                    resource?.setLoopCount(repeat)
                    var firstSkip = false
                    doAsync {
                        var running = true
                        while (running) {
                            if (resource == null) {
                                running = false
                                uiThread {
                                    completeBlock?.invoke()
                                }
                            } else {
                                if (resource.isRunning) {
                                    firstSkip = true
                                }
                                if (firstSkip && !(resource.isRunning)) {
                                    running = false
                                    uiThread {
                                        completeBlock?.invoke()
                                    }
                                }
                            }
                        }
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    doAsync {
                        uiThread {
                            completeBlock?.invoke()
                        }
                    }
                    return false
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun intoMorenImage(context: Context?,iv:ImageView,srcPath: String?,mipmap:Int){
        try {
            Glide.with(context!!).load(srcPath)
                .error(mipmap)
                .placeholder(mipmap)
                .fallback(mipmap)
                .into(iv)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }





}