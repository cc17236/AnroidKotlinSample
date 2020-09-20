package com.huawen.baselibrary.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

inline fun AppCompatActivity.indexOfFragment(fragment: androidx.fragment.app.Fragment): Int {
    val f = supportFragmentManager?.fragments
    if (f != null && f.size > 0) {
        for (i in 0 until f.size) {
            val fragment_ = f[i]
            if (fragment_ != null && fragment == fragment_) {
                return i
            }
        }
    }
    return -1
}

inline fun Activity.checkSelfPermissions(vararg p: String): Boolean {
    var has = true
    loop@ p.forEach {
       val GRANT= ContextCompat.checkSelfPermission(this, it)
        if ( GRANT!= PackageManager.PERMISSION_GRANTED) {
            has = false
            return@forEach
        }
    }
    return has
}

inline fun Activity.checkSelfPermissions2( p: Array<String>): Boolean {
    var has = true
    loop@ p.forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            has = false
            return@forEach
        }
    }
    return has
}
