package com.eeontheway.android.applocker.main;

import com.eeontheway.android.applocker.app.AppInfo;

/**
 * App锁定信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AppSelectInfo {
    private AppInfo appInfo;
    private boolean selected;

    /**
     * 获取App信息
     * @return App信息
     */
    public AppInfo getAppInfo() {
        return appInfo;
    }

    /**
     * 设置App信息
     * @param appInfo App信息
     */
    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    /**
     * 返回应用是否选择信息
     * @return 应用是否选择
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置应用选择信息
     * @param selected 应用是否被选择
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
