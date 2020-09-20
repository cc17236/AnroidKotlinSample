package com.huawen.baselibrary.views.wave

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log

class DelegatingHelper(private val delegate: WaveDelegate) : WaveDelegate, WaveCounter {
    private var count = 0
    private val mHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            0 -> {
                if (count > 4) {
                    count = 0
                    counting(count)
                } else {
                    counting(count)
                }
                count++
            }
            1 -> {
            }
        }
        return@Handler true
    }
    private var radar = RadarProcessor()
    private var wave = WaveProcessor()

    private var starting = false
    override fun start() {
        starting = true
        radar.start()
        wave.start()
        startCounting()
    }

    override fun draw(canvas: Canvas) {
        if (!starting) return
        if (rect.isEmpty) return
        radar.draw(canvas)
        wave.draw(canvas)

    }

    override fun end() {
        radar.end()
        wave.end()
        starting = false
        stopCounting()
        postRefresh()
    }


    override fun startCounting() {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessage(0)
    }

    override fun stopCounting() {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessage(1)
    }

    override fun counting(count: Int) {
        radar.setLevel(count, rect.left, rect.top, rect.right, rect.bottom)
        wave.setLevel(count, rect.left, rect.top, rect.right, rect.bottom)
        Log.e("Delegate", "starting==>${starting}   rect.isEmpty===>${rect.isEmpty}")
        if (!starting) return
        if (rect.isEmpty) return
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessageDelayed(0, 300)
        postRefresh()
    }

    override fun postRefresh() {
        delegate.postRefresh()
    }

    private val rect = Rect()
    fun setBoxRect(left: Int, top: Int, right: Int, bottom: Int) {
        if (!this.rect.isEmpty && starting) return
        this.rect.setEmpty()
        this.rect.set(left, top, right, bottom)
        Log.e("Delegate", "starting2==>${starting}   rect.isEmpty2===>${rect.isEmpty}")
        if (starting) {
            count = 0
            counting(count)
            count++
        }
    }

}