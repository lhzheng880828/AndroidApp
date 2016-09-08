package com.eeontheway.android.applocker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 系统信息工具
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class SystemUtils {
    /**
     * 格式化时间字符串
     * @param time 时间字符串
     * @param format 格式，参考"yyyy-MM-dd HH:mm:ss"
     * @return 时间对像
     */
    public static Date formatDate (String time, String format) {
        Date date  = null;

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化时间字符串
     * @param date 指定时间
     * @param format 格式，参考"yyyy-MM-dd HH:mm:ss"
     * @return 时间对像
     */
    public static String formatDate (Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化时间字符串
     * @param calendar 指定时间
     * @param format 格式，参考"yyyy-MM-dd HH:mm:ss"
     * @return 时间对像
     */
    public static String formatDate (Calendar calendar, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }


    /**
     * 获取当前App安装的时间
     *
     * @return 安装的时间
     */
    public static long getAppInstalledTime(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return info.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 查看应用的详细信息
     *
     * @param packageName 应用的包名
     */
    public static void viewAppInfo(Context context, String packageName) {
        // 查看应用的详细信息
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    /**
     * 启动应用
     *
     * @param packageName 应用的包名
     */
    public static void startApp (Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            Toast.makeText(context, R.string.unable_start, Toast.LENGTH_SHORT).show();
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 移除应用
     *
     * @param packageName 应用的包名
     */
    public static void uninstallApp (Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        if (intent == null) {
            Toast.makeText(context, R.string.unable_uninstall, Toast.LENGTH_SHORT).show();
        } else {
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
        }
    }


    /**
     * 安装指定应用
     * @param apkPath APP安装包路径
     */
    public static void installApp (Context context, String apkPath,
                                            boolean checkResult, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        if (checkResult) {
            ((Activity)context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }


    /**
     * 判断应用是否安装
     * @param packageName 应用的包名
     * @return true 安装; false 未安装
     */
    public static boolean isAppInstalled (Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(
                PackageManager.GET_ACTIVITIES);

        for (PackageInfo info : packageInfos) {
            if (packageName.equals(info.applicationInfo.packageName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取本APP用户可读的版本名
     * @return 版本名称
     */
    public static String getVersionName (Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取APP版本号
     * @return 版本号
     */
    public static int getVersionCode (Context context) {
        int versionCode = 0;

        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * 获取指定网络是否连接
     * @param context 上下文
     * @param type 指定的网络类型,取值ConnectivityManager的TYPE_MOBILE或者TYPE_WIFI
     * @return true 已连接; false 未连接
     */
    public static boolean isNetworkConnected (Context context, int type) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
                                        Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < 21) {
            NetworkInfo info = manager.getNetworkInfo(type);
            if ((info != null) && info.isConnected()) {
                return true;
            }
        } else {
            Network[] networks = manager.getAllNetworks();
            for (Network net : networks) {
                NetworkInfo info = manager.getNetworkInfo(net);
                if ((info.getType() == type) && info.isConnected()) {
                    return true;
                }
            }
        }

        return false;
    }
}

