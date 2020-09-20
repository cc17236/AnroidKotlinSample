package com.huawen.baselibrary.utils

inline fun String.isLetterDigit(): Boolean {
    val regex = "^[A-Za-z0-9-]{8,20}\$"
    val match= this.matches(regex.toRegex())
    return match
}