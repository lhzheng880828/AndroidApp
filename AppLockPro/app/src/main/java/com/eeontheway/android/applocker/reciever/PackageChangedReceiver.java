package com.eeontheway.android.applocker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.eeontheway.android.applocker.utils.SystemUtils;

/**
 * 包修改广播接受器
 * 用于在检查到安装包发生变化，例如升级时调用，启动自己
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PackageChangedReceiver extends BroadcastReceiver {
    /**
     * Receiver的onRecive回调
     * @param context 上下文
     * @param intent 数据意图
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // 安装包更新完毕后，立即启动自己
//        SystemUtils.startApp(context, context.getPackageName());
//        Toast.makeText(context, "hello", Toast.LENGTH_SHORT);
    }
}
