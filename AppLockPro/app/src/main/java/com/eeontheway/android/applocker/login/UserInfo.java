package com.eeontheway.android.applocker.login;

/**
 * 用户信息类
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UserInfo {
    private String userName;
    private String password;
    private String email;
    private boolean emailVerified;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private String lastLoginTime;
    private String registerTime;
    private String cid;

    /**
     * 构造函数
     * @param userName 用户名
     * @param password 密码(MD5值)
     */
    public UserInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取Email
     * @return Email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置Email
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Email是否被验证过
     * @return true/false
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * 设置Email是否被验证过
     * @param emailVerified
     */
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * 获取手机号
     * @return 手机号
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置手机号
     * @param phoneNumber 手机号
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 手机号是验证过
     * @return true/false
     */
    public boolean isPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    /**
     * 设置手机号是否验证过
     * @param phoneNumberVerified
     */
    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    /**
     * 获取上一次登陆时间
     * @return 上一次登陆时间
     */
    public String getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * 设置上一次登陆时间
     * @param lastLoginTime 上一次登陆时间
     */
    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * 获取注册时间
     * @return 注册时间
     */
    public String getRegisterTime() {
        return registerTime;
    }

    /**
     * 设置注册时间
     * @param createTime 注册时间
     */
    public void setRegisterTime (String createTime) {
        this.registerTime = createTime;
    }

    /**
     * 该用户的唯一id
     * @return id
     */
    public String getCid() {
        return cid;
    }

    /**
     * 设置该用户的id
     * @param cid 用户id
     */
    public void setCid(String cid) {
        this.cid = cid;
    }
}
