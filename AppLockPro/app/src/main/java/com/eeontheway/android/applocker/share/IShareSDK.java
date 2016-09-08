package com.eeontheway.android.applocker.share;


import android.content.Context;

/**
 * 第三方分享接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public interface IShareSDK {
    /**
     * 初始化分享接口
     *
     * @param context 上下文
     */
    void init (Context context);

    void share ();
}
