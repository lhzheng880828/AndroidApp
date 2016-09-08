package com.eeontheway.android.applocker.feedback;

/**
 * 信息反馈基类
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public abstract class FeedBackBase implements IFeedBack {
    protected static int QUERY_TIME_OLDER = 0;           // 查找类型，更早的时间
    protected static int QUERY_TIME_NEWER = 1;           // 查询类型，更新的时间

    protected IFeedBack.SendStatusListener sendListener;
    protected IFeedBack.SendStatusListener updateListener;
    protected IFeedBack.QueryTopicStatusListener queryTopicListener;
    protected IFeedBack.QueryResponseStatusListener queryResponseListener;

    /**
     * 设置发送状态的监听器
     * @param listener
     */
    public void setSendListener(IFeedBack.SendStatusListener listener) {
        sendListener = listener;
    }

    /**
     * 设置更新状态的监听器
     * @param listener
     */
    public void setUpdateListener(IFeedBack.SendStatusListener listener) {
        updateListener = listener;
    }

    /**
     * 设置查询状态的监听器
     * @param listener
     */
    public void setQueryTopicListener(IFeedBack.QueryTopicStatusListener listener) {
        queryTopicListener = listener;
    }

    /**
     * 设置查询状态的监听器
     * @param listener
     */
    public void setQueryResponseicListener(IFeedBack.QueryResponseStatusListener listener) {
        queryResponseListener = listener;
    }
}
