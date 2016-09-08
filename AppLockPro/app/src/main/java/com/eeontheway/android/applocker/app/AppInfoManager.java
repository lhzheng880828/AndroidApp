package com.eeontheway.android.applocker.app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程信息管理器
 * 主要用于获取进程的相关信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AppInfoManager {
    /**
     * APP类型
     */
    public enum AppType {
        ALL_APP, /**
         * 所有App
         */
        USER_APP, /**
         * 用户App
         */
        SYSTEM_APP      /** 系统App */
    }

    private Context context;
    private ActivityManager activityManager;
    private PackageManager packageManager;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public AppInfoManager(Context context) {
        this.context = context;
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = context.getPackageManager();
    }

    /**
     * 通过包名，获取完整的App信息
     * @param packageName 应用的包名
     * @return App信息
     */
    public AppInfo querySimpleAppInfo (String packageName) {
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);

            AppType type = ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) ?
                    AppType.USER_APP : AppType.SYSTEM_APP;AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfo.setIcon(info.loadIcon(packageManager));
            appInfo.setUserApp(type == AppType.USER_APP);
            appInfo.setName(info.loadLabel(packageManager).toString());
            return appInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取所有的App信息列表
     *
     * @param appType 待获取的App类型
     * @return App信息列表
     */
    public List<BaseAppInfo> queryAllAppInfo(AppType appType) {
        List<BaseAppInfo> appInfoList = new ArrayList<>();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(0);

        for (ApplicationInfo info : applicationInfoList) {
            AppType type = ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) ?
                    AppType.USER_APP : AppType.SYSTEM_APP;

            if ((appType == AppType.ALL_APP) || (appType == type)) {
                AppInfo appInfo = new AppInfo();
                appInfo.setPackageName(info.packageName);
                appInfo.setIcon(info.loadIcon(packageManager));
                appInfo.setName(info.loadLabel(packageManager).toString());
                appInfo.setUserApp(type == AppType.USER_APP);
                appInfoList.add(appInfo);
            }
        }

        return appInfoList;
    }

    /**
     * 查看位于前台的App
     * @return App的包名
     */
    public String queryFirstAppPackageName () {
        if (true) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
            if (runningTaskInfos.size() > 0) {
                return runningTaskInfos.get(0).topActivity.getPackageName();
            }
        } else {
            try {
                Field processStateTopField = ActivityManager.class.getDeclaredField("PROCESS_STATE_TOP");
                int processStateTop = processStateTopField.getInt(activityManager);

                List<ActivityManager.RunningAppProcessInfo> infoList = activityManager.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : infoList) {
                    Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
                    int currentState = processStateField.getInt(info);
                    if (currentState == processStateTop) {
                        return info.processName;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 查看位于前台的App
     * @return App的顶层Activity类名
     */
    public String queryFirstAppActivityName () {
        if (Build.VERSION.SDK_INT < 21) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
            if (runningTaskInfos.size() > 0) {
                return runningTaskInfos.get(0).topActivity.getClassName();
            }
        }

        return null;
    }


    /**
     * 查看位于前台的App
     * @return 任务信息
     */
    public ComponentName queryTopComponentName () {
        if (Build.VERSION.SDK_INT < 21) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
            if (runningTaskInfos.size() > 0) {
                return runningTaskInfos.get(0).topActivity;
            }
        }

        return null;
    }


    /**
     * 判断指定应用是否正在运行
     * @param packageName 要判断的任务
     * @return true 是; false 否
     */

    public boolean isPackageRunning (String packageName) {
        List<ActivityManager.RunningAppProcessInfo> infoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infoList) {
            try {
                if (info.processName.equals(packageName)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
