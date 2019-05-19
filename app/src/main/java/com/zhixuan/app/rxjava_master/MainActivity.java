package com.zhixuan.app.rxjava_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private static String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 在Android 环境下可以正常输出Log
    //        Observable.interval(1, TimeUnit.SECONDS)
    //                .subscribe(
    //                        new Action1<Long>() {
    //                            @Override
    //                            public void call(Long aLong) {
    //                                // ①
    //                                Log.e(TAG,aLong + "");
    //                            }
    //                        });

      //背
//    Observable.interval(1, TimeUnit.MILLISECONDS)
//        //         .subscribeOn(Schedulers.newThread())
//        // 将观察者的工作放在新线程环境中
//                .observeOn(Schedulers.newThread())
//        // 观察者处理每1000ms才处理一个事件
//        .subscribe(
//            new Action1<Long>() {
//              @Override
//              public void call(Long aLong) {
//                try {
//                  Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.w("TAG", "---->" + aLong);
//              }
//            });

            Observable.interval(1, TimeUnit.MILLISECONDS)
                    .onBackpressureDrop()
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<Long>() {

                        @Override
                        public void onStart() {
                            Log.w("TAG","start");
                            request(1);
                        }

                        @Override
                        public void onCompleted() {

                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.e("ERROR",e.toString());
                        }

                        @Override
                        public void onNext(Long aLong) {
                            Log.w("TAG","---->"+aLong);
                            try {
                                Thread.sleep(100);
                                request(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            //注：之所以出现0-15这样连贯的数据，就是是因为observeOn操作符内部有一个长度为16的缓存区，它会首先请求16个事件缓存起来....
  }
}
