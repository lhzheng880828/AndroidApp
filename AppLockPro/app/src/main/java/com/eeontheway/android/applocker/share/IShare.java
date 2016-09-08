package com.eeontheway.android.applocker.share;

import android.content.Context;

/**
 * 第三方分享接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public interface IShare {
    int SHARE_TYPE_WX_FRIEND = 0;       // 微信朋友分享
    int SHARE_TYPE_WX_TIMELINE = 1;     // 微信朋友圈分享
    int SHARE_TYPE_QQ_FRIEND = 2;       // QQ好友分享
    int SHARE_TYPE_QZONE = 3;           // QQ空间分享
    int SHARE_TYPE_ALIPAY = 4;          // 支付宝分享
    int SHARE_TYPE_SYSTEM = 5;          // 使用系统分享

    /**
     * 操作结果监听器
     */
    interface OnFinishListener {
        void onFinish (int code, String msg);
    }

    /**
     * 初始化分享接口
     *
     */
    void init (Context context);

    /**
     * 反初始化微信
     */
    void uninit ();

    /**
     * 判断是否支持分享功能
     * @return true 支持分享; false 不支持分享
     */
    boolean isSupported ();

    /**
     * 分享信息到相应接口
     * @param info 待分享的信息
     */
    void share (ShareInfo info);

    /**
     * 是否分享到朋友
     * @return true 分享到朋友; false 否
     */
    boolean isToFriend ();

    /**
     * 设置接受的对像为朋友
     * @param toFriend
     */
    void setToFriend (boolean toFriend);

    /**
     * 设置分享结束时的监听器
     * @param listener 监听器
     */
    void setListener(IShare.OnFinishListener listener);

    /**
     * 获取分享结束时，应当调用的监听器
     * @return 监听器
     */
    IShare.OnFinishListener getListener();
}
