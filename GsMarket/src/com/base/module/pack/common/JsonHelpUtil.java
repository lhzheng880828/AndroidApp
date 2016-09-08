package com.base.module.pack.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.AppSoftwareInfo;
import com.base.module.pack.bean.AppType;
import com.base.module.pack.bean.GradeInfo;

/**
 * support json process util  
 * ClassName:JsonHelpUtil
 * @author   czheng
 * @version 1.0 2012-2-22
 * @since 1.0
 * @see
 */
public class JsonHelpUtil {

    private static  StringWriter sw =new StringWriter();

    private static  JsonFactory js =new JsonFactory();

    private static  JsonGenerator jg;

    private static ObjectMapper om;

    static{
        om =new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY);  
        om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    /**
     * support javaObject convert json format String 
     * @Title: JavaObjToJson
     * @param @param os
     * @param @param obj
     * @param @throws IOException    
     * @return void   
     * @throws
     * @creator     :czheng
     * @create date :2012-2-22
     * @modificator :
     * @modify date :
     * @version 1.0 
     */ 
    public static void JavaObjToJson(java.io.Writer os,Object obj) throws IOException{
        jg=js.createJsonGenerator(os);
        om.writeValue(jg, obj);
    }



    /**
     * support Json Format String convert Java Object 
     * @Title: JsonToJavaObj
     * @param @param json
     * @param @param cls
     * @param @return
     * @param @throws JsonParseException
     * @param @throws JsonMappingException
     * @param @throws IOException    
     * @return T   
     * @throws
     * @creator     :czheng
     * @create date :2012-2-22
     * @modificator :
     * @modify date :
     * @version 1.0 
     */ 
    public static <T> T JsonToJavaObj(String jsonstr, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
        return om.readValue(jsonstr, cls);
    }

    public static <T> List<T> JsonToJavaObjList(String str, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
        JavaType javaType = getCollectionType(ArrayList.class, cls); 
        return om.readValue(str, javaType);
    }

    public static <T> List<T> JsonToJavaObj(URL url, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
        JavaType javaType = getCollectionType(ArrayList.class, cls); 
        return om.readValue(url, javaType);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {   
        return om.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
    /*  {"description":"Make free Skype-to-Skype video calls, and call phones at Skype rates on the move Free voice and video calls to anyone else on Skype, whether they?re on an Android, iPhone, Mac or PC, as well as IMs to your friends and family, no matter where they are.",

        "status":"1",
        "searchWord":null,
        "biz_gradeInfo":null,
        "apptypecode":"app",
        "sysid":142,
        "appname":"skype",
        "iconurl":"http://market.ipvideotalk.com:9023/filestorage//resources/software/skype/res/drawable-mdpi/skype_blue.png",
        "developerid":1,
        "downloadCount":8079,
        "gradenum":1.4545455,
        "appcode":"skype",
        "thumbnailurl":"",
        "ischarge":2,
        "createtime":"2014-02-24 00:50:13",
        "creator":"osadmin",
        "updatetime":null,
        "updator":null,
        "biz_developerInfo":null,

        "biz_appType":{"status":"1","createtime":"2011-12-31 09:37:11","creator":"osadmin","updatetime":"2012-08-23 23:11:40",
        "updator":"osadmin","code":"app","name_cn":"??","name_en":"Apps"},

        "biz_appVersionInfo":[{"size":"16.42","version":"4.6.0.42007","packagename":"com.skype.raider",
        "downloadurl":"http://market.ipvideotalk.com:9023/filestorage//resources/software/skype/skype_4.6.0.42007.apk?packagename=com.skype.raider&appname=skype",
        "sysid":213,"appname":null,"appcode":null,"appid":142,"versioncode":67544087,"publishdata":"2014-02-24","updatedetail":""}],

        "biz_appDownloadInfo":null},*/

    /*public static void JavaObjToJson(java.io.Writer os,Object obj) throws IOException{
        jg=js.createJsonGenerator(os);
        om.writeValue(jg, obj);
    }*/

    public static String JavaObjToJson(Object obj){
        String json="";
        try {
            json =  om.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static List<AppSoftwareInfo> JsonToJavaObj(String jsonstr){
        List<AppSoftwareInfo> list = new ArrayList<AppSoftwareInfo>();
        if(TextUtils.isEmpty(jsonstr)){return list;}
        try {
            JSONArray array = new JSONArray(jsonstr);
            for (int i = 0; i < array.length(); i++) {
                List<ApkInfo> apkInfoList = new ArrayList<ApkInfo>();
                AppSoftwareInfo info = new AppSoftwareInfo();
                JSONObject obg =array.getJSONObject(i);
                // JSONObject obg = new JSONObject(jsonstr);
                info.setAppcode(obg.getString("appcode"));
                //info.setAppDownloadInfo();
                info.setAppname(obg.getString("appname"));
                // info.setAppType();
                info.setApptypecode(obg.getString("apptypecode"));
                info.setCreatetime(obg.getString("createtime"));
                info.setCreator(obg.getString("creator"));
                info.setDescription(obg.getString("description"));
                info.setDeveloperid(obg.getInt("developerid"));
                info.setGradenum(obg.getLong("gradenum"));
                info.setIconurl(obg.getString("iconurl"));
                info.setIscharge(obg.getInt("ischarge"));
                info.setSearchWord(obg.getString("searchWord"));
                info.setStatus(obg.getString("status"));
                info.setThumbnailurl(obg.getString("thumbnailurl"));
                info.setUpdatetime(obg.getString("createtime"));
                info.setUpdator(obg.getString("updator"));
                info.setSysid(obg.getInt("sysid"));
                info.setDownloadCount(obg.getInt("downloadCount"));
                if(obg.get("biz_appVersionInfo")!=null){
                    JSONArray appVersionArray = obg.getJSONArray("biz_appVersionInfo");
                    int appVersionLength = appVersionArray.length();
                    for (int j = 0; j < appVersionLength; j++) {
                        JSONObject obgType = appVersionArray.getJSONObject(j);
                        ApkInfo appInfo = new ApkInfo();

                        if(j==0 && appVersionLength>1){
                            info.setLatestVersion(obgType.getString("version"));
                        }else if(j==0){
                            info.setLatestVersion("");
                        }
                        appInfo.setPackageName(obgType.getString("packagename"));
                        appInfo.setSize(obgType.getString("size"));
                        appInfo.setVersion(obgType.getString("version"));
                        appInfo.setVersioncode(obgType.getInt("versioncode"));
                        appInfo.setDownloadUrl(obgType.getString("downloadurl"));
                        appInfo.setSysid(obgType.getInt("sysid"));
                        appInfo.setAppid(obgType.getInt("appid"));
                        appInfo.setUpdatedetail(obgType.getString("updatedetail"));
                        appInfo.setPublishdata(obgType.getString("publishdata"));
                        apkInfoList.add(appInfo);
                    }
                }
                if(obg.get("biz_appType")!=null){
                    JSONObject obgType = new JSONObject(obg.get("biz_appType").toString());
                    AppType versionInfo = new AppType();
                    versionInfo.setCode(obgType.getString("code"));
                    versionInfo.setCreatetime(obgType.getString("createtime"));
                    versionInfo.setCreator(obgType.getString("creator"));
                    versionInfo.setName_cn(obgType.getString("name_cn"));
                    versionInfo.setName_en(obgType.getString("name_en"));
                    versionInfo.setStatus(obgType.getString("status"));
                    versionInfo.setUpdatetime(obgType.getString("updatetime"));
                    versionInfo.setUpdator(obgType.getString("updator"));
                }
                info.setApkInfo(apkInfoList);
                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * comment
     * @param jsonstr
     * @return
     */
    public static List<GradeInfo> JsonCommentToJava(String jsonstr){
        List<GradeInfo> list = new ArrayList<GradeInfo>();
        try {
            JSONObject obg = new JSONObject(jsonstr);
            String commentJson =  obg.getString("data");
            JSONArray array = new JSONArray(commentJson);
            for (int i = 0; i < array.length(); i++) {
                GradeInfo info = new GradeInfo();
                JSONObject obgcomment = array.getJSONObject(i);
                info.setComment(obgcomment.getString("comment"));
                info.setGradenum(obgcomment.getInt("gradenum"));
                info.setGradetime(obgcomment.getString("gradetime"));
                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String doDownloadFromHttpServer(final String urlStr){
        String result=null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            if(urlConnection.getResponseCode()!=200){
                return "error";
            }
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bf = new BufferedReader(reader);

            String tmp;
            result = "";
            while ((tmp=bf.readLine())!=null) {
                result+=tmp;
            }
        } catch (Exception e) {
            result="error";
            e.printStackTrace();
        }
        return result;
    }
}