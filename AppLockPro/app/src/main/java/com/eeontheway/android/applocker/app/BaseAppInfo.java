package com.eeontheway.android.applocker.app;

import android.graphics.drawable.Drawable;

/**
 * 应用或进程信息的基类
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class BaseAppInfo {
    /**
     * 应用的名称
     */
    private String name;

    /**
     * 获取应用名
     *
     * @return 应用名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置应用名信息
     *
     * @param name 应用名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 应用的图标
     */
    private Drawable icon;

    /**
     * 获取图标
     *
     * @return 应用图标
     */
    public Drawable getIcon() {
        return icon;
    }

    /**
     * 设置图标信息
     *
     * @param icon 应用图标
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * 应用占用的存储空间
     */
    private long usedSize;

    /**
     * 获取占用的空间大小
     *
     * @return 占用的空间大小
     */
    public long getUsedSize() {
        return usedSize;
    }

    /**
     * 设置占用空间信息
     *
     * @param usedSize 占用的空间大小
     */
    public void setUsedSize(long usedSize) {
        this.usedSize = usedSize;
    }

    /**
     * 应用的包名
     */
    private String packageName;

    /**
     * 获取应用包名
     *
     * @return 应用包名
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 设置应用包名
     *
     * @param packageName 应用包名信息
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * 是否是用户App
     */
    private boolean userApp;

    /**
     * 判断是否是用户App
     *
     * @return true 是用户App; false 系统App
     */
    public boolean isUserApp() {
        return userApp;
    }

    /**
     * 设置应用的App类型
     *
     * @param userApp true 用户App; false 系统App
     */
    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    /**
     * 该项是否选中，仅用于UI
     */
    private boolean selected;

    /**
     * 该项是否被选中?
     *
     * @return true 选中; false 未选中
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置该项是否被选中
     *
     * @param selected true 选中; false 未选中
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 获取版本名称
     *
     * @return 版本名称
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * 设置版本名
     *
     * @param versionName 新的版本名
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * 应用签名信息
     */
    private String signatures;

    /**
     * 获取应用签名
     *
     * @return 应用签名
     */
    public String getSignatures() {
        return signatures;
    }

    /**
     * 设置应用签名
     *
     * @param signatures 应用签名
     */
    public void setSignatures(String signatures) {
        this.signatures = signatures;
    }

    /**
     * Cache大小
     */
    private long cacheSize;

    /**
     * 获得缓存大小
     *
     * @return 缓存大小
     */
    public long getCacheSize() {
        return cacheSize;
    }

    /**
     * 设置缓存大小
     *
     * @param cacheSize 缓存大小
     */
    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
}
