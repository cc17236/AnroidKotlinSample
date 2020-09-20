package com.huawen.baselibrary.utils.utils

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.util.Log

/**
 * 应用程序被禁用项判断，如：是否禁止在通知栏显示通知、是否禁用悬浮窗
 * DateTime:2016/6/15 23:17
 * Builder:Android Studio
 *
 * @see AppOpsManager
 */
object AppOpsUtils {
    val OP_NONE = -1
    val OP_COARSE_LOCATION = 0
    val OP_FINE_LOCATION = 1
    val OP_GPS = 2
    val OP_VIBRATE = 3
    val OP_READ_CONTACTS = 4
    val OP_WRITE_CONTACTS = 5
    val OP_READ_CALL_LOG = 6
    val OP_WRITE_CALL_LOG = 7
    val OP_READ_CALENDAR = 8
    val OP_WRITE_CALENDAR = 9
    val OP_WIFI_SCAN = 10
    val OP_POST_NOTIFICATION = 11
    val OP_NEIGHBORING_CELLS = 12
    val OP_CALL_PHONE = 13
    val OP_READ_SMS = 14
    val OP_WRITE_SMS = 15
    val OP_RECEIVE_SMS = 16
    val OP_RECEIVE_EMERGECY_SMS = 17
    val OP_RECEIVE_MMS = 18
    val OP_RECEIVE_WAP_PUSH = 19
    val OP_SEND_SMS = 20
    val OP_READ_ICC_SMS = 21
    val OP_WRITE_ICC_SMS = 22
    val OP_WRITE_SETTINGS = 23
    val OP_SYSTEM_ALERT_WINDOW = 24
    val OP_ACCESS_NOTIFICATIONS = 25
    val OP_CAMERA = 26
    val OP_RECORD_AUDIO = 27
    val OP_PLAY_AUDIO = 28
    val OP_READ_CLIPBOARD = 29
    val OP_WRITE_CLIPBOARD = 30
    val OP_TAKE_MEDIA_BUTTONS = 31
    val OP_TAKE_AUDIO_FOCUS = 32
    val OP_AUDIO_MASTER_VOLUME = 33
    val OP_AUDIO_VOICE_VOLUME = 34
    val OP_AUDIO_RING_VOLUME = 35
    val OP_AUDIO_MEDIA_VOLUME = 36
    val OP_AUDIO_ALARM_VOLUME = 37
    val OP_AUDIO_NOTIFICATION_VOLUME = 38
    val OP_AUDIO_BLUETOOTH_VOLUME = 39
    val OP_WAKE_LOCK = 40
    val OP_MONITOR_LOCATION = 41
    val OP_MONITOR_HIGH_POWER_LOCATION = 42
    val OP_GET_USAGE_STATS = 43
    val OP_MUTE_MICROPHONE = 44
    val OP_TOAST_WINDOW = 45
    val OP_PROJECT_MEDIA = 46
    val OP_ACTIVATE_VPN = 47
    val OP_WRITE_WALLPAPER = 48
    val OP_ASSIST_STRUCTURE = 49
    val OP_ASSIST_SCREENSHOT = 50
    val OP_READ_PHONE_STATE = 51
    val OP_ADD_VOICEMAIL = 52
    val OP_USE_SIP = 53
    val OP_PROCESS_OUTGOING_CALLS = 54
    val OP_USE_FINGERPRINT = 55
    val OP_BODY_SENSORS = 56
    val OP_READ_CELL_BROADCASTS = 57
    val OP_MOCK_LOCATION = 58
    val OP_READ_EXTERNAL_STORAGE = 59
    val OP_WRITE_EXTERNAL_STORAGE = 60
    val OP_TURN_SCREEN_ON = 61
    private val TAG = "liyujiang"

    /**
     * 是否禁用通知
     */
    fun allowNotification(context: Context): Boolean {
        return isAllowed(context, OP_POST_NOTIFICATION)
    }

    /**
     * 是否禁用悬浮窗
     */
    fun allowFloatWindow(context: Context): Boolean {
        return isAllowed(context, OP_SYSTEM_ALERT_WINDOW)
    }

    /**
     * 是否禁用某项操作
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun isAllowed(context: Context, op: Int): Boolean {
        Log.d(TAG, "api level: " + Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT < 19) {
            return true
        }
        Log.d(TAG, "op is $op")
        val packageName = context.applicationContext.packageName
        val aom = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val types = arrayOf<Class<*>>(Int::class.java, Int::class.java, String::class.java)
        val args = arrayOf(op, Binder.getCallingUid(), packageName)
        try {
            val method = aom.javaClass.getDeclaredMethod("checkOpNoThrow", *types)
            val mode = method.invoke(aom, *args)
            Log.d(TAG, "invoke checkOpNoThrow: $mode")
            if (mode is Int && mode == AppOpsManager.MODE_ALLOWED) {
                Log.d(TAG, "allowed")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "invoke error: $e")
            e.printStackTrace()
        }

        return false
    }

}