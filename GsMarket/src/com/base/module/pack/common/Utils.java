package com.base.module.pack.common;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.base.module.pack.devicesparm.AbstractDeviceParm;
import com.base.module.pack.devicesparm.GAC2200Param;

public class Utils {
    public static boolean isEmpty(String s) {
        if (TextUtils.isEmpty(s)) {
            return true;
        }
        return false;
    }


    /**
     * judge obj if is empty
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return Boolean.TRUE;
        }
        if (obj instanceof String) {
            return "".equals(obj);
        }else if (obj.getClass().isArray()) {
            return ((Object [] )obj).length == 0 ? Boolean.TRUE:Boolean.FALSE;
        }else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }else if (obj instanceof Map) {
            return ((Map)obj).isEmpty();
        }else if (obj instanceof Boolean) {
            return obj.equals(Boolean.FALSE);
        }
        return true;
    }

    /**
     * judge one or more Object both are true;
     * @param objs
     * @return
     */
    public static boolean judgeObjNotNull(Object... objs) {
        for (Object obj : objs) {
            if (isEmpty(obj)) {
                return false;
            }
        }
        return true;
    }



    public static void deleteFile(String path){
        Log.i("-----", path);
        try {
            Runtime.getRuntime().exec(new String[] { "sh", "-c", " rm "+ toShellStr(path) });//.exec("chmod 777 "+(path.replaceAll(" ", "\" \"")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void chmod(String path, String rwx) {
        Log.i("-----", path);
        Log.i("----", rwx);

        try {
            Runtime.getRuntime().exec(new String[] { "sh", "-c", " chmod " + rwx +" "+ toShellStr(path) });//.exec("chmod 777 "+(path.replaceAll(" ", "\" \"")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toShellStr(String str) {
        if (str.contains("\\")) {
            str = str.replace("\\", "");
        }
        str = str.replace("~", "\\~").replace("!", "\\!").replace("#", "\\#").replace("$", "\\$").replace("^", "\\^").replace("&", "\\&").replace("*", "\\*")
                .replace("(", "\\(").replace(")", "\\)").replace("=", "\\=").replace("|", "\\|").replace("\"", "\\\"").replace("'", "\'")
                .replace(" ", "\\ ").replace("<", "\\<").replace(">", "\\>").replace("'", "\\'");
        return str;
    }

    public static boolean saveFile(InputStream is, OutputStream os) {
        boolean result = false;
        try {
            if (is != null) {
                byte[] b = new byte[1024];
                while (is.read(b) != -1) {
                    os.write(b);
                }
            }
            result = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getSpace(){
        String space=" ";
        if ("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            space = "";
        }
        return space;
    }


    public static AbstractDeviceParm getDeviceType(){

        return new GAC2200Param();
    }

    public static boolean isPrimitiveType(Class<?> cls) {
        try {
            return ((Class)cls.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
        }
        return Boolean.FALSE;
    }

    public static boolean isPrimitiveType(Object obj) {
        if(obj == null)
            return Boolean.FALSE;
        return isPrimitiveType(obj.getClass());
    }

    public static String FirstCharUpperCase(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public static boolean isPackageExit(Context context,String packageName){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return pi != null;
        }catch(Exception e){
            e.printStackTrace();
        }   

        return false;
    }
    public static String getAppVersion(Context context,String packageName){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return pi != null?pi.versionName:"";
        }catch(Exception e){
            e.printStackTrace();
        }   
        return "";
    }
    public static boolean isHasNewVesion(Context context,String packageName,String latestVersion){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if(pi==null)return false;

            return TextUtils.isEmpty(latestVersion)?false:!pi.versionName.equals(latestVersion);
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public static void openApplication(Context context,String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(launchIntentForPackage);
    }
    public static String getDownloadCountShow(int count){
        StringBuilder s = new StringBuilder();
        if(count <= 0){
            s.append("0");
        }else if(count < 100){
            s.append("100 -");
        }else if(count<100000){
            s.append(count);
            if(s.length()>3){
                s.insert(s.length()-3, ",");
            }
        }else if(count == 100000){
            s.append("100,000");
        }else if(count<100000000){
            s.append("100,000 +");
        }else if(count == 100000000){
            s.append("100,000,000");
        }else{
            s.append("100,000,000 +");
        }

        return s.toString();
    }
}