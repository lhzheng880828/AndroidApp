package com.eeontheway.android.applocker.ui;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 等待进度对话框
 */
public class WaitingProgressDialog {
    private Context context;
    private ProgressDialog progressDialog;

    /**
     * 构造函数
     * @param context 上下文
     */
    public WaitingProgressDialog (Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    /**
     * 是否显示
     * @param show 是否显示
     */
    public void show (boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    /**
     * 设置要显示的消息
     * @param msg 要显示的消息
     */
    public void setMessage (String msg) {
        progressDialog.setMessage(msg);
    }
}
