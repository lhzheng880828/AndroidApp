package com.base.module.pack.method;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import com.base.module.pack.common.Log;
public class PackMethod {
    public static final String BROWSER_DATACHANGED_ACTION = "com.base.module.pack.action.browser_data_broadcast";

    public static final String REFRESH_ACTION = "com.base.module.pack.action.update_broadcast";
    public static final String ICONDOWN_RETURN_ACTION = "com.base.module.pack.action.icon_down_return_broadcast";
    public static final String INSTALL_SUCCESS_ACTION = "com.base.module.pack.action.install_success_broadcast";
    private static final String TAG = "PackMethod";
    public static final String DOWN_ACTION = "com.base.module.pack.intent.action.DOWN_ACTION";
    public static final String PACKAGENAME = "PACKAGENAME";
    private static PackDaoInter mPackDao;

    public final static String APK_INFO="apkinfo";

    private static void initDao(Context context) {
        if (mPackDao == null){
            mPackDao = PackDao.getInstance(context);
        }
        else if (!mPackDao.isOpen()){
            mPackDao.open();
        }
    }

    /**
     * send broader to update the view
     * 
     *
     */
    public static void sendRefreshBroader(Context context) {
        Log.d("sendRefreshBroader");
        Intent intent = new Intent();
        intent.setAction(REFRESH_ACTION);
        context.sendBroadcast(intent);
    }

    public static void sendBrowserDataChangedBroader(Context context,String packagename) {
        Log.d("sendBrowserDataChangedBroader");
        Intent intent = new Intent();
        intent.setAction(BROWSER_DATACHANGED_ACTION);
        intent.putExtra(PACKAGENAME, packagename);
        context.sendStickyBroadcast(intent);
    }

    public static void sendUpdateBroader(Context context, String uri,ApkInfo apkInfo) {
        Intent intent = new Intent();
        intent.setAction(DOWN_ACTION);
        intent.putExtra("uri", uri);
        intent.putExtra("toUpdate", true);
        if(apkInfo!=null){
            intent.putExtra(APK_INFO, apkInfo);
        }
        context.sendBroadcast(intent);
    }

    public static void sendDownloadBroader(Context context, String uri,String iconUrl,ApkInfo apkinfo) {
        Log.d("sendDownloadBroader");
        Intent intent = new Intent();
        intent.setAction(DOWN_ACTION);
        intent.putExtra("uri", uri);
        intent.putExtra("iconUrl", iconUrl);
        if (apkinfo != null) {
            intent.putExtra(APK_INFO, apkinfo);
        }
        context.sendBroadcast(intent);
    }

    public static void sendDownloadBroader(Context context, String uri,String iconUrl,ApkInfo apkinfo,long fileSize) {
        Log.d("sendDownloadBroader");
        Intent intent = new Intent();
        intent.setAction(DOWN_ACTION);
        intent.putExtra("uri", uri);
        intent.putExtra("iconUrl", iconUrl);
        intent.putExtra("fileSize", fileSize);
        if (apkinfo != null) {
            intent.putExtra(APK_INFO, apkinfo);
        }
        context.sendBroadcast(intent);
    }

    public static synchronized void sendInstallSuccessBroader(Context context, String l_name, String app_name, String downType, String uri,String appcode) {
        Log.d();
        Intent intent = new Intent();
        intent.setAction(INSTALL_SUCCESS_ACTION);
        intent.putExtra("app_name", app_name);
        intent.putExtra("down_type", downType);
        intent.putExtra("pack_name", l_name);
        //intent.putExtra("class_name", class_name);
        intent.putExtra("uri", uri);
        intent.putExtra("appcode", appcode);
        context.sendBroadcast(intent);
    }

    public static synchronized void sendIconDownReturnBroader(Context context, String l_name, int result) {
        Intent intent = new Intent();		
        intent.setAction(ICONDOWN_RETURN_ACTION);
        intent.putExtra("pack_name", l_name);
        intent.putExtra("result", result);
        context.sendBroadcast(intent);
    }

    public static void update(Context context, String l_name, String uri,ApkInfo apkInfo) {
        initDao(context);
        mPackDao.setInUpdate(l_name);
        sendUpdateBroader(context, uri,apkInfo);
        sendRefreshBroader(context);
    }

    public static void uninstall(Context context, String l_name) {
        Uri packageURI = Uri.parse("package:" + l_name);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(uninstallIntent);
    }

    public static void deleteRecord(Context context, Pack pack) {
        File file = new File(pack.getPackNationRoute(), pack.getPackSerial());
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        initDao(context);
        mPackDao.deleteRecord(pack.getPackLName());
    }

    public static void openApllication(Context context,String packLname) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage(packLname);
        context.startActivity(intent);
    }


    public static void packFail(Context context, String l_name) {
        Log.d(l_name);
        initDao(context);
        mPackDao.failDownloadByLname(l_name);
        sendRefreshBroader(context);
    }	

    public static String ShowSize(long length) {
        if (0 > length && length < 1024)
            return length + "byte";
        else if (length < (1024 * 1024))
            return new java.text.DecimalFormat("0.00").format((float) length / 1024) + " K";
        else if (length < (1024 * 1024 * 1024))
            return new java.text.DecimalFormat("0.00").format((float) length / (1024 * 1024)) + " M";
        else if (length > 0)
            return new java.text.DecimalFormat("0.00").format((float) length / (1024 * 1024 * 1024)) + " G";
        return "0 k";
    }

    /*
     * setPreference
     */
    public static void setPreference(Context context, String key, String words) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = mSharedPreferences.edit();
        edit.putString(key, words);
        edit.commit();
    }

    /*
     * initPreference
     */
    public static void initPreference(Context context, String key) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = mSharedPreferences.edit();
        edit.putString(key, "");
        edit.remove(key);
        edit.commit();
    }

    /*
     * clearPreference
     */
    public static void clearPreference(Context context, String key) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = mSharedPreferences.edit();
        edit.remove(key);
        edit.commit();
    }

    /*
     * clearPreference
     */
    public static void clearAllPreference(Context context) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = mSharedPreferences.edit();
        edit.clear();
        edit.commit();
    }

    public static boolean isValid(String urlString,boolean isGetVersionUrl) {
        com.base.module.pack.common.Log.d("urlString" + urlString);

        boolean result = false;

        if (urlString != null && ((!isGetVersionUrl && urlString.matches("^https?://.+?\\.apk.*$")) || (isGetVersionUrl && urlString.matches("^https?://.+$")))) {
            URL url;
            try {
                url = new URL(urlString);
                HttpURLConnection connt = (HttpURLConnection) url.openConnection();
                connt.setRequestMethod("HEAD");
                String strMessage = connt.getResponseMessage();
                if (strMessage.compareTo("Not Found") != 0) {
                    result = true;
                }
                connt.disconnect();

            } catch (Exception e) {

                Log.e(TAG, "e...cant url." + e.toString());

            }
        }
        return result;
    }

    /**
     * judge that the net is available
     * @param context
     * @return
     */
    public static boolean isHaveInternet(final Context context) {
        boolean result=false;
        try {
            ConnectivityManager manger = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manger.getActiveNetworkInfo();

            result=(info!=null && info.isConnected());

            if(!result){
                WifiManager wifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if(wifiManager.getWifiState()== WifiManager.WIFI_STATE_ENABLED){
                    result=true;
                }
            }


        } catch (Exception e) {


        }
        Log.i(TAG, "isHaveInternet:"+String.valueOf(result));
        return result;


    }

    public static String getUrlParameter(String s,String parameter){

        return s.replaceFirst("^.+\\?.*?"+parameter+"=([^&]*).*$", "$1");
    }

    /**
     * get the space with different local language
     * @return
     */
    public static String getSpace(){
        String space=" ";
        if ("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            space = "";
        }
        return space;
    }
}