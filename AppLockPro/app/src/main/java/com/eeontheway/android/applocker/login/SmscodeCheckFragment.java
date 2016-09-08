package com.eeontheway.android.applocker.login;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

/**
 * 手机验证码检查Activity
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class SmscodeCheckFragment extends Fragment implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_sms_code;
    private Button bt_request_smscode;
    private ProgressDialog progressDialog;
    private SmsCodeResendTimer resendTimer;

    private Activity parentActivity;

    private IUserManager userManager;
    private boolean checkPhoneNumerValid;

    /**
     * 获取短信验证码
     * @return 短信验证码，如果没有，则为空
     */
    public String getSmsCode () {
        String smscode = et_sms_code.getText().toString();
        return smscode;
    }

    /**
     * 获取手机号
     * @return 手机号，如果没有，则为空
     */
    public String getPhoneNumber () {
        String phoneNumer = et_phone.getText().toString();
        return phoneNumer;
    }

    /**
     * 获取短信验证码前检查手机号是否有效
     * 即判断该手机号是否跟特定用户绑定
     */
    public void setCheckPhoneNumValid (boolean check) {
        checkPhoneNumerValid = check;
    }

    /**
     * Fragment的onCreate回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = getActivity();
        resendTimer = new SmsCodeResendTimer(60000, 1000);

        initUserManager();
    }

    /**
     * Fragment的onCreateView回调
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smscode_check, null);
        et_phone = (EditText)view.findViewById(R.id.et_phone);
        et_sms_code = (EditText)view.findViewById(R.id.et_sms_code);
        bt_request_smscode = (Button)view.findViewById(R.id.bt_request_smscode);
        bt_request_smscode.setOnClickListener(this);

        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        return view;
    }

    /**
     * Fragment的onDestroy回调
     */
    @Override
    public void onDestroy() {
        resendTimer.cancel();
        userManager.unInit();
        super.onDestroy();
    }

    /**
     * 初始化用户管理器
     */
    private void initUserManager () {
        userManager = UserManagerFactory.create(parentActivity);
        userManager.init(parentActivity);

        // 注册验证码请求事件监听器
        userManager.setRequestSmsCodeListener(new IUserManager.OnRequestSmsCodeListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(parentActivity, R.string.smscode_send_fail, Toast.LENGTH_SHORT).show();
                closeProgressDialog();

                // 发送失败，允许重试
                resendTimer.cancel();
                bt_request_smscode.setText(R.string.send_sms_code);
                bt_request_smscode.setEnabled(true);
            }

            @Override
            public void onSuccess(String smsId) {
                Toast.makeText(parentActivity, R.string.smscode_send_ok, Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }
        });


        // 注册查询手机号是否验证的监听器
        userManager.setCheckPhoneNumberVerifiedListener(new IUserManager.OnQueryResultCodeListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(parentActivity, getString(R.string.query_failed, msg),
                                                        Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onSuccess(int code) {
                if (code > 0) {
                    userManager.reqeustSmsCode(et_phone.getText().toString(), "startRequestSMSCode");
                } else {
                    Toast.makeText(parentActivity, getString(R.string.phonenum_not_verified),
                                                        Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                }
            }
        });
    }

    /**
     * 请求发送验证码
     */
    private void startRequestSMSCode(String phoneNumber) {
        if (checkPhoneNumerValid) {
            userManager.checkPhoneNumberVerified(phoneNumber);
        } else {
            userManager.reqeustSmsCode(phoneNumber, "startRequestSMSCode");
        }
    }

    /**
     * 点击事件处理器
     * @param v
     */
    @Override
    public void onClick(View v) {
        // 电话号码不能为空，必须以1开头，11位
        String telRegex = "[1]\\d{10}";
        String phone = et_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(parentActivity, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches(telRegex)) {
            Toast.makeText(parentActivity, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()) {
            case R.id.bt_request_smscode:   // 请求验证码
                showProgressDialog(getString(R.string.smscode_requesting));
                resendTimer.start();
                bt_request_smscode.setEnabled(false);

                startRequestSMSCode(phone);
                break;
        }
    }

    /**
     * 显示进度对话框
     * @param msg 待显示的消息
     */
    private void showProgressDialog (String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog () {
        progressDialog.dismiss();
    }

    /**
     * SMScode重发的计时器
     */
    class SmsCodeResendTimer extends CountDownTimer {
        public SmsCodeResendTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            bt_request_smscode.setText(getString(R.string.smscode_resend_after_time,
                                                            millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            bt_request_smscode.setText(R.string.smscode_resend);
        }
    }
}
