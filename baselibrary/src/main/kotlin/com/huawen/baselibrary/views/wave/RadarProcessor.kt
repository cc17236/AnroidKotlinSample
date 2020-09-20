package com.huawen.baselibrary.views.wave

import android.graphics.Canvas
import android.graphics.Rect
import com.huawen.baselibrary.views.wave.WaveDelegate

class RadarProcessor : WaveDelegate {
    private var level = 0
    private val boxRect = Rect()
    override fun start() {
        level = 0
    }

    override fun end() {
        level = 0
    }

    override fun draw(canvas: Canvas) {

    }

    override fun postRefresh() = Unit
    fun setLevel(count: Int, left: Int, top: Int, right: Int, bottom: Int) {
        level = count
        boxRect.setEmpty()
        boxRect.set(left, top, right, bottom)
    }

}