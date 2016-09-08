package com.eeontheway.android.applocker.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;

/**
 * 用于地址的Marker View
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class MapMakerWindow implements View.OnClickListener {
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_STATELLITE = 1;

    private Context context;
    private TextView tv_title;
    private View ll_background;
    private View myView;
    private TextView tv_ok;
    private OnSetUsePostionListener usePostionListener;

    /**
     * 构造函数
     * @param context 上下文
     */
    public MapMakerWindow(Context context) {
        this.context = context;
    }

    /**
     * 选择此Mark的事件
     */
    public interface OnSetUsePostionListener {
        void useMarkerPostion ();
    }

    /**
     * 设置点击事件处理器
     * @param listener
     */
    public void SetUsePostionListener(OnSetUsePostionListener listener) {
        this.usePostionListener = listener;
    }

    /**
     * 初始化各种View
     */
    public View initViews () {
        myView = View.inflate(context, R.layout.view_map_marker, null);

        ll_background = myView.findViewById(R.id.ll_background);
        tv_ok = (TextView)myView.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);
        tv_title = (TextView) myView.findViewById(R.id.tv_title);
        tv_title.setTextSize(12);       // Fix
        tv_ok.setTextSize(14);
        return myView;
    }

    /**
     * 点击事件处理器
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (usePostionListener != null) {
                    usePostionListener.useMarkerPostion();
                }
                break;
        }
    }

    /**
     * 获取显示界面
     * @return 显示界面
     */
    public View getView() {
        return myView;
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public void setTitle (String title) {
        tv_title.setText(title);
    }

    /**
     * 设置显示的样式
     * @param style 显示样式
     */
    public void setStyle (int style) {
        if (style == STYLE_NORMAL) {
            ll_background.setBackgroundResource(R.drawable.ic_marker_normal_background);
            tv_title.setTextColor(context.getResources().getColor(R.color.colorSecondTextColor));
        } else {
            ll_background.setBackgroundResource(R.drawable.ic_marker_statellite_background);
            tv_title.setTextColor(context.getResources().getColor(R.color.colorSecondTextColor));
        }
    }
}
