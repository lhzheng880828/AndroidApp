package com.eeontheway.android.applocker.share;

import android.content.Context;

/**
 * 将消息分享到QQ好友
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class QQShareFriend extends QQShareBase {
    /**
     * 初始化分享接口
     * @param context 上下文
     */
    @Override
    public void init(Context context) {
        super.init(context);
        setToFriend(true);
    }
}
