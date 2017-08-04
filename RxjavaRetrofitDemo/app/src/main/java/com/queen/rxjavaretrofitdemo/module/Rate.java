package com.queen.rxjavaretrofitdemo.module;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-4
 */
@AutoValue
public abstract class Rate implements Parcelable {

    /*private int max;
    private int average;
    private String stars;
    private int min;*/


    abstract int max();
    abstract float average();
    abstract String stars();
    abstract int min();

    public static JsonAdapter<Rate> jsonAdapter(Moshi moshi) {
        return new AutoValue_Rate.MoshiJsonAdapter(moshi);
        //return null;
    }

    public static Builder builder() {
        return new AutoValue_Rate.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder max(int max);
        public abstract Builder average(float average);
        public abstract Builder stars(String stars);
        public abstract Builder min(int min);
        public abstract Rate build();
    }

}
