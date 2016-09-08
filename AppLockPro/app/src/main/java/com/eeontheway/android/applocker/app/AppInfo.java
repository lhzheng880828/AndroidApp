package com.eeontheway.android.applocker.app;

/**
 * 应用信息类
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AppInfo extends BaseAppInfo {
    private int versionCode;
    private boolean inRom;

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * 设置版本号信息
     *
     * @param versionCode 版本号
     */
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 判断是位于内部Rom
     *
     * @return true 位于内部ROM; false 不位于内部Rom
     */
    public boolean isInRom() {
        return inRom;
    }

    /**
     * 设置是否位于内部ROM信息
     *
     * @param inRom true 位于内部ROM; false 不位于内部Rom
     */
    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }
}
