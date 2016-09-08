package com.eeontheway.android.applocker.lock;

import com.eeontheway.android.applocker.app.AppInfo;

/**
 * 配置锁定的App基本信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AppLockInfo {
    private long id;
    private AppInfo appInfo;
    private LockModeInfo modeInfo;
    private boolean enable;
    private boolean selected;


    /**
     * 获取模式ID
     * @return ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置模式ID
     * @param id 模式ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取App信息
     * @return App信息
     */
    public AppInfo getAppInfo () {
        return appInfo;
    }

    /**
     * 设置App信息
     * @param appInfo App信息
     */
    public void setAppInfo (AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    /**
     * 获取锁定模式信息
     * @return 模式信息
     */
    public LockModeInfo getModeInfo() {
        return modeInfo;
    }

    /**
     * 设置锁定模式信息
     * @param modeInfo 模式信息
     */
    public void setModeInfo(LockModeInfo modeInfo) {
        this.modeInfo = modeInfo;
    }

    /**
     * 是否使能锁定
     * @return true/false
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 设置是否使能锁定
     * @param enable true/false
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 获取应用的包名
     */
    public String getPackageName () {
        return getAppInfo().getPackageName();
    }

    /**
     * 是否被选中
     * @return true/fase
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置是否被选中
     * @param selected 是否被选中
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
