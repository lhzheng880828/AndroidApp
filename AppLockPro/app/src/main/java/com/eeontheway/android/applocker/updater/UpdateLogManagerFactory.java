package com.eeontheway.android.applocker.updater;

import android.content.Context;

import com.eeontheway.android.applocker.utils.Configuration;

/**
 * 升级日志纪录器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UpdateLogManagerFactory {
    /**
     * 创建升级信息获取的对像
     * @return 升级信息获取对像
     */
    public static IUpdateLogManager create (Context context) {
        IUpdateLogManager op = null;
        switch (Configuration.UPDATE_WRITE_LOG_METHOD) {
            case Configuration.UPDATE_WRITE_LOG_METHOD_BMOB:
                op = new BmobUpdateLogManager(context);
                break;
        }

        return op;
    }
}
