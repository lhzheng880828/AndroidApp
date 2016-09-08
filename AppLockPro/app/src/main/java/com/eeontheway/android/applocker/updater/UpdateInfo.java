package com.eeontheway.android.applocker.updater;

/**
 * 升级信息类
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UpdateInfo {
    private boolean force;
    private int versionNum;
    private String versionName;
    private long fileSize;
    private String updateLog;
    private String url;
    private boolean enable;

    /**
     * 是否使能
     * @return true/false
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 设置是否使能
     * @param enable true/false
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 是否强制更新
     * @return true/false
     */
    public boolean isForece() {
        return force;
    }

    /**
     * 设置是否强制更新
     * @param force true/false
     */
    public void setForce (boolean force) {
        this.force = force;
    }

    /**
     * 获取版本号
     * @return 版本号
     */
    public int getVersionNum() {
        return versionNum;
    }

    /**
     * 设置版本号
     * @param version 版本号
     */
    public void setVersionNum(int version) {
        this.versionNum = version;
    }

    /**
     * 获取版本名称
     * @return 版本名称
     */
    public String getVersionName () {
        return versionName;
    }

    /**
     * 设置版本名称
     * @param versionName 版本名称
     */
    public void setVersionName (String versionName) {
        this.versionName = versionName;
    }

    /**
     * 获取升级文件大小
     * @return 升级文件大小
     */
    public long getFileSize() {
        return fileSize;
    }


    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取更新日志
     * @return 更新日志
     */
    public String getUpdateLog() {
        return updateLog;
    }

    /**
     * 设置更新日志
     * @param updateLog 更新日志
     */
    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    /**
     * 获取下载的URL
     * @return 下载的URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置下载的URL
     * @param url 下载的URL
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
