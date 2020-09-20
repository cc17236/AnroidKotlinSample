package com.huawen.baselibrary.schedule.rxresult2

import android.content.Intent

import io.reactivex.Observable

interface OnPreResult<T> {
    fun response(requestCode: Int, resultCode: Int, data: Intent?): Observable<T>
}
