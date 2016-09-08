package com.eeontheway.android.applocker.share;

import android.content.Context;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.utils.Configuration;
import com.eeontheway.android.applocker.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

/**
 * 微信分享接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class WXShareBase extends ShareBase implements IShare {
    protected static IWXAPI api;
    private static int instanceCount;

    protected Context context;

    /**
     * 获取WX对像
     * @return WX对像
     */
    public static IWXAPI getAPI () {
        return api;
    }

    /**
     * 初始化分享接口
     */
    @Override
    public void init (Context context) {
        this.context = context;
        if (api == null) {
            api = WXAPIFactory.createWXAPI(context, Configuration.WX_APPID, false);
            api.registerApp(Configuration.WX_APPID);
        }
        instanceCount++;
    }

    /**
     * 反初始化接口
     */
    @Override
    public void uninit() {
        if (instanceCount > 0) {
            if (--instanceCount == 0) {
                api.unregisterApp();
                api = null;
            }
        }
    }

    /**
     * 判断是否支持分享功能
     * @return true 支持分享; false 不支持分享
     */
    public boolean isSupported () {
        boolean isInstalled = api.isWXAppInstalled();
        boolean isSupportApi = api.isWXAppSupportAPI();
        return isInstalled && isSupportApi;
    }

    /**
     * 分享信息到相应接口
     * @param info 待分享的信息
     */
    public void share (ShareInfo info) {
        // 配置回调监听器
        WXEntryActivity.setListener(getListener());

        // 构造一个网页对像
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = info.url;

        // 创建消息对像
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = info.title;
        msg.description = info.content;
        msg.setThumbImage(info.thumbBitmap);

        // 分享信息到微信会话
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isToFriend() ? req.WXSceneSession : req.WXSceneTimeline;
        api.sendReq(req);
    }
}
