package com.queen.rxjavaretrofitdemo.core;

import android.content.Context;

import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-4
 */

public class ServiceGenerator {

    public final static Moshi moshi = new Moshi.Builder()
            //.add()
            .add(MyAdpaterFactory.create())
            .build();


    private final static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);


    private final static Retrofit.Builder builder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(new StringResponseConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi));

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(new ResponseInterceptor())
            .build();


    public static <S> S createService(final Context context, Class<S> serviceClass) {
        OkHttpClient client = httpClient.newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        //对请求的做预处理，方便后续请求参数处理
                        Request original = chain.request();

                        /*String[] headers = {
                                "application/vnd.github.html+json",
                                "application/vnd.github.raw+json"
                        };

                        String token = TokenStore.getInstance(context).getToken();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Token " + token)
                                .method(original.method(), original.body());

                        if (original.header("Accept") == null) {
                            requestBuilder.addHeader("Accept", TextUtils.join(",", headers));
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);*/
                        return chain.proceed(original);
                    }
                }).build();

        Retrofit retrofit = builder.baseUrl("http://api.douban.com")
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }

}
