package com.eeontheway.android.applocker.updater;

import cn.bmob.v3.BmobObject;

/**
 * 专用于BmobSDK的更新信息类
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class BmobUpdateInfo extends BmobObject {
    private boolean enable;
    private boolean force;
    private int versionNum;
    private String versionName;
    private long fileSize;
    private String updateLog;
    private String url;

    /**
     * 构造函数
     * @param baseInfo
     */
    public BmobUpdateInfo (UpdateInfo baseInfo) {
        // Bmob要求信息域必须定义在BmobObject的子类中
        // 所以，只能重复定义下，重复使用
        force = baseInfo.isForece();
        versionNum = baseInfo.getVersionNum();
        versionName = baseInfo.getVersionName();
        fileSize = baseInfo.getFileSize();
        updateLog = baseInfo.getUpdateLog();
        url = baseInfo.getUrl();
        enable = baseInfo.isEnable();
    }

    /**
     * 获取反馈内容
     * @return 反馈内容
     */
    public UpdateInfo toUpdateInfo () {
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setForce(force);
        updateInfo.setVersionNum(versionNum);
        updateInfo.setVersionName(versionName);
        updateInfo.setFileSize(fileSize);
        updateInfo.setUpdateLog(updateLog);
        updateInfo.setUrl(url);
        updateInfo.setEnable(enable);

        return updateInfo;
    }
}
