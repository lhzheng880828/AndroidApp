package com.eeontheway.android.applocker.push;

import android.content.Context;

import com.eeontheway.android.applocker.share.IShare;
import com.eeontheway.android.applocker.utils.Configuration;

import java.security.InvalidParameterException;

/**
 * 推送工厂
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PushFactory {
    /**
     * 创建推送接口
     * @return 推送接口
     */
    public static IPush create (Context context) {
        IPush iPush = null;

        switch (Configuration.PUSH_SERVICE_PROVIDER) {
            case IPush.PUSH_TYPE_GETUI:
                iPush = new GetTuiPush();
                break;
            default:
                throw new InvalidParameterException("Unkown type");
        }
        return iPush;
    }

}
