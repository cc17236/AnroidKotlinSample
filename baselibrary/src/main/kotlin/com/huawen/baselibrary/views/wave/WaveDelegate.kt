package com.huawen.baselibrary.views.wave

import android.graphics.Canvas

interface WaveDelegate {
    fun start()
    fun end()
    fun draw(canvas: Canvas)
    fun postRefresh()
}