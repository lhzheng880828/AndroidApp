package com.eeontheway.android.applocker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * APP应用市场的工具类*
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AppMarketUtils {
    private Context context;
    private List<String> appMarketList = new ArrayList<>();

    /**
     * 构造函数
     * @param context 上下文
     */
    public AppMarketUtils(Context context) {
        this.context = context;

        // 获取所有的包
        queryMarketPkgs();
    }

    /**
     * 切换到应用市场，给App评分
     */
    public void gotoVoteInMarket () {
        if (!appMarketList.isEmpty()) {

            // 启动应用市场分享
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 查询所有应用商店的包名列表
     */
    private void queryMarketPkgs() {
        // 获取所有的应用商店列表
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MARKET);
        PackageManager pm = context.getPackageManager();

        // 解析出包名
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {
            String packageName = info.activityInfo.packageName;

            // 如果市场包名和自己已支持发布的相同，才确认可用
            for (String appMarket : Configuration.appMarketPackageName) {
                if (TextUtils.equals(packageName, appMarket)) {
                    appMarketList.add(packageName);
                }
            }
        }
    }
}
