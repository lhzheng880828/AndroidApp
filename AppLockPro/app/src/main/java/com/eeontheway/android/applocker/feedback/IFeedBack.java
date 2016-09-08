package com.eeontheway.android.applocker.feedback;

import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * 信息反馈接口
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public interface IFeedBack {
    /**
     * 发送回调接口
     */
    interface SendStatusListener {
        void onStart ();
        void onSuccess ();
        void onFail (String msg);
    }

    /**
     * 查询回调接口
     */
    interface QueryTopicStatusListener {
        void onStart ();
        void onSuccess (int type, List<FeedBackTopic> infoList);
        void onFail (int type, String msg);
    }

    /**
     * 查询回调接口
     */
    interface QueryResponseStatusListener {
        void onStart ();
        void onSuccess ();
        void onFail (String msg);
    }

    /**
     * 设置发送状态的监听器
     * @param listener
     */
    void setSendListener(SendStatusListener listener);

    /**
     * 设置更新状态的监听器
     * @param listener
     */
    void setUpdateListener(SendStatusListener listener);

    /**
     * 设置查询状态的监听器
     * @param listener
     */
    void setQueryTopicListener(QueryTopicStatusListener listener);

    /**
     * 设置查询状态的监听器
     * @param listener
     */
    void setQueryResponseicListener(QueryResponseStatusListener listener);

    /**
     * 初始化反馈接口
     * @return true 成功; false 失败
     */
    boolean init(Context context);

    /**
     * 反初始化反馈接口
     */
    void unInit();

    /**
     * 反送反馈信息
     * @param info 反馈信息
     */
    void sendFeedBack (FeedBackInfo info);

    /**
     * 更新反馈信息
     * @param info 反馈信息
     */
    void updateFeedBack (FeedBackInfo info);

    /**
     * 获取指定ID的反馈
     * @param id 获取的纪录条数
     */
    void queryFeedBackById (String id);

    /**
     * 获取比指定时间更早的最多N条纪录
     * @param count 获取的纪录条数
     */
    void queryFeedBackOlder (int count, Date cmpDate, Date updateDate);

    /**
     * 获取比指定时间更新的最多N条纪录
     * @param count 获取的纪录条数
     * @param cmpDate 比较的时间
     */
    void queryFeedBackNewer (int count, Date cmpDate, Date updateDate);

    /**
     * 获取指定主题的所有回复
     */
    void queryAllTopicResponse (final FeedBackTopic topic);
}
