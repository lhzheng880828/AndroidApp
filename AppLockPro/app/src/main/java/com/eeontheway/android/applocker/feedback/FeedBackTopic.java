package com.eeontheway.android.applocker.feedback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 反馈主题
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class FeedBackTopic implements Serializable {
    private FeedBackInfo topic;
    private List<FeedBackInfo> responseList = new ArrayList<>();

    /**
     * 获取主题信息
     * @return 主题信息
     */
    public FeedBackInfo getTopic() {
        return topic;
    }

    /**
     * 设置主题信息
     * @param topic 主题信息
     */
    public void setTopic(FeedBackInfo topic) {
        this.topic = topic;
    }

    /**
     * 获取所有的回复信息
     * @return 所有的回复信息
     */
    public FeedBackInfo getResponseAt (int index) {
        if (responseList.size() > 0) {
            return responseList.get(index);
        } else {
            return null;
        }
    }

    /**
     * 添加回复
     * @param response 回复
     */
    public void addResponse (FeedBackInfo response) {
        responseList.add(response);
    }
}
