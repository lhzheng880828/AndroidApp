package com.eeontheway.android.applocker.lock;

import android.content.Context;
import android.util.Log;

import com.eeontheway.android.applocker.app.AppInfoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 已解锁的App列表
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UnlockedList {
    private Context context;
    private AppInfoManager appInfoManager;
    private List<String> appList = new ArrayList<>();

    /**
     * 构造函数
     * @param context
     */
    public UnlockedList(Context context) {
        this.context = context;
        appInfoManager = new AppInfoManager(context);
    }

    /**
     * 清除App列表
     */
    synchronized public void clear () {
        appList.clear();
    }

    /**
     * 判断队列是否为空
     * @return true 是; false 否
     */
    synchronized public boolean isEmpty () {
        return appList.isEmpty();
    }

    /**
     * 添加App
     * @param packageName App的包名
     */
    synchronized public void add (String packageName) {
        if (!appList.contains(packageName)) {
            appList.add(packageName);
        }
    }

    /**
     * 将App从队列中移除
     * @param packageName
     */
    synchronized public void remove (String packageName) {
        appList.remove(packageName);
    }

    /**
     * 检查是否包含指定的App
     * @param packageName App的包名
     * @return true 包含; false 不包含
     */
    synchronized public boolean contains (String packageName) {
        return appList.contains(packageName);
    }

    /**
     * 遍历App列表，将已经退出的App从中移除
     */
    synchronized public void removeExitedApps () {
        for (String name : appList) {
            boolean existed = appInfoManager.isPackageRunning(name);
            if (existed == false) {
                appList.remove(name);
                Log.d("AppLocker", "Remove exited:" + name);
            }
        }
    }
}
