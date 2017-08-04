package com.queen.rxjavaretrofitdemo.module;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:17-8-3
 */
@AutoValue
public abstract class Subject implements Parcelable {


    public static Builder builder() {
        return new AutoValue_Subject.Builder();
    }

    public static JsonAdapter<Subject> jsonAdapter(Moshi moshi) {
        return new AutoValue_Subject.MoshiJsonAdapter(moshi);
        //return null;
    }

    abstract Rate rating();


    abstract String id();

    @Nullable
    abstract String alt();

    @Nullable
    abstract String year();

    @Nullable
    abstract String title();

    @Json(name = "original_title")
    abstract String originalTitle();

    abstract List<String> genres();

    abstract List<Cast> casts();

    abstract List<Cast> directors();

    abstract Avatars images();

    abstract String subtype();

    @Json(name = "collect_count")
    abstract int collectCount();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder rating(Rate rating);

        public abstract Builder id(String id);

        public abstract Builder subtype(String subtype);

        public abstract Builder collectCount(int collectCount);

        public abstract Builder alt(String alt);

        public abstract Builder year(String year);

        public abstract Builder title(String title);

        public abstract Builder originalTitle(String originalTitle);

        public abstract Builder genres(List<String> genres);

        public abstract Builder casts(List<Cast> casts);

        public abstract Builder directors(List<Cast> directors);

        public abstract Builder images(Avatars images);

        public abstract Subject build();
    }
}
