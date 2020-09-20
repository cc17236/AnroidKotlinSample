package com.huawen.baselibrary.utils.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.provider.Settings

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * <pre>
 * author : Senh Linsh
 * github : https://github.com/SenhLinsh
 * date   : 2017/11/10
 * desc   : 工具类: Intent 意图相关
 *
 * 注: 部分 API 直接参考或使用 https://github.com/Blankj/AndroidUtilCode 中 IntentUtils 类里面的方法
</pre> *
 */
object IntentUtils {


    /**
     * 获取 MIUI 版本号
     */
    private// LogUtils.i("MiuiVersion = " + line);
    val miuiVersion: String?
        get() {
            val propName = "ro.miui.ui.version.name"
            val line: String
            var input: BufferedReader? = null
            try {
                val p = Runtime.getRuntime().exec("getprop $propName")
                input = BufferedReader(
                        InputStreamReader(p.inputStream), 1024)
                line = input.readLine()
                input.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            } finally {
                if (input != null) {
                    try {
                        input.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            return line
        }

    /**
     * 获取跳转「设置界面」的意图
     *
     * @return 意图
     */
    val settingIntent: Intent
        get() = Intent(Settings.ACTION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    /**
     * 跳转: 「权限设置」界面
     *
     *
     * 根据各大厂商的不同定制而跳转至其权限设置
     * 目前已测试成功机型: 小米V7V8V9, 华为, 三星, 锤子, 魅族; 测试失败: OPPO
     *
     * @return 成功跳转权限设置, 返回 true; 没有适配该厂商或不能跳转, 则自动默认跳转设置界面, 并返回 false
     */
    fun gotoPermissionSetting(context: Activity): Boolean {
        var success = true
        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val packageName = context.packageName

        val romType = OSUtils.romType
        when (romType) {
            OSUtils.ROM.EMUI // 华为
            -> {
                intent.putExtra("packageName", packageName)
                intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
            }
            OSUtils.ROM.Flyme // 魅族
            -> {
                intent.action = "com.meizu.safe.security.SHOW_APPSEC"
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.putExtra("packageName", packageName)
            }
            OSUtils.ROM.MIUI // 小米
            -> {
                val rom = miuiVersion
                if ("V6" == rom || "V7" == rom) {
                    intent.action = "miui.intent.action.APP_PERM_EDITOR"
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                    intent.putExtra("extra_pkgname", packageName)
                } else if ("V8" == rom || "V9" == rom) {
                    intent.action = "miui.intent.action.APP_PERM_EDITOR"
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                    intent.putExtra("extra_pkgname", packageName)
                } else {
                    intent = getAppDetailsSettingsIntent(packageName)
                }
            }
            OSUtils.ROM.Sony // 索尼
            -> {
                intent.putExtra("packageName", packageName)
                intent.component = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
            }
            OSUtils.ROM.ColorOS // OPPO
            -> {
                intent.putExtra("packageName", packageName)
                intent.component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionManagerActivity")
            }
            OSUtils.ROM.EUI // 乐视
            -> {
                intent.putExtra("packageName", packageName)
                intent.component = ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps")
            }
            OSUtils.ROM.LG // LG
            -> {
                intent.action = "android.intent.action.MAIN"
                intent.putExtra("packageName", packageName)
                val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity")
                intent.component = comp
            }
            OSUtils.ROM.SamSung // 三星
                , OSUtils.ROM.SmartisanOS // 锤子
            -> gotoAppDetailSetting(context, packageName)
            else -> {
                intent.action = Settings.ACTION_SETTINGS
                //LogUtils.i("没有适配该机型, 跳转普通设置界面");
                success = false
            }
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // 跳转失败, 前往普通设置界面
            IntentUtils.gotoSetting(context)
            success = false
            //LogUtils.i("无法跳转权限界面, 开始跳转普通设置界面");
        }

        return success
    }

    /**
     * 获取跳转「应用详情」的意图
     *
     * @param packageName 应用包名
     * @return 意图
     */
    fun getAppDetailsSettingsIntent(packageName: String): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 跳转:「应用详情」界面
     *
     * @param packageName 应用包名
     */
    fun gotoAppDetailSetting(context: Activity, packageName: String) {
        context.startActivity(getAppDetailsSettingsIntent(packageName))
    }

    /**
     * 跳转:「设置」界面
     */
    fun gotoSetting(context: Activity) {
        context.startActivity(settingIntent)
    }

}
