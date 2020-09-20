package com.example.applicationkotlinsample.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Stack;

/**
 */

public class ActivityUtil {

    public static final String INTENT_KEY = "bundle";
    public static final int REQUEST_MARKET = 10500;

    public static void startActivity(Activity context, Class<? extends Activity> clazz) {
        startActivity(context, clazz, null);
    }

    public static void startActivity(Activity context, Class<? extends Activity> clazz, @Nullable Bundle args) {
        startActivityForResult(context, clazz, args, -1);
    }

    public static void startActivityForResult(Activity context, Class<? extends Activity> clazz, int requestCode) {
        startActivityForResult(context, clazz, null, requestCode);
    }

    public static void startActivityForResult(Activity context, Class<? extends Activity> clazz, Bundle args, int requestCode) {
        Intent intent = new Intent(context, clazz);
        if (args != null) {
            intent.putExtras(args);
        }
        context.startActivityForResult(intent, requestCode);
    }

    public static void goToMarket(Activity activity, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivityForResult(goToMarket, REQUEST_MARKET);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "打开应用市场失败，请检查手机是否安装应用市场", Toast.LENGTH_LONG).show();
        }
    }

    private static Stack<Activity> activityStack;

    public ActivityUtil() {
    }

    /**
     * 单一实例
     */
    public static ActivityUtil getInstance() {
        return SingleApp.INSTANCE;
    }
    public static class SingleApp {
        public static ActivityUtil INSTANCE = new ActivityUtil();
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 移除Activity
     */
    public void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
                activityStack.remove(activity);
            }
        }
    }
    /**
     * 结束除当前传入以外所有Activity
     */
    public void finishOthersActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (!activity.getClass().equals(cls)) {
                    activity.finish();
                }
            }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                activity.finish();
            }
        activityStack.clear();
    }


    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());// 杀死该应用进程
            System.exit(0);
        } catch (Exception e) {
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                    break;
                }
            }
        }catch (Exception e){

        }

    }

}
