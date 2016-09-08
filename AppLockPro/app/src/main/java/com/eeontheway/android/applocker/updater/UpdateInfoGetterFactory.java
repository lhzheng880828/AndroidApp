package com.eeontheway.android.applocker.updater;

import android.content.Context;

import com.eeontheway.android.applocker.utils.Configuration;

/**
 * 升级信息获取器接口工厂
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class UpdateInfoGetterFactory {
    /**
     * 创建升级信息获取的对像
     * @return 升级信息获取对像
     */
    public static IUpdateInfoGetter create (Context context) {
        IUpdateInfoGetter getter = null;
        switch (Configuration.UPDATE_INFOGET_METHOD) {
            case Configuration.UPDATE_INFOGET_METHOD_BMOB:
                getter = new BmobUpdateInfoGetter(context);
                break;
        }

        return getter;
    }
}
