package com.eeontheway.android.applocker.feedback;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.login.IUserManager;
import com.eeontheway.android.applocker.login.UserInfo;
import com.eeontheway.android.applocker.login.UserManagerFactory;
import com.eeontheway.android.applocker.push.IPush;
import com.eeontheway.android.applocker.push.PushFactory;
import com.eeontheway.android.applocker.push.PushInfo;
import com.eeontheway.android.applocker.push.PushMessageProcessor;
import com.eeontheway.android.applocker.push.PushMessageReceiver;
import com.eeontheway.android.applocker.utils.Configuration;

import java.util.List;

/**
 * 特定反馈信息的详细Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class FeedBackInfoActivity extends AppCompatActivity {
    private static final String PARAM_WIDTH_FEEBACK_ID = "feedback_with_id";
    private static final String PARAM_FEEDBACK_TOPIC = "feedback_topic";

    private TextView tv_reqeust_content;
    private TextView tv_reqeust_time;
    private TextView tv_reqeust_responsed;
    private EditText et_response_content;
    private TextView tv_response_time;
    private View pb_progress;

    private FeedBackBase feedBackManager;
    private IUserManager userManager;
    private FeedBackTopic topic;
    private IPush iPush;

    /**
     * 启动Activity
     *
     * @param context 上下文
     */
    public static void start(Context context, FeedBackTopic feedBackTopic) {
        Intent intent = new Intent(context, FeedBackInfoActivity.class);
        intent.putExtra(PARAM_FEEDBACK_TOPIC, feedBackTopic);
        context.startActivity(intent);
    }

    /**
     * Activity的onCreate回调
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_info2);

        iPush = PushFactory.create(this);

        setTitle(R.string.feed_back_details);

        initFeedBack();
        initToolBar();
        initViews();
        initUserManager();
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
     * 初始化用户管理器
     */
    private void initUserManager () {
        userManager = UserManagerFactory.create(this);
        userManager.init(this);
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
        // 获取各个View
        tv_reqeust_content = (TextView)findViewById(R.id.et_request_content);
        tv_reqeust_time = (TextView)findViewById(R.id.tv_reqeust_time);
        tv_reqeust_time.setText(getString(R.string.feed_back_time, 0));
        tv_reqeust_responsed = (TextView)findViewById(R.id.tv_reqeust_responsed);
        et_response_content = (EditText)findViewById(R.id.et_response_content);
        tv_response_time = (TextView)findViewById(R.id.tv_response_time);
        tv_response_time.setText(getString(R.string.feed_back_time, 0));
        pb_progress = findViewById(R.id.pb_progress);

        // 检查是否有管理员权限，无权限不允许回复
        if (!Configuration.isDevelop) {
            et_response_content.setEnabled(false);
            et_response_content.setFocusable(false);
        }

        // 初始化各个值
        Intent intent = getIntent();
        String id = intent.getStringExtra(PushMessageProcessor.KEY_ID);
        if (id != null) {
            getTopic(id);
        } else {
            topic = (FeedBackTopic) getIntent().getSerializableExtra(PARAM_FEEDBACK_TOPIC);

            updateViews();
            getResponse();
        }
    }

    /**
     * 更新界面显示
     */
    private void updateViews () {
        FeedBackInfo topicInfo = topic.getTopic();
        tv_reqeust_content.setText(topicInfo.getContent());
        tv_reqeust_time.setText(getString(R.string.feed_back_time, topicInfo.getCreateTime()));
        if (topicInfo.isResponsed()) {
            tv_reqeust_responsed.setText(getString(R.string.replyed));
            tv_reqeust_responsed.setTextColor(getResources().getColor(R.color.feedback_responsed));
        } else {
            tv_reqeust_responsed.setText(getString(R.string.not_replyed));
            tv_reqeust_responsed.setTextColor(getResources().getColor(R.color.feedback_not_responsed));
        }
    }

    /**
     * Activiy的onCreateOptionMenu回调
     *
     * @param menu 创建的菜单
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 检查是否有管理员权限，无权限不允许回复
        if (Configuration.isDevelop) {
            getMenuInflater().inflate(R.menu.menu_feedback_sumbit, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 初始化反馈功能
     */
    public void initFeedBack () {
        feedBackManager = FeedBackManagerFactory.create(this);
        feedBackManager.init(this);

        // 设置更新成功的处理
        feedBackManager.setUpdateListener(new FeedBackBase.SendStatusListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess() {
                // 获取发表反馈的已登陆用户cid或未登陆用户的icd
                String cid;
                UserInfo userInfo = topic.getTopic().getUserInfo();
                if (userInfo != null) {
                    cid = userInfo.getCid();
                } else {
                    cid = topic.getTopic().getCid();
                }

                // 如果cid有效，则推送反馈消息回去
                if (cid != null) {
                    // 完成写入成功后，推送消息，告知有反馈信息
                    PushInfo pushInfo = new PushInfo();
                    pushInfo.createJSONFeedBack(topic.getTopic().getId(),
                                                    getString(R.string.recv_feedback),
                                                    et_response_content.getText().toString());
                    iPush.pushMsg(pushInfo, cid);

                    // 提示反馈发送成功
                    Toast.makeText(FeedBackInfoActivity.this, R.string.feedback_send_ok,
                            Toast.LENGTH_SHORT).show();
                    pb_progress.setVisibility(View.GONE);

                    // 提交成功，结束反馈
                    finish();
                }
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(FeedBackInfoActivity.this,
                        getString(R.string.feedback_send_fail, msg), Toast.LENGTH_SHORT).show();
                pb_progress.setVisibility(View.GONE);
            }
        });

        // 设置回复成功的处理器
        feedBackManager.setSendListener(new FeedBackBase.SendStatusListener() {
            @Override
            public void onStart() {
                pb_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess() {
                // 更新原反馈信息，表示有人已经回复过
                FeedBackInfo info = topic.getTopic();
                info.setResponsed(true);
                feedBackManager.updateFeedBack(info);

                // 提交成功后，重新加载回复
                getResponse();
            }

            @Override
            public void onFail(String msg) {
                // 提示用户失败
                Toast.makeText(FeedBackInfoActivity.this,
                        getString(R.string.feedback_send_fail, msg), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 提交对反馈的回复
     */
    public void submitResponse () {
        // 获取用户cid
        if (topic.getTopic().isResponsed()) {
            FeedBackInfo responseInfo = topic.getResponseAt(0);
            responseInfo.setContent(et_response_content.getText().toString());
            feedBackManager.updateFeedBack(responseInfo);
        } else {
            FeedBackInfo feedBackInfo = new FeedBackInfo();
            feedBackInfo.setContent(et_response_content.getText().toString());
            feedBackInfo.setContact(getString(R.string.app_author));
            feedBackInfo.setTopic(false);
            feedBackInfo.setParentId(topic.getTopic().getId());

            feedBackManager.sendFeedBack(feedBackInfo);
        }
    }

    /**
     * 获取标题
     */
    private void getTopic (String id) {
        feedBackManager.setQueryTopicListener(new FeedBackBase.QueryTopicStatusListener() {
            @Override
            public void onStart() {
                pb_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int type, List<FeedBackTopic> infoList) {
                if (infoList.size() > 0) {
                    topic = infoList.get(0);
                    updateViews();
                    getResponse();
                } else {
                    pb_progress.setVisibility(View.VISIBLE);
                    Toast.makeText(FeedBackInfoActivity.this, R.string.no_more_items,
                                                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(int type, String msg) {
                pb_progress.setVisibility(View.GONE);
                Toast.makeText(FeedBackInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        feedBackManager.queryFeedBackById(id);
    }

    /**
     * 获取所有的响应
     */
    private void getResponse () {
        feedBackManager.setQueryResponseicListener(new FeedBackBase.QueryResponseStatusListener() {
            @Override
            public void onStart() {
                pb_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess() {
                FeedBackInfo responseInfo = topic.getResponseAt(0);
                if (responseInfo != null) {
                    et_response_content.setText(responseInfo.getContent());
                    tv_response_time.setText(getString(R.string.feed_back_time, responseInfo.getCreateTime()));
                } else {
                    tv_response_time.setText(R.string.unknwon);
                }

                pb_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFail(String msg) {
                pb_progress.setVisibility(View.GONE);
                Toast.makeText(FeedBackInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        feedBackManager.queryAllTopicResponse(topic);
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
                submitResponse();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
