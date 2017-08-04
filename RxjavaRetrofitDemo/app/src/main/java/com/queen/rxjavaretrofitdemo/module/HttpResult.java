package com.queen.rxjavaretrofitdemo.module;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-4
 */
@AutoValue
public abstract class HttpResult implements Parcelable {

    /*private int count;
    private int start;
    private int total;
    private String title;

    //用来模仿Data
    private List<Subject> subjects;*/

    public abstract int count();

    public abstract int start();

    public abstract int total();

    public abstract String title();

    public abstract List<Subject> subjects();

    public static JsonAdapter<HttpResult> jsonAdapter(Moshi moshi) {
        return new AutoValue_HttpResult.MoshiJsonAdapter(moshi);
        //return null;
    }

    public static Builder builder() {
        return new AutoValue_HttpResult.Builder();
        //return null;
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder count(int count);
        public abstract Builder start(int start);
        public abstract Builder total(int total);
        public abstract Builder title(String title);
        public abstract Builder subjects(List<Subject> subjects);
        public abstract HttpResult build();
    }

}
