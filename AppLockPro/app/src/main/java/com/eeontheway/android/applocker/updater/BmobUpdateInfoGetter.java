package com.eeontheway.android.applocker.updater;

import android.content.Context;

import com.eeontheway.android.applocker.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * BMOB升级信息获取器接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class BmobUpdateInfoGetter extends UpdateInfoGetterBase {
    private Context context;

    /**
     * 构造函数
     * @param context 上下文
     */
    public BmobUpdateInfoGetter (Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 初始化
     */
    @Override
    public void init () {
    }

    /**
     * 反初始化
     */
    @Override
    public void unInit () {
    }

    /**
     * 获取升级信息结构
     * @param src 获取的源地址
     */
    public void requestGetInfo (String src) {
        BmobQuery<BmobUpdateInfo> query = new BmobQuery<>();
        query.order("-versionNum");         // 按版本号从最大到最小排序
        query.setLimit(1);                  // 查询最新的一条
        query.findObjects(context, new FindListener<BmobUpdateInfo>() {
            @Override
            public void onSuccess(List<BmobUpdateInfo> list) {
                if (list.size() > 0) {
                    UpdateInfo info = list.get(0).toUpdateInfo();
                    if (info.isEnable() && (listener != null)) {
                        listener.onSuccess(list.get(0).toUpdateInfo());
                    }
                } else {
                    if (listener != null) {
                        listener.onFail(-1, context.getString(R.string.no_updateinfo));
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                if (listener != null) {
                    listener.onFail(-1, context.getString(R.string.no_updateinfo));
                }
            }
        });
    }
}
