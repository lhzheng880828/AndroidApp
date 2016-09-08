package com.eeontheway.android.applocker.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 服务类工具
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ServiceUtils {
    /**
     * 检查服务是否正在运行
     * @param mContext 上下文
     * @param serviceName 服务类名称
     * @return true 正在运行; false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = am.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }

        for (int i = 0; i < myList.size(); i++) {
            String name = myList.get(i).service.getClassName().toString();
            if (name.equals(serviceName)) {
                isWork = true;
                break;
            }
        }

        return isWork;
    }
}
