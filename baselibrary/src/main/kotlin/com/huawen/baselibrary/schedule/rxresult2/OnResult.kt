package com.huawen.baselibrary.schedule.rxresult2

import android.content.Intent
import androidx.annotation.Nullable
import java.io.Serializable


interface OnResult : Serializable {
    fun response(requestCode: Int, resultCode: Int, @Nullable data: Intent?)
    fun error(throwable: Throwable)
}
