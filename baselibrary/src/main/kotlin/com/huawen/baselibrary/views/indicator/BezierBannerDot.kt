package com.huawen.baselibrary.views.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.viewpager.widget.ViewPager
import com.huawen.baselibrary.R

class BezierBannerDot @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), androidx.viewpager.widget.ViewPager.OnPageChangeListener {
    //选中画笔
    private var mCirclePaint: Paint? = null
    //背景画笔
    private var mCirclePaint2: Paint? = null
    //选中路径
    private val mPath = Path()
    //背景路径
    private val mPath2 = Path()
    //选中颜色
    private var mSelectedColor: Int = 0
    //未选中颜色
    private var mUnSelectedColor: Int = 0
    //圆点之间距离
    private var distance = 80f
    //起始圆初始半径
    private var mRadius = 30f
    //起始圆变化半径
    private var mChangeRadius: Float = 0.toFloat()
    //背景圆初始半径
    private var mNomarlRadius = 20f
    //背景圆变化半径 背景圆都用这个
    private var mChangeBgRadius: Float = 0.toFloat()
    //起始圆辅助圆变化半径
    private var mSupportChangeRadius: Float = 0.toFloat()
    //将要到达位置的背景圆的辅助圆变化半径
    private var mSupport_Next_ChangeRadius: Float = 0.toFloat()
    //起始圆圆心坐标
    internal var mCenterPointX: Float = 0.toFloat()
    internal var mCenterPointY: Float = 0.toFloat()
    //起始圆辅助圆圆心坐标
    internal var mSupportCircleX: Float = 0.toFloat()
    internal var mSupportCircleY: Float = 0.toFloat()
    //当前背景圆圆心坐标
    internal var mSupport_next_centerX: Float = 0.toFloat()
    internal var mSupport_next_centerY: Float = 0.toFloat()
    //将要到达位置的背景圆圆心坐标
    internal var mbgNextPointX: Float = 0.toFloat()
    internal var mbgNextPointY: Float = 0.toFloat()

    //是否进入自动移动状态
    private val autoMove = false
    //第一阶段运动进度
    private var mProgress = 0f
    //第二阶段运动进度
    private var mProgress2 = 0f
    //整体运动进度 也是原始进度
    private var mOriginProgress: Float = 0.toFloat()

    //当前选中的位置
    private var mSelectedIndex = 0
    private var count: Int = 0

    //第一阶段运动
    private val MOVE_STEP_ONE = 1
    //第二阶段运动
    private val MOVE_STEP_TWO = 2
    //控制点坐标
    internal var controlPointX: Float = 0.toFloat()
    internal var controlPointY: Float = 0.toFloat()
    //起点坐标
    internal var mStartX: Float = 0.toFloat()
    internal var mStartY: Float = 0.toFloat()
    //终点坐标
    internal var endPointX: Float = 0.toFloat()
    internal var endPointY: Float = 0.toFloat()
    private var mDrection: Int = 0

    internal var accelerateinterpolator: Interpolator = AccelerateDecelerateInterpolator()

    init {
        initattrs(attrs)
        initPaint()
    }


    private fun initPaint() {
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = mSelectedColor
        p.style = Paint.Style.FILL
        p.isAntiAlias = true
        p.isDither = true
        mCirclePaint = p

        val p1 = Paint(Paint.ANTI_ALIAS_FLAG)
        p1.color = mUnSelectedColor
        p1.style = Paint.Style.FILL
        p1.isAntiAlias = true
        p1.isDither = true
        mCirclePaint2 = p1

    }

    private fun initattrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BezierBannerDot)
        mSelectedColor = typedArray.getColor(R.styleable.BezierBannerDot_selectedColor, -0x1)
        mUnSelectedColor = typedArray.getColor(R.styleable.BezierBannerDot_unSelectedColor, -0x555556)
        mRadius = typedArray.getDimension(R.styleable.BezierBannerDot_selectedRaduis, mRadius)
        mNomarlRadius = typedArray.getDimension(R.styleable.BezierBannerDot_unSelectedRaduis, mNomarlRadius)
        distance = typedArray.getDimension(R.styleable.BezierBannerDot_spacing, distance)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        //宽度等于所有圆点宽度+之间的间隔+padding;要留出当左右两个是大圆点的左右边距
        val width =
            (mNomarlRadius * 2f * count.toFloat() + (mRadius - mNomarlRadius) * 2 + (count - 1) * distance + paddingLeft.toFloat() + paddingRight.toFloat()).toInt()
        val height = (2 * mRadius + paddingTop.toFloat() + paddingBottom.toFloat()).toInt()

        val mHeight: Int
        val mWidth: Int

        if (widthMode == View.MeasureSpec.EXACTLY) {
            mWidth = widthSize
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            mWidth = Math.min(widthSize, width)
        } else {
            mWidth = widthSize
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            mHeight = heightSize
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            mHeight = Math.min(heightSize, height)
        } else {
            mHeight = heightSize
        }

        setMeasuredDimension(mWidth, mHeight)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        //画暂不活动的背景圆
        for (i in 0 until count) {
            if (mDrection == DIRECTION_RIGHT) {
                if (i == mSelectedIndex || i == mSelectedIndex + 1) {
                    //活动的就不用画了
                } else {
                    canvas.drawCircle(getCenterPointAt(i), mRadius, mNomarlRadius, mCirclePaint2!!)
                }

            } else if (mDrection == DIRECTION_LEFT) {
                if (i == mSelectedIndex || i == mSelectedIndex - 1) {
                    //活动的就不用画了
                } else {
                    canvas.drawCircle(getCenterPointAt(i), mRadius, mNomarlRadius, mCirclePaint2!!)
                }
            }
        }

        //画活动背景圆
        canvas.drawCircle(mSupport_next_centerX, mSupport_next_centerY, mSupport_Next_ChangeRadius, mCirclePaint2!!)
        canvas.drawCircle(mbgNextPointX, mbgNextPointY, mChangeBgRadius, mCirclePaint2!!)
        canvas.drawPath(mPath2, mCirclePaint2!!)
        //画选中圆
        canvas.drawCircle(mSupportCircleX, mSupportCircleY, mSupportChangeRadius, mCirclePaint!!)
        canvas.drawCircle(mCenterPointX, mCenterPointY, mChangeRadius, mCirclePaint!!)
        canvas.drawPath(mPath, mCirclePaint!!)

        canvas.restore()

    }

    /**
     * 转化整体进度值使两个阶段的运动进度都是0-1
     *
     * @param progress 当前整体进度
     */
    fun setProgress(progress: Float) {
        //viewpager滑动完毕返回的0不需要，拦截掉
        if (progress == 0f) {
            Log.d(TAG, "拦截")
            return
        }
        mOriginProgress = progress
        if (progress <= 0.5) {
            mProgress = progress / 0.5f
            mProgress2 = 0f
        } else {
            mProgress2 = (progress - 0.5f) / 0.5f
            mProgress = 1f

        }
        if (mDrection == DIRECTION_RIGHT) {
            moveToNext()
        } else {
            moveToPrivous()
        }
        invalidate()
        Log.d(TAG, "刷新")


    }

    /**
     * 向右移动
     */
    private fun moveToNext() {
        //重置路径
        mPath.reset()
        mPath2.reset()
        //使用一个插值器使圆的大小变化两边慢中间快
        val mRadiusProgress = accelerateinterpolator.getInterpolation(mOriginProgress)
        //----------------------选中圆--------------------------------
        //起始圆圆心
        mCenterPointX =
            getValue(getCenterPointAt(mSelectedIndex), getCenterPointAt(mSelectedIndex + 1) - mRadius, MOVE_STEP_TWO)
        mCenterPointY = mRadius
        //起始圆半径
        mChangeRadius = getValue(mRadius, 0f, mRadiusProgress)

        //起点与起始圆圆心间的角度
        val radian = Math.toRadians(getValue(45f, 0f, MOVE_STEP_ONE).toDouble())
        //X轴距离圆心距离
        val mX = (Math.sin(radian) * mChangeRadius).toFloat()
        //Y轴距离圆心距离
        val mY = (Math.cos(radian) * mChangeRadius).toFloat()

        //辅助圆
        mSupportCircleX =
            getValue(getCenterPointAt(mSelectedIndex) + mRadius, getCenterPointAt(mSelectedIndex + 1), MOVE_STEP_ONE)
        mSupportCircleY = mRadius
        mSupportChangeRadius = getValue(0f, mRadius, mRadiusProgress)

        //终点与辅助圆圆心间的角度
        val supportradian = Math.toRadians(getValue(0f, 45f, MOVE_STEP_TWO).toDouble())
        //X轴距离圆心距离
        val msupportradianX = (Math.sin(supportradian) * mSupportChangeRadius).toFloat()
        //Y轴距离圆心距离
        val msupportradianY = (Math.cos(supportradian) * mSupportChangeRadius).toFloat()

        //起点
        mStartX = mCenterPointX + mX
        mStartY = mCenterPointY - mY

        //终点
        endPointX = mSupportCircleX - msupportradianX
        endPointY = mRadius - msupportradianY

        //控制点
        controlPointX =
            getValueForAll(getCenterPointAt(mSelectedIndex) + mRadius, getCenterPointAt(mSelectedIndex + 1) - mRadius)
        controlPointY = mRadius

        //移动到起点
        mPath.moveTo(mStartX, mStartY)

        //形成闭合区域
        mPath.quadTo(controlPointX, controlPointY, endPointX, endPointY)
        mPath.lineTo(endPointX, mRadius + msupportradianY)
        mPath.quadTo(controlPointX, mRadius, mStartX, mStartY + 2 * mY)
        mPath.lineTo(mStartX, mStartY)

        //----------------------背景圆反方向移动--------------------------------
        //起始圆圆心
        mbgNextPointX = getValue(
            getCenterPointAt(mSelectedIndex + 1),
            getCenterPointAt(mSelectedIndex) + mNomarlRadius,
            MOVE_STEP_TWO
        )
        mbgNextPointY = mRadius
        //起始圆半径
        mChangeBgRadius = getValue(mNomarlRadius, 0f, mRadiusProgress)

        //起点与起始圆圆心间的角度
        val m_Next_radian = Math.toRadians(getValue(45f, 0f, MOVE_STEP_ONE).toDouble())
        val mX_next = (Math.sin(m_Next_radian) * mChangeBgRadius).toFloat()
        val mY_next = (Math.cos(m_Next_radian) * mChangeBgRadius).toFloat()
        //辅助圆圆心
        mSupport_next_centerX = getValue(
            getCenterPointAt(mSelectedIndex + 1) - mNomarlRadius,
            getCenterPointAt(mSelectedIndex),
            MOVE_STEP_ONE
        )
        mSupport_next_centerY = mRadius
        //辅助圆半径
        mSupport_Next_ChangeRadius = getValue(0f, mNomarlRadius, mRadiusProgress)

        //终点与辅助圆圆心间的角度
        val mSupport_Next_radian = Math.toRadians(getValue(0f, 45f, MOVE_STEP_TWO).toDouble())
        val mSupport_Next_radianX = (Math.sin(mSupport_Next_radian) * mSupport_Next_ChangeRadius).toFloat()
        val mSupport_Next_radianY = (Math.cos(mSupport_Next_radian) * mSupport_Next_ChangeRadius).toFloat()

        //起点
        val startPoint_support_nextX = mbgNextPointX - mX_next
        val startPoint_support_nextY = mbgNextPointY - mY_next

        //终点
        val endPoint_support_nextX = mSupport_next_centerX + mSupport_Next_radianX
        val endPoint_support_nextY = mSupport_next_centerY - mSupport_Next_radianY

        //控制点
        val controlPointX_Next = getValueForAll(
            getCenterPointAt(mSelectedIndex + 1) - mNomarlRadius,
            getCenterPointAt(mSelectedIndex) + mNomarlRadius
        )
        val controlPointY_Next = mRadius

        //移动到起点
        mPath2.moveTo(startPoint_support_nextX, startPoint_support_nextY)
        //形成闭合区域
        mPath2.quadTo(controlPointX_Next, controlPointY_Next, endPoint_support_nextX, endPoint_support_nextY)
        mPath2.lineTo(endPoint_support_nextX, mRadius + mSupport_Next_radianY)
        mPath2.quadTo(
            controlPointX_Next,
            controlPointY_Next,
            startPoint_support_nextX,
            startPoint_support_nextY + 2 * mY_next
        )
        mPath2.lineTo(startPoint_support_nextX, startPoint_support_nextY)

    }


    /**
     * 向左移动(与向右过程大致相同)
     */
    private fun moveToPrivous() {
        mPath.reset()
        mPath2.reset()

        val mRadiusProgress = accelerateinterpolator.getInterpolation(mOriginProgress)

        //----------------------选中圆--------------------------------
        mCenterPointX =
            getValue(getCenterPointAt(mSelectedIndex), getCenterPointAt(mSelectedIndex - 1) + mRadius, MOVE_STEP_TWO)
        mCenterPointY = mRadius
        mChangeRadius = getValue(mRadius, 0f, mRadiusProgress)
        //起点与起始圆圆心间的角度
        val radian = Math.toRadians(getValue(45f, 0f, MOVE_STEP_ONE).toDouble())
        //X轴距离圆心距离
        val mX = (Math.sin(radian) * mChangeRadius).toFloat()
        //Y轴距离圆心距离
        val mY = (Math.cos(radian) * mChangeRadius).toFloat()

        //辅助圆
        mSupportCircleX =
            getValue(getCenterPointAt(mSelectedIndex) - mRadius, getCenterPointAt(mSelectedIndex - 1), MOVE_STEP_ONE)
        mSupportCircleY = mRadius
        mSupportChangeRadius = getValue(0f, mRadius, mRadiusProgress)


        //终点与辅助圆圆心间的角度
        val supportradian = Math.toRadians(getValue(0f, 45f, MOVE_STEP_TWO).toDouble())
        //X轴距离圆心距离
        val msupportradianX = (Math.sin(supportradian) * mSupportChangeRadius).toFloat()
        //Y轴距离圆心距离
        val msupportradianY = (Math.cos(supportradian) * mSupportChangeRadius).toFloat()

        mStartX = mCenterPointX - mX
        mStartY = mCenterPointY - mY

        endPointX = mSupportCircleX + msupportradianX
        endPointY = mRadius - msupportradianY

        controlPointX =
            getValueForAll(getCenterPointAt(mSelectedIndex) - mRadius, getCenterPointAt(mSelectedIndex - 1) + mRadius)
        controlPointY = mRadius

        mPath.moveTo(mStartX, mStartY)
        mPath.quadTo(controlPointX, controlPointY, endPointX, endPointY)
        mPath.lineTo(endPointX, mRadius + msupportradianY)
        mPath.quadTo(controlPointX, mRadius, mStartX, mStartY + 2 * mY)
        mPath.lineTo(mStartX, mStartY)


        //----------------------背景圆反方向移动--------------------------------
        mbgNextPointX = getValue(
            getCenterPointAt(mSelectedIndex - 1),
            getCenterPointAt(mSelectedIndex) - mNomarlRadius,
            MOVE_STEP_TWO
        )
        mbgNextPointY = mRadius
        mChangeBgRadius = getValue(mNomarlRadius, 0f, mRadiusProgress)
        //起点与起始圆圆心间的角度
        val m_Next_radian = Math.toRadians(getValue(45f, 0f, MOVE_STEP_ONE).toDouble())
        //X轴距离圆心距离
        val mX_next = (Math.sin(m_Next_radian) * mChangeBgRadius).toFloat()
        //Y轴距离圆心距离
        val mY_next = (Math.cos(m_Next_radian) * mChangeBgRadius).toFloat()

        mSupport_next_centerX = getValue(
            getCenterPointAt(mSelectedIndex - 1) + mNomarlRadius,
            getCenterPointAt(mSelectedIndex),
            MOVE_STEP_ONE
        )
        mSupport_next_centerY = mRadius
        mSupport_Next_ChangeRadius = getValue(0f, mNomarlRadius, mRadiusProgress)

        //终点与辅助圆圆心间的角度
        val mSupport_Next_radian = Math.toRadians(getValue(0f, 45f, MOVE_STEP_TWO).toDouble())
        //X轴距离圆心距离
        val mSupport_Next_radianX = (Math.sin(mSupport_Next_radian) * mSupport_Next_ChangeRadius).toFloat()
        //Y轴距离圆心距离
        val mSupport_Next_radianY = (Math.cos(mSupport_Next_radian) * mSupport_Next_ChangeRadius).toFloat()

        val startPoint_support_nextX = mbgNextPointX + mX_next
        val startPoint_support_nextY = mbgNextPointY - mY_next

        val endPoint_support_nextX = mSupport_next_centerX - mSupport_Next_radianX
        val endPoint_support_nextY = mSupport_next_centerY - mSupport_Next_radianY

        val controlPointX_Next = getValueForAll(
            getCenterPointAt(mSelectedIndex - 1) + mNomarlRadius,
            getCenterPointAt(mSelectedIndex) - mNomarlRadius
        )
        val controlPointY_Next = mRadius

        mPath2.moveTo(startPoint_support_nextX, startPoint_support_nextY)
        mPath2.quadTo(controlPointX_Next, controlPointY_Next, endPoint_support_nextX, endPoint_support_nextY)
        mPath2.lineTo(endPoint_support_nextX, mRadius + mSupport_Next_radianY)
        mPath2.quadTo(
            controlPointX_Next,
            controlPointY_Next,
            startPoint_support_nextX,
            startPoint_support_nextY + 2 * mY_next
        )
        mPath2.lineTo(startPoint_support_nextX, startPoint_support_nextY)

    }


    /**
     * 获取当前值(适用分阶段变化的值)
     *
     * @param start 初始值
     * @param end   终值
     * @param step  第几活动阶段
     * @return
     */
    fun getValue(start: Float, end: Float, step: Int): Float {
        return if (step == MOVE_STEP_ONE) {
            start + (end - start) * mProgress
        } else {
            start + (end - start) * mProgress2
        }
    }

    /**
     * 获取当前值（适用全过程变化的值）
     *
     * @param start 初始值
     * @param end   终值
     * @return
     */
    fun getValueForAll(start: Float, end: Float): Float {
        return start + (end - start) * mOriginProgress
    }

    /**
     * 通过进度获取当前值
     *
     * @param start    初始值
     * @param end      终值
     * @param progress 当前进度
     * @return
     */
    fun getValue(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }

    /**
     * 获取圆心X坐标
     *
     * @param index 第几个圆
     * @return
     */
    private fun getCenterPointAt(index: Int): Float {
        return if (index == 0) {
            mRadius
        } else index * (distance + 2 * mNomarlRadius) + mNomarlRadius + (mRadius - mNomarlRadius)
    }


    fun setDirection(direction: Int) {
        mDrection = direction
    }

    /**
     * 重置进度
     */
    fun resetProgress() {
        mProgress = 0f
        mProgress2 = 0f
        mOriginProgress = 0f
    }

    /**
     * 绑定viewpager
     */
    fun attachToViewpager(viewPager: ViewPager?) {
        if (viewPager == null) return
        viewPager.addOnPageChangeListener(this)
        count = viewPager.adapter!!.count
        moveToNext()
        mDrection = DIRECTION_RIGHT
        invalidate()
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //偏移量为0 说明运动停止
        if (positionOffset == 0f) {
            mSelectedIndex = position
            Log.d("tag", "到达")
            resetProgress()
        }
        //向左滑，指示器向右移动
        if (position + positionOffset - mSelectedIndex > 0) {
            mDrection = DIRECTION_RIGHT
            //向左快速滑动 偏移量不归0 但是position发生了改变 需要更新当前索引
            if (mDrection == DIRECTION_RIGHT && position + positionOffset > mSelectedIndex + 1) {
                mSelectedIndex = position
                Log.d(TAG, "向左快速滑动")
            } else {
                setProgress(positionOffset)
            }
        } else if (position + positionOffset - mSelectedIndex < 0) { //向右滑，指示器向左移动
            mDrection = DIRECTION_LEFT
            //向右快速滑动
            if (mDrection == DIRECTION_LEFT && position + positionOffset < mSelectedIndex - 1) {
                mSelectedIndex = position
                Log.d(TAG, "向右快速滑动")
            } else {
                setProgress(1 - positionOffset)
            }
        }

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    companion object {
        //向右滑 向左滚动
        var DIRECTION_LEFT = 1
        //向左滑 向右滚动
        var DIRECTION_RIGHT = 2
        private val TAG = "tag"
    }

}