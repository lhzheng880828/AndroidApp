package com.eeontheway.android.applocker.main;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.eeontheway.android.applocker.locate.LocationService;
import com.eeontheway.android.applocker.utils.Configuration;
import com.igexin.sdk.PushManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * 主Application
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class StartupApplcation extends Application {
    private static final String BMOB_APPID = "46120064d8e98adb870a67247a102485";
    public LocationService locationService;
    public Vibrator mVibrator;

    /**
     * Application的onCreate回调
     */
    @Override
    public void onCreate() {
        super.onCreate();

        initSDK();
    }

    /**
     * 初始化Bmob SDK
     */
    private void initSDK () {
        // 初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());
        locationService = LocationService.getInstance(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        // 初始化BMOB SDK
        Bmob.initialize(this, BMOB_APPID);
        BmobInstallation.getCurrentInstallation(this).save();

        // 初始化个推的SDK
        PushManager.getInstance().initialize(getApplicationContext());
    }
}
