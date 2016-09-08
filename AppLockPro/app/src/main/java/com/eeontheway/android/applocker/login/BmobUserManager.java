package com.eeontheway.android.applocker.login;

import android.content.Context;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.push.PushMessageReceiver;
import com.eeontheway.android.applocker.updater.BmobUpdateInfo;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobSmsState;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QuerySMSStateListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * 基于BMOB的用户管理接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class BmobUserManager extends UserManagerBase {
    private Context context;

    /**
     * 构造函数
     * @param context 上下文
     */
    public BmobUserManager(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 初始化反馈接口
     * @return true 成功; false 失败
     */
    @Override
    public boolean init (Context context) {
        return true;
    }

    /**
     * 反初始化反馈接口
     */
    @Override
    public void unInit() {
    }

    /**
     * 使用指定的用户名和密码登陆
     * @param userName 用户名
     * @param password 密码
     */
    public void loginByUserName (String userName, String password) {
        BmobUser.loginByAccount(context, userName, password, new LogInListener<BmobUserInfo>() {
            @Override
            public void done(BmobUserInfo userInfo, BmobException e) {
                if(e == null){
                    if (loginResultListener != null) {
                        loginResultListener.onSuccess();
                    }
                }else{
                    if (loginResultListener != null) {
                        loginResultListener.onFail(e.getErrorCode(),
                                            getErrorMsg(e.getErrorCode(), e.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 使用指定的手机号和密码登陆
     * @param phoneNumber 手机号
     * @param password 密码
     */
    public void loginByPhoneNumber (String phoneNumber, String password) {
        loginByUserName(phoneNumber, password);
    }

    /**
     * 退出登陆
     */
    public void logout () {
        BmobUser.logOut(context);
        if (logoutResultListener != null) {
            logoutResultListener.onSuccess();
        }
    }

    /**
     * 使用指定的手机号和验证码登陆
     * @param phoneNumber 手机号
     * @param code 验证码
     */
    public void loginBySmsCode (String phoneNumber, String code) {
        if (!isSmsCodeValid(code)) {
            if (loginResultListener != null) {
                loginResultListener.onFail(-1, context.getString(R.string.smscode_invalid));
            }
            return;
        }

        BmobUser.loginBySMSCode(context, phoneNumber, code, new LogInListener<BmobUserInfo>() {
            @Override
            public void done(BmobUserInfo userInfo, BmobException e) {
                if(e == null){
                    if (loginResultListener != null) {
                        loginResultListener.onSuccess();
                    }
                }else{
                    if (loginResultListener != null) {
                        loginResultListener.onFail(e.getErrorCode(),
                                getErrorMsg(e.getErrorCode(), e.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 请求发送手机验证码用于登陆
     * @param phoneNumber 手机号
     * @param msgTemplate 发送消息的模板
     */
    public void reqeustSmsCode (String phoneNumber, String msgTemplate) {
        BmobSMS.requestSMSCode(context, phoneNumber, msgTemplate, new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if(e == null){
                    if (requestSmsCodeListener != null) {
                        requestSmsCodeListener.onSuccess("" + smsId);
                    }
                }else{
                    if (requestSmsCodeListener != null) {
                        requestSmsCodeListener.onFail(e.getErrorCode(),
                                getErrorMsg(e.getErrorCode(), e.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 检查手机验证码的有效性
     * @param phoneNumber 手机号
     * @param code 手同验证码
     */
    public void verifySmsCode (String phoneNumber, String code) {
        if (!isSmsCodeValid(code)) {
            if (verifySmsCodeListener != null) {
                verifySmsCodeListener.onFail(-1, context.getString(R.string.smscode_invalid));
            }
            return;
        }

        BmobSMS.verifySmsCode(context, phoneNumber, code, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    if (verifySmsCodeListener != null) {
                        verifySmsCodeListener.onSuccess();
                    }
                }else{
                    if (verifySmsCodeListener != null) {
                        verifySmsCodeListener.onFail(e.getErrorCode(),
                                getErrorMsg(e.getErrorCode(), e.getMessage()));
                    }
                }
            }
        });
    }

    /**
     * 查询手机验证码的发送状态
     * @param smsId 短信ID
     */
    public void querySmsCode (String smsId) {
        BmobSMS.querySmsState(context, Integer.parseInt(smsId), new QuerySMSStateListener() {
            @Override
            public void done(BmobSmsState state, BmobException e) {
                if (querySmsCodeListener != null) {
                    int finalState;
                    boolean verified;

                    switch (state.getSmsState()) {
                        case "SUCCESS":
                            finalState = SMS_SEND_OK;
                            break;
                        case "SENDING":
                            finalState = SMS_SEND_SENDING;
                            break;
                        case "FAIL":
                        default:
                            finalState = SMS_SEND_FAIL;
                            break;
                    }

                    switch (state.getVerifyState()) {
                        case "true":
                            verified = true;
                            break;
                        case "false":
                        default:
                            verified = false;
                            break;
                    }

                    querySmsCodeListener.onQueryResult(finalState, verified);
                }
            }
        });
    }

    /**
     * 使用指定的用户名/密码/邮件/手机号 注册
     * @param userName 用户名
     * @param password 密码
     */
    public void signUp (String userName, String password) {
        BmobUser user = new BmobUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.signUp(context, new SaveListener() {
            @Override
            public void onSuccess() {
                if (signUpResultListener != null) {
                    signUpResultListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (signUpResultListener != null) {
                    signUpResultListener.onFail(i, getErrorMsg(i, s));
                }
            }
        });
    }

    /**
     * 检查验证码的有效性
     */
    public boolean isSmsCodeValid (String smscode) {
        String smsRegex = "\\d{6}";
        if ((smscode != null) && (!smscode.isEmpty())) {
            return smscode.matches(smsRegex);
        }

        return false;
    }

    /**
     * 使用短信验证码一键注册和登陆
     * @param phoneNumber 手机号
     * @param smsCode 短信验证码
     * @param phoneNumber 手机号
     */
    public void signUpAndLoginBySmsCode(String phoneNumber, String smsCode) {
        if (!isSmsCodeValid(smsCode)) {
            if (signUpResultListener != null) {
                signUpResultListener.onFail(-1, context.getString(R.string.smscode_invalid));
            }
            return;
        }

        BmobUser.signOrLoginByMobilePhone(context, phoneNumber, smsCode, new LogInListener<BmobUserInfo>() {
            @Override
            public void done(BmobUserInfo user, BmobException e) {
                if(user != null){
                    if (signUpResultListener != null) {
                        signUpResultListener.onSuccess();
                    }
                } else {
                    if (signUpResultListener != null) {
                        signUpResultListener.onFail(e.getErrorCode(),
                                getErrorMsg(e.getErrorCode(), e.getLocalizedMessage()));
                    }
                }
            }
        });
    }

    /**
     * 获取已登陆的用户信息
     * @return 用户信息; 为null表示用户未登陆
     */
    public UserInfo getMyUserInfo () {
        BmobUserInfo userInfo = BmobUser.getCurrentUser(context, BmobUserInfo.class);
        if (userInfo != null) {
            return userInfo.toUserInfo();
        }
        return null;
    }


    /**
     * 更新自己的用户信息
     * @param userInfo 用户信息
     */
    public void updateMyUserInfo (UserInfo userInfo) {
        BmobUserInfo info = new BmobUserInfo();

        // userInfo不包含objectID，只更新这几项，其它不更新
        info.setObjectId(BmobUser.getCurrentUser(context).getObjectId());
        info.setCid(userInfo.getCid());
        info.update(context, new UpdateListener() {
            @Override
            public void onSuccess() {
                if (updateInfoResultListener != null) {
                    updateInfoResultListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (updateInfoResultListener != null) {
                    updateInfoResultListener.onFail(i, getErrorMsg(i, s));
                }
            }
        });
    }

    /**
     * 使用旧密码修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void modifyPassword (String oldPassword, String newPassword) {
        BmobUser.updateCurrentUserPassword(context, oldPassword, newPassword, new UpdateListener() {
            @Override
            public void onSuccess() {
                if (passwordModifyResultListener != null) {
                    passwordModifyResultListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (passwordModifyResultListener != null) {
                    passwordModifyResultListener.onFail(code, getErrorMsg(code, msg));
                }
            }
        });
    }

    /**
     * 使用短信验证码来修改密码
     * @param smsCode 短信验证码
     * @param newPassword 新密码
     */
    public void modifyPasswordBySmsCode (String smsCode, String newPassword) {
        if (!isSmsCodeValid(smsCode)) {
            if (passwordModifyResultListener != null) {
                passwordModifyResultListener.onFail(-1, context.getString(R.string.smscode_invalid));
            }
            return;
        }
        BmobUser.resetPasswordBySMSCode(context, smsCode, newPassword, new ResetPasswordByCodeListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    if (passwordModifyResultListener != null) {
                        passwordModifyResultListener.onSuccess();
                    }
                }else{
                    if (passwordModifyResultListener != null) {
                        passwordModifyResultListener.onFail(e.getErrorCode(),
                                getErrorMsg(e.getErrorCode(), e.getLocalizedMessage()));
                    }
                }
            }
        });
    }

    /**
     * 为当前用户绑定手机号
     * @param phoneNumber 待邦定的手机号
     */
    public void bindPhoneNumer (String phoneNumber) {
        BmobUserInfo user = new BmobUserInfo();
        user.setMobilePhoneNumber(phoneNumber);
        user.setMobilePhoneNumberVerified(true);
        BmobUserInfo currentUser = BmobUser.getCurrentUser(context, BmobUserInfo.class);

        user.update(context, currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                if (bindPhoneListener != null) {
                    bindPhoneListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (bindPhoneListener != null) {
                    bindPhoneListener.onFail(i, getErrorMsg(i, s));
                }
            }
        });
    }


    /**
     * 检查手机号是否被验证过
     * @param phoneNumber
     */
    public void checkPhoneNumberVerified (String phoneNumber) {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber", phoneNumber);
        query.findObjects(context, new FindListener<BmobUser>() {
            @Override
            public void onSuccess(List<BmobUser> list) {
                if (checkPhoneVerified != null) {
                    checkPhoneVerified.onSuccess(list.size());
                }
            }

            @Override
            public void onError(int i, String s) {
                if (bindPhoneListener != null) {
                    bindPhoneListener.onFail(i, getErrorMsg(i, s));
                }
            }
        });
    }

    /**
     * 获取错误信息串
     * @param code 错误代码
     * @param msg 原始错误消息
     * @return 最终的错误信息串
     */
    public static String getErrorMsg (int code, String msg) {
        String finaMsg;

        switch (code) {
            // SDK错误码
            case 9001: finaMsg = "Application Id为空，请初始化."; break;
            case 9002: finaMsg = "解析返回数据出错"; break;
            case 9003: finaMsg = "上传文件出错"; break;
            case 9004: finaMsg = "文件上传失败"; break;
            case 9005: finaMsg = "批量操作只支持最多50条"; break;
            case 9006: finaMsg = "objectId为空"; break;
            case 9007: finaMsg = "文件大小超过10M"; break;
            case 9008: finaMsg = "上传文件不存在"; break;
            case 9009: finaMsg = "没有缓存数据"; break;
            case 9010: finaMsg = "网络超时或者没有给应用网络权限"; break;
            case 9011: finaMsg = "BmobUser类不支持批量操作"; break;
            case 9012: finaMsg = "上下文为空"; break;
            case 9013: finaMsg = "BmobObject（数据表名称）格式不正确"; break;
            case 9014: finaMsg = "第三方账号授权失败"; break;
            case 9015: finaMsg = "其他错误均返回此code"; break;
            case 9016: finaMsg = "无网络连接，请检查您的手机网络."; break;
            case 9017: finaMsg = "与第三方登录有关的错误，具体请看对应的错误描述"; break;
            case 9018: finaMsg = "参数不能为空"; break;
            case 9019: finaMsg = "格式不正确：手机号码、邮箱地址、验证码"; break;

            // 支付功能
            case -1:  finaMsg = "微信返回的错误码，可能是未安装微信，也可能是微信没获得网络权限等"; break;
            case -2:  finaMsg = "微信支付用户中断操作"; break;
            case -3:  finaMsg = "未安装微信支付插件"; break;
            case 102:  finaMsg = "设置了安全验证，但是签名或IP不对"; break;
            case 6001:  finaMsg = "支付宝支付用户中断操作"; break;
            case 4000:  finaMsg = "支付宝支付出错，可能是参数有问题"; break;
            case 1111:  finaMsg = "解析服务器返回的数据出错，可能是提交参数有问题"; break;
            case 2222:  finaMsg = "服务器端返回参数出错，可能是提交的参数有问题（如查询的订单号不存在）"; break;
            case 3333:  finaMsg = "解析服务器数据出错，可能是提交参数有问题"; break;
            case 5277:  finaMsg = "查询订单号时未输入订单号"; break;
            case 7777:  finaMsg = "微信客户端未安装"; break;
            case 8888:  finaMsg = "微信客户端版本不支持微信支付"; break;
            case 10003:  finaMsg = "商品名或详情不符合微信/支付宝的规定（如微信商品名不可以超过42个中文）"; break;
            case 10777:  finaMsg = "上次发起的请求还未处理完成，禁止下次请求，可用BmobPay.ForceFree()解除"; break;

            // REST API错误码列表
            case 101: finaMsg ="用户名或密码不正确"; break;
            case 202: finaMsg ="用户名已经存在"; break;
            case 205: finaMsg ="没有找到此用户名的用户"; break;
            case 207: finaMsg ="验证码错误"; break;
            case 209: finaMsg ="该手机号码已经存在"; break;
            case 210: finaMsg ="旧密码不正确"; break;

            case 10010: finaMsg = "短信验证码发送次数过多，请稍候重试"; break;
            default: finaMsg = msg;
        }

        return finaMsg;
    }

    /**
     * 获取自己的cid
     * @return cid
     */
    @Override
    public String getMyCid() {
        String cid =null;
        UserInfo userInfo = getMyUserInfo();
        if (userInfo != null) {
            cid = userInfo.getCid();
        }
        return cid;
    }

    /**
     * 设置自己的cid
     * @return 设置成功或失败。用户未登陆时，失败
     */
    public boolean setMyCid (String cid) {
        // 更新服务器上的cid
        UserInfo userInfo = getMyUserInfo();
        if (userInfo != null) {
            userInfo.setCid(cid);
            updateMyUserInfo(userInfo);
            return true;
        }

        return false;
    }
}
