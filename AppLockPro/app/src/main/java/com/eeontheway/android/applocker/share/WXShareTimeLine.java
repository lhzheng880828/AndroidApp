package com.eeontheway.android.applocker.share;

import android.content.Context;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

/**
 * 将消息分享到微信朋友圈
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class WXShareTimeLine extends WXShareBase {
    // 分享到朋友圈的最低版本
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    /**
     * 初始化分享接口
     * @param context 上下文
     */
    @Override
    public void init(Context context) {
        super.init(context);
        setToFriend(false);
    }

    /**
     * 判断是否支持分享功能
     * @return true 支持分享; false 不支持分享
     */
    public boolean isSupported () {
        boolean support = super.isSupported();
        if (support == false) return false;

        if (api.getWXAppSupportAPI() < TIMELINE_SUPPORTED_VERSION) {
            Toast.makeText(context, R.string.wx_share_timeline_notsupport, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
