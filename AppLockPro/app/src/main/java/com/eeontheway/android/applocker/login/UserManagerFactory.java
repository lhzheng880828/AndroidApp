package com.eeontheway.android.applocker.login;

import android.content.Context;

import com.eeontheway.android.applocker.share.AlipayShare;
import com.eeontheway.android.applocker.share.IShare;
import com.eeontheway.android.applocker.share.QQShareFriend;
import com.eeontheway.android.applocker.share.QQShareQzone;
import com.eeontheway.android.applocker.share.SystemShare;
import com.eeontheway.android.applocker.share.WXShareFriend;
import com.eeontheway.android.applocker.share.WXShareTimeLine;
import com.eeontheway.android.applocker.utils.Configuration;

/**
 * 用户管理接口
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class UserManagerFactory {
    /**
     * 创建用户管理对像
     * @param context 上下文
     * @return 用户管理接口
     */
    public static IUserManager create (Context context) {
        IUserManager manager  = null;

        switch (Configuration.USER_MANAGER_TYPE) {
            case Configuration.USER_MANAGER_TYPE_BMOB:
                manager = new BmobUserManager(context);
                break;
        }
        return manager;
    }
}
