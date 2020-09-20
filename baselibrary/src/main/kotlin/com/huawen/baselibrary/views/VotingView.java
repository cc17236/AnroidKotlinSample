package com.huawen.baselibrary.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.huawen.baselibrary.R;

import java.text.DecimalFormat;

/**
 * 自定义百分比左右进度条，根据两个值的百分比不同，在一个进度条中显示出来，中间的隔线为斜线
 * Created by cg on 2016/7/28 0028.
 */
public class VotingView extends View {

    private float leftNum = 50;                     //进(左)的数量
    private int leftColor = Color.RED;              //进的颜色
    private float rightNum = 50;                     //出(右)的数量
    private int rightColor = Color.GREEN;            //出的颜色
    private int mInclination = 40;               //两柱中间的倾斜度
    private int leftTextColor = Color.WHITE;        //进的百分比数字颜色
    private int rightTextColor = Color.WHITE;        //出的百分比数字颜色
    private int TextSize = 30;                   //百分比字体大小


    private float leftVote;
    private float rightVote;

    private String txtLeftVote;                      //显示进的百分比
    private String txtRightVote;                      //显示出的百分比

    private Paint mPaint;
    private Rect mBound;                        //包含文字的框

    public VotingView(Context context) {
        this(context, null);
    }

    public VotingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VotingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray arry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VotingView, defStyleAttr, 0);
        int n = arry.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = arry.getIndex(i);
            if (attr == R.styleable.VotingView_leftNum) {
                leftNum = arry.getFloat(attr, 50);

            } else if (attr == R.styleable.VotingView_leftColor) {
                leftColor = arry.getColor(attr, Color.RED);

            } else if (attr == R.styleable.VotingView_rightNum) {
                rightNum = arry.getFloat(attr, 50);

            } else if (attr == R.styleable.VotingView_rightColor) {
                rightColor = arry.getColor(attr, Color.GREEN);

            } else if (attr == R.styleable.VotingView_Inclination) {
                mInclination = arry.getInt(attr, 40);

            } else if (attr == R.styleable.VotingView_leftTextColor) {
                leftTextColor = arry.getColor(attr, Color.WHITE);

            } else if (attr == R.styleable.VotingView_rightTextColor) {
                rightTextColor = arry.getColor(attr, Color.WHITE);

            } else if (attr == R.styleable.VotingView_TextSize) {
                TextSize = arry.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

            }
        }

        arry.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);

        mBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getWidth() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingTop() + getHeight() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        leftVote = (leftNum / (leftNum + rightNum)) * getWidth();
        rightVote = (rightNum / (leftNum + rightNum)) * getWidth();

        //Log.e("mPre", "leftVote:" + leftVote + "  rightVote:" + rightVote + "width" + getWidth());

        //如果进值或出值有一个为0，则另一个就会占满整个进度条，这时就不需要倾斜角度了
        if (leftNum == 0 || rightVote == 0) {
            mInclination = 0;
        }

        Path leftPath = new Path();
        leftPath.moveTo(0, 0);
        leftPath.lineTo(leftVote + mInclination, 0);
        leftPath.lineTo(leftVote, getHeight());
        leftPath.lineTo(0, getHeight());
        leftPath.close();

        mPaint.setColor(leftColor);
        canvas.drawPath(leftPath, mPaint);


        Path oPath = new Path();
        oPath.moveTo(leftVote + mInclination, 0);
        oPath.lineTo(getWidth(), 0);
        oPath.lineTo(getWidth(), getHeight());
        oPath.lineTo(leftVote - mInclination, getHeight());
        oPath.close();

        mPaint.setColor(rightColor);
        canvas.drawPath(oPath, mPaint);

        txtLeftVote = getProValText(leftNum / (leftNum + rightNum) * 100);
        txtRightVote = getProValText(rightNum / (leftNum + rightNum) * 100);


        mPaint.setColor(leftTextColor);
        mPaint.setTextSize(TextSize);

        mPaint.getTextBounds(txtLeftVote, 0, txtLeftVote.length(), mBound);
        //判断一下，如果进值为0则不显示，如果进值不为空而出值为0，则进值的数值显示居中显示
        if (leftNum != 0 && rightNum != 0) {

            canvas.drawText(txtLeftVote, 20, getHeight() / 2 + mBound.height() / 2, mPaint);

        } else if (leftNum != 0 && rightNum == 0) {

            canvas.drawText(txtLeftVote, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);

        }

        mPaint.setColor(rightTextColor);
        mPaint.getTextBounds(txtRightVote, 0, txtRightVote.length(), mBound);
        if (rightNum != 0 && leftNum != 0) {

            canvas.drawText(txtRightVote, getWidth() - 20 - mBound.width(), getHeight() / 2 + mBound.height() / 2, mPaint);

        } else if (rightNum != 0 && leftNum == 0) {


            canvas.drawText(txtRightVote, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
        }
    }

    /**
     * 格式化显示的百分比
     *
     * @param proValue
     * @return
     */
    private String getProValText(float proValue) {
        DecimalFormat format = new DecimalFormat("#0.0");
        return format.format(proValue) + "%";
    }

    /**
     * 动态设置进值
     *
     * @param leftNum
     */
    public void setLeftNum(float leftNum) {
        this.leftNum = leftNum;
        postInvalidate();
    }

    /**
     * 动态设置出值
     *
     * @param rightNum
     */
    public void setRightNum(float rightNum) {
        this.rightNum = rightNum;
        postInvalidate();
    }
}
