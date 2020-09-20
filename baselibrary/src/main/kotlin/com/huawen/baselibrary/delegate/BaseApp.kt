package com.huawen.baselibrary.delegate

import android.app.Activity
import java.util.*

interface BaseApp {
    fun getAppStack(): Stack<Activity>
    fun inSplashHost(): Boolean
}