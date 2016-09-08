package com.eeontheway.android.applocker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

/**
 * 密码显示自定义View
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class NumberPasswordView extends FrameLayout implements View.OnClickListener {
    private static final int MSG_DELAY = 0;

    private EditText et_input;
    private Button bt_ok;
    private ImageButton bt_0;
    private ImageButton bt_1;
    private ImageButton bt_2;
    private ImageButton bt_3;
    private ImageButton bt_4;
    private ImageButton bt_5;
    private ImageButton bt_6;
    private ImageButton bt_7;
    private ImageButton bt_8;
    private ImageButton bt_9;
    private ImageButton bt_del;
    private TextView tv_password_set_error;

    private Context context;
    private PasswordCallback callback;
    private boolean setPassMode;
    private int passwordErrorDelay;
    private int passwordErrorCount;
    private int maxRetryCount;
    private boolean waitSecondPasswordState;
    private String firstPassword;

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 各项属性
     */
    public NumberPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initViews();
        readAttributes(attrs);
    }

    /**
     * 初始化各种View
     */
    public void initViews () {
        // 初始化UI
        View view = inflate(context, R.layout.view_number_password, this);
        et_input = (EditText)view.findViewById(R.id.et_input);
        bt_ok = (Button)view.findViewById(R.id.bt_ok);
        bt_del = (ImageButton)view.findViewById(R.id.bt_del);
        bt_del.setOnClickListener(this);
        bt_0 = (ImageButton)view.findViewById(R.id.bt_0);
        bt_0.setOnClickListener(this);
        bt_1 = (ImageButton)view.findViewById(R.id.bt_1);
        bt_1.setOnClickListener(this);
        bt_2 = (ImageButton)view.findViewById(R.id.bt_2);
        bt_2.setOnClickListener(this);
        bt_3 = (ImageButton)view.findViewById(R.id.bt_3);
        bt_3.setOnClickListener(this);
        bt_4 = (ImageButton)view.findViewById(R.id.bt_4);
        bt_4.setOnClickListener(this);
        bt_5 = (ImageButton)view.findViewById(R.id.bt_5);
        bt_5.setOnClickListener(this);
        bt_6 = (ImageButton)view.findViewById(R.id.bt_6);
        bt_6.setOnClickListener(this);
        bt_7 = (ImageButton)view.findViewById(R.id.bt_7);
        bt_7.setOnClickListener(this);
        bt_8 = (ImageButton)view.findViewById(R.id.bt_8);
        bt_8.setOnClickListener(this);
        bt_9 = (ImageButton)view.findViewById(R.id.bt_9);
        bt_9.setOnClickListener(this);
        tv_password_set_error = (TextView)findViewById(R.id.tv_password_set_error);

    }

    /**
     * 读取各项配置属性，设置UI
     * @param attrs 配置属性
     */
    private void readAttributes(AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPasswordView);

        // 读取当前模式
        setPassMode = attrArray.getBoolean(R.styleable.NumberPasswordView_setpass_mode, false);
        if (setPassMode) {
            et_input.setHint(R.string.set_first_password);
            waitSecondPasswordState = false;
        } else {
            et_input.setHint(R.string.please_input_password);
        }

        // 设置文本编辑框的提示
        String hint = attrArray.getString(R.styleable.NumberPasswordView_edittext_hint);
        if (hint != null) {
            et_input.setHint(hint);
        }

        // 设置按钮的文字
        bt_ok.setOnClickListener(this);
        String buttonText = attrArray.getString(R.styleable.NumberPasswordView_button_text);
        if (buttonText != null) {
            bt_ok.setText(buttonText);
        } else {
            bt_ok.setText(context.getString(R.string.ok));
        }

        // 读取密码错误时冻结输入的延时
        passwordErrorDelay = attrArray.getInt(R.styleable.NumberPasswordView_passwordErrorDelay, 5);
        setMaxRetryCount(attrArray.getInt(R.styleable.NumberPasswordView_maxRetryCount, 3));
    }

    /**
     * 设置最大允许的重试次数
     * @param count 最大重试次数
     */
    public void setMaxRetryCount (int count) {
        maxRetryCount = count;
    }

    /**
     * 按下的事件处理器
     * @param v 哪个View按下
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_0: et_input.append("0"); break;
            case R.id.bt_1: et_input.append("1"); break;
            case R.id.bt_2: et_input.append("2"); break;
            case R.id.bt_3: et_input.append("3"); break;
            case R.id.bt_4: et_input.append("4"); break;
            case R.id.bt_5: et_input.append("5"); break;
            case R.id.bt_6: et_input.append("6"); break;
            case R.id.bt_7: et_input.append("7"); break;
            case R.id.bt_8: et_input.append("8"); break;
            case R.id.bt_9: et_input.append("9"); break;
            case R.id.bt_del:
                String text = et_input.getText().toString();
                int cursorPos = et_input.getSelectionEnd();
                et_input.setText(text.length() > 0 ? text.subSequence(0, text.length() - 1) : "");
                if (cursorPos > 0) {
                    et_input.setSelection(cursorPos - 1);
                }
                break;
            case R.id.bt_ok:
                String password = et_input.getText().toString();
                if (password.isEmpty()) {
                    // 检查密码输入是否为空
                    Toast.makeText(context, R.string.password_empty, Toast.LENGTH_SHORT).show();
                } else {
                    tv_password_set_error.setVisibility(VISIBLE);
                    if (setPassMode) {
                        // 密码输入时的事件处理
                        if (waitSecondPasswordState == false) {
                            // 第一次按下时，保存密码，并提示再次输入
                            firstPassword = password;
                            et_input.setText("");
                            tv_password_set_error.setText(context.getString(R.string.please_input_password_again));
                            waitSecondPasswordState = true;
                        } else {
                            // 第二次按下时，检查密码，并根据前后是否匹配做不同处理
                            if (password.equals(firstPassword)) {
                                // 前后匹配，可以结束了，调用回调函数
                                callback.onPasswordSetOk(password);
                                tv_password_set_error.setText(context.getString(R.string.password_match));
                            } else {
                                // 不匹配，清空，提示前后不一致
                                et_input.setText("");
                                tv_password_set_error.setText(context.getString(R.string.password_mismatch));
                            }
                            waitSecondPasswordState = false;
                        }
                    } else {
                        // 密码检查的处理
                        boolean ok = callback.verifyPassword(et_input.getText().toString());
                        if (!ok) {
                            // 密码超过次数，则冻结整个输入
                            if (++passwordErrorCount >= maxRetryCount) {
                                callback.onMaxErrorAccuor();
                                disableInputForAWhile();
                            } else {
                                showPasswordError();
                            }
                            et_input.setText("");
                        }
                    }
                }
                break;
        }
    }

    /**
     * 显示密码错误
     */
    private void showPasswordError() {
        // 在界面上提示密码错误，还有多少次机会
        String alertString = context.getString(R.string.password_error_count_alert,
                                                    maxRetryCount - passwordErrorCount);
        tv_password_set_error.setText(alertString);
        tv_password_set_error.setVisibility(View.VISIBLE);
    }

    /**
     * 冻结整个界面
     */
    private void disableInputForAWhile () {
        // 冻结输入界面一会儿，防止用户频繁操作
        enableAllInput(false);

        // 设置一个Handler，专门用于处理当密码错误次数过大时，冻结一小会儿输入的功能
        // 不能用定时器，因为其相当于单独设置了一个线程，不能在其中操作UI
        final Handler handler = new Handler() {
            private int count = passwordErrorDelay;     // 最大延时次数

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_DELAY) {
                    if (--count > 0) {
                        // 超时未到
                        String alertMsg = context.getString(R.string.wait_delay_to_retry, count).toString();
                        tv_password_set_error.setText(alertMsg);

                        // 继续自己给自己发消息，以实现不断地刷新
                        sendEmptyMessageDelayed(MSG_DELAY, 1000);
                    } else {
                        // 超时到达
                        enableAllInput(true);
                        tv_password_set_error.setText(
                                context.getString(R.string.password_error_count_alert, maxRetryCount));

                        // 重设计数
                        passwordErrorCount = 0;
                    }
                }
            }
        };

        // 触发第一次延时冻结操作
        handler.sendEmptyMessageDelayed(MSG_DELAY, 0);
    }

    /**
     * 使能所有输入
     * @param enable 是否使能
     */
    private void enableAllInput (boolean enable) {
        et_input.setEnabled(enable);
        bt_ok.setEnabled(enable);
        bt_0.setEnabled(enable);
        bt_1.setEnabled(enable);
        bt_2.setEnabled(enable);
        bt_3.setEnabled(enable);
        bt_4.setEnabled(enable);
        bt_5.setEnabled(enable);
        bt_6.setEnabled(enable);
        bt_7.setEnabled(enable);
        bt_8.setEnabled(enable);
        bt_9.setEnabled(enable);
        bt_del.setEnabled(enable);
        tv_password_set_error.setEnabled(enable);
    }

    /**
     * 事件监听器
     */
    public interface PasswordCallback  {
        boolean verifyPassword (String password);
        void onMaxErrorAccuor();
        void onPasswordSetOk(String password);
    }

    /**
     * 设置事件监听器
     * @param callback 监听器
     */
    public void setPasswordCallback (PasswordCallback callback) {
        this.callback = callback;
    }
}
