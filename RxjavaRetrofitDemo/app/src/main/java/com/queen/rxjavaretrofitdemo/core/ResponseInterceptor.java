package com.queen.rxjavaretrofitdemo.core;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-4
 */

public class ResponseInterceptor implements Interceptor {

    private static final String TAG = ResponseInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {

        Response response = chain.proceed(chain.request());

        //可以对HTTP返回的数据做预处理，方便后续转化
       ResponseBody responseBody =  response.body();
        Log.d(TAG,"body is "+responseBody.string());
        return response;
    }
}
