package cn.aihuaiedu.school.base

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.annotation.ColorInt
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import org.jetbrains.anko.dip

class ClassicsGifHeader : LinearLayout, RefreshHeader {


    private var animation: AnimationDrawable? = null
    private var imageView: ImageView? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.initView(context)
    }

    private fun initView(context: Context) {
        gravity = Gravity.CENTER
        minimumHeight =dip(80)
        imageView = ImageView(context)
        animation = AnimationDrawable()
//        animation?.addFrame(resources.getDrawable(R.drawable.anim_load_1), 100)
//        animation?.addFrame(resources.getDrawable(R.drawable.anim_load_2), 100)
//        animation?.addFrame(resources.getDrawable(R.drawable.anim_load_3), 100)
//        animation?.addFrame(resources.getDrawable(R.drawable.anim_load_4), 100)
//        animation?.isOneShot = false
        addView(imageView, dip(70), dip(70))
    }

    override fun getView(): View {
        return this//真实的视图就是自己，不能返回null
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate//指定为平移，不能null
    }

    override fun onStartAnimator(layout: RefreshLayout, headHeight: Int, maxDragHeight: Int) {
        animation?.start()//开始动画
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        animation?.stop()//停止动画
        //        if (success){
        //            mHeaderText.setText("刷新完成");
        //        } else {
        //            mHeaderText.setText("刷新失败");
        //        }
        return 500//延迟500毫秒之后再弹回
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
            }
            RefreshState.Refreshing -> {
            }
            RefreshState.ReleaseToRefresh -> {
            }
        }
        //                mHeaderText.setText("下拉开始刷新");
        //                mArrowView.setVisibility(VISIBLE);//显示下拉箭头
        //                mProgressView.setVisibility(GONE);//隐藏动画
        //                mArrowView.animate().rotation(0);//还原箭头方向
        //                mHeaderText.setText("正在刷新");
        //                mProgressView.setVisibility(VISIBLE);//显示加载动画
        //                mArrowView.setVisibility(GONE);//隐藏箭头
        //                mHeaderText.setText("释放立即刷新");
        //                mArrowView.animate().rotation(180);//显示箭头改为朝上
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {}

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {

    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        var idx = 0
        if (percent in 0f..0.25f) {
            idx = 0
        } else if (percent in 0.26f..0.5f) {
            idx = 1
        } else if (percent in 0.51f..0.75f) {
            idx = 2
        } else if (percent in 0.76f..0.1f) {
            idx = 3
        }

        if (percent >= 1.0f) {
            if (imageView?.background is AnimationDrawable && animation?.isRunning == true) {
                return
            }
            imageView?.setImageDrawable(null)
            imageView?.background = animation
            animation?.start()
            return
        } else {
            if (animation?.isRunning == true)
                animation?.stop()
        }
        imageView?.background = null

        val frame = animation?.getFrame(idx)
        if (frame == null) return

        imageView?.clearAnimation()
        imageView?.setImageDrawable(null)
        imageView?.setImageDrawable(frame)
    }

    override fun onReleased(refreshLayout: RefreshLayout, headerHeight: Int, maxDragHeight: Int) {
        imageView?.clearAnimation()
        imageView?.setImageDrawable(null)
        imageView?.background = animation
    }

    override fun setPrimaryColors(@ColorInt vararg colors: Int) {}
}