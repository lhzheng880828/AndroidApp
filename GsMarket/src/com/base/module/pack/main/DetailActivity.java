package com.base.module.pack.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.base.module.language.GuiDisplay;
import com.base.module.pack.R;
import com.base.module.pack.common.Log;
import com.base.module.pack.listener.CustomWebViewDownLoadListener;
//import com.base.GMI.GMIInstance;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import com.base.module.pack.method.PackMethod;

/**
 * User: czheng
 * Date: 5/7/13
 * Time: 9:42 AM
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved.
 */
public class DetailActivity extends Activity {

    private GuiDisplay mGuidisplay = GuiDisplay.instance();
    private WebView detailweb;
    private BroadcastReceiver updateBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.detail);
        Intent intent = getIntent();
        String url = intent.getStringExtra(ApkListFragment.CLICKURLITENTEXTRANAME);
        detailweb = (WebView) this.findViewById(R.id.detail);
        final ProgressDialog mProgressDialog = new ProgressDialog(DetailActivity.this);
        mProgressDialog.setMessage(mGuidisplay.getValue(DetailActivity.this, 3361));
        mProgressDialog.show();
        detailweb.loadUrl(url);
        detailweb.getSettings().setJavaScriptEnabled(true);
        detailweb.setDownloadListener(new CustomWebViewDownLoadListener(this));
        detailweb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);    //To change body of overridden methods use File | Settings | File Templates.
                mProgressDialog.cancel();
            }

        });

        //detailweb.addJavascriptInterface(new GMIInstance(this, this.getClass()), AppActivity.GMIINTERFACE);
        detailweb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);    //To change body of overridden methods use File | Settings | File Templates.
            }

        });

        updateBroadcastReceiver = new WebViewDataChangedBroadcastReceiver();
        this.registerReceiver(updateBroadcastReceiver, new IntentFilter(PackMethod.BROWSER_DATACHANGED_ACTION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(updateBroadcastReceiver);
        super.onDestroy();
    }

    private class WebViewDataChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
               /* if (intent != null) {
                    String uppackagename = intent.getStringExtra(PackMethod.PACKAGENAME);

                }*/
            Log.d("updatestatues ");
            detailweb.loadUrl("javascript:updateStatus()");
        }

    };
}