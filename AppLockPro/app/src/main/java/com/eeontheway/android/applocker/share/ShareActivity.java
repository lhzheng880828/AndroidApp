package com.eeontheway.android.applocker.share;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方分享的Activity
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class ShareActivity extends AppCompatActivity {
    private static final String PARAM_SHARE_INFO = "share_info";

    private GridView gv_share_list;

    // 分享动作信息
    private static final String ICON_NAME = "icon_name";
    private static final String NAME_NAME = "name_name";
    private ShareActionInfo [] actionInfos = {
            new ShareActionInfo(R.drawable.wx_friend, R.string.wx_friend, IShare.SHARE_TYPE_WX_FRIEND),
            new ShareActionInfo(R.drawable.wx_timeline, R.string.wx_timeline, IShare.SHARE_TYPE_WX_TIMELINE),
            new ShareActionInfo(R.drawable.qq_logo, R.string.qq_friend, IShare.SHARE_TYPE_QQ_FRIEND),
            new ShareActionInfo(R.drawable.qzone_logo, R.string.qzone, IShare.SHARE_TYPE_QZONE),
            //new ShareActionInfo(R.drawable.ic_alipay, R.string.alipay, IShare.SHARE_TYPE_ALIPAY),
            new ShareActionInfo(R.drawable.share_platform_more, R.string.share_more, IShare.SHARE_TYPE_SYSTEM)
    };

    /**
     * 启动Activity
     * @param context 上下文
     */
    public static void start (Context context, ShareInfo info) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(PARAM_SHARE_INFO, info);
        context.startActivity(intent);
    }

    /**
     * Activity的onCreate回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        adjustShowPosition(Gravity.BOTTOM);
        initViews();
    }

    /**
     * 调整窗口的显示位置
     * @param postion 显示位置，例如Gravity.BOTTOM
     */
    public void adjustShowPosition (int postion) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = postion;
        getWindow().setAttributes(lp);
    }

    /**
     * 初始化各种View
     */
    private void initViews() {
        // 初始化分享按钮事件X
        List<Map<String, Object>> infoList = new ArrayList<>();
        for (ShareActionInfo info : actionInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put(ICON_NAME, info.imageResId);
            map.put(NAME_NAME, getString(info.nameResId));
            infoList.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, infoList, R.layout.item_share,
                new String[] {ICON_NAME, NAME_NAME}, new int[] {R.id.iv_icon, R.id.tv_name});
        gv_share_list = (GridView)findViewById(R.id.gv_share_list);
        gv_share_list.setAdapter(adapter);
        gv_share_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shareInfo (actionInfos[position].type);
            }
        });
    }

    /**
     * 分享信息
     */
    private void shareInfo(int type) {
        final IShare iShare = ShareFactory.create(ShareActivity.this, type);
        iShare.setListener(new IShare.OnFinishListener() {
            @Override
            public void onFinish(int code, String msg) {
                iShare.uninit();
                Toast.makeText(ShareActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        if (iShare.isSupported()) {
            iShare.share((ShareInfo) getIntent().getParcelableExtra(PARAM_SHARE_INFO));
        } else {
            Toast.makeText(ShareActivity.this, R.string.share_not_unsupported,
                                                                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  分享项信息
     */
    private class ShareActionInfo {
        public int imageResId;
        public int nameResId;
        public int type;

        /**
         * 构造函数
         * @param imageResId
         * @param nameResId
         * @param type
         */
        public ShareActionInfo(int imageResId, int nameResId, int type) {
            this.imageResId = imageResId;
            this.nameResId = nameResId;
            this.type = type;
        }
    }
}
