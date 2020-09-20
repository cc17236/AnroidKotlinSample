/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.cardview.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.RequiresApi

@RequiresApi(17)
internal class FixableCardViewApi17Impl : CardViewApi17Impl() {

    fun changeShadowColor(cardView: CardViewDelegate, startColor: Int, endColor: Int) {
        val shadow = getShadowBackground_(cardView)
        shadow?.shadowColors(startColor, endColor)
    }

    private fun getShadowBackground_(cardView: CardViewDelegate): FixedRoundRectDrawableWithShadow? {
        return cardView.cardBackground as FixedRoundRectDrawableWithShadow
    }

    override fun initialize(cardView: CardViewDelegate, context: Context,
                            backgroundColor: ColorStateList?, radius: Float, elevation: Float, maxElevation: Float) {

        val background = createBackground(context, backgroundColor, radius,
                elevation, maxElevation)
        background.setAddPaddingForCorners(cardView.preventCornerOverlap)
        cardView.cardBackground = background
        updatePadding(cardView)

    }

    private fun createBackground(context: Context,
                                 backgroundColor: ColorStateList?, radius: Float, elevation: Float,
                                 maxElevation: Float): FixedRoundRectDrawableWithShadow {
        return FixedRoundRectDrawableWithShadow(context.resources, backgroundColor, radius,
                elevation, maxElevation)
    }

    override fun setRadius(cardView: CardViewDelegate, radius: Float) {
        val shadow = getShadowBackground_(cardView) ?: return
        shadow.cornerRadius = radius
        updatePadding(cardView)
    }

    fun invalidate(cardView: CardViewDelegate) {
        getShadowBackground_(cardView)?.invalidateSelf()
    }


    override fun initStatic() {
        FixedRoundRectDrawableWithShadow.sRoundRectHelper =object : FixedRoundRectDrawableWithShadow.RoundRectHelper2 {
            override fun drawRoundRect(canvas: Canvas, bounds: RectF, cornerRadius: Float, paint: Paint) {
                canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint)
            }

        }
    }
}
