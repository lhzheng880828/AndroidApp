package com.eeontheway.android.applocker.lock;


import java.io.Serializable;

/**
 * App锁定日志信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AccessLog implements Serializable {
    public static final String PHOTO_PATH_PREFIX = "AppLockerPhoto";
    private long id;
    private String appName;
    private String packageName;
    private String photoPath;
    private String time;
    private String location;
    private int passwordErrorCount;
    private boolean selected;

    /**
     * 复制一个AccessLog
     * @param log 复制源
     */
    public void copy (AccessLog log) {
        this.id = log.id;
        this.appName = new String(log.appName);
        this.packageName = new String(log.packageName);
        this.photoPath = new String(log.photoPath);
        this.time = log.time;
        this.location = new String(log.location);
        this.passwordErrorCount = log.passwordErrorCount;
    }

    /**
     * 获取内部ID
     * @return 内部ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置内部ID
     * @param id 内部ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取应用的包名
     * @return 应用的包名
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 设置应用的包名
     * @return 应用的包名
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    /**
     * 获取App名称
     * @return App名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 设置App名称
     * @param appName App名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 获取日志纪录时间
     * @return 日志纪录时间
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置日志纪录时间
     * @param time 日志纪录时间
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 获取密码次数
     * @return 密码次数
     */
    public int getPasswordErrorCount() {
        return passwordErrorCount;
    }

    /**
     * 设置密码次数
     * @param passwordErrorCount 密码次数
     */
    public void setPasswordErrorCount(int passwordErrorCount) {
        this.passwordErrorCount = passwordErrorCount;
    }

    /**
     * 获取拍摄的照片路径
     * @return 照片路径
     */
    public String getPhotoPath() {
        return photoPath;
    }

    /**
     * 设置拍摄的照片路径
     * @param photoPath 照片路径
     */
    public void setPhotoPath (String photoPath) {
        this.photoPath = photoPath;
    }

    /**
     * 图像是否保存在内部
     * @return true 是; false 否
     */
    public boolean isPhotoInInternal () {
        return photoPath.startsWith("/");
    }

    /**
     * 获取日志产生的地理位置
     * @return 地理位置
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置日志产生的地址位置
     * @param location 地理位置
     */
    public void setLocation(String location) {
        this.location = location;
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
