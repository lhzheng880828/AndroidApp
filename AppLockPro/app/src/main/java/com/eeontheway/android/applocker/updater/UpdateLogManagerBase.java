package com.eeontheway.android.applocker.updater;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * APK升级日志纪录
 * 用于将各个用户的升级信息主动发送到服务器中用于后续统计纪录
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public abstract class UpdateLogManagerBase implements IUpdateLogManager {
    private Context context;
    protected SaveResultListener listener;

    /**
     * 构造函数
     * @param context 上下文
     */
    public UpdateLogManagerBase(Context context) {
        this.context = context;
    }

    /**
     * 设置获取结果监听器
     * @param listener
     */
    public void setSaveResultListener(SaveResultListener listener) {
        this.listener = listener;
    }

    /**
     * 分配一个升级日志对像
     * @return  日志对像
     */
    public UpdateLog allocateUpdateLog (int oldVersion, int newVersion, boolean skipped) {
        UpdateLog updateLog = new UpdateLog();
        updateLog.setOldVersionNum(oldVersion);
        updateLog.setNewVersionNum(newVersion);
        updateLog.setSkipped(skipped);
        updateLog.setTime(new SimpleDateFormat().format(new Date()));
        return updateLog;
    }
}
