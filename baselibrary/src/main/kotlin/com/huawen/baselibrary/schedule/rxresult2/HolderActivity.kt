/*
 * Copyright 2016 Copyright 2016 Víctor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawen.baselibrary.schedule.rxresult2

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle

class HolderActivity : Activity() {
    private var onPreResult: OnPreResult<*>? = null
    private var onResult: OnResult? = null
    private var resultCode: Int = 0
    private var requestCode: Int = 0
    private var data: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (request == null) {
            finish()
            return
        }

        onPreResult = request?.onPreResult()
        onResult = request?.onResult()

        if (savedInstanceState != null) return

        if (request is RequestIntentSender) {
            val requestIntentSender = request as RequestIntentSender?

            if (requestIntentSender!!.options == null)
                startIntentSender(requestIntentSender)
            else
                startIntentSenderWithOptions(requestIntentSender)
        } else {
            try {
                startActivityForResult(request!!.intent(), 0)
            } catch (e: ActivityNotFoundException) {
                onResult?.error(e)
            }

        }
    }

    private fun startIntentSender(requestIntentSender: RequestIntentSender) {
        try {
            startIntentSenderForResult(requestIntentSender.intentSender, 0,
                    requestIntentSender.fillInIntent, requestIntentSender.flagsMask,
                    requestIntentSender.flagsValues, requestIntentSender.extraFlags)
        } catch (exception: IntentSender.SendIntentException) {
            exception.printStackTrace()
            onResult!!.response(FAILED_REQUEST_CODE, Activity.RESULT_CANCELED, null)
        }

    }

    private fun startIntentSenderWithOptions(requestIntentSender: RequestIntentSender) {
        try {
            startIntentSenderForResult(requestIntentSender.intentSender, 0,
                    requestIntentSender.fillInIntent, requestIntentSender.flagsMask,
                    requestIntentSender.flagsValues, requestIntentSender.extraFlags, requestIntentSender.options)
        } catch (exception: IntentSender.SendIntentException) {
            exception.printStackTrace()
            onResult!!.response(FAILED_REQUEST_CODE, Activity.RESULT_CANCELED, null)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.resultCode = resultCode
        this.requestCode = requestCode
        this.data = data
        this.onPreResult?.response(requestCode, resultCode, data)
                ?.doOnComplete { finish() }
                ?.subscribe() ?: finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        onResult?.response(requestCode, resultCode, data)
    }

    companion object {
        private var request: Request? = null
        private val FAILED_REQUEST_CODE = -909

        internal fun setRequest(aRequest: Request) {
            request = aRequest
        }
    }
}
