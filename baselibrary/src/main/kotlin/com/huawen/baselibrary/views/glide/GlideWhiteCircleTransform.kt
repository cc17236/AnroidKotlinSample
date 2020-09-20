package com.huawen.baselibrary.views.glide

import android.content.Context
import android.content.res.Resources
import android.graphics.*

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import java.security.MessageDigest

class GlideWhiteCircleTransform : BitmapTransformation {

    private var mBorderPaint: Paint?=null
    private var mBorderWidth: Float?=null


    constructor(context: Context, borderWidth: Int, borderColor: Int) : super() {
        mBorderWidth = Resources.getSystem().displayMetrics.density * borderWidth

        mBorderPaint = Paint()
        mBorderPaint?.isDither = true
        mBorderPaint?.isAntiAlias = true
        mBorderPaint?.color = borderColor
        mBorderPaint?.style = Paint.Style.STROKE
        mBorderPaint?.strokeWidth = mBorderWidth?:0f
    }


    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null

        val size = (Math.min(source.width.toFloat(), source.height.toFloat()) - (mBorderWidth?:0f) / 2.0f)
        val x = (source.width.toFloat() - size) / 2.0f
        val y = (source.height.toFloat() - size) / 2.0f
        // TODO this could be acquired from the pool too
        val squared = Bitmap.createBitmap(source, x.toInt(), y.toInt(), size.toInt(), size.toInt())
        var result: Bitmap? = pool.get(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
        if (result == null) {
            result = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result!!)
        val paint = Paint()

        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        if (mBorderPaint != null) {
            val borderRadius = (r - (mBorderWidth?:0f) / 2.0f)
            canvas.drawCircle(r, r, borderRadius, mBorderPaint!!)
        }
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }
}