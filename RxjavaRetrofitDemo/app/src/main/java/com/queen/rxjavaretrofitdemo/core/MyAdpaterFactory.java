package com.queen.rxjavaretrofitdemo.core;

import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;
import com.squareup.moshi.JsonAdapter;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-4
 */

@MoshiAdapterFactory(nullSafe = true)
public abstract class MyAdpaterFactory implements JsonAdapter.Factory{
    public static JsonAdapter.Factory create() {
        return new AutoValueMoshi_MyAdpaterFactory();
    }
}
