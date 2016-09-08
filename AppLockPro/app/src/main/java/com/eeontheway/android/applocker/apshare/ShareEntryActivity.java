package com.eeontheway.android.applocker.apshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alipay.share.sdk.openapi.BaseReq;
import com.alipay.share.sdk.openapi.BaseResp;
import com.alipay.share.sdk.openapi.IAPAPIEventHandler;
import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.share.AlipayShare;
import com.eeontheway.android.applocker.share.IShare;

/**
 * 支付宝分享接口
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ShareEntryActivity extends AppCompatActivity {
    private static IShare.OnFinishListener listener;

    /**
     * 配置结束时调用的监听器
     * @param listener 监听器
     */
    public static void setListener(IShare.OnFinishListener listener) {
        ShareEntryActivity.listener = listener;
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
        registerShareEventHandler();
    }

    /**
     * 注册微信事件响应器
     */
    public void registerShareEventHandler () {
        IAPAPIEventHandler handler = new IAPAPIEventHandler() {
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
                    case BaseResp.ErrCode.ERR_SENT_FAILED:
                        result = R.string.share_error;
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        result = R.string.share_not_unsupported;
                        break;
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


        AlipayShare.getAPI().handleIntent(getIntent(), handler);
    }
}
