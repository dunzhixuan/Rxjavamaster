package com.zhixuan.app.rxjava_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //在Android 环境下可以正常输出Log
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(
                        new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                // ①
                                Log.e(TAG,aLong + "");
                            }
                        });
    }
}
