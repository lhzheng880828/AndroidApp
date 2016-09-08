package com.eeontheway.android.applocker.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.push.PushMessageReceiver;

/**
 * 用户注册或登陆的Activity
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class LoginOrRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int BIND_REQUEST = 0;
    private static final int RESETPASS_REQUEST = 1;
    private static final String PARAM_LOGIN_MODE = "loginMode";

    private TableRow tr_secondPassword;
    private EditText et_account;
    private EditText et_first_password;
    private EditText et_second_password;
    private Button btn_sumbit;
    private TextView tv_create_account;
    private View ll_register_way;
    private View ll_login;
    private ViewStub vs_register_mode;
    private TextView tv_login_account;

    private IUserManager userManager;
    private boolean isLoginMode;

    private ProgressDialog progressDialog;

    /**
     * 启动应用锁配置界面
     * @param loginMode 是否是登陆模式
     * @param activity 上下文
     */
    public static void startForResult(Activity activity, boolean loginMode, int requestCode) {
        Intent intent = new Intent(activity, LoginOrRegisterActivity.class);
        intent.putExtra(PARAM_LOGIN_MODE, loginMode);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动应用锁配置界面
     * @param loginMode 是否是登陆模式
     * @param fragment fragment
     */
    public static void startForResult(Fragment fragment, boolean loginMode, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LoginOrRegisterActivity.class);
        intent.putExtra(PARAM_LOGIN_MODE, loginMode);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 终中绑定过程
     */
    public void end (int resultCode) {
        setResult(resultCode);
        finish();
    }

    /**
     *
     * Activity的onCreate回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        isLoginMode = getIntent().getBooleanExtra(PARAM_LOGIN_MODE, true);

        initUserManager();
        initToolBar();
        initViews();

        // 根据配置切换模式
        switchMode(isLoginMode);
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
     * Activity的onActivityResult回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BIND_REQUEST) {
            // 不管是否绑定手机号，都认为是注册成功
            end(RESULT_OK);
        } else if (requestCode == RESETPASS_REQUEST) {
            if (resultCode == RESULT_OK) {
                // 重设之后，要求重新登陆
                Toast.makeText(this, R.string.password_ok_relogin, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        tr_secondPassword = (TableRow)findViewById(R.id.tr_secondPassword);
        et_account = (EditText)findViewById(R.id.et_account);
        et_first_password = (EditText)findViewById(R.id.et_first_password);
        et_second_password = (EditText)findViewById(R.id.et_second_password);
        btn_sumbit = (Button)findViewById(R.id.btn_sumbit);
        btn_sumbit.setOnClickListener(this);
        tv_create_account = (TextView)findViewById(R.id.tv_create_account);
        tv_create_account.setOnClickListener(this);
        vs_register_mode = (ViewStub)findViewById(R.id.vs_register_mode);
        ll_login = findViewById(R.id.ll_login);
        ll_login.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 初始化用户管理器
     */
    private void initUserManager () {
        userManager = UserManagerFactory.create(this);
        userManager.init(this);
        userManager.logout();

        // 登陆监听器
        userManager.setOnLoginListener(new IUserManager.OnResultListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(LoginOrRegisterActivity.this, getString(R.string.login_failed, msg),
                                                    Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess() {
                // 登陆成功后，刷新下cid
                String cid = userManager.getMyCid();
                if (cid != null) {
                    userManager.setMyCid(PushMessageReceiver.cid);
                }

                // 登陆成功，设置成功返回值，结束自己
                progressDialog.dismiss();
                end(RESULT_OK);
            }
        });

        // 用户注册监听器
        userManager.setOnSignUpListener(new IUserManager.OnResultListener() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(LoginOrRegisterActivity.this, getString(R.string.register_failed, msg),
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess() {
                // 注册成功，设置成功返回值，结束自己
                Toast.makeText(LoginOrRegisterActivity.this, getString(R.string.register_ok),
                        Toast.LENGTH_SHORT).show();progressDialog.dismiss();

                // 启动手机绑定
                PhoneBindActivity.startForResult(LoginOrRegisterActivity.this, BIND_REQUEST);
            }
        });
    }
    /**
     * 切换至注册模式
     */
    private void switchMode (boolean isLoginMode) {
        if (isLoginMode) {
            setTitle(R.string.login);

            // 登陆模式，隐藏第二遍密码输入，以及其它注册方式，更改底部文字提示
            tr_secondPassword.setVisibility(View.GONE);
            ll_login.setVisibility(View.VISIBLE);
            if (ll_register_way != null) {
                ll_register_way.setVisibility(View.GONE);
            }
        } else {
            setTitle(R.string.register);

            // 注册模式，显示第二遍密码输入，及其它注册方式，更改底部文字提示
             if (ll_register_way == null) {
                View view = vs_register_mode.inflate();
                ll_register_way = view.findViewById(R.id.ll_register_way);
                tv_login_account = (TextView)view.findViewById(R.id.tv_login_account);
                tv_login_account.setOnClickListener(this);
            }
            tr_secondPassword.setVisibility(View.VISIBLE);
            ll_login.setVisibility(View.GONE);
            ll_register_way.setVisibility(View.VISIBLE);
        }

        // 切换模式，要清空所有输入
        et_account.setText("");
        et_first_password.setText("");
        et_second_password.setText("");

        this.isLoginMode = isLoginMode;
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
     * 点击事件处理器
     * @param v 发生点击事件的View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_account:
                switchMode(false);
                break;
            case R.id.tv_login_account:
                switchMode(true);
                break;
            case R.id.btn_sumbit:
                if (isLoginMode) {
                    startLogin();
                } else {
                    starRegister();
                }
                break;
            case R.id.ll_login:
                // 获取短信验证码，之后再启动密码修改功能
                PasswordResetBySmscodeActivity.startForResult(this, RESETPASS_REQUEST);
                break;
        }
    }

    /**
     * 启动登陆
     */
    private void startLogin () {
        String account = et_account.getText().toString();
        String password = et_first_password.getText().toString();

        // 必要的检查
        if (TextUtils.isEmpty(account)) {
            et_account.requestFocus();
            Toast.makeText(this, R.string.username_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            et_first_password.requestFocus();
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // 弹出进度条，开始登陆
        progressDialog.setMessage(getString(R.string.logining));
        progressDialog.show();

        // 启动注册
        userManager.loginByUserName(account, password);
    }

    /**
     * 启动注册
     */
    private void starRegister () {
        String account = et_account.getText().toString();
        String firstPassword = et_first_password.getText().toString();
        String secondPassword = et_second_password.getText().toString();

        // 必要的检查
        if (TextUtils.isEmpty(account)) {
            et_account.requestFocus();
            Toast.makeText(this, R.string.username_empty, Toast.LENGTH_SHORT).show();
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

        // 显示进度
        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        // 启动注册
        userManager.signUp(account, firstPassword);
    }
}