package com.queen.rxjavaretrofitdemo.http;

//import com.queen.rxjavaretrofitdemo.entity.HttpResult;

import com.queen.rxjavaretrofitdemo.module.HttpResult;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liukun on 16/3/9.
 */
public interface MovieService {

//    @GET("top250")
//    Call<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);

//    @GET("top250")
//    Observable<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);

//    @GET("top250")
//    Observable<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);

    //@GET("top250")
    //Observable<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);

    @GET("/v2/movie/top250")
    Single<Response<HttpResult>> getTopMovieTop(@Query("start") int start, @Query("count") int count);
}
