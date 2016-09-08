package com.eeontheway.android.applocker.main;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;

/**
 * 模式重命名对话框
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ModeRenameDialog {
    private Context context;
    private boolean addMode;
    private ResultListener listener;

    /**
     * 构造函数
     * @param context
     */
    public ModeRenameDialog(Context context, boolean addMode) {
        this.context = context;
        this.addMode = addMode;
    }

    /**
     * 设置结果监听器
     * @param listener
     */
    public void setListener(ResultListener listener) {
        this.listener = listener;
    }

    /**
     * 创建并显示对话框
     */
    public void show () {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(context, R.layout.view_mode_createor, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        final TextView et_mode = (TextView) view.findViewById(R.id.et_mode);
        final Button btn_add = (Button) view.findViewById(R.id.btn_add);
        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        if (addMode) {
            btn_add.setText(R.string.add);
            tv_title.setText(R.string.mode_add);
        } else {
            btn_add.setText(R.string.ok);
            tv_title.setText(R.string.rename_mode);
        }

        // 没有输入模式名称，不允许按添加按钮
        et_mode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_mode.getText().toString().isEmpty()) {
                    btn_add.setEnabled(false);
                } else {
                    btn_add.setEnabled(true);
                }
            }
        });

        // 添加按钮
        btn_add.setEnabled(false);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.rename(et_mode.getText().toString());
                }
                dialog.dismiss();
            }
        });

        // 取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.cancel();
                }
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    /**
     * 结果监听器
     */
    interface ResultListener {
        void rename (String newName);
        void cancel ();
    }
}
