package com.eeontheway.android.applocker.share;

import android.content.Context;

/**
 * 将消息分享到QQ空间
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class QQShareQzone extends QQShareBase {
    /**
     * 初始化分享接口
     * @param context 上下文
     */
    @Override
    public void init(Context context) {
        super.init(context);
        setToFriend(false);
    }
}
