package com.eeontheway.android.applocker.share;

import android.content.Context;

/**
 * 微信分享接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ShareFactory {
    /**
     * 创建分享实例接口
     * @param type 分享类型
     * @return 分享接口
     */
    public static IShare create (Context context, int type) {
        IShare shareSDK  = null;

        switch (type) {
            case IShare.SHARE_TYPE_WX_FRIEND:        // 分享到微信朋友
                shareSDK = new WXShareFriend();
                shareSDK.init(context);
                break;
            case IShare.SHARE_TYPE_WX_TIMELINE:      // 分享到微信朋友圈
                shareSDK = new WXShareTimeLine();
                shareSDK.init(context);
                break;
            case IShare.SHARE_TYPE_QQ_FRIEND:        // 分享到QQ好友
                shareSDK = new QQShareFriend();
                shareSDK.init(context);
                break;
            case IShare.SHARE_TYPE_QZONE:            // 分享到QQ空间
                shareSDK = new QQShareQzone();
                shareSDK.init(context);
                break;
            case IShare.SHARE_TYPE_ALIPAY:          // 支付宝分享
                shareSDK = new AlipayShare();
                shareSDK.init(context);
                break;
            case IShare.SHARE_TYPE_SYSTEM:          // 使用系统分享
                shareSDK = new SystemShare();
                shareSDK.init(context);
                break;
        }
        return shareSDK;
    }
}
