package com.queen.rxjavaretrofitdemo.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-4
 */

public class StringResponseConverterFactory extends Converter.Factory{

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if(type == String.class)
            return new StringResponseConverter();
        else
            return null;
    }

    public class StringResponseConverter implements Converter<ResponseBody, String>  {


        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }

}
