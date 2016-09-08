package com.base.module.pack.bean;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.util.Log;
/**
 * COpyright:Grandstreram
 */
public class HttpNetUtils {

    public static final String CHARACTER_ENCODING = "UTF-8";     
    public static final String PATH_SIGN = "/";     
    public static final String METHOD_POST = "POST";     
    public static final String METHOD_GET = "GET";     
    public static final String CONTENT_TYPE = "Content-Type"; 

    private static class MytmArray implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub

            return new X509Certificate[]{};
        }
    }


    private static TrustManager[] xtmArray = new MytmArray[] { new MytmArray() };

    /**
     * 
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, xtmArray, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);//

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //private static X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

    private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            // System.out.println("Warning: URL Host: " + hostname + " vs. "
            // + session.getPeerHost());
            //Log.i("step", "9");
            return true;
        }
    };

    public static String request(String urlString, String requestData,     
            String ecoding,String requestMethod) throws IOException{ 
        Log.i("URL", urlString);
        // Log.i("requestData", requestData);
        return request(urlString,requestData,null,ecoding,requestMethod);
    }


    /**   
     * request with request method post  
     *    
     * @param urlString   
     * @param requestData   
     * @param requestProperties   
     * @return return response string   
     * @throws IOException 
     * @throws Exception   
     */  

    public static String request(String urlString, String requestData,     
            Properties requestProperties,String ecoding,String requestMethod) throws IOException{  

        if(ecoding==null||"".equals(ecoding)){
            ecoding=CHARACTER_ENCODING;
        }
        String responseData = "";     

        HttpURLConnection con = null;     

        try {     

            URL url = new URL(urlString); 

            if (url.getProtocol().toLowerCase().equals("https")) { 
                trustAllHosts();
                con = (HttpsURLConnection) url.openConnection();
                //((HttpsURLConnection) con).setHostnameVerifier(DO_NOT_VERIFY);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            if ((requestProperties != null) && (requestProperties.size() > 0)) {

                for (Map.Entry<Object, Object> entry : requestProperties     
                        .entrySet()) {     
                    String key = String.valueOf(entry.getKey());     
                    String value = String.valueOf(entry.getValue());     
                    con.setRequestProperty(key, value);     
                }     
            }     

            con.setRequestMethod(requestMethod); // 
            con.setConnectTimeout(20000);
            con.setReadTimeout(50000);            
            con.setDoInput(true); //
            if ("post".equals(requestMethod.toLowerCase())) {
                con.setDoOutput(true); //
            }

            if (requestData != null) {     
                DataOutputStream dos = new DataOutputStream(con     
                        .getOutputStream());     
                dos.write(requestData.getBytes(ecoding));     
                dos.flush();     
                dos.close();     
            }  

            BufferedReader br=null;
            Log.i("ResponseCode", String.valueOf(con.getResponseCode()));
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {  

                String chunked=con.getHeaderField("Transfer-Encoding");
                if(chunked!=null){
                    Log.i("chunked", chunked);
                }
                int length = con.getContentLength();  
                Log.i("length", String.valueOf(length));
                if (length != -1||(chunked!=null&&"chunked".equalsIgnoreCase(chunked))) { 	            	
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(),ecoding));     	
                    String temp=null;
                    while((temp=br.readLine())!=null){
                        responseData+=(temp+"\n");
                    }

                } 
            }else{
                if(con.getErrorStream()==null){
                    Log.e("error", "ErrorStream null");
                }else{
                    br= new BufferedReader(new InputStreamReader(con.getErrorStream(),ecoding));     		              
                    String error="";
                    String temp=null;
                    while((temp=br.readLine())!=null){
                        error+=(temp+"\n");
                    }
                    Log.e("error", error);
                    br.close();	    
                }
            }            	              

        }finally {     
            if (con != null) {     
                con.disconnect();     
                con = null;     
            }     
        }     
        if(responseData!=null){
            Log.i("responseData", responseData);
        }
        return responseData;
    }   
}