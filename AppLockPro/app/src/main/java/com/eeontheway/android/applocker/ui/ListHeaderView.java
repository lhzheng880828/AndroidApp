package com.eeontheway.android.applocker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;

/**
 * 用于列表头部的View
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ListHeaderView extends FrameLayout implements View.OnClickListener {
    private Context context;
    private TextView tv_title;
    private CheckBox cb_select_all;
    private TextView tv_return_top;
    private View ll_space;

    private ClickListener listener;
    private long lastClickedTime;

    /**
     * 点击事件回调接口
     */
    public interface ClickListener {
        void onCheckAllSetListener (boolean isChecked);
        void onDoubleClickedListener ();
    }

    /**
     * 设置事件监听器
     * @param listener 事件监听器
     */
    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 各项属性
     */
    public ListHeaderView (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initViews();
        readAttributes(attrs);
    }

    /**
     * 初始化各种View
     */
    public void initViews () {
        View view = inflate(context, R.layout.layout_list_header_view, this);

        tv_return_top = (TextView) view.findViewById(R.id.tv_return_top);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        cb_select_all = (CheckBox) view.findViewById(R.id.cb_select_all);
        ll_space = view.findViewById(R.id.ll_space);
        cb_select_all.setOnClickListener(this);
        ll_space.setOnClickListener(this);
    }


    /**
     * 读取各项配置属性，设置UI
     * @param attrs 配置属性
     */
    private void readAttributes(AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.ListHeaderView);

        String title = attrArray.getString(R.styleable.ListHeaderView_header_title);
        tv_title.setText(title);

        int color = attrArray.getColor(R.styleable.ListHeaderView_textColor, Color.BLACK);
        tv_title.setTextColor(color);
        cb_select_all.setTextColor(color);
        tv_return_top.setTextColor(color);

        int size = attrArray.getDimensionPixelSize(R.styleable.ListHeaderView_textSize, 12);
        tv_title.setTextSize(size);
        cb_select_all.setTextSize(size);
        tv_return_top.setTextSize(size);

        String cbText = attrArray.getString(R.styleable.ListHeaderView_checkAll_text);
        cb_select_all.setText(cbText);
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public void setTitle (String title) {
        tv_title.setText(title);
    }

    /**
     * 设置是否勾选全选
     * @param check 是否勾选
     */
    public void setCheckAll (boolean check) {
        cb_select_all.setChecked(check);
    }

    /**
     * 设置显示返回到顶部的提示
     * @param show 是否显示
     */
    public void showReturnTopAlert (boolean show) {
        tv_return_top.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    /**
     * 点击事件处理
     * @param v 被点击的对像
     */
    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.cb_select_all:
                listener.onCheckAllSetListener(cb_select_all.isChecked());
                break;
            case R.id.ll_space:
                long currentTime = SystemClock.currentThreadTimeMillis();
                if ((currentTime - lastClickedTime) <= 200) {
                    listener.onDoubleClickedListener();
                }
                lastClickedTime = currentTime;
                break;
        }
    }
}
