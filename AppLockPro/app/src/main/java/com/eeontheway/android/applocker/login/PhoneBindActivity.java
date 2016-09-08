package com.eeontheway.android.applocker.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

/**
 * 手机验证码检查Activity
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class PhoneBindActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_skip;
    private Button btn_sumbit;
    private ProgressDialog progressDialog;
    private SmscodeCheckFragment smscodeFragment;

    private String phone;
    private String smscode;

    private IUserManager userManager;

    /**
     * 启动Activty
     * @param activity 上下文
     * @param requestCode 请求码，用于Activiy返回检查
     */
    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PhoneBindActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 终中绑定过程
     */
    public void end (int resultCode) {
        setResult(resultCode);
        finish();
    }

    /**
     * Activity的onCreate回调
     *
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_bind);

        setTitle(R.string.phone_bind);
        initUserManager();

        initToolBar();
        initViews();
    }

    /**
     * Activity的onDestroy回调
     */
    @Override
    protected void onDestroy() {
        userManager.unInit();
        super.onDestroy();
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tl_header);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * 初始化各种View
     */
    private void initViews() {
        btn_sumbit = (Button) findViewById(R.id.btn_sumbit);
        btn_sumbit.setOnClickListener(this);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_skip.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        smscodeFragment = (SmscodeCheckFragment) getFragmentManager().findFragmentById(
                                                                    R.id.fragment_smscode);
    }

    /**
     * Activiy的onCreateOptionMenu回调
     *
     * @param menu 创建的菜单
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 处理返回按钮按下的响应
     *
     * @param item 被按下的项
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.tv_skip:
                end(RESULT_CANCELED);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * 返回键处理
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        end(RESULT_CANCELED);
    }

    /**
     * 初始化用户管理器
     */
    private void initUserManager () {
        userManager = UserManagerFactory.create(this);
        userManager.init(this);

        // 注册验证码校验事件
        userManager.setVerifySmsCodeListener(new IUserManager.OnResultListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(PhoneBindActivity.this, getString(R.string.verify_smscode_failed, msg),
                        Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onSuccess() {
                // 校验成功，开始绑定
                userManager.bindPhoneNumer(phone);
            }
        });

        // 注册手机绑定监听器
        userManager.setBindPhoneListener(new IUserManager.OnResultListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(PhoneBindActivity.this, getString(R.string.phone_bind_failed, msg),
                                                    Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(PhoneBindActivity.this, R.string.phone_bind_ok,
                                            Toast.LENGTH_SHORT).show();
                closeProgressDialog();
                end(RESULT_OK);
            }
        });
    }

    /**
     * 开始绑定
     */
    private void startBind (String phoneNumber, String smsCode) {
        userManager.verifySmsCode(phoneNumber, smsCode);
    }

    /**
     * 点击事件处理器
     * @param v
     */
    @Override
    public void onClick(View v) {
        // 检查是否跳过
        switch (v.getId()) {
            case R.id.tv_skip:
                end(RESULT_CANCELED);
                return;
            case R.id.btn_sumbit:
                // 电话号码不能为空，必须以1开头，11位
                String telRegex = "[1]\\d{10}";
                phone = smscodeFragment.getPhoneNumber();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!phone.matches(telRegex)) {
                    Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 检查验证码有效性
                smscode = smscodeFragment.getSmsCode();
                if (TextUtils.isEmpty(smscode)) {
                    Toast.makeText(this, R.string.smscode_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialog(getString(R.string.binding));
                startBind(phone, smscode);
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
}
