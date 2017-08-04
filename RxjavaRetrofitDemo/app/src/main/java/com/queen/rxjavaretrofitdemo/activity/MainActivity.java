package com.queen.rxjavaretrofitdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.queen.rxjavaretrofitdemo.R;
import com.queen.rxjavaretrofitdemo.core.ServiceGenerator;
import com.queen.rxjavaretrofitdemo.module.HttpResult;
import com.queen.rxjavaretrofitdemo.module.Subject;
import com.queen.rxjavaretrofitdemo.http.MovieService;
import com.queen.rxjavaretrofitdemo.subscribers.SubscriberOnNextListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.click_me_BN)
    Button clickMeBN;
    @Bind(R.id.result_TV)
    TextView resultTV;

    private SubscriberOnNextListener getTopMovieOnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getTopMovieOnNext = new SubscriberOnNextListener<List<Subject>>() {
            @Override
            public void onNext(List<Subject> subjects) {
                resultTV.setText(subjects.toString());
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.click_me_BN)
    public void onClick() {
        getMovie();
    }

    private void getMovies(){

        ServiceGenerator.createService(this, MovieService.class)
                .getTopMovieTop(0,10)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                //.compose(this.bindToLifecycle())
                .subscribe(new Consumer<Response<HttpResult>>() {
                    @Override
                    public void accept(Response<HttpResult> httpResultResponse) throws Exception {
                        Log.d(TAG, "httpResultResponse is " + httpResultResponse.toString());

                        HttpResult result = httpResultResponse.body();

                        Log.d(TAG,"result is "+result.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "throwable is " + throwable);
                    }
                });
                /*.subscribe(response -> {
                    HttpResult<List<com.queen.rxjavaretrofitdemo.module.Subject>> httpResult = response.body();
                    com.queen.rxjavaretrofitdemo.module.Subject subject = (com.queen.rxjavaretrofitdemo.module.Subject)httpResult.getSubjects();
                    Log.d(TAG,"sub is "+subject);
                    //Log.d(TAG,"response is "+response.b);
                    //configurePager();
                }, e -> {
                    //ToastUtils.show(this, R.string.error_person_load);
                    //loadingBar.setVisibility(View.GONE);
                    Log.d(TAG,"error!");

                });*/
    }

    //进行网络请求
    private void getMovie(){
        getMovies();
        //HttpMethods.getInstance().getTopMovie(new ProgressSubscriber(getTopMovieOnNext, MainActivity.this), 0, 10);
    }
}
