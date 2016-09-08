package com.eeontheway.android.applocker.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
 * 利用手机验证码登陆的的Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PARAM_MODE = "mode";
    private static final String PARAM_SMSCODE = "smscode";
    private static final int MODE_MODIFY_WITH_OLDPASS = 0;      // 结合旧密码修改
    private static final int MODE_MODIFY_WITH_SMSCODE = 1;      // 使用短信验证码修改
    private int mode;

    private EditText et_old_password;
    private EditText et_new_password1;
    private EditText et_new_password2;
    private Button btn_sumbit;

    private IUserManager userManager;
    private ProgressDialog progressDialog;

    /**
     * 启动密码修改界面，使用旧密码修改密码
     * @param resultCode 请求码
     * @param activity 上下文
     */
    public static void startForResultByOldPassword (Activity activity, int resultCode) {
        startForResultForMode(activity, MODE_MODIFY_WITH_OLDPASS, "", resultCode);
    }

    /**
     * 启动密码修改界面，使用短信验证码修改密码
     * @param resultCode 请求码
     * @param activity 上下文
     */
    public static void startForResultBySmscode (Activity activity, String smscode, int resultCode) {
        startForResultForMode(activity, MODE_MODIFY_WITH_SMSCODE, smscode, resultCode);
    }

    /**
     * 启动配置界面
     * @param resultCode 请求码
     * @param mode 修改模式
     * @param activity 上下文
     */
    private static void startForResultForMode (Activity activity, int mode,
                                               String smscode, int resultCode) {
        Intent intent = new Intent(activity, ModifyPasswordActivity.class);
        intent.putExtra(PARAM_MODE, mode);
        if (mode == MODE_MODIFY_WITH_SMSCODE) {
            intent.putExtra(PARAM_SMSCODE, smscode);
        }
        activity.startActivityForResult(intent, resultCode);
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
        setContentView(R.layout.activity_modify_password);

        mode = getIntent().getIntExtra(PARAM_MODE, MODE_MODIFY_WITH_OLDPASS);

        setTitle(R.string.modify_password);
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
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password1 = (EditText) findViewById(R.id.et_new_password1);
        et_new_password2 = (EditText) findViewById(R.id.et_new_password2);
        btn_sumbit = (Button) findViewById(R.id.btn_sumbit);
        btn_sumbit.setOnClickListener(this);

        // 短信验证，旧密码输入框不可见
        if (mode != MODE_MODIFY_WITH_OLDPASS) {
            et_old_password.setVisibility(View.GONE);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
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
     * 初始化用户管理器
     */
    private void initUserManager() {
        userManager = UserManagerFactory.create(this);
        userManager.init(this);

        // 配置密码修改结果监听器
        userManager.setModifyPasswordListener(new IUserManager.OnResultListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(ModifyPasswordActivity.this,
                        getString(R.string.modify_password_failed, msg), Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onSuccess() {
                closeProgressDialog();
                end(RESULT_OK);
            }
        });
    }

    /**
     * 点击事件处理器
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        String oldPassword = et_old_password.getText().toString();
        String newPassword1 = et_new_password1.getText().toString();
        String newPassword2 = et_new_password2.getText().toString();

        // 检查两次密码匹配度
        if (TextUtils.equals(newPassword1, newPassword2)) {
            Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查输入有效性
        if (mode != MODE_MODIFY_WITH_OLDPASS) {
            if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword1)
                    || TextUtils.isEmpty(newPassword2)) {
                Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            // 启动密码修改
            showProgressDialog(getString(R.string.password_modifying));
            userManager.modifyPassword(oldPassword, newPassword1);
        } else {
            // 启动密码修改
            showProgressDialog(getString(R.string.password_modifying));
            userManager.modifyPasswordBySmsCode(
                                        getIntent().getStringExtra(PARAM_SMSCODE), newPassword1);
        }
    }

    /**
     * 显示进度对话框
     *
     * @param msg 待显示的消息
     */
    private void showProgressDialog(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        progressDialog.dismiss();
    }
}
