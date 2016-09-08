package com.eeontheway.android.applocker.share;

import android.content.Context;

import com.alipay.share.sdk.openapi.APAPIFactory;
import com.alipay.share.sdk.openapi.APMediaMessage;
import com.alipay.share.sdk.openapi.APWebPageObject;
import com.alipay.share.sdk.openapi.IAPApi;
import com.alipay.share.sdk.openapi.SendMessageToZFB;
import com.eeontheway.android.applocker.apshare.ShareEntryActivity;
import com.eeontheway.android.applocker.utils.Configuration;
import com.eeontheway.android.applocker.utils.SystemUtils;

/**
 * 支付宝分享接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AlipayShare extends ShareBase implements IShare {
    protected static IAPApi api;
    private static int instanceCount;

    protected Context context;

    /**
     * 获取WX对像
     * @return WX对像
     */
    public static IAPApi getAPI () {
        return api;
    }

    /**
     * 初始化分享接口
     */
    @Override
    public void init (Context context) {
        this.context = context;
        if (api == null) {
            api = APAPIFactory.createZFBApi(context.getApplicationContext(),
                                Configuration.ALIPAY_APPID, true);
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
                api = null;
            }
        }
    }

    /**
     * 判断是否支持分享功能
     * @return true 支持分享; false 不支持分享
     */
    public boolean isSupported () {
        boolean isZFBInstalled = api.isZFBSupportAPI();
        boolean isZFBSupportApi = api.isZFBSupportAPI();
        return isZFBInstalled && isZFBSupportApi;
    }

    /**
     * 分享信息到相应接口
     * @param info 待分享的信息
     */
    public void share (ShareInfo info) {
        // 配置回调监听器
        ShareEntryActivity.setListener(getListener());

        // 分享出去
        APWebPageObject webPageObject = new APWebPageObject();
        webPageObject.webpageUrl = info.url;

        APMediaMessage webMessage = new APMediaMessage();
        webMessage.title = info.title;
        webMessage.description = info.content;
        webMessage.mediaObject = webPageObject;
        //webMessage.setThumbImage(info.thumbBitmap);

        SendMessageToZFB.Req webReq = new SendMessageToZFB.Req();
        webReq.message = webMessage;
        webReq.transaction = buildTransaction("webpage");
        api.sendReq(webReq);
    }

    /**
     * 创建传输类型
     * @param type 传输类型
     * @return 字符串，带时间
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                                        : type + System.currentTimeMillis();
    }
}
