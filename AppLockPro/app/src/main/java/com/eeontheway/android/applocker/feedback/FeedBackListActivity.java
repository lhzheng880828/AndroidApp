package com.eeontheway.android.applocker.feedback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.login.IUserManager;
import com.eeontheway.android.applocker.login.UserInfo;
import com.eeontheway.android.applocker.login.UserManagerFactory;
import com.eeontheway.android.applocker.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户历史反馈列表Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class FeedBackListActivity extends AppCompatActivity {
    private static final int FIRST_QUERY_COUNT = 20;
    private static final int MORE_QUERY_COUNT = 5;

    private SwipeRefreshLayout srl_more;
    private RecyclerView rcv_list;
    private FloatingActionButton fab;
    private TextView tv_empty;

    private FeedBackListAdapter rcv_adapter;
    private IUserManager userManager;
    private List<FeedBackTopic> feedBackTopicList = new ArrayList<>();
    private FeedBackBase feedBackManager;
    private Date lastUpdateDate;

    /**
     * 启动Activity
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, FeedBackListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_list);

        setTitle(R.string.feed_back_list);

        initFeedbackManager();
        initToolBar();
        initViews();
        initUserManager();
    }

    /**
     * 初始化用户管理器
     */
    private void initUserManager() {
        userManager = UserManagerFactory.create(this);
        userManager.init(this);

        UserInfo userInfo = userManager.getMyUserInfo();
        if (userInfo == null) {
            Toast.makeText(this, R.string.feed_back_not_login, Toast.LENGTH_SHORT).show();
        }
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
     * Activity的onResum回调
     */
    @Override
    protected void onResume() {
        super.onResume();
        startLoadData();
    }


    /**
     * 初始化Feedback管理器
     */
    private void initFeedbackManager () {
        feedBackManager = FeedBackManagerFactory.create(this);
        feedBackManager.init(this);
        feedBackManager.setQueryTopicListener(new FeedBackBase.QueryTopicStatusListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int type, List<FeedBackTopic> infoList) {
                if (infoList.size() > 0) {
                    // 遍历所有的列表
                    for (int i = 0; i < infoList.size(); i++) {
                        FeedBackTopic topic = infoList.get(i);

                        Date topicDate = SystemUtils.formatDate(topic.getTopic().getCreateTime(),
                                                                    "yyyy-MM-dd HH:mm:ss");
                        if ((lastUpdateDate != null) && topicDate.before(lastUpdateDate)) {
                            // 如果创建时间比上一次更新要早，则说明该信息是刚回复过的标题，刷新既可
                            boolean found = false;
                            for (int j = 0; j < feedBackTopicList.size(); j++) {
                                FeedBackInfo updateTopicInfo = feedBackTopicList.get(j).getTopic();
                                if (updateTopicInfo.getId().equals(topic.getTopic().getId())) {
                                    updateTopicInfo.update(topic.getTopic());
                                    rcv_adapter.notifyItemChanged(j);
                                    found = true;
                                    break;
                                }
                            }

                            // 如果没有找到，则说明是加载更老回复返回的结果，插入到尾部
                            if (found == false) {
                                int insertPos = feedBackTopicList.size();
                                feedBackTopicList.add(insertPos, topic);
                                rcv_adapter.notifyItemRangeInserted(insertPos, 1);
                            }
                        } else {
                            // 否则，则说明是新创建的回复，插入到相应的位置
                            // 按时间降序顺序，逐步插入到队列中
                            if (type == FeedBackBase.QUERY_TIME_NEWER) {
                                int insertPos = i;
                                feedBackTopicList.add(insertPos, topic);
                                rcv_adapter.notifyItemRangeInserted(insertPos, 1);
                            } else if (type == FeedBackBase.QUERY_TIME_OLDER){
                                int insertPos = feedBackTopicList.size();
                                feedBackTopicList.add(insertPos, topic);
                                rcv_adapter.notifyItemRangeInserted(insertPos, 1);
                            }
                        }
                    }

                    // 纪录最后一次更新的时间
                    lastUpdateDate = new Date();

                    // 允许加载更多
                    rcv_adapter.setUpLoadingMoreEnable(false);
                } else {
                    // 没有更多数据
                    rcv_adapter.setUpLoadingMoreEnable(false);
                    Toast.makeText(FeedBackListActivity.this, R.string.no_more_items,
                                                                Toast.LENGTH_SHORT).show();
                }

                tv_empty.setVisibility(feedBackTopicList.size() > 0 ? View.GONE : View.VISIBLE);
                rcv_adapter.setUpLoadingMoreEnable(false);
                srl_more.setRefreshing(false);
            }

            @Override
            public void onFail(int type, String msg) {
                Toast.makeText(FeedBackListActivity.this, msg, Toast.LENGTH_SHORT).show();

                rcv_adapter.setUpLoadingMoreEnable(false);
                srl_more.setRefreshing(false);
            }
        });
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
        srl_more = (SwipeRefreshLayout)findViewById(R.id.srl_more);
        srl_more.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        srl_more.setOnRefreshListener(new SwipeRefreshListener());

        rcv_list = (RecyclerView)findViewById(R.id.rcv_list);
        rcv_list.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_list.setLayoutManager(layoutManager);
        rcv_list.addOnScrollListener(new ScrollerListener());

        rcv_adapter = new FeedBackListAdapter(this, feedBackTopicList);
        rcv_adapter.setItemClickListener(new FeedBackListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(int postion) {
                // 查看特定反馈的所有内容
                FeedBackInfoActivity.start(FeedBackListActivity.this, feedBackTopicList.get(postion));
            }
        });
        rcv_list.setAdapter(rcv_adapter);

        tv_empty = (TextView)findViewById(R.id.tv_empty);

        // 配置添加按钮
        fab = (FloatingActionButton)findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedBackSubmitActivity.start(FeedBackListActivity.this);
            }
        });
    }

    /**
     * 开始加载数据
     */
    private void startLoadData () {
        srl_more.post(new Runnable() {
            @Override
            public void run() {
                srl_more.setRefreshing(true);
                loadLatestData();
            }
        });
    }

    /**
     * 加载最新的数据
     */
    private void loadLatestData () {
        if (feedBackTopicList.size() > 0) {
            // 加载更新的数据
            Date date = SystemUtils.formatDate(
                    feedBackTopicList.get(0).getTopic().getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            feedBackManager.queryFeedBackNewer(MORE_QUERY_COUNT, date, lastUpdateDate);
        } else {
            // 为空，强制刷新，加载
            feedBackManager.queryFeedBackOlder(FIRST_QUERY_COUNT, new Date(), lastUpdateDate);
        }
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
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * SwipeRefreshLayout加载更多的监听器
     */
    private class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            loadLatestData();
        }
    }

    /**
     * RecyclerView加载更多的监听器
     */
    private class ScrollerListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            if ((newState ==RecyclerView.SCROLL_STATE_IDLE) &&
                    (lastVisibleItem != -1) &&
                    (lastVisibleItem + 1 == feedBackTopicList.size())) {
                // 加载更多更老的数据
                FeedBackTopic topic = feedBackTopicList.get(feedBackTopicList.size() - 1);
                Date date = SystemUtils.formatDate(topic.getTopic().getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                rcv_adapter.setUpLoadingMoreEnable(true);
                feedBackManager.queryFeedBackOlder(MORE_QUERY_COUNT, date, lastUpdateDate);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }
}