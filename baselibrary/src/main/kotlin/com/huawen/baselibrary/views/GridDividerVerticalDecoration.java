package com.huawen.baselibrary.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Gridview
 * 四周都是分割线的ItemDecoration,因为网上都是RecyclerView的边框部分没有分割线,主要代码在于用isfirstRow去判断
 * Created by _SOLID
 * Date:2016/10/8
 * Time:16:50
 * Desc:
 */


public class GridDividerVerticalDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaintHorizontal;
    private Paint mPaintVertical;

    private int mDividerHorizontalWidth;
    private int mDividerHorizontalHeight;
    private int mDividerVerticalWidth;
    private int mDividerVerticalHeight;
    private boolean complementLastDivider;

    @SuppressWarnings("SuspiciousNameCombination")
    public GridDividerVerticalDecoration(int lineWidth, @ColorInt int color) {
        mDividerHorizontalWidth = lineWidth;
        mDividerVerticalHeight = lineWidth;
        mPaintHorizontal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintHorizontal.setColor(color);
        mPaintHorizontal.setStyle(Paint.Style.FILL);
        mPaintVertical = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintVertical.setColor(color);
        mPaintVertical.setStyle(Paint.Style.FILL);

    }

    @SuppressWarnings("SuspiciousNameCombination")
    public GridDividerVerticalDecoration(int height, @ColorInt int color, boolean complementLastDivider) {
        mDividerHorizontalWidth = height;
        mDividerVerticalHeight = height;
        mPaintHorizontal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintHorizontal.setColor(color);
        mPaintHorizontal.setStyle(Paint.Style.FILL);
        mPaintVertical = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintVertical.setColor(color);
        mPaintVertical.setStyle(Paint.Style.FILL);
        this.complementLastDivider = complementLastDivider;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public GridDividerVerticalDecoration(int horizontalWidth, int verticalHeight, @ColorInt int colorHorizontal,
                                         @ColorInt int colorVertical, boolean complementLastDivider) {

        mDividerHorizontalWidth = horizontalWidth;
        mDividerVerticalHeight = verticalHeight;
        mPaintHorizontal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintHorizontal.setColor(colorHorizontal);
        mPaintHorizontal.setStyle(Paint.Style.FILL);
        mPaintVertical = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintVertical.setColor(colorVertical);
        mPaintVertical.setStyle(Paint.Style.FILL);
        this.complementLastDivider = complementLastDivider;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public GridDividerVerticalDecoration(
            int horizontalWidth, int horizontalHeight,
            int verticalWidth, int verticalHeight,
            @ColorInt int colorHorizontal,
            @ColorInt int colorVertical, boolean complementLastDivider) {

        mDividerHorizontalWidth = horizontalWidth;
        mDividerVerticalHeight = verticalHeight;


        mDividerHorizontalHeight = horizontalHeight;
        mDividerVerticalWidth = verticalWidth;


        mPaintHorizontal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintHorizontal.setColor(colorHorizontal);
        mPaintHorizontal.setStyle(Paint.Style.FILL);
        mPaintVertical = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintVertical.setColor(colorVertical);
        mPaintVertical.setStyle(Paint.Style.FILL);
        this.complementLastDivider = complementLastDivider;
    }


    public void setComplementLastDivider(boolean complementLastDivider) {
        this.complementLastDivider = complementLastDivider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        boolean isLastRow = isLastRow(parent, itemPosition, spanCount, childCount);
        boolean isfirstRow = isfirstRow(parent, itemPosition, spanCount, childCount);
        int top;
        int left;
        int right;
        int bottom;
        int eachWidth = (spanCount - 1) * mDividerHorizontalWidth / spanCount;
        int dl = mDividerHorizontalWidth - eachWidth;
        left = (itemPosition % spanCount) * dl;
        right = eachWidth - left;
        if (isfirstRow && isLastRow) {
            top = 0;
            bottom = 0;
        } else if (isLastRow) {
            top = 0;
            bottom = 0;
        } else if (isfirstRow) {
            top = 0;
            bottom = mDividerVerticalHeight;
        } else {
            top = 0;
            bottom = mDividerVerticalHeight;
        }
        outRect.set(left, top, right, bottom);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        draw(c, parent);
    }


    //绘制横向 item 分割线
    private void draw(Canvas canvas, RecyclerView parent) {
        int childSize = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        int lackVar = (spanCount - childSize & spanCount);
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            boolean isLastRow = isLastRow(parent, i, spanCount, childSize);
            boolean isLastColumn = isLastColumn(parent, i, spanCount, childSize);
            boolean isLastRowColumnEnd = false;
            if (isLastColumn && isLastRow) {
                boolean var = childSize == (i + 1);
                if (var)
                    isLastRowColumnEnd = true;
            }

//            mDividerVerticalWidth
            //画水平分隔线
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDividerVerticalHeight;
            if (mPaintHorizontal != null) {
                if (mDividerVerticalWidth != 0) {
                    int var = Math.abs(right - left)/2;
                    if (var > mDividerVerticalWidth/2) {
                        int var2 = mDividerVerticalWidth / 2;
                        int var3 = var - var2;
                        left += var3;
                        right -= var3;
                    }
                }
                canvas.drawRect(left, top, right, bottom, mPaintHorizontal);
            }

            //mDividerHorizontalHeight
            //画垂直分割线
            top = child.getTop();
            bottom = child.getBottom() + mDividerVerticalHeight;
            left = child.getRight() + layoutParams.rightMargin;
            right = left + mDividerHorizontalWidth;
            if (isLastRow && isLastRowColumnEnd && !complementLastDivider) return;
            if (mPaintVertical != null) {
                if (mDividerHorizontalHeight != 0) {
                    int var = Math.abs(top - bottom)/2;
                    if (var > mDividerHorizontalHeight/2) {
                        int var2 = mDividerHorizontalHeight / 2;
                        int var3 = var - var2;
                        top += var3;
                        bottom -= var3;
                    }
                }
                canvas.drawRect(left, top, right, bottom, mPaintVertical);
            }
            if (mPaintVertical != null) {
                if (i == childSize - 1) {
                    int count = lackVar - 1;
                    if (count > 0) {
                        int eachWidth = (spanCount - 1) * mDividerHorizontalWidth / spanCount;
                        int dl = mDividerHorizontalWidth - eachWidth;
                        for (int j = 0; j < count; j++) {
                            int leftVar = (j + (i + 1) % spanCount) * dl;
                            int rightVar = eachWidth - leftVar;
                            left += child.getWidth() + layoutParams.rightMargin + mDividerHorizontalWidth - rightVar;
                            right = left + mDividerHorizontalWidth;
                            canvas.drawRect(left, top, right, bottom, mPaintVertical);
                        }
                    }
                }
            }
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (computeLast(childCount, pos, spanCount)) {
                return true;
            } else if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean computeLast(int childCount, int pos, int spanCount) {
        if (childCount == 0) return true;
        int left = childCount - (pos + 1);
        return (left >= 0 && spanCount >= left) && ((pos + 1) % spanCount != 0);
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // childCount = childCount - childCount % spanCount;
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isfirstRow(RecyclerView parent, int pos, int spanCount,
                               int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // childCount = childCount - childCount % spanCount;
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            //如是第一行则返回true
            if ((pos / spanCount + 1) == 1) {
                return true;
            } else {
                return false;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }
}