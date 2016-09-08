package com.eeontheway.android.applocker.updater;

import cn.bmob.v3.BmobObject;

/**
 * 专用于BMOB的升级日志
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class BmobUpdateLog extends BmobObject {
    private int oldVersionNum;
    private int newVersionNum;
    private boolean skipped;

    /**
     * 构造函数
     * @param log 日志信息
     */
    public BmobUpdateLog (UpdateLog log) {
        // Bmob要求信息域必须定义在BmobObject的子类中
        // 所以，只能重复定义下，重复使用
        oldVersionNum = log.getOldVersionNum();
        newVersionNum = log.getNewVersionNum();
        skipped = log.isSkipped();
    }

    /**
     * 获取反馈内容
     * @return 反馈内容
     */
    public UpdateLog toUpdateLog () {
        UpdateLog log = new UpdateLog();
        log.setOldVersionNum(oldVersionNum);
        log.setNewVersionNum(newVersionNum);
        log.setSkipped(skipped);
        log.setTime(getCreatedAt());

        return log;
    }
}
