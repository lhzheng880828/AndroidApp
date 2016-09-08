package com.eeontheway.android.applocker.wxapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.share.IShare;
import com.eeontheway.android.applocker.share.WXShareBase;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 微信分享接口处理接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class WXEntryActivity extends AppCompatActivity  {
    private static IShare.OnFinishListener listener;

    /**
     * 配置结束时调用的监听器
     * @param listener 监听器
     */
    public static void setListener(IShare.OnFinishListener listener) {
        WXEntryActivity.listener = listener;
    }

    /**
     * Activity的onCreate回调
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);

        // 注册分享接口
        registerWXShareEventHandler();
    }

    /**
     * 注册微信事件响应器
     */
    public void registerWXShareEventHandler () {
        IWXAPIEventHandler handler = new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {
            }

            @Override
            public void onResp(BaseResp baseResp) {
                int result = 0;

                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        result = R.string.share_success;
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        result = R.string.share_cancel;
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        result = R.string.share_deny;
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        result = R.string.share_not_unsupported;
                        break;
                    case BaseResp.ErrCode.ERR_SENT_FAILED:
                    default:
                        result = R.string.share_error;
                        break;
                }

                if (listener != null) {
                    listener.onFinish(baseResp.errCode, getString(result));
                }
                finish();
            }
        };


        WXShareBase.getAPI().handleIntent(getIntent(), handler);
    }
}

