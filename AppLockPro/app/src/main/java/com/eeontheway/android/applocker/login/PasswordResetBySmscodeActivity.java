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
import android.widget.EditText;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

/**
 * 手机验证码检查Activity
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class PasswordResetBySmscodeActivity extends AppCompatActivity
                                            implements View.OnClickListener {
    private ProgressDialog progressDialog;
    private EditText et_first_password;
    private EditText et_second_password;
    private Button btn_sumbit;
    private SmscodeCheckFragment smscodeFragment;

    private IUserManager userManager;
    private String phoneNumber;
    private String smscode;

    /**
     * 启动Activty
     * @param activity 上下文
     * @param requestCode 请求码，用于Activiy返回检查
     */
    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PasswordResetBySmscodeActivity.class);
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
        setContentView(R.layout.activity_password_reset_by_smscode);

        setTitle(R.string.reset_password);
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
        et_first_password = (EditText)findViewById(R.id.et_first_password);
        et_second_password = (EditText)findViewById(R.id.et_second_password);
        btn_sumbit = (Button)findViewById(R.id.btn_sumbit);
        btn_sumbit.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        smscodeFragment = (SmscodeCheckFragment) getFragmentManager().findFragmentById(
                R.id.fragment_smscode);
        smscodeFragment.setCheckPhoneNumValid(true);
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

        // 注册密码修改结果监听器
        userManager.setModifyPasswordListener(new IUserManager.OnResultListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(PasswordResetBySmscodeActivity.this,
                        getString(R.string.password_reset_fail, msg), Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(PasswordResetBySmscodeActivity.this,
                        R.string.password_reset_ok, Toast.LENGTH_SHORT).show();
                closeProgressDialog();
                end(RESULT_OK);
            }
        });

    }

    /**
     * 开始绑定
     */
    private void startResetPassword () {
        userManager.modifyPasswordBySmsCode(smscode, et_first_password.getText().toString());
    }

    /**
     * 点击事件处理器
     * @param v
     */
    @Override
    public void onClick(View v) {
        String firstPassword = et_first_password.getText().toString();
        String secondPassword = et_second_password.getText().toString();

        switch (v.getId()) {
            case R.id.btn_sumbit:
                smscode = smscodeFragment.getSmsCode();
                if (smscode.isEmpty()) {
                    Toast.makeText(this, R.string.smscode_invalid, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(firstPassword)) {
                    et_first_password.requestFocus();
                    Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.equals(firstPassword, secondPassword)) {
                    et_first_password.requestFocus();
                    Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialog(getString(R.string.password_modifying));
                startResetPassword();
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
