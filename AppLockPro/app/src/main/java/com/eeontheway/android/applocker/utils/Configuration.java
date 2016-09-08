package com.eeontheway.android.applocker.utils;

import com.eeontheway.android.applocker.push.IPush;

/**
 * App配置类
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public final class Configuration {
    /**
     * 是否是开发者模式
     */
    public static final boolean isDevelop = true;

    /**
     * BMOB的APPID
     */
    public static final String BMOB_APPID = "46120064d8e98adb870a67247a102485";

    /**
     * App官方网站
     */
    public static final String webSiteUrl = "http://app.eeontheway.com/";

    /**
     * APP更新地址
     */
    public static final String updateSiteUrl = "http://android.eeontheway.com/update/updateinfo.xml";

    /**
     * 本应用支持的应用市场包名
     */
    public static final String [] appMarketPackageName = {
            "com.qihoo.appstore",                   // 360手机助手
            "com.taobao.appcenter",                 // 淘宝手机助手
            "com.tencent.android.qqdownloader",     // 应用宝
            "com.hiapk.marketpho",                  // 安卓市场
            "com.huawei.appmarket",                 // 华为应用市场
            "cn.goapk.market"                       // 安智市场
    };

    public static final int BMOB_FEEDBACK = 0;      // BMOB的反馈管理器

    /**
     * 反馈管理器所用的SDK组件
     */
    public static final int FeedBackMangerType = BMOB_FEEDBACK;

    /**
     * Log显示的TAG
     */
    public static final String LOG_TAG = "applocker";

    /**
     * APP的描述信息
     */
    public static final String APP_DESCRIPTION = "智能应用锁是一款用于保护App免于被其它人查看的锁软件。" +
                                                 "当用户打开被锁定的特定应用时，会弹出密码输入界面，只有" +
                                                 "当密码输入正确时，才能打开App。";

    /**
     * 分享APP给其它人的标题
     */
    public static final String SHARE_APP_TITLE = "亲，给你介绍一款好用的App!";

    /**
     * 推送服务提供端
     */
    public static final int PUSH_SERVICE_PROVIDER = IPush.PUSH_TYPE_GETUI;  // 个推

    /**
     * 微信的APPID
     */
    public static final String WX_APPID = "wxb1f98f7e3d3563a9";

    /**
     * QQ的APPID和包名
     */
    public static final String QQ_APPID = "1105120437";
    public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";

    /**
     * 支付宝APPID
     */
    public static final String ALIPAY_APPID = "2016022101154813";

    /**
     * 升级的方式和配置
     */
    public static final int UPDATE_INFOGET_METHOD_BMOB = 0;     // 通过BMOB升级
    public static final int UPDATE_INFOGET_METHOD = UPDATE_INFOGET_METHOD_BMOB;

    public static final int UPDATE_WRITE_LOG_METHOD_BMOB = 0;
    public static final int UPDATE_WRITE_LOG_METHOD = UPDATE_WRITE_LOG_METHOD_BMOB;

    public static final int UPDATE_DOWNLOAD_METHOD_BMOB = 0;
    public static final int UPDATE_DOWNLOAD_METHOD = UPDATE_DOWNLOAD_METHOD_BMOB;

    /**
     * 用户管理接口类型
     */
    public static final int USER_MANAGER_TYPE_BMOB = 0;         // 通过BMOB管理用户
    public static final int USER_MANAGER_TYPE = USER_MANAGER_TYPE_BMOB;
}
