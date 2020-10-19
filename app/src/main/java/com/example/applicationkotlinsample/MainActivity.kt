package com.example.applicationkotlinsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.applicationkotlinsample.mvp.controller.activity.TestSampleActivity
import com.huawen.baselibrary.startRxAccessibilityForResult
import com.huawen.baselibrary.startRxActivityForResult
import com.huawen.baselibrary.utils.Debuger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        Debuger.print("===========SSSSSSS===========")

        startRxActivityForResult<TestSampleActivity>() { data, code -> }
    }
}
