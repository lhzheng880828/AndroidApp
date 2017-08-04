package com.queen.rxjavaretrofitdemo.module;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-3
 */
@AutoValue
public abstract class Cast implements Parcelable {

    abstract String id();

    @Nullable
    abstract String name();

    @Nullable
    abstract String alt();

    abstract Avatars avatars();

    public static JsonAdapter<Cast> jsonAdapter(Moshi moshi) {
        return new AutoValue_Cast.MoshiJsonAdapter(moshi);
    }

    public static Builder builder() {
        return new AutoValue_Cast.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);
        public abstract Builder name(String name);
        public abstract Builder alt(String alt);
        public abstract Builder avatars(Avatars avatars);
        public abstract Cast build();
    }
}
