package com.eeontheway.android.applocker.lock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.main.MainActivity;

import java.io.IOException;

/**
 * 单条AppLockLog显示的Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class LockLogActivity extends AppCompatActivity {
    private static final String PARAM_LOGINFO = "param_loginfo";
    private AccessLog accessLog;

    private ImageView iv_photo;
    private View view_see_more;
    private Button bt_ok;
    private TextView tv_msg;

    /**
     * 启动Activity
     * @param logInfo 锁定日志信息
     */
    public static void startActivity (Context context, AccessLog logInfo) {
        Intent intent = new Intent(context, LockLogActivity.class);
        intent.putExtra(PARAM_LOGINFO, logInfo);
        context.startActivity(intent);
    }

    /**
     * Activity的onCreate回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_locker_log);

        setTitle(R.string.app_locker);

        // 获取传递的参数，并显示到界面上
        accessLog = (AccessLog)getIntent().getSerializableExtra(PARAM_LOGINFO);
        showLockLogInfo(accessLog);
    }

    /**
     * 显示完整的日志信息
     * @param accessLog 日志信息
     */
    private void showLockLogInfo(AccessLog accessLog) {
        iv_photo = (ImageView)findViewById(R.id.iv_photo);
        view_see_more = findViewById(R.id.view_see_more);
        bt_ok = (Button)findViewById(R.id.bt_ok);
        tv_msg = (TextView)findViewById(R.id.tv_msg);

        // 配置提示信息
        tv_msg.setText(getString(R.string.app_somebody_access, accessLog.getAppName()));

        // 根据图径，从不同的位置解析出图片，然后显示在界面上
        Bitmap bitmap = null;
        String imagePath = accessLog.getPhotoPath();
        if (imagePath != null) {
            if (accessLog.isPhotoInInternal()) {
                bitmap = BitmapFactory.decodeFile(imagePath);
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            iv_photo.setImageBitmap(bitmap);
        }

        // 配置查看更多按钮
        view_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动查看更多，然后关闭自己
                MainActivity.start(LockLogActivity.this);
                finish();
            }
        });

        // 配置确认按钮
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
