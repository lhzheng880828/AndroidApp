/**
 * Author:yyzhu
 * Copyright:GrandStream
 */
package com.base.module.pack.common;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";

    /**
     * request with request method post
     * 
     * @param path
     * @throws IOException
     * @throws Exception
     */

    public static ResposeInfo requestServer(String path, String json){
        DataOutputStream out = null;
        HttpURLConnection con = null;
        ResposeInfo info = null;
        try {

            URL url = new URL(path);
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(METHOD_POST);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);

            out = new DataOutputStream(con.getOutputStream());
            if (json != null ){
                out.write(json.getBytes("utf-8"));
            }
            /*if (responseCode == 200) {
                InputStream inputStream = (InputStream) con.getInputStream();
               // changeInputStream(inputStream, "utf-8");
            }*/
            out.flush();
            out.close();
            info = new ResposeInfo(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }
    /* public static String changeInputStream(InputStream inputStream,
            String encode) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {

            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);

                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Log.i("post comment result   "+result);
        return result;
    }*/
    public static class ResposeInfo {
        //private InputStream mInputStream;
        // private int mContentLength;
        private int mResponseCode;

        public ResposeInfo(int responseCode) {
            super();
            //  this.mInputStream = inputStream;
            //  this.mContentLength = contentLength;
            this.mResponseCode = responseCode;

        }
        public int getResponseCode() {
            return mResponseCode;
        }
    }
}
