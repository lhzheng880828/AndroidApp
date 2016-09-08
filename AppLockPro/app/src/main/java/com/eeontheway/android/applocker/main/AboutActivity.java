package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.feedback.FeedBackListActivity;
import com.eeontheway.android.applocker.updater.UpdaterManager;
import com.eeontheway.android.applocker.utils.AppMarketUtils;
import com.eeontheway.android.applocker.utils.Configuration;
import com.eeontheway.android.applocker.utils.SystemUtils;

/**
 * 应用锁配置界面Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class AboutActivity extends AppCompatActivity {
    private TextView tv_version;
    private ListView lv_menu;
    private ArrayAdapter lv_adapter;

    // 各选择按钮的文本资源ID
    private static final int[] menu_name_res = {
            R.string.goto_vote,
            R.string.check_update,
            R.string.app_website,
            R.string.feed_back,
            //R.string.pay_support      支付功能暂不支持
    };
    private String [] menuNames;

    /**
     * 启动应用锁配置界面
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    /**
     * Activity的onCreate回调
     *
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle(R.string.about);

        initToolBar();
        initViews();
    }

    /**
     * 等待初始密码的设置结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UpdaterManager.REQUEST_INSTALL_APP) {
            // 用户取消了安装过程
            Toast.makeText(this, R.string.update_canceled, Toast.LENGTH_SHORT).show();
        }
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
        // 初始化版本显示
        tv_version = (TextView)findViewById(R.id.tv_version);
        tv_version.setText(getString(R.string.version, SystemUtils.getVersionName(this)));

        // 初始化菜单列表
        menuNames = new String[menu_name_res.length];
        for (int i = 0; i < menu_name_res.length; i++) {
            menuNames[i] = getString(menu_name_res[i]);
        }
        lv_adapter = new ArrayAdapter<>(this, R.layout.item_menu_about,
                                                R.id.tv_title, menuNames);
        lv_menu = (ListView)findViewById(R.id.lv_menu);
        lv_menu.setAdapter(lv_adapter);
        lv_menu.setOnItemClickListener(new ListViewClickListener());
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
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * 列表项中任意子项被点击时的处理
     */
    private class ListViewClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (menu_name_res[position]) {
                case R.string.goto_vote:        // 进入应用市场评分
                    AppMarketUtils appMarketUtils = new AppMarketUtils(AboutActivity.this);
                    appMarketUtils.gotoVoteInMarket();
                    break;
                case R.string.check_update:     // 启动更新检查
                    UpdaterManager updaterManager = new UpdaterManager(AboutActivity.this,
                                                            MainActivity.REQUEST_INSTALL_APP);
                    updaterManager.manuUpdate(Configuration.updateSiteUrl);
                    break;
                case R.string.app_website:      // 打开App的网站
                    WebViewActivity.start(AboutActivity.this, Configuration.webSiteUrl, menuNames[position]);
                    break;
                case R.string.feed_back:        // 启动反馈窗口
                    FeedBackListActivity.start(AboutActivity.this);
                    break;
                case R.string.pay_support:      // 启动捐赠接口

                    break;
            }
        }
    }
}
