package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.eeontheway.android.applocker.R;

/**
 * 浏览器访问界面
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class WebViewActivity extends AppCompatActivity {
    private static final String URL_PARAM = "url";
    private static final String NAME_PARAM = "name";

    private WebView webView;
    private ProgressBar pb_progress;

    /**
     * 启动Activity
     *
     * @param context 上下文
     */
    public static void start(Context context, String url, String name) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL_PARAM, url);
        intent.putExtra(NAME_PARAM, name);
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
        setContentView(R.layout.activity_web_view);

        initToolBar();
        initViews();
    }

    /**
     * 开始访问
     */
    @Override
    protected void onStart() {
        super.onStart();

        // 取名称，修改标题
        String name = getIntent().getStringExtra(NAME_PARAM);
        setTitle(name);

        // 取URL，让WebView去访问
        String url = getIntent().getStringExtra(URL_PARAM);
        if ((url != null) && (!url.isEmpty())) {
            webView.loadUrl(url);
        }
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tl_header);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
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
     * 初始化各种View
     */
    private void initViews() {
        // 配置进度条
        pb_progress = (ProgressBar)findViewById(R.id.pb_progress);
        pb_progress.setMax(100);
        pb_progress.setProgress(0);

        // 配置WebView
        webView = (WebView) findViewById(R.id.webview);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);

        //加载数据
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb_progress.setProgress(newProgress);

                // 加载完成时，隐藏进度条
                if (newProgress == 100) {
                    pb_progress.setVisibility(View.GONE);
                } else {
                    pb_progress.setVisibility(View.VISIBLE);
                }
            }
        });

        // 当网页链接点击时的处理
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    /**
     * 返回Key按下处理函数
     * 用于浏览器返回上一页浏览
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // 返回上一次浏览的网页
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }
}
