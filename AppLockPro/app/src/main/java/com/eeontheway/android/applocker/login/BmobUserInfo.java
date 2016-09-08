package com.eeontheway.android.applocker.login;

import com.eeontheway.android.applocker.updater.UpdateInfo;

import org.w3c.dom.CDATASection;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * 专用于BmobSDK的更新信息类
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class BmobUserInfo extends BmobUser {
    private String lastLoginTime;
    private String cid;

    /**
     * 构造函数
     */
    public BmobUserInfo() {
    }

    /**
     * 构造函数
     * @param info 用户信息
     */
    public BmobUserInfo (UserInfo info) {
        // 从info中提取各项信息
        // registerTime由服务器自动管理
        setUsername(info.getUserName());
        setPassword(info.getPassword());
        setEmail(info.getEmail());
        setEmailVerified(info.isEmailVerified());
        setMobilePhoneNumber(info.getPhoneNumber());
        setMobilePhoneNumberVerified(info.isPhoneNumberVerified());
        lastLoginTime = info.getLastLoginTime();
        cid = info.getCid();
    }

    /**
     * 转换用户信息
     * @return 用户信息
     */
    public UserInfo toUserInfo () {
        // BmobUser不提供密码获取函数, 本地不保存密码
        UserInfo info = new UserInfo(getUsername(), "");
        info.setEmail(getEmail());
        Boolean emailVerified = getEmailVerified();
        info.setEmailVerified((emailVerified == null) ? false : emailVerified.booleanValue());
        info.setPhoneNumber(getMobilePhoneNumber());
        Boolean phoneVerified = getMobilePhoneNumberVerified();
        info.setPhoneNumberVerified((phoneVerified == null) ? false : phoneVerified.booleanValue());
        info.setLastLoginTime(lastLoginTime);
        info.setRegisterTime(getCreatedAt());
        info.setCid(cid);
        return info;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
