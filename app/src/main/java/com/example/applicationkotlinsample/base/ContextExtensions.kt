package cn.aihuaiedu.school.base

import android.app.Activity
import android.app.ActivityOptions
import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import android.view.View
import cn.aihuaiedu.school.base.context.BaseActivity
import cn.aihuaiedu.school.base.context.BaseFragment
import com.huawen.baselibrary.utils.Debuger
import com.huawen.baselibrary.utils.isDoubleClick
import org.jetbrains.anko.internals.AnkoInternals


/**
 * @作者: #Administrator #
 *@日期: #2018/4/29 #
 *@时间: #2018年04月29日 20:59 #
 *@File:Kotlin File
 */

/**
 * unused extension
 */
inline fun Context.toDrawable(@DrawableRes id: Int): Drawable? {
    var draw: Drawable? = null
    try {
        draw = ContextCompat.getDrawable(this, id)
    } catch (e: Exception) {
    }
    return draw
}
inline fun BaseFragment<*, *>.setOnClickWithHead(head:View?, vararg ids: Int, crossinline click: (View) -> Unit) {
    ids.forEach {
        head?.findViewById<View>(it)?.setOnClickListener {
            if(isDoubleClick()){
                return@setOnClickListener
            }
            click.invoke(it)
        }
    }
}
inline fun BaseFragment<*, *>.setOnClick(vararg ids: Int, crossinline click: (View) -> Unit) {
    ids.forEach {
        findview(it)?.setOnClickListener {
            if(isDoubleClick()){
                return@setOnClickListener
            }
            click.invoke(it)
        }
    }
}

inline fun BaseActivity<*, *>.setOnClick(vararg ids: Int, crossinline click: (View) -> Unit) {
    ids.forEach {
        findview(it)?.setOnClickListener {
            if(isDoubleClick()){
                return@setOnClickListener
            }
            click.invoke(it)
        }
    }
}
inline fun BaseActivity<*, *>.setOnClickWithHead(head:View, vararg ids: Int, crossinline click: (View) -> Unit) {
    ids.forEach {
        head?.findViewById<View>(it)?.setOnClickListener {
            click.invoke(it)
        }
    }
}


inline fun Application.getMetaData(key: String): String {
    val appInfo = this.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    val bundle = appInfo.metaData
    return getMeta(bundle, key)
}

fun getMeta(bundle: Bundle, key: String): String {
    var msg = ""
    try {
        if (bundle.containsKey(key)) {
            val any = bundle.get(key)
            if (any is Int) {
                msg = any.toString()
            } else if (any is String) {
                msg = any
            } else if (any is Boolean) {
                msg = any.toString()
            } else if (any is Float) {
                msg = any.toString()
            } else if (any is Short) {
                msg = any.toString()
            } else if (any is Double) {
                msg = any.toString()
            } else if (any is Long) {
                msg = any.toString()
            } else if (any is Any) {
                msg = any.toString()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Debuger.print(e.message)
    }
    return msg
}

inline fun Activity.getMetaData(key: String): String {
    val info = this.packageManager
            .getActivityInfo(componentName,
                    PackageManager.GET_META_DATA)
    val bundle = info.metaData
    return getMeta(bundle, key)
}


inline fun Bitmap.resize(newWidth: Float, newHeight: Float): Bitmap? {
    // 获得图片的宽高.
    val width = getWidth()
    val height =getHeight()
    // 计算缩放比例.
    val scaleWidth = newWidth / width
    val scaleHeight = newHeight / height
    // 取得想要缩放的matrix参数.
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    // 得到新的图片.
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

inline fun Service.getMetaData(key: String, serviceClazz: Class<in Service>): String {
    val cn = ComponentName(this, serviceClazz)
    val info = this.packageManager.getServiceInfo(cn, PackageManager.GET_META_DATA)
    val bundle = info.metaData
    return getMeta(bundle, key)
}

inline fun <reified T : View> androidx.fragment.app.Fragment.find(@IdRes id: Int): T? = view?.findViewById(id) as? T

inline fun <reified T : Activity> Activity.startAnimAct(vararg params: Pair<String, Any?>) {
    if (Build.VERSION.SDK_INT >= 21) {
//        window.enterTransition = Explode().setDuration(2000)
//        window.exitTransition = Explode().setDuration(2000)
        startActivity(AnkoInternals.createIntent(this, T::class.java, params), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    } else {
        startActivity(AnkoInternals.createIntent(this, T::class.java, params))
    }
}
