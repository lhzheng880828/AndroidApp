package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * App锁配置操作类showLockAlert
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class SettingsManager {
    public static final int SCREENLOCK_CLEAN_NO_LOCK = 0;
    public static final int SCREENLOCK_CLEAN_ALL_LOCKED = 1;
    public static final int SCREENLOCK_CLEAN_ALL_LOCKED_AFTER_3MIN = 2;

    // 各个配置项的Key
    // 注意要与pref_applocker_config.xml中的保持一致
    public static final String applock_password_key = "applock_password";
    public static final String unlock_failed_capture_enable_key = "unlock_failed_capture_enable";
    public static final String unlock_failed_capture_errcount_key = "unlock_failed_capture_errcount";
    public static final String unlock_failed_capture_location_key = "unlock_failed_capture_location";
    public static final String screen_lock_mode_key = "screen_lock_mode";
    public static final String autolock_on_app_quit_key = "autolock_on_app_quit";
    public static final String one_password_unlock_all_key = "one_password_unlock_all";
    public static final String alert_lock_unlock_key = "alert_lock_unlock";
    public static final String add_tag_to_photo_key = "add_tag_to_photo";
    public static final String locate_interval_key = "locate_interval";
    public static final String locate_default_distance_key = "locate_default_distance";
    private static SettingsManager instance;
    SharedPreferences sharedPref;
    // 各个配置项
    private String applock_password;
    private boolean unlock_failed_capture_enable;
    private int unlock_failed_capture_errcount;
    private boolean unlock_failed_capture_location;
    private int screen_lock_mode;
    private boolean autolock_on_app_quit;
    private boolean one_password_unlock_all;
    private boolean alert_lock_unlock;
    private boolean add_tag_to_photo;
    private int locate_interval;
    private int locate_default_distance;
    // 更新配置
    private boolean updateOnlyWhenWifiConntected;
    private Context context;

    /**
     * 构造函数
     */
    protected SettingsManager(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // 检查是否存在缺省配置文件，不存在则创建一个
        // 因为SettingsFragment只有打开才会创建，第一次启动应用时，配置文件是空的！！！
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPref.contains("locate_default_distance_key")) {
            createDefaultConfigFile();
        }

        // 加载所有配置
        reloadAllPreferences();
    }

    /**
     * 获取实例：单例模式
     */
    public static SettingsManager getInstance (Context context) {
        if (instance == null) {
            instance = new SettingsManager(context);
            instance.context = context;
        }

        return instance;
    }

    public static void freeInstance() {

    }

    /**
     * 创建缺省的配置文件
     */
    private void createDefaultConfigFile() {
        // 配置文件不存在时，加载出来的都是缺省配置，原样写入
        reloadAllPreferences();
        writeAllPreferences();
    }

    /**
     * 加载所有的配置项
     * 这里的缺省值，覆盖了xml配置文件中的缺省值
     */
    private void reloadAllPreferences() {
        updateSetting(applock_password_key, sharedPref);
        updateSetting(unlock_failed_capture_enable_key, sharedPref);
        updateSetting(unlock_failed_capture_errcount_key, sharedPref);
        updateSetting(unlock_failed_capture_location_key, sharedPref);
        updateSetting(screen_lock_mode_key, sharedPref);
        updateSetting(autolock_on_app_quit_key, sharedPref);
        updateSetting(one_password_unlock_all_key, sharedPref);
        updateSetting(alert_lock_unlock_key, sharedPref);
        updateSetting(add_tag_to_photo_key, sharedPref);
        updateSetting(locate_default_distance_key, sharedPref);
        updateSetting(locate_interval_key, sharedPref);
    }

    /**
     * 写入所有的缺省配置
     */
    private void writeAllPreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(applock_password_key, applock_password);
        editor.putBoolean(unlock_failed_capture_enable_key, unlock_failed_capture_enable);
        editor.putString(unlock_failed_capture_errcount_key, unlock_failed_capture_errcount + "");
        editor.putBoolean(unlock_failed_capture_location_key, unlock_failed_capture_location);
        editor.putString(screen_lock_mode_key, screen_lock_mode + "");
        editor.putBoolean(autolock_on_app_quit_key, autolock_on_app_quit);
        editor.putBoolean(one_password_unlock_all_key, one_password_unlock_all);
        editor.putBoolean(alert_lock_unlock_key, alert_lock_unlock);
        editor.putBoolean(add_tag_to_photo_key, add_tag_to_photo);
        editor.putString(locate_default_distance_key, locate_default_distance + "");
        editor.putString(locate_interval_key, locate_interval + "");
        editor.commit();
    }

    /**
     * 保存密码
     * @param password
     */
    public void savePassword (String password) {
        applock_password = password;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(applock_password_key, password);
        editor.commit();
    }

    /**
     * 更新配置
     * @param key 待更新的配置项键值
     * @param preferences 配置项数据
     */
    public void updateSetting (String key, SharedPreferences preferences) {
        switch (key) {
            case applock_password_key:
                applock_password = preferences.getString(applock_password_key, "");
                break;
            case unlock_failed_capture_enable_key:
                unlock_failed_capture_enable = preferences.getBoolean(unlock_failed_capture_enable_key, true);
                break;
            case unlock_failed_capture_errcount_key:
                unlock_failed_capture_errcount = Integer.parseInt(preferences.getString(unlock_failed_capture_errcount_key, "3"));
                break;
            case unlock_failed_capture_location_key:
                unlock_failed_capture_location = preferences.getBoolean(unlock_failed_capture_location_key, false);
                break;
            case screen_lock_mode_key:
                screen_lock_mode = Integer.parseInt(preferences.getString(screen_lock_mode_key, "1"));
                break;
            case autolock_on_app_quit_key:
                autolock_on_app_quit = preferences.getBoolean(autolock_on_app_quit_key, true);
                break;
            case one_password_unlock_all_key:
                one_password_unlock_all = preferences.getBoolean(one_password_unlock_all_key, false);
                break;
            case alert_lock_unlock_key:
                alert_lock_unlock = preferences.getBoolean(alert_lock_unlock_key, true);
                break;
            case add_tag_to_photo_key:
                add_tag_to_photo = preferences.getBoolean(add_tag_to_photo_key, true);
                break;
            case locate_default_distance_key:
                locate_default_distance = Integer.parseInt(preferences.getString(locate_default_distance_key, "200"));
                break;
            case locate_interval_key:
                locate_interval = Integer.parseInt(preferences.getString(locate_interval_key, "60"));
                break;
        }
    }

    /**
     * 获取安全密码
     * @return 安全密码，也即解锁密码
     */
    public String getPassword() {
        return applock_password;
    }

    /**
     * 检查是否在解锁失败时，抓拍
     * @return true 开启; false 关闭
     */
    public boolean isCaptureOnFailEnable() {
        return unlock_failed_capture_enable;
    }

    /**
     * 获取得应当抓拍时解锁失败的次数
     * @return 解锁失败次数
     */
    public int getCaptureOnFailCount () {
        return unlock_failed_capture_errcount;
    }

    /**
     * 获取抓拍图片的存储图径
     * @return true 存储的相册路径中; false 存储照片到内部
     */
    public boolean isCaptureOnFailPhotoInGallray() {
        return unlock_failed_capture_location;
    }

    /**
     * 获取锁屏时的锁定模式
     * @return 锁定模式
     */
    public int getScreenUnlockLockMode() {
        return screen_lock_mode;
    }

    /**
     * 是否在在应用退出时重新锁定
     * @return true 开启; false 关闭
     */
    public boolean isAutoLockOnAppQuit() {
        return autolock_on_app_quit;
    }

    /**
     * 是否一次解锁后，解锁所有的应用
     * @return true 开启; false 关闭
     */
    public boolean isUnlockAnyUnlockAllEnabled () {
        return one_password_unlock_all;
    }

    /**
     * 是否显示锁定信息提示
     * @return true 显示; false 不显示
     */
    public boolean isAlertLockUnlockEnabled () {
        return alert_lock_unlock;
    }

    /**
     * 是否添加拍摄标记到照片
     * @return true 开启; false 关闭
     */
    public boolean isAddTagToPhoto () {
        return add_tag_to_photo;
    }

    /**
     * 是否只允许在WIFI状态下更新
     * @return
     */
    public boolean isUpdateOnlyWhenWifiConntected () {
        updateOnlyWhenWifiConntected = true;
        return updateOnlyWhenWifiConntected;
    }

    /**
     * 获取缺省的定位偏差距离
     *
     * @return 偏差距离
     */
    public int getLocateDefaultDistance() {
        return locate_default_distance;
    }

    /**
     * 获取定位时间间隔
     *
     * @return 时间间隔
     */
    public int getLocateInterval() {
        return locate_interval;
    }
}


