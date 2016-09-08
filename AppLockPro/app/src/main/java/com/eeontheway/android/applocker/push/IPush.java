package com.eeontheway.android.applocker.push;

/**
 * 终端消息推送器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public interface IPush {

    int PUSH_TYPE_GETUI = 0;

    /**
     * 推送消息给客户端
     * @param info
     */
    void pushMsg (PushInfo info, String clientId);
}
