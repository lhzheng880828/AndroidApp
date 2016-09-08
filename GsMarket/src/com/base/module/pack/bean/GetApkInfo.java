package com.base.module.pack.bean;
/**
 * COpyright:Grandstreram
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.text.TextUtils;

import com.base.module.pack.main.ApkListFragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.base.module.pack.common.Log;

public class GetApkInfo {
    private final static String TAG="GetApkInfo";
    private final static String GET_APK_INFO_URL= ApkListFragment.MARKET_SERVERURL+"/getapkinfobycodeandversion";
    //private final static String GET_APK_INFO_URL="http://market.ipvideotalk.com:9023/marketinterface/getapkinfobycodeandversion";
    private final static String GET_NEW_VERSION_APK_INFO= ApkListFragment.MARKET_SERVERURL+"/getapkinfobycode";
    //private final static String GET_NEW_VERSION_APK_INFO="http://market.ipvideotalk.com:9023/marketinterface/getapkinfobycode";

    public static ApkInfo getApkInfo(String downloadUrl){
        Log.i(TAG, "downloadUrl:"+downloadUrl);

        String urltemp=URLDecoder.decode(downloadUrl);
        String s = urltemp.replaceFirst("^.+/([^\\?,/]+)\\.apk.+$", "$1");
        if (s.contains("_")) {
            try {
                String appcode = URLEncoder.encode(s.replaceFirst("^(.+)_[^_]+$", "$1"),"UTF-8");
                String version = URLEncoder.encode(s.replaceFirst("^.+_([^_]+)$", "$1"),"UTF-8");
                return getApkInfo(GET_APK_INFO_URL+"?appcode="+ appcode +"&version="+version,downloadUrl);
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }

        }

        return null;
    }

    /*{"size":"13.89","version":"10.0","packagename":"org.mozilla.firefox","appname":"firefox","appcode":"firefox"}*/

    private static ApkInfo getApkInfo(String url,String downloadUrl){
        ApkInfo apkInfo = null;
        Log.i(TAG, "url:"+url);
        try {
            String resposeString=HttpNetUtils.request(url, null, "UTF-8","GET");
            Log.d("reposeString :"+resposeString);
            if(!TextUtils.isEmpty(resposeString)){
                JSONObject jo=new JSONObject(resposeString);
                if(jo!=null){
                    apkInfo=new ApkInfo();
                    if(downloadUrl==null){
                        downloadUrl=jo.optString("downloadurl", "");
                        if(TextUtils.isEmpty(downloadUrl)){
                            return null;
                        }
                    }
                    apkInfo.setDownloadUrl(downloadUrl);

                    String appname=jo.optString("appname", "");
                    if(!TextUtils.isEmpty(appname)){
                        apkInfo.setAppname(appname);
                    }else{
                        return null;
                    }
                    String appcode=jo.optString("appcode", "");
                    if(!TextUtils.isEmpty(appcode)){
                        apkInfo.setAppcode(appcode);
                    }else{
                        return null;
                    }

                    String packageName=jo.optString("packagename", "");
                    if(!TextUtils.isEmpty(packageName)){
                        apkInfo.setPackageName(packageName);
                    }else{
                        return null;
                    }

                    String version=jo.optString("version", "");
                    if(!TextUtils.isEmpty(version)){
                        apkInfo.setVersion(version);
                    }else{
                        return null;
                    }

                    String fileSize = jo.optString("size", "");
                    if(!TextUtils.isEmpty(fileSize)){
                        long packSize  = Long.parseLong(String.valueOf((long)(Float.parseFloat(fileSize) * 1024.0f * 1024.0f)).replaceFirst("([^\\.]*)\\..*", "$1"));
                        apkInfo.setFilesize(packSize);
                    }else{
                        return null;
                    }

                    apkInfo.setDownloadUrl(downloadUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch(NumberFormatException e){
            e.printStackTrace();
        }

        return apkInfo;
    }

    public static ApkInfo getNewVersionApkInfo(String appcode) {
        try {
            appcode = URLEncoder.encode(appcode,"UTF-8");
            String url=GET_NEW_VERSION_APK_INFO + "?appcode="+appcode;
            return getApkInfo(url,null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }
}