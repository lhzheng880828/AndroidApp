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
public abstract class Avatars implements Parcelable{

    abstract String small();

    @Nullable
    abstract String medium();
    abstract String large();

    public static JsonAdapter<Avatars> jsonAdapter(Moshi moshi) {
        return new AutoValue_Avatars.MoshiJsonAdapter(moshi);
    }
    public static Builder builder() {
        return new AutoValue_Avatars.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder small(String small);

        public abstract Builder medium(String medium);

        public abstract Builder large(String large);

        public abstract Avatars build();
    }

}
