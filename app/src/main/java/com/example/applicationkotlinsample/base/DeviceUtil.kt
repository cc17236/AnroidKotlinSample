@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package cn.aihuaiedu.school.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.res.Configuration
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import com.example.applicationkotlinsample.DisposalApp
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Method
import java.util.*
import java.util.regex.Pattern

object DeviceUtil {

    /**
     *   获得独一无二的Psuedo ID
     */
    fun uniquePsuedoID(): String {
        if (!TextUtils.isEmpty(DisposalApp.app?.mDeviceID)) {
            return DisposalApp.app?.mDeviceID!!
        }

        var serial: String? = null

        val m_szDevIDShort = "35" +
                Build.BOARD.length % 10 + Build.BRAND.length % 10 +
                Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 +
                Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
                Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +
                Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +
                Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                Build.USER.length % 10 //13 位

        try {
            serial = android.os.Build::class.java.getField("SERIAL").get(null).toString()
            if (TextUtils.isEmpty(serial)) {
                serial = "serial"
            }
            //API>=9 使用serial号
            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString();
        } catch (exception: Exception) {
            //serial需要一个初始化
            serial = "serial" // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return UUID(m_szDevIDShort.hashCode().toLong(), serial!!.hashCode().toLong()).toString()
    }


    fun getDeviceUUID(context: Context): String {
        var uuid = ""
        try {
            uuid = loadDeviceUUID(context)
            if (TextUtils.isEmpty(uuid)) {
                uuid = buildDeviceUUID(context)
                saveDeviceUUID(context, uuid)
            }
        } catch (e: Exception) {
        }
        return uuid
    }

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun buildDeviceUUID(context: Context): String {
        var androidId = getAndroidId(context)
        if ("9774d56d682e549c".equals(androidId)) {
            val random = Random()
            androidId =
                "${Integer.toHexString(random.nextInt())}${Integer.toHexString(random.nextInt())}${Integer.toHexString(
                    random.nextInt()
                )}"
        }
        return UUID(androidId.hashCode().toLong(), getBuildInfo().hashCode().toLong()).toString()
    }

    fun getBuildInfo(): String {
//这里选用了几个不会随系统更新而改变的值
        val buildSB = StringBuffer()
        buildSB.append(Build.BRAND).append("/")
        buildSB.append(Build.PRODUCT).append("/")
        buildSB.append(Build.DEVICE).append("/")
        buildSB.append(Build.ID).append("/")
        buildSB.append(Build.VERSION.INCREMENTAL)
        return buildSB.toString()
// return Build.FINGERPRINT;
    }


    fun saveDeviceUUID(context: Context, uuid: String) {
        context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE)
            .edit()
            .putString("uuid", uuid)
            .apply()
    }

    fun loadDeviceUUID(context: Context): String {
        return context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE)
            .getString("uuid", null)?:""
    }

    private val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private val KEY_FLYME_VERSION_NAME = "ro.build.display.id"
    private val FLYME = "flyme"
    private val ZTEC2016 = "zte c2016"
    private val ZUKZ1 = "zuk z1"
    private val ESSENTIAL = "essential"
    private val MEIZUBOARD = arrayOf("m9", "M9", "mx", "MX")
    private var sMiuiVersionName: String? = null
    private var sFlymeVersionName: String? = null
    private var sIsTabletChecked = false
    private var sIsTabletValue = false
    private val BRAND = Build.BRAND.toLowerCase()

    init {
        val properties = Properties()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
                properties.load(fileInputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }

        var clzSystemProperties: Class<*>? = null
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties")
            val getMethod = clzSystemProperties!!.getDeclaredMethod("get", String::class.java)
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME)
            //flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun _isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 判断是否为平板设备
     */
    fun isTablet(context: Context): Boolean {
        if (sIsTabletChecked) {
            return sIsTabletValue
        }
        sIsTabletValue = _isTablet(context)
        sIsTabletChecked = true
        return sIsTabletValue
    }

    /**
     * 判断是否是flyme系统
     */
    fun isFlyme(): Boolean {
        return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName!!.contains(FLYME)
    }

    /**
     * 判断是否是MIUI系统
     */
    fun isMIUI(): Boolean {
        return !TextUtils.isEmpty(sMiuiVersionName)
    }

    fun isMIUIV5(): Boolean {
        return "v5" == sMiuiVersionName
    }

    fun isMIUIV6(): Boolean {
        return "v6" == sMiuiVersionName
    }

    fun isMIUIV7(): Boolean {
        return "v7" == sMiuiVersionName
    }

    fun isMIUIV8(): Boolean {
        return "v8" == sMiuiVersionName
    }

    fun isMIUIV9(): Boolean {
        return "v9" == sMiuiVersionName
    }

    fun isFlymeVersionHigher5_2_4(): Boolean {
        //查不到默认高于5.2.4
        var isHigher = true
        if (sFlymeVersionName != null && sFlymeVersionName != "") {
            val pattern = Pattern.compile("(\\d+\\.){2}\\d")
            val matcher = pattern.matcher(sFlymeVersionName)
            if (matcher.find()) {
                val versionString = matcher.group()
                if (versionString != null && versionString != "") {
                    val version = versionString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (version.size == 3) {
                        if (Integer.valueOf(version[0]) < 5) {
                            isHigher = false
                        } else if (Integer.valueOf(version[0]) > 5) {
                            isHigher = true
                        } else {
                            if (Integer.valueOf(version[1]) < 2) {
                                isHigher = false
                            } else if (Integer.valueOf(version[1]) > 2) {
                                isHigher = true
                            } else {
                                if (Integer.valueOf(version[2]) < 4) {
                                    isHigher = false
                                } else if (Integer.valueOf(version[2]) >= 5) {
                                    isHigher = true
                                }
                            }
                        }
                    }

                }
            }
        }
        return isMeizu() && isHigher
    }

    fun isMeizu(): Boolean {
        return isPhone(MEIZUBOARD) || isFlyme()
    }

    /**
     * 判断是否为小米
     * https://dev.mi.com/doc/?p=254
     */
    fun isXiaomi(): Boolean {
        return Build.MANUFACTURER.toLowerCase() == "xiaomi"
    }

    fun isVivo(): Boolean {
        return BRAND.contains("vivo") || BRAND.contains("bbk")
    }

    fun isOppo(): Boolean {
        return BRAND.contains("oppo")
    }

    fun isHuawei(): Boolean {
        return BRAND.contains("huawei") || BRAND.contains("honor")
    }

    fun isEssentialPhone(): Boolean {
        return BRAND.contains("essential")
    }


    /**
     * 判断是否为 ZUK Z1 和 ZTK C2016。
     * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
     */
    fun isZUKZ1(): Boolean {
        val board = Build.MODEL
        return board != null && board.toLowerCase().contains(ZUKZ1)
    }

    fun isZTKC2016(): Boolean {
        val board = Build.MODEL
        return board != null && board.toLowerCase().contains(ZTEC2016)
    }

    private fun isPhone(boards: Array<String>): Boolean {
        val board = Build.BOARD ?: return false
        for (board1 in boards) {
            if (board == board1) {
                return true
            }
        }
        return false
    }

    /**
     * 判断悬浮窗权限（目前主要用户魅族与小米的检测）。
     */
    fun isFloatWindowOpAllowed(context: Context): Boolean {
        val version = Build.VERSION.SDK_INT
        return if (version >= 19) {
            checkOp(context, 24)  // 24 是AppOpsManager.OP_SYSTEM_ALERT_WINDOW 的值，该值无法直接访问
        } else {
            try {
                context.applicationInfo.flags and (1 shl 27) == 1 shl 27
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }
    }

    @TargetApi(19)
    private fun checkOp(context: Context, op: Int): Boolean {
        val version = Build.VERSION.SDK_INT
        if (version >= Build.VERSION_CODES.KITKAT) {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val method = manager.javaClass.getDeclaredMethod(
                    "checkOp",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                val property = method.invoke(
                    manager, op,
                    Binder.getCallingUid(), context.packageName
                ) as Int
                return AppOpsManager.MODE_ALLOWED == property
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return false
    }

    private fun getLowerCaseName(p: Properties, get: Method, key: String): String? {
        var name: String? = p.getProperty(key)
        if (name == null) {
            try {
                name = get.invoke(null, key) as String
            } catch (ignored: Exception) {
            }

        }
        if (name != null) name = name.toLowerCase()
        return name
    }
}