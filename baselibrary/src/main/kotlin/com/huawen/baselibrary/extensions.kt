package com.huawen.baselibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.huawen.baselibrary.schedule.rxlifecycle2.components.support.RxFragment
import com.huawen.baselibrary.schedule.rxresult2.RxActivityResult
import com.huawen.baselibrary.utils.Debuger
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.AnkoException
import org.jetbrains.anko.internals.AnkoInternals
import java.io.Serializable
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
/**
 * @作者: #Administrator #
 *@日期: #2018/5/4 #
 *@时间: #2018年05月04日 15:02 #
 *@File:Kotlin File
 */
inline fun <T : View> View.findSafety(id: Int): T? {
    var t: T? = null
    try {
        t = findViewById(id)
    } catch (e: Exception) {
    }
    return t
}


inline fun String.versionCanUpgrade(selfVersion: String): Boolean {
    val self = selfVersion.replace("v", "").replace("V", "")
    val remote = this.replace("v", "").replace("V", "")
    Debuger.print("v1  ${self} , v2  $remote")
    if (self.trim().isEmpty()) return false
    if (TextUtils.isEmpty(remote.trim())) return false
    if (self == remote) {
        return false
    }
    if (self.replace(".", "").toIntOrNull() == null) return false
    if (remote.replace(".", "").toIntOrNull() == null) return false

    val version1Array = self.split(".")
    val version2Array = remote.split(".")
    var index = 0;
    val minLen = Math.min(version1Array.size, version2Array.size)
    var diff = 0
    try {
        while (index < minLen && ({
                diff =
                    Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index]);diff
            }()) == 0) {
            index++
        }
    } catch (e: Exception) {
        return false
    }
    if (diff == 0) {
        for (i in index until version1Array.size) {
            if (Integer.parseInt(version1Array[i]) > 0) {
                return version2Array.size >= version1Array.size
            }
        }
        for (i in index until version2Array.size) {
            if (Integer.parseInt(version2Array[i]) > 0) {
                return version1Array.size >= version1Array.size
            }
        }
        return false
    } else {
        return diff < 0
    }
}

/**
 * 获取本地视频的第一帧
 *
 * @param filePath
 * @return
 */
inline fun Any.getLocalVideoThumbnailFromString(filePath: String): Pair<Bitmap?, Long> {
    var bitmap: Bitmap? = null
    var duration: Long = 0
    //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
    //的接口，用于从输入的媒体文件中取得帧和元数据；
    val retriever = MediaMetadataRetriever()
    try {
        //根据文件路径获取缩略图
        retriever.setDataSource(filePath)
        //获得第一帧图片
        bitmap = retriever.frameAtTime
        duration =
            retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                .toLong()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return Pair(bitmap, duration)
}


inline fun View.unDisplayViewSize(): IntArray {
    val size = IntArray(2)
    val width = View.MeasureSpec.makeMeasureSpec(
        0,
        View.MeasureSpec.UNSPECIFIED
    )
    val height = View.MeasureSpec.makeMeasureSpec(
        0,
        View.MeasureSpec.UNSPECIFIED
    )
    measure(width, height)
    size[0] = measuredWidth
    size[1] = measuredHeight
    return size
}


/**
 * 防止重复点击
 *
 * @param target 目标view
 * @param listener 监听器
 */
inline fun View.preventRepeatedClick(ms: Long = 1000, noinline clickListener: (View) -> Unit) {
    preventRepeatedClick(ms, View.OnClickListener { v -> clickListener.invoke(v) })
}

inline fun View.preventRepeatedClick(ms: Long = 1000, clickListener: View.OnClickListener) {
    RxView.clicks(this).throttleFirst(ms, TimeUnit.MILLISECONDS)
        .subscribe({}, {}, {
            clickListener.onClick(this)
        }).toString()
}

/**
 * 判断服务是否正在运行
 *
 * @param serviceName
 * @return
 */
inline fun Context.isServiceRunning(serviceName: String): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceName == service.service.className) {
            return true
        }
    }
    return false
}

inline fun createIntent(vararg params: Pair<String, Any?>): Intent {
    val intent = Intent()
    if (params.isNotEmpty()) {
        params.forEach {
            val value = it.second
            when (value) {
                null -> intent.putExtra(it.first, null as Serializable?)
                is Int -> intent.putExtra(it.first, value)
                is Long -> intent.putExtra(it.first, value)
                is CharSequence -> intent.putExtra(it.first, value)
                is String -> intent.putExtra(it.first, value)
                is Float -> intent.putExtra(it.first, value)
                is Double -> intent.putExtra(it.first, value)
                is Char -> intent.putExtra(it.first, value)
                is Short -> intent.putExtra(it.first, value)
                is Boolean -> intent.putExtra(it.first, value)
                is Serializable -> intent.putExtra(it.first, value)
                is Bundle -> intent.putExtra(it.first, value)
                is Parcelable -> intent.putExtra(it.first, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                    else -> throw AnkoException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> intent.putExtra(it.first, value)
                is LongArray -> intent.putExtra(it.first, value)
                is FloatArray -> intent.putExtra(it.first, value)
                is DoubleArray -> intent.putExtra(it.first, value)
                is CharArray -> intent.putExtra(it.first, value)
                is ShortArray -> intent.putExtra(it.first, value)
                is BooleanArray -> intent.putExtra(it.first, value)
                else -> throw AnkoException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            return@forEach
        }
    }
    return intent
}

@SuppressLint("CheckResult")
inline fun <reified T : Activity> Activity.startRxActivityForResult(
    vararg params: Pair<String, Any?>,
    extra: Bundle? = null,
    crossinline fun0: ((Intent?, Int) -> Unit)
) {

    val intent = try {
        AnkoInternals.createIntent(this, T::class.java, params)
    } catch (e: Exception) {
        AnkoInternals.createIntent(this, T::class.java, arrayOf())
    }
    if (extra != null)
        intent.putExtras(extra)
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}


@SuppressLint("CheckResult")
inline fun Activity.startRxAccessibilityForResult(
    crossinline fun0: ((Intent?, Int) -> Unit)
) {

//    val packageURI = Uri.parse("package:" + getPackageName());, packageURI
    //注意这个是8.0新API
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}

@SuppressLint("CheckResult")
inline fun Service.startRxAccessibilityForResult(
    crossinline fun0: ((Intent?, Int) -> Unit)
) {

//    val packageURI = Uri.parse("package:" + getPackageName());, packageURI
    //注意这个是8.0新API
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}


@SuppressLint("CheckResult")
inline fun Activity.startRxActivityForResult3(
    activity: Activity,
    intent: Intent,
    crossinline fun0: (Intent?, Int) -> Unit
) {

    RxActivityResult.on(activity).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}

@SuppressLint("CheckResult")
inline fun Activity.startRxSettingsActivityForResult(
    crossinline fun0: ((Intent?, Int) -> Unit)
) {

    val packageURI = Uri.parse("package:" + getPackageName());
    //注意这个是8.0新API
    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}

@SuppressLint("CheckResult")
inline fun Activity.startRxNotifySettingsActivityForResult(
    crossinline fun0: ((Intent?, Int) -> Unit)
) {

    val packageURI = Uri.fromParts("package", packageName, null)
    //注意这个是8.0新API
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}

@SuppressLint("CheckResult")
inline fun Activity.startRxPermissionsActivityForResult(
    crossinline fun0: ((Intent?, Int) -> Unit)
) {

    val packageURI = Uri.parse("package:" + getPackageName());
    //注意这个是8.0新API
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}

@SuppressLint("CheckResult")
inline fun <reified T : Activity> Activity.startRxActivityForResult2(
    params: Array<out Pair<String, Any?>>?, crossinline fun0: ((Intent?, Int) -> Unit)
) {

    val intent = Intent(this, T::class.java)
    if (params?.isNotEmpty() == true) {
        params.forEach {
            val value = it.second
            when (value) {
                null -> intent.putExtra(it.first, null as Serializable?)
                is Int -> intent.putExtra(it.first, value)
                is Long -> intent.putExtra(it.first, value)
                is CharSequence -> intent.putExtra(it.first, value)
                is String -> intent.putExtra(it.first, value)
                is Float -> intent.putExtra(it.first, value)
                is Double -> intent.putExtra(it.first, value)
                is Char -> intent.putExtra(it.first, value)
                is Short -> intent.putExtra(it.first, value)
                is Boolean -> intent.putExtra(it.first, value)
                is Serializable -> intent.putExtra(it.first, value)
                is Bundle -> intent.putExtra(it.first, value)
                is Parcelable -> intent.putExtra(it.first, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                    else -> throw AnkoException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> intent.putExtra(it.first, value)
                is LongArray -> intent.putExtra(it.first, value)
                is FloatArray -> intent.putExtra(it.first, value)
                is DoubleArray -> intent.putExtra(it.first, value)
                is CharArray -> intent.putExtra(it.first, value)
                is ShortArray -> intent.putExtra(it.first, value)
                is BooleanArray -> intent.putExtra(it.first, value)
                else -> throw AnkoException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            return@forEach
        }
    }
    RxActivityResult.on(this).startIntent(intent)
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}

@SuppressLint("CheckResult")
inline fun <reified T : Activity> androidx.fragment.app.Fragment.startRxActivityForResult(
    vararg params: Pair<String, Any?>, crossinline fun0: ((Intent?, Int) -> Unit)
) {
    Debuger.print("跳转页面：" + T::class.simpleName + ",参数:" + mapOf(*params).toString())
    val ctx = activity as? Context
    if (ctx == null) {
        fun0.invoke(null, 0)
        return
    }

    RxActivityResult.on(this).startIntent(AnkoInternals.createIntent(ctx, T::class.java, params))
        .subscribe { result ->
            val data = result.data()
            val resultCode = result.resultCode()
            result.targetUI().apply {
                fun0(data, resultCode)
            }
        }
}


/**
 * 安全查找view,因为kotlin里面findView会抛异常
 */
inline fun View.findViewSafety(id: Int): View? {
    var t: View? = null
    try {
        t = findViewById(id)
    } catch (e: Exception) {
    }
    return t
}

//fun ColorStateList.reverseState(): ColorStateList {
//    var newStateList: ColorStateList? = null
//    try {
//        val slDraClass = ColorStateList::class.java
//        val getStateCountField = slDraClass.getDeclaredField("mColors")
//        val getStateSetMethod = slDraClass.getDeclaredMethod("getStateSet", Int::class.java)
//        val getDrawableMethod = slDraClass.getDeclaredMethod("getStateDrawable", Int::class.java)
//        val count = getStateCountField.get(this) as? Array<IntArray>
//        if (count?.size != 0) {
//            val stateTemp = arrayListOf<IntArray>()
//            val drawableTemp = arrayListOf<Drawable>()
//            for (i in 0 until count!!.size) {
//                val stateSet = getStateSetMethod.invoke(this, i) as? IntArray
//                val d = getDrawableMethod.invoke(this, i) as? Drawable
//                if (stateSet != null && d != null) {
//                    stateTemp.add(stateSet)
//                    drawableTemp.add(d)
//                }
//            }
//            if (!drawableTemp.isEmpty() && !stateTemp.isEmpty()) {
//                for (i in 0 until stateTemp.size) {
//                    val state = stateTemp[i]
//                    val pos = (stateTemp.size - 1) - i
//                    val drawable = drawableTemp[pos]
//                    newDrawable.addState(state, drawable)
//                }
//                stateTemp.clear()
//                drawableTemp.clear()
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    if (newStateList == null) {
//        newStateList = this
//    }
//    return newStateList
//}

fun StateListDrawable.reverseState(): StateListDrawable {
    val newDrawable = StateListDrawable()
    try {
        val slDraClass = StateListDrawable::class.java
        val getStateCountMethod = slDraClass.getDeclaredMethod("getStateCount")
        val getStateSetMethod = slDraClass.getDeclaredMethod("getStateSet", Int::class.java)
        val getDrawableMethod = slDraClass.getDeclaredMethod("getStateDrawable", Int::class.java)
        val count = getStateCountMethod.invoke(this) as? Int
        if (count != 0) {
            val stateTemp = arrayListOf<IntArray>()
            val drawableTemp = arrayListOf<Drawable>()
            for (i in 0 until count!!) {
                val stateSet = getStateSetMethod.invoke(this, i) as? IntArray
                val d = getDrawableMethod.invoke(this, i) as? Drawable
                if (stateSet != null && d != null) {
                    stateTemp.add(stateSet)
                    drawableTemp.add(d)
                }
            }
            if (!drawableTemp.isEmpty() && !stateTemp.isEmpty()) {
                for (i in 0 until stateTemp.size) {
                    val state = stateTemp[i]
                    val pos = (stateTemp.size - 1) - i
                    val drawable = drawableTemp[pos]
                    newDrawable.addState(state, drawable)
                }
                stateTemp.clear()
                drawableTemp.clear()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return newDrawable
}

/**
 * 可变参数出现任何一个为空指针或空字符都会返回true
 */
inline fun Any.isStringEmptyOnce(vararg args: String?): Boolean {
    loop@ args.forEach {
        if (it.isNullOrBlank()) {
            return true
        } else if (it?.toLowerCase().equals("null")) {
            return true
        }
    }
    return false
}

/**
 * 长度不小于判断
 */
inline fun String.lengthUnLessThan(length: Int): Boolean {
    return this.length >= length
}

/**
 * 正则验证手机号码格式是否正确
 */
inline fun String.isValidPhone(): Boolean {
    /*
    移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、178(新)、182、184、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、170、173、177、180、181、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
    return if (TextUtils.isEmpty(this)) {
        false
    } else {
        val num =
            Regex("[1][0123456789]\\d{9}")//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        //matches():字符串是否在给定的正则表达式匹配
        this.matches(num)
    }
}

inline fun String.mobileHidden(): String {
    if (isValidPhone()) {
        return "${substring(0, 3)}****${substring(length - 4, length)}"
    } else return "****"
}

inline fun String.toSubString(subSize: Int): String {
    if (this.length >= subSize) {
        return substring(0, subSize)
    } else return this
}

inline fun String.isValidJson(): Boolean {
    if (isNullOrEmpty()) {
        isValidPhone()
        return false
    }
    try {
        JsonParser().parse(this)
        return true
    } catch (e: JsonSyntaxException) {
        return false
    } catch (e: JsonParseException) {
        return false
    }
}

inline fun <reified T : Activity> RxFragment.startActivity(vararg params: Pair<String, Any?>) {
    if (activity == null) return
    AnkoInternals.internalStartActivity(activity as Context, T::class.java, params)
}


inline fun <reified T : Activity> RxFragment.startActivityForResult(
    requestCode: Int,
    vararg params: Pair<String, Any?>
) {
    if (activity == null) return
    startActivityForResult(
        AnkoInternals.createIntent(activity as Context, T::class.java, params),
        requestCode
    )
}

inline fun <reified T : Service> RxFragment.startService(vararg params: Pair<String, Any?>) {
    if (activity == null) return
    AnkoInternals.internalStartService(activity as Context, T::class.java, params)
}


inline fun <reified T : Service> RxFragment.stopService(vararg params: Pair<String, Any?>) {
    if (activity == null) return
    AnkoInternals.internalStopService(activity as Context, T::class.java, params)

}

inline fun <reified T : Any> RxFragment.intentFor(vararg params: Pair<String, Any?>): Intent {
    if (activity == null) return Intent()
    return AnkoInternals.createIntent(activity as Context, T::class.java, params)
}


inline fun <T> AnkoInternals.createIntentFromIntent(
    ctx: Context,
    clazz: Class<out T>,
    intent: Intent,
    params: Array<out Pair<String, Any?>>
): Intent {
    if (params?.isNotEmpty() == true) {
        params.forEach {
            val value = it.second
            when (value) {
                null -> intent.putExtra(it.first, null as Serializable?)
                is Int -> intent.putExtra(it.first, value)
                is Long -> intent.putExtra(it.first, value)
                is CharSequence -> intent.putExtra(it.first, value)
                is String -> intent.putExtra(it.first, value)
                is Float -> intent.putExtra(it.first, value)
                is Double -> intent.putExtra(it.first, value)
                is Char -> intent.putExtra(it.first, value)
                is Short -> intent.putExtra(it.first, value)
                is Boolean -> intent.putExtra(it.first, value)
                is Serializable -> intent.putExtra(it.first, value)
                is Bundle -> intent.putExtra(it.first, value)
                is Parcelable -> intent.putExtra(it.first, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                    else -> throw AnkoException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> intent.putExtra(it.first, value)
                is LongArray -> intent.putExtra(it.first, value)
                is FloatArray -> intent.putExtra(it.first, value)
                is DoubleArray -> intent.putExtra(it.first, value)
                is CharArray -> intent.putExtra(it.first, value)
                is ShortArray -> intent.putExtra(it.first, value)
                is BooleanArray -> intent.putExtra(it.first, value)
                else -> throw AnkoException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            return@forEach
        }
    }
    if (!clazz.simpleName.equals(Activity::class.java.simpleName))
        intent.setClass(ctx, clazz)
    return intent
}

public fun <T> List<T>.first(): T? {
    if (isEmpty())
        return null
    return this[0]
}

public fun <T> List<T>.last(): T? {
    if (isEmpty())
        return null
    return this[lastIndex]
}