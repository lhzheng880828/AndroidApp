package com.eeontheway.android.applocker.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eeontheway.android.applocker.login.IUserManager;
import com.eeontheway.android.applocker.login.UserInfo;
import com.eeontheway.android.applocker.login.UserManagerFactory;
import com.igexin.sdk.PushConsts;

/**
 * 个推的推送服务接受器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class PushMessageReceiver extends BroadcastReceiver {
    public static String cid;
    private IUserManager userManager;

    /**
     * 初始化用户管理器
     */
    private void initUserManager (Context context) {
        userManager = UserManagerFactory.create(context);
        userManager.init(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        // 初始化用户管理
        initUserManager(context);

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    // 调用消息处理器处理消息
                    PushMessageProcessor processor = new PushMessageProcessor(context);
                    processor.processMessage(payload);
                }
                break;
            case PushConsts.GET_CLIENTID:
                String cid = bundle.getString("clientid");

                // 更新服务器上的cid
                boolean ok = userManager.setMyCid(cid);
                if (ok == false) {
                    this.cid = cid;
                }
                break;
            default:
                break;
        }
    }
}