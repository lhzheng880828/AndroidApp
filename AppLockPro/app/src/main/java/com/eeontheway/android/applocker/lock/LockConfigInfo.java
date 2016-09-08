package com.eeontheway.android.applocker.lock;

/**
 * App锁定配置信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class LockConfigInfo {
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
     * 是否被锁定
     */
    private boolean locked;

    /**
     * 返回应用是否锁定信息
     * @return 应用是否锁定
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 设置应用锁定信息
     * @param locked 应用是否锁定
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
