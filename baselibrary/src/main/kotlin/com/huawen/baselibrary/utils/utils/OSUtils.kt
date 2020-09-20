package com.huawen.baselibrary.utils.utils

import android.os.Environment

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties
import java.util.regex.Pattern

/**
 * <pre>
 * author : Senh Linsh
 * github : https://github.com/SenhLinsh
 * date   : 2017/11/10
 * desc   : 工具类: OS 系统相关
 * 简介  : 由于国内定制系统的泛滥, 不同定制系统的一些功能或实现方法会有所不同, 如果需要做到足够好的适配工作, 需要
 * 对不同的定制系统做一些专门的适配.
 * API  : 获取 ROM 类型等
</pre> *
 */
object OSUtils {

    /**
     * ROM 类型
     */
    /**
     * 获取 ROM 类型
     *
     * @return ROM
     */
    val romType = initRomType()

    private val KEY_DISPLAY_ID = "ro.build.display.id"
    private val KEY_BASE_OS_VERSION = "ro.build.version.base_os"
    private val KEY_CLIENT_ID_BASE = "ro.com.google.clientidbase"

    // 小米 : MIUI
    private val KEY_MIUI_VERSION = "ro.build.version.incremental" // "7.6.15"
    private val KEY_MIUI_VERSION_NANE = "ro.miui.ui.version.name" // "V8"
    private val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code" // "6"

    private val VALUE_MIUI_CLIENT_ID_BASE = "android-xiaomi"
    // 华为 : EMUI
    private val KEY_EMUI_VERSION = "ro.build.version.emui" // "EmotionUI_3.0"
    private val KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level" //
    private val KEY_EMUI_SYSTEM_VERSION = "ro.confg.hw_systemversion" // "T1-A21wV100R001C233B008_SYSIMG"
    // 魅族 : Flyme
    private val KEY_FLYME_PUBLISHED = "ro.flyme.published" // "true"
    private val KEY_FLYME_SETUP = "ro.meizu.setupwizard.flyme" // "true"

    private val VALUE_FLYME_DISPLAY_ID_CONTAIN = "Flyme" // "Flyme OS 4.5.4.2U"
    // OPPO : ColorOS
    private val KEY_COLOROS_VERSION = "ro.oppo.theme.version" // "703"
    private val KEY_COLOROS_THEME_VERSION = "ro.oppo.version" // ""
    private val KEY_COLOROS_ROM_VERSION = "ro.rom.different.version" // "ColorOS2.1"

    private val VALUE_COLOROS_BASE_OS_VERSION_CONTAIN = "OPPO" // "OPPO/R7sm/R7sm:5.1.1/LMY47V/1440928800:user/release-keys"
    private val VALUE_COLOROS_CLIENT_ID_BASE = "android-oppo"
    // vivo : FuntouchOS
    private val KEY_FUNTOUCHOS_BOARD_VERSION = "ro.vivo.board.version" // "MD"
    private val KEY_FUNTOUCHOS_OS_NAME = "ro.vivo.os.name" // "Funtouch"
    private val KEY_FUNTOUCHOS_OS_VERSION = "ro.vivo.os.version" // "3.0"
    private val KEY_FUNTOUCHOS_DISPLAY_ID = "ro.vivo.os.build.display.id" // "FuntouchOS_3.0"
    private val KEY_FUNTOUCHOS_ROM_VERSION = "ro.vivo.rom.version" // "rom_3.1"

    private val VALUE_FUNTOUCHOS_CLIENT_ID_BASE = "android-vivo"
    // Samsung
    private val VALUE_SAMSUNG_BASE_OS_VERSION_CONTAIN = "samsung" // "samsung/zeroltezc/zeroltechn:6.0.1/MMB29K/G9250ZCU2DQD1:user/release-keys"
    private val VALUE_SAMSUNG_CLIENT_ID_BASE = "android-samsung"
    // Sony
    private val KEY_SONY_PROTOCOL_TYPE = "ro.sony.irremote.protocol_type" // "2"
    private val KEY_SONY_ENCRYPTED_DATA = "ro.sony.fota.encrypteddata" // "supported"

    private val VALUE_SONY_CLIENT_ID_BASE = "android-sonyericsson"
    // 乐视 : eui
    private val KEY_EUI_VERSION = "ro.letv.release.version" // "5.9.023S"
    private val KEY_EUI_VERSION_DATE = "ro.letv.release.version_date" // "5.9.023S_03111"
    private val KEY_EUI_NAME = "ro.product.letv_name" // "乐1s"
    private val KEY_EUI_MODEL = "ro.product.letv_model" // "Letv X500"
    // 金立 : amigo
    private val KEY_AMIGO_ROM_VERSION = "ro.gn.gnromvernumber" // "GIONEE ROM5.0.16"
    private val KEY_AMIGO_SYSTEM_UI_SUPPORT = "ro.gn.amigo.systemui.support" // "yes"

    private val VALUE_AMIGO_DISPLAY_ID_CONTAIN = "amigo" // "amigo3.5.1"
    private val VALUE_AMIGO_CLIENT_ID_BASE = "android-gionee"
    // 酷派 : yulong
    private val KEY_YULONG_VERSION_RELEASE = "ro.yulong.version.release" // "5.1.046.P1.150921.8676_M01"
    private val KEY_YULONG_VERSION_TAG = "ro.yulong.version.tag" // "LC"

    private val VALUE_YULONG_CLIENT_ID_BASE = "android-coolpad"
    // HTC : Sense
    private val KEY_SENSE_BUILD_STAGE = "htc.build.stage" // "2"
    private val KEY_SENSE_BLUETOOTH_SAP = "ro.htc.bluetooth.sap" // "true"

    private val VALUE_SENSE_CLIENT_ID_BASE = "android-htc-rev"
    // LG : LG
    private val KEY_LG_SW_VERSION = "ro.lge.swversion" // "D85720b"
    private val KEY_LG_SW_VERSION_SHORT = "ro.lge.swversion_short" // "V20b"
    private val KEY_LG_FACTORY_VERSION = "ro.lge.factoryversion" // "LGD857AT-00-V20b-CUO-CN-FEB-17-2015+0"
    // 联想
    private val KEY_LENOVO_DEVICE = "ro.lenovo.device" // "phone"
    private val KEY_LENOVO_PLATFORM = "ro.lenovo.platform" // "qualcomm"
    private val KEY_LENOVO_ADB = "ro.lenovo.adb" // "apkctl,speedup"

    private val VALUE_LENOVO_CLIENT_ID_BASE = "android-lenovo"

    /**
     * 初始化 ROM 类型
     */
    private fun initRomType(): ROM {
        var rom = ROM.Other
        var `is`: FileInputStream? = null
        try {
            val buildProperties = Properties()
            `is` = FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
            buildProperties.load(`is`)

            if (buildProperties.containsKey(KEY_MIUI_VERSION_NANE) || buildProperties.containsKey(KEY_MIUI_VERSION_CODE)) {
                // MIUI
                rom = ROM.MIUI
                if (buildProperties.containsKey(KEY_MIUI_VERSION_NANE)) {
                    val versionName = buildProperties.getProperty(KEY_MIUI_VERSION_NANE)
                    if (StringUtils.notEmpty(versionName) && versionName.matches("[Vv]\\d+".toRegex())) { // V8
                        try {
                            rom.baseVersion = Integer.parseInt(versionName.split("[Vv]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                if (buildProperties.containsKey(KEY_MIUI_VERSION)) {
                    val versionStr = buildProperties.getProperty(KEY_MIUI_VERSION)
                    if (StringUtils.notEmpty(versionStr) && versionStr.matches("[\\d.]+".toRegex())) {
                        rom.version = versionStr
                    }
                }
            } else if (buildProperties.containsKey(KEY_EMUI_VERSION) || buildProperties.containsKey(KEY_EMUI_API_LEVEL)
                    || buildProperties.containsKey(KEY_EMUI_SYSTEM_VERSION)) {
                // EMUI
                rom = ROM.EMUI
                if (buildProperties.containsKey(KEY_EMUI_VERSION)) {
                    val versionStr = buildProperties.getProperty(KEY_EMUI_VERSION)
                    val matcher = Pattern.compile("EmotionUI_([\\d.]+)").matcher(versionStr) // EmotionUI_3.0
                    if (StringUtils.notEmpty(versionStr) && matcher.find()) {
                        try {
                            val version = matcher.group(1)
                            rom.version = version
                            rom.baseVersion = Integer.parseInt(version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            } else if (buildProperties.containsKey(KEY_FLYME_SETUP) || buildProperties.containsKey(KEY_FLYME_PUBLISHED)) {
                // Flyme
                rom = ROM.Flyme
                if (buildProperties.containsKey(KEY_DISPLAY_ID)) {
                    val versionStr = buildProperties.getProperty(KEY_DISPLAY_ID)
                    val matcher = Pattern.compile("Flyme[^\\d]*([\\d.]+)[^\\d]*").matcher(versionStr) // Flyme OS 4.5.4.2U
                    if (StringUtils.notEmpty(versionStr) && matcher.find()) {
                        try {
                            val version = matcher.group(1)
                            rom.version = version
                            rom.baseVersion = Integer.parseInt(version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            } else if (buildProperties.containsKey(KEY_COLOROS_VERSION) || buildProperties.containsKey(KEY_COLOROS_THEME_VERSION)
                    || buildProperties.containsKey(KEY_COLOROS_ROM_VERSION)) {
                // ColorOS
                rom = ROM.ColorOS
                if (buildProperties.containsKey(KEY_COLOROS_ROM_VERSION)) {
                    val versionStr = buildProperties.getProperty(KEY_COLOROS_ROM_VERSION)
                    val matcher = Pattern.compile("ColorOS([\\d.]+)").matcher(versionStr) // ColorOS2.1
                    if (StringUtils.notEmpty(versionStr) && matcher.find()) {
                        try {
                            val version = matcher.group(1)
                            rom.version = version
                            rom.baseVersion = Integer.parseInt(version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            } else if (buildProperties.containsKey(KEY_FUNTOUCHOS_OS_NAME) || buildProperties.containsKey(KEY_FUNTOUCHOS_OS_VERSION)
                    || buildProperties.containsKey(KEY_FUNTOUCHOS_DISPLAY_ID)) {
                // FuntouchOS
                rom = ROM.FuntouchOS
                if (buildProperties.containsKey(KEY_FUNTOUCHOS_OS_VERSION)) {
                    val versionStr = buildProperties.getProperty(KEY_FUNTOUCHOS_OS_VERSION)
                    if (StringUtils.notEmpty(versionStr) && versionStr.matches("[\\d.]+".toRegex())) { // 3.0
                        try {
                            rom.version = versionStr
                            rom.baseVersion = Integer.parseInt(versionStr.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            } else if (buildProperties.containsKey(KEY_EUI_VERSION) || buildProperties.containsKey(KEY_EUI_NAME)
                    || buildProperties.containsKey(KEY_EUI_MODEL)) {
                // EUI
                rom = ROM.EUI
                if (buildProperties.containsKey(KEY_EUI_VERSION)) {
                    val versionStr = buildProperties.getProperty(KEY_EUI_VERSION)
                    val matcher = Pattern.compile("([\\d.]+)[^\\d]*").matcher(versionStr) // 5.9.023S
                    if (StringUtils.notEmpty(versionStr) && matcher.find()) {
                        try {
                            val version = matcher.group(1)
                            rom.version = version
                            rom.baseVersion = Integer.parseInt(version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            } else if (buildProperties.containsKey(KEY_AMIGO_ROM_VERSION) || buildProperties.containsKey(KEY_AMIGO_SYSTEM_UI_SUPPORT)) {
                // amigo
                rom = ROM.AmigoOS
                if (buildProperties.containsKey(KEY_DISPLAY_ID)) {
                    val versionStr = buildProperties.getProperty(KEY_DISPLAY_ID)
                    val matcher = Pattern.compile("amigo([\\d.]+)[a-zA-Z]*").matcher(versionStr) // "amigo3.5.1"
                    if (StringUtils.notEmpty(versionStr) && matcher.find()) {
                        try {
                            val version = matcher.group(1)
                            rom.version = version
                            rom.baseVersion = Integer.parseInt(version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            } else if (buildProperties.containsKey(KEY_SONY_PROTOCOL_TYPE) || buildProperties.containsKey(KEY_SONY_ENCRYPTED_DATA)) {
                // Sony
                rom = ROM.Sony
            } else if (buildProperties.containsKey(KEY_YULONG_VERSION_RELEASE) || buildProperties.containsKey(KEY_YULONG_VERSION_TAG)) {
                // YuLong
                rom = ROM.YuLong
            } else if (buildProperties.containsKey(KEY_SENSE_BUILD_STAGE) || buildProperties.containsKey(KEY_SENSE_BLUETOOTH_SAP)) {
                // Sense
                rom = ROM.Sense
            } else if (buildProperties.containsKey(KEY_LG_SW_VERSION) || buildProperties.containsKey(KEY_LG_SW_VERSION_SHORT)
                    || buildProperties.containsKey(KEY_LG_FACTORY_VERSION)) {
                // LG
                rom = ROM.LG
            } else if (buildProperties.containsKey(KEY_LENOVO_DEVICE) || buildProperties.containsKey(KEY_LENOVO_PLATFORM)
                    || buildProperties.containsKey(KEY_LENOVO_ADB)) {
                // Lenovo
                rom = ROM.Lenovo
            } else if (buildProperties.containsKey(KEY_DISPLAY_ID)) {
                val displayId = buildProperties.getProperty(KEY_DISPLAY_ID)
                if (StringUtils.notEmpty(displayId)) {
                    if (displayId.contains(VALUE_FLYME_DISPLAY_ID_CONTAIN)) {
                        return ROM.Flyme
                    } else if (displayId.contains(VALUE_AMIGO_DISPLAY_ID_CONTAIN)) {
                        return ROM.AmigoOS
                    }
                }
            } else if (buildProperties.containsKey(KEY_BASE_OS_VERSION)) {
                val baseOsVersion = buildProperties.getProperty(KEY_BASE_OS_VERSION)
                if (StringUtils.notEmpty(baseOsVersion)) {
                    if (baseOsVersion.contains(VALUE_COLOROS_BASE_OS_VERSION_CONTAIN)) {
                        return ROM.ColorOS
                    } else if (baseOsVersion.contains(VALUE_SAMSUNG_BASE_OS_VERSION_CONTAIN)) {
                        return ROM.SamSung
                    }
                }
            } else if (buildProperties.containsKey(KEY_CLIENT_ID_BASE)) {
                val clientIdBase = buildProperties.getProperty(KEY_CLIENT_ID_BASE)
                when (clientIdBase) {
                    VALUE_MIUI_CLIENT_ID_BASE -> return ROM.MIUI
                    VALUE_COLOROS_CLIENT_ID_BASE -> return ROM.ColorOS
                    VALUE_FUNTOUCHOS_CLIENT_ID_BASE -> return ROM.FuntouchOS
                    VALUE_SAMSUNG_CLIENT_ID_BASE -> return ROM.SamSung
                    VALUE_SONY_CLIENT_ID_BASE -> return ROM.Sony
                    VALUE_YULONG_CLIENT_ID_BASE -> return ROM.YuLong
                    VALUE_SENSE_CLIENT_ID_BASE -> return ROM.Sense
                    VALUE_LENOVO_CLIENT_ID_BASE -> return ROM.Lenovo
                    VALUE_AMIGO_CLIENT_ID_BASE -> return ROM.AmigoOS
                    else -> {
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return rom
    }

    enum class ROM {
        MIUI, // 小米
        Flyme, // 魅族
        EMUI, // 华为
        ColorOS, // OPPO
        FuntouchOS, // vivo
        SmartisanOS, // 锤子
        EUI, // 乐视
        Sense, // HTC
        AmigoOS, // 金立
        _360OS, // 奇酷360
        NubiaUI, // 努比亚
        H2OS, // 一加
        YunOS, // 阿里巴巴
        YuLong, // 酷派

        SamSung, // 三星
        Sony, // 索尼
        Lenovo, // 联想
        LG, // LG

        Google, // 原生

        Other;
        // CyanogenMod, Lewa OS, 百度云OS, Tencent OS, 深度OS, IUNI OS, Tapas OS, Mokee

        var baseVersion = -1
            internal set
        var version: String? = null
            internal set
    }
}
