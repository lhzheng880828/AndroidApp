package com.eeontheway.android.applocker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eeontheway.android.applocker.lock.LockService;

/**
 * 开机启动广播接受器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private Context context;

    /**
     * Receiver的onRecive回调
     * @param context 上下文
     * @param intent 数据意图
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // 启动应用锁服务
        LockService.startBlockService(context);
    }
}
