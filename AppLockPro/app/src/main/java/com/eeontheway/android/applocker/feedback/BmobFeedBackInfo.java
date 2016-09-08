package com.eeontheway.android.applocker.feedback;

import com.eeontheway.android.applocker.login.BmobUserInfo;
import com.eeontheway.android.applocker.login.UserInfo;

import cn.bmob.v3.BmobObject;

/**
 * 专用于BmobSDK的反馈信息类
 * 用于FeedBackInfo与BmobFeedBackInfo间的转换
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class BmobFeedBackInfo extends BmobObject {
    private String parentId;
    private String content;
    private String contact;
    private boolean responsed;
    private boolean isTopic;
    private String from;
    private BmobUserInfo bmobUserInfo;
    private String cid;

    /**
     * 构造函数
     * @param baseInfo
     */
    public BmobFeedBackInfo(FeedBackInfo baseInfo) {
        // Bmob要求信息域必须定义在BmobObject的子类中
        // 所以，只能重复定义下，重复使用
        contact = baseInfo.getContact();
        content = baseInfo.getContent();
        responsed = baseInfo.isResponsed();
        setObjectId(baseInfo.getId());
        parentId = baseInfo.getParentId();
        isTopic = baseInfo.isTopic();
        from = baseInfo.getFrom();

        // 用户可能未登陆
        UserInfo userInfo = baseInfo.getUserInfo();
        if (userInfo != null) {
            bmobUserInfo = new BmobUserInfo(userInfo);
        }
        cid = baseInfo.getCid();
    }

    /**
     * 获取反馈内容
     * @return 反馈内容
     */
    public FeedBackInfo toFeedBackInfo () {
        FeedBackInfo info = new FeedBackInfo();
        info.setContent(content);
        info.setContact(contact);
        info.setCreateTime(super.getCreatedAt());
        info.setResponsed(responsed);
        info.setParentId(parentId);
        info.setTopic(isTopic);
        info.setId(getObjectId());
        info.setFrom(from);

        // 用户可能未登陆
        if (bmobUserInfo != null) {
            info.setUserInfo(bmobUserInfo.toUserInfo());
        }
        info.setCid(cid);
        return info;
    }
}
