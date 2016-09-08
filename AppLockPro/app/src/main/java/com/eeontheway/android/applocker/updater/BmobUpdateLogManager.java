package com.eeontheway.android.applocker.updater;

import android.content.Context;

import cn.bmob.v3.listener.SaveListener;

/**
 * 专用于BMOB的升级日志管理器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class BmobUpdateLogManager extends UpdateLogManagerBase {
    private Context context;

    /**
     * 构造函数
     * @param context 上下文
     */
    public BmobUpdateLogManager(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 保存日志信息
     * @param log 待保存的日志信息
     */
    public void saveUpdateLog (UpdateLog log) {
        BmobUpdateLog updateLog = new BmobUpdateLog(log);
        updateLog.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (listener != null) {
                    listener.onFail(i, s);
                }
            }
        });
    }
}
