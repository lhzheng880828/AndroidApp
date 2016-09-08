package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.feedback.FeedBackListActivity;
import com.eeontheway.android.applocker.lock.DataObservable;
import com.eeontheway.android.applocker.lock.LockConfigManager;
import com.eeontheway.android.applocker.lock.LockService;
import com.eeontheway.android.applocker.lock.PasswordSetActivity;
import com.eeontheway.android.applocker.ui.FragmentPagerViewAdapter;
import com.eeontheway.android.applocker.ui.FragmentPagerViewInfo;
import com.eeontheway.android.applocker.updater.UpdaterManager;
import com.eeontheway.android.applocker.utils.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 主Activity
 * 用于显示程序主界面
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_SET_PASS = 0;
    public static final int REQUEST_INSTALL_APP = 1;
    // Fragment信息数组
    private final FragmentPagerViewInfo[] fragmentInfoArray = {
//            new FragmentPagerViewInfo(
//                    new SummaryFragment(),
//                    R.string.app_start,
//                    R.drawable.ic_app_home_selected,
//                    R.drawable.ic_app_home_normal),
            new FragmentPagerViewInfo(
                    new AppLockListFragment(),
                    R.string.applock_list,
                    R.drawable.ic_app_lock_list_selected,
                    R.drawable.ic_app_lock_list_normal),
            new FragmentPagerViewInfo(
                    new LockConditionFragment(),
                    R.string.lock_condition,
                    R.drawable.ic_app_lock_cond_selected,
                    R.drawable.ic_app_lock_cond_normal),

            new FragmentPagerViewInfo(
                    new AccessLogsFragment(),
                    R.string.access_logs,
                    R.drawable.ic_app_defense_logs_selected,
                    R.drawable.ic_app_defense_logs_normal)
    };
    private Toolbar tb_main;
    private DrawerLayout dl_main;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private long lastBackKeyPressTime = 0;
    private SettingsManager settingsManager;
    private MainLeftFragment mainLeftFragment;
    private UpdaterManager updaterManager;
    private FragmentPagerViewAdapter fragmentPagerAdapter;
    private List<FragmentPagerViewInfo> fragmentList = new ArrayList<>();
    private LockConfigManager lockConfigManager;
    private Observer observer;

    /**
     * 启动该Activity
     * @param context 上下文
     */
    public static void start (Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
    /**
     * Activity的onCreate函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsManager = SettingsManager.getInstance(this);
        mainLeftFragment = (MainLeftFragment) getSupportFragmentManager().findFragmentById(
                                                                R.id.main_left_fragment);
        updaterManager = new UpdaterManager(MainActivity.this, REQUEST_INSTALL_APP);
        lockConfigManager = LockConfigManager.getInstance(this);

        initViews();
        setTitle(R.string.app_locker);
        initDataObserver();

        // 检查密码是否设置，只有当设置后，才能启动
        checkPassword();
    }

    /**
     * Activity的onDestroy函数
     */
    @Override
    protected void onDestroy() {
        lockConfigManager.freeInstance();
        super.onDestroy();
    }

    /**
     * 初始化ActionBar
     */
    private void initViews() {
        // 配置标题栏
        tb_main = (Toolbar)findViewById(R.id.tl_main);
        tb_main.setTitle(getString(R.string.app_locker));
        tb_main.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(tb_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        updateTitle();

        // 配置DrawLayout
        dl_main = (DrawerLayout)findViewById(R.id.dl_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl_main, tb_main,
                R.string.opene, R.string.close);
        actionBarDrawerToggle.syncState();
        dl_main.setDrawerListener(actionBarDrawerToggle);

        // 配置显示Tab
        initFragmentList();
        initTabs();
    }

    /**
     * 初始化FragmentList
     * 初始只加载第1个Fragment，余后的在Tap切换时再加载
     */
    private void initFragmentList() {
        fragmentList.add(fragmentInfoArray[0]);
    }

    /**
     *  初始化UI
     *  显示各个Fragment
     */
    private void initTabs() {
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.view_page);

        // 初始化适配器及ViewPager
        fragmentPagerAdapter = new FragmentPagerViewAdapter(MainActivity.this,
                                                getFragmentManager(), fragmentInfoArray);
        viewPager.setAdapter(fragmentPagerAdapter);

        // 关联Tab和Laout
        tabLayout.setupWithViewPager(viewPager);

        // 之后，重新配置所有的Tab，设置自定义样式
        for (int i = 0; i < fragmentInfoArray.length; i++) {
            final FragmentPagerViewInfo info = fragmentInfoArray[i];

            // 初始化显示的自定义View
            View tabView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main_tablayout, null);
            ImageView iconView = (ImageView)tabView.findViewById(R.id.iv_icon);
            TextView nameView = (TextView)tabView.findViewById(R.id.tv_name);
            iconView.setImageResource(info.getIconUnSelectedResId());
            nameView.setText(info.getNameResId());
            nameView.setTextColor(getResources().getColor(R.color.color_tab_text_unselected));

            // 使用自定义的View
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(tabView);

            // 设置监听器，用于切换时更改图标和字体颜色
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                private void showTabSelected (TabLayout.Tab tab, boolean selected) {
                   // 获取Fragment相关的显示
                    FragmentPagerViewInfo info = fragmentList.get(tab.getPosition());
                    View tabView = tab.getCustomView();
                    ImageView iconView = (ImageView) tabView.findViewById(R.id.iv_icon);
                    TextView nameView = (TextView) tabView.findViewById(R.id.tv_name);

                    // 更新图标显示
                    if (selected == true) {
                        iconView.setImageResource(info.getIconSelectedResId());
                        nameView.setTextColor(getResources().getColor(R.color.color_tab_text_selected));
                    } else {
                        iconView.setImageResource(info.getIconUnSelectedResId());
                        nameView.setTextColor(getResources().getColor(R.color.color_tab_text_unselected));
                    }

                    // 切换标题
                    setTitle(info.getNameResId());
                }

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    // 延迟加载Fragment
                    if (tab.getPosition() >= (fragmentList.size())) {
                        for (int pos = fragmentList.size(); pos <= tab.getPosition(); pos++) {
                            fragmentList.add(fragmentInfoArray[pos]);
                        }
                    }

                    showTabSelected(tab, true);
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    showTabSelected(tab, false);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    onTabSelected(tab);
                }
            });

        }
        // 选中第0个
        tabLayout.getTabAt(0).select();
    }

    /**
     * 检查密码
     */
    private void checkPassword() {
        String password = settingsManager.getPassword();
        if((password == null) || (password.isEmpty())) {
            PasswordSetActivity.statActivity(this, REQUEST_SET_PASS);
        } else {
            startCheckUpdate();
            LockService.startBlockService(this);
        }
    }

    /**
     * 等待初始密码的设置结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            return;
        }

        switch (requestCode) {
            case REQUEST_SET_PASS:      // 密码设置
                if (resultCode == RESULT_OK) {
                    // 密码设置正确，保存密码
                    settingsManager.savePassword(data.getStringExtra(
                                                PasswordSetActivity.RETURN_PARAM_PASS));

                    // 正常启动
                    startCheckUpdate();
                    LockService.startBlockService(this);
                } else {
                    // 取消设置，结束应用
                    finish();
                }
                break;
            case REQUEST_INSTALL_APP:   // 升级安装
                Toast.makeText(this, R.string.update_canceled, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * Activiy的onCreateOptionMenu回调
     * @param menu 创建的菜单
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_locker_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 处理返回按钮按下的响应
     * @param item 被按下的项
     * @return 是否需要被继续处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_feedback:
                FeedBackListActivity.start(this);
                return true;
            case R.id.action_checkupdate:
                updaterManager.manuUpdate(Configuration.updateSiteUrl);
                return true;
            case R.id.action_applocker_setting:
                SettingsActivity.start(MainActivity.this);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * 连续两次按返回键才认为是退出
     */
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastBackKeyPressTime) < 2000) {
            finish();
        } else {
            Toast.makeText(MainActivity.this, R.string.exit_confirm_alert, Toast.LENGTH_SHORT).show();
            lastBackKeyPressTime = currentTime;
        }
    }

    /**
     * 检查更新
     */
    private void startCheckUpdate() {
        // 读取存储的更新使能设置
        SharedPreferences ref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String key = getResources().getString(R.string.autoupdateKey);
        boolean checkUpdateEnable = ref.getBoolean(key, true);

        // 如果要更新，则调用更新管理器
        if (checkUpdateEnable) {
            updaterManager.autoUpdate(Configuration.updateSiteUrl);
        }
    }

    /**
     * 注册数据变化监听器
     */
    private void initDataObserver () {
        observer = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                // 通知Adapter数据发生变化
                DataObservable.ObserverInfo info = (DataObservable.ObserverInfo)data;
                if (info.dataType != DataObservable.DataType.MODE_LIST) {
                    return;
                } else {
                    updateTitle();
                }
            }
        };

        lockConfigManager.registerObserver(observer);
    }

    /**
     * 刷新标题栏
     */
    private void updateTitle () {
        String modeName = getString(R.string.app_name) + " - " +
                                    lockConfigManager.getCurrentLockModeName();
        tb_main.setTitle(modeName);
    }
}
