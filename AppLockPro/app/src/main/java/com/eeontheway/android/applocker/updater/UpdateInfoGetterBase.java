package com.eeontheway.android.applocker.updater;

import android.content.Context;

/**
 * BMOB升级信息获取器基础类
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public abstract class UpdateInfoGetterBase implements IUpdateInfoGetter {
    private Context context;
    protected ResultListener listener;

    /**
     * 构造函数
     * @param context 上下文
     */
    public UpdateInfoGetterBase(Context context) {
        this.context = context;
    }

    /**
     * 设置获取结果监听器
     * @param listener
     */
    public void setResultListener(ResultListener listener) {
        this.listener = listener;
    }
}
