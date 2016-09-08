package com.eeontheway.android.applocker.feedback;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.login.IUserManager;
import com.eeontheway.android.applocker.login.UserManagerFactory;
import com.eeontheway.android.applocker.push.PushMessageReceiver;

/**
 * 用户反馈Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class FeedBackSubmitActivity extends AppCompatActivity {
    private EditText et_content;
    private EditText et_contact;
    private ProgressBar pb_progress;

    private String lastContent;
    private FeedBackBase feedBackManager;
    private FeedBackSendListener feedbackListener;
    private IUserManager userManager;

    /**
     * 启动Activity
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, FeedBackSubmitActivity.class);
        context.startActivity(intent);
    }

    /**
     * Activity的onCreate函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        setTitle(R.string.feed_back);

        initToolBar();
        initViews();
        initFeedBack();
        initUserManager();
    }

    /**
     * 初始化用户管理器
     */
    private void initUserManager () {
        userManager = UserManagerFactory.create(this);
        userManager.init(this);
    }

    /**
     * Activity的onDestroy回调
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        feedBackManager.unInit();
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tl_header);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * 初始化各种View
     */
    private void initViews() {
        pb_progress = (ProgressBar)findViewById(R.id.pb_progress);
        pb_progress.setVisibility(View.GONE);
        et_content = (EditText)findViewById(R.id.et_content);
        et_contact = (EditText)findViewById(R.id.et_contact);
    }

    /**
     * 初始化反馈模块
     */
    private void initFeedBack () {
        feedbackListener = new FeedBackSendListener();
        feedBackManager = FeedBackManagerFactory.create(this);
        feedBackManager.init(this);
        feedBackManager.setSendListener(feedbackListener);
    }

    /**
     * Activiy的onCreateOptionMenu回调
     *
     * @param menu 创建的菜单
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback_sumbit, menu);
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
                finish();
                return true;
            case R.id.action_submit:
                submitFeedBack();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * 提交反馈信息
     */
    private void submitFeedBack () {
        String content = et_content.getText().toString();
        if(!TextUtils.isEmpty(content)){
            if((lastContent != null) && content.equals(lastContent)){
                Toast.makeText(FeedBackSubmitActivity.this,
                        getString(R.string.no_send_feedback_again), Toast.LENGTH_SHORT).show();
            }else {
                // 获取用户cid
                String cid = userManager.getMyCid();
                if (cid == null) {
                    cid = PushMessageReceiver.cid;
                }

                // 发送反馈信息
                FeedBackInfo feedBackInfo = new FeedBackInfo();
                feedBackInfo.setContent(content);
                feedBackInfo.setContact(et_contact.getText().toString());
                feedBackInfo.setTopic(true);
                feedBackInfo.setCid(cid);
                feedBackManager.sendFeedBack(feedBackInfo);
            }
        }else {
            Toast.makeText(FeedBackSubmitActivity.this,
                    getString(R.string.please_add_feedback), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 反馈管理器发送反馈时的回调
     */
    private class FeedBackSendListener implements FeedBackBase.SendStatusListener {
        @Override
        public void onStart() {
            pb_progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess() {
            // 缓存已提交的内容
            lastContent = et_content.getText().toString();

            // 提示用户成功
            Toast.makeText(FeedBackSubmitActivity.this, getString(R.string.feedback_send_ok),
                    Toast.LENGTH_SHORT).show();
            pb_progress.setVisibility(View.INVISIBLE);

            // 提交成功后退出
            finish();
        }

        @Override
        public void onFail(String msg) {
            // 提示用户失败
            Toast.makeText(FeedBackSubmitActivity.this, getString(R.string.feedback_send_fail, msg),
                    Toast.LENGTH_SHORT).show();

            pb_progress.setVisibility(View.INVISIBLE);
        }
    }
}
