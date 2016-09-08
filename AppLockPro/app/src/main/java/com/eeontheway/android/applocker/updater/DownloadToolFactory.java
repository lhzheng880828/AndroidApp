package com.eeontheway.android.applocker.updater;

import android.content.Context;

import com.eeontheway.android.applocker.utils.Configuration;

/**
 * 下载模块生成工厂
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class DownloadToolFactory {
    /**
     * 创建升级信息获取的对像
     * @return 升级信息获取对像
     */
    public static IDownloadTool create (Context context) {
        IDownloadTool tool = null;
        switch (Configuration.UPDATE_DOWNLOAD_METHOD) {
            case Configuration.UPDATE_DOWNLOAD_METHOD_BMOB:
                tool = new HttpDownloadTool(context);
                break;
        }

        return tool;
    }
}
