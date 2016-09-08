package com.eeontheway.android.applocker.main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 用于RecyleView的分割符
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class LockListViewItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    private Paint paint;
    private int orientation;
    private int color = Color.LTGRAY;
    private int size = 1;

    /**
     * 构造函数
     */
    public LockListViewItemDecoration() {
        this(HORIZONTAL);
    }

    /**
     * 构造函数
     * @param orientation 分割线的方向
     */
    public LockListViewItemDecoration(int orientation) {
        this.orientation = orientation;
        paint = new Paint();
        paint.setColor(color);
    }

    /**
     * 绘制分割线
     * @param c 画板
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        // 根据不同的方向绘制不同的分割线
        if (orientation == HORIZONTAL) {
            drawHorizontal(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    /**
     * 设置分割线颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    /**
     * 设置分割线尺寸
     *
     * @param size 尺寸
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 绘制水平分割线
     * @param c 画板
     * @param parent
     */
    protected void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        if (childCount == 0) {
            return;
        } else {
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = child.getRight() + params.rightMargin;
                int right = left + size;

                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    /**
     * 绘制垂直分割线
     * @param c 画板
     * @param parent
     */
    protected void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        if (childCount == 0) {
            return;
        } else {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + size;

                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
