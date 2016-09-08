package com.eeontheway.android.applocker.login;

import android.content.Context;

import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * 基于BMOB的用户管理接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public abstract class UserManagerBase implements IUserManager {
    private Context context;
    protected OnResultListener loginResultListener;
    protected OnResultListener signUpResultListener;
    protected OnResultListener updateInfoResultListener;
    protected OnResultListener logoutResultListener;
    protected OnResultListener passwordModifyResultListener;
    protected OnRequestSmsCodeListener requestSmsCodeListener;
    protected OnResultListener verifySmsCodeListener;
    protected OnQuerySmsCodeListener querySmsCodeListener;
    protected OnResultListener bindPhoneListener;
    protected OnQueryResultCodeListener checkPhoneVerified;

    /**
     * 构造函数
     * @param context 上下文
     */
    public UserManagerBase(Context context) {
        this.context = context;
    }

    /**
     * 设置登陆回调
     * @param listener
     */
    public void setOnLoginListener(OnResultListener listener) {
        loginResultListener = listener;
    }

    /**
     * 设置注册回调
     * @param listener 注册事件监听器
     */
    public void setOnSignUpListener (OnResultListener listener) {
        signUpResultListener = listener;
    }

    /**
     * 设置修改资料回调
     * @param listener 注册事件监听器
     */
    public void setUpdateInfoListener (OnResultListener listener) {
        updateInfoResultListener = listener;
    }

    /**
     * 设置退出登陆事件
     * @param listener 注册事件监听器
     */
    public void setOnLogoutListener (OnResultListener listener) {
        loginResultListener = listener;
    }

    /**
     * 设置密码修改监听器
     * @param listener 注册事件监听器
     */
    public void setModifyPasswordListener (OnResultListener listener) {
        passwordModifyResultListener = listener;
    }

    /**
     * 设置验证码申请的监听器
     * @param listener 注册事件监听器
     */
    public void setRequestSmsCodeListener(OnRequestSmsCodeListener listener) {
        this.requestSmsCodeListener = listener;
    }

    /**
     * 设置短信注册码的查询回调
     * @param listener 注册事件监听器
     */
    public void setQuerySmsCodeListener(OnQuerySmsCodeListener listener) {
        this.querySmsCodeListener = listener;
    }

    /**
     * 设置短信注册码的校验回调
     * @param listener 注册事件监听器
     */
    public void setVerifySmsCodeListener(OnResultListener listener) {
        this.verifySmsCodeListener = listener;
    }

    /**
     * 设置手机号绑定的事件回调
     * @param listener 注册事件监听器
     */
    public void setBindPhoneListener(OnResultListener listener) {
        this.bindPhoneListener = listener;
    }

    /**
     * 检查手机号是否注册过
     * @param listener 注册事件监听器
     */
    public void setCheckPhoneNumberVerifiedListener (OnQueryResultCodeListener listener) {
        this.checkPhoneVerified = listener;
    }

}
