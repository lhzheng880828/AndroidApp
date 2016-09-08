package com.eeontheway.android.applocker.updater;

/**
 * 升级日志
 * 用于将用户的升级行为搜索到起来存储到服务器中
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UpdateLog {
    private int oldVersionNum;
    private int newVersionNum;
    private boolean skipped;
    private String time;

    /**
     * 获取老版本号
     * @return 老版本号
     */
    public int getOldVersionNum() {
        return oldVersionNum;
    }

    /**
     * 设置老版本号
     * @param oldVersionNum 老版本号
     */
    public void setOldVersionNum(int oldVersionNum) {
        this.oldVersionNum = oldVersionNum;
    }

    /**
     * 获取新版本号
     * @return
     */
    public int getNewVersionNum() {
        return newVersionNum;
    }

    /**
     * 设置新版本号
     * @param newVersionNum
     */
    public void setNewVersionNum(int newVersionNum) {
        this.newVersionNum = newVersionNum;
    }

    /**
     * 该新版本是否被跳过
     * @return 是否跳过
     */
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * 设置是否被跳过
     * @param skipped 被跳过
     */
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    /**
     * 获取操作发生的时间
     * @return 发生的时间
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置操作发生的时间
     * @param time 操作发生的时间
     */
    public void setTime(String time) {
        this.time = time;
    }
}
