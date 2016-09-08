package com.eeontheway.android.applocker.main;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.lock.PasswordSetActivity;

/**
 * 应用锁配置界面Fragment
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class SettingsFragment extends PreferenceFragment {
    private Activity parentActivity;
    private ListPreferenceChangeListener listPreferenceChangeListener;
    private PreferenceChangedListener preferenceChangedListener;

    /**
     * PreferenceFragment的onCreate回调
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_applocker_config);

        parentActivity = getActivity();
        initPrefrences();
    }


    /**
     * PreferenceFragment的onResume回调
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(preferenceChangedListener);
    }

    /**
     * PreferenceFragment的onPause回调
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(preferenceChangedListener);
    }

    /**
     * 配置某些特定的Preference
     */
    private void initPrefrences() {
        // 初始化各Preference的监听器
        preferenceChangedListener = new PreferenceChangedListener();
        listPreferenceChangeListener = new ListPreferenceChangeListener();

        // 配置解锁密码失败次数的summary，以便能显示出当前的选择
        ListPreference listPreference = (ListPreference) findPreference(SettingsManager.unlock_failed_capture_errcount_key);
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(listPreferenceChangeListener);

        // 配置锁定模式的summary，以便能显示出当前的选择
        listPreference = (ListPreference) findPreference(SettingsManager.screen_lock_mode_key);
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(listPreferenceChangeListener);

        // 配置GPS定位时间间隔的summary，以便能显示出当前选择
        listPreference = (ListPreference) findPreference(SettingsManager.locate_interval_key);
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(listPreferenceChangeListener);

        // 配置GPS定位偏差的summary，以便能显示出当前选择
        listPreference = (ListPreference) findPreference(SettingsManager.locate_default_distance_key);
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(listPreferenceChangeListener);
    }

    /**
     * 任何项目被点击时的处理
     *
     * @param preferenceScreen
     * @param preference
     * @return
     */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(SettingsManager.applock_password_key)) {
            PasswordSetActivity.statActivity(parentActivity, MainActivity.REQUEST_SET_PASS);
            return true;
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    /**
     * Preference配置发生变化的监听器
     */
    class PreferenceChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // 一旦监听到任何配置发生变化，通知管理器重新加载配置
            SettingsManager asm = SettingsManager.getInstance(parentActivity);
            asm.updateSetting(key, sharedPreferences);
        }
    }

    /**
     * ListPreference监听器
     * 功能为监听变化的值，然后将新值对应的字符串以Summary的形式显示在界面上，方便查看
     */
    class ListPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ListPreference listPreference = (ListPreference)preference;

            // 扫描值列表，找出新值对应的索引
            int index = 0;
            CharSequence[] values = listPreference.getEntryValues();
            for (CharSequence value : values) {
                if (TextUtils.equals(value, (String) newValue)) {
                    break;
                }

                index++;
            }

            // 然后取出值对应的字符串，显示为Summary
            CharSequence[] entries = ((ListPreference) preference).getEntries();
            preference.setSummary(entries[index]);
            return true;
        }
    }
}
