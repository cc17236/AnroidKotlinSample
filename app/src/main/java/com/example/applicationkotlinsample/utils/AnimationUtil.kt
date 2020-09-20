package cn.aihuaiedu.school.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

class AnimationUtil {

    companion object{
        /**
         * 如果是从现在的位置移动到别的位置，那么初始XY的值都是0，向左边，那么结束X值就是 负数，向右则是正数，上下亦是如此
         * 如果是从别的位置移动到现在的位置，那么结束XY的值都是0，其他的和上面一样
         * @param fromX 初始X位置
         * @param toX   结束X位置
         * @param fromY 初始Y位置
         * @param toY   结束Y位置
         * @param time  动画时间
         * @param view  控件
         */

        fun setAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, time: Long, view: View,isSet:Boolean = true) {
            /**位移动画 */
            val mTranslateAnimation = TranslateAnimation(fromX, toX, fromY, toY)
            mTranslateAnimation.setDuration(time)

            /** 缩放动画 */
            val mScaleAnimation = ScaleAnimation(
                1.0f, 0.8f, 1.0f, 0.8f, // 整个屏幕就0.0到1.0的大小//缩放
                Animation.INFINITE, 0.8f,
                Animation.INFINITE, 0.8f
            )
            mScaleAnimation.setDuration(time)

            /** 组合动画 */
            val mAnimationSet = AnimationSet(isSet)
            mAnimationSet.addAnimation(mScaleAnimation)
            mAnimationSet.setFillAfter(true)//true 设置动画结束后，控件位置保持不动，false 则是返回初始位置
            mAnimationSet.addAnimation(mTranslateAnimation)
            view.startAnimation(mAnimationSet)
        }

        /**
         * 如果是从现在的位置移动到别的位置，那么初始XY的值都是0，向左边，那么结束X值就是 负数，向右则是正数，上下亦是如此
         * 如果是从别的位置移动到现在的位置，那么结束XY的值都是0，其他的和上面一样
         * @param fromX 初始X位置
         * @param toX   结束X位置
         * @param fromY 初始Y位置
         * @param toY   结束Y位置
         * @param time  动画时间
         * @param view  控件
         */

        fun setAnimation2(fromX: Float, toX: Float, fromY: Float, toY: Float, time: Long, view: View,isSet:Boolean = true) {
            /**位移动画 */
            val mTranslateAnimation = TranslateAnimation(fromX, toX, fromY, toY)
            mTranslateAnimation.setDuration(time)

            /** 缩放动画 */
            val mScaleAnimation = ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f, // 整个屏幕就0.0到1.0的大小//缩放
                Animation.INFINITE, 1.2f,
                Animation.INFINITE, 1.2f
            )
            mScaleAnimation.setDuration(time)

            /** 组合动画 */
            val mAnimationSet = AnimationSet(isSet)
            mAnimationSet.addAnimation(mScaleAnimation)
            mAnimationSet.setFillAfter(true)//true 设置动画结束后，控件位置保持不动，false 则是返回初始位置
            mAnimationSet.addAnimation(mTranslateAnimation)
            view.startAnimation(mAnimationSet)
        }


    }


}