package com.eeontheway.android.applocker.lock;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.ui.NumberPasswordView;

/**
 * App密码设置Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PasswordSetActivity extends AppCompatActivity {
    public static String RETURN_PARAM_PASS = "password";
    private NumberPasswordView cv_password;

    /**
     * 启动Activity
     * @param activity 上下文
     */
    public static void statActivity (Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PasswordSetActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Activity的onCreate回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_password_set);

        initViews ();
    }

    /**
     * 初始化各种View
     */
    private void initViews() {
        cv_password = (NumberPasswordView)findViewById(R.id.cv_password);
        cv_password.setPasswordCallback(new NumberPasswordView.PasswordCallback() {
            @Override
            public boolean verifyPassword(String password) {
                // 不必实现
                return false;
            }

            @Override
            public void onMaxErrorAccuor() {
                // 不必实现
            }

            @Override
            public void onPasswordSetOk(String password) {
                // 返回给主Activity，由其保存
                Intent intent = getIntent();
                intent.putExtra(RETURN_PARAM_PASS, password);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 按下返回键时的处理
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_CANCELED);
        finish();
    }
}
