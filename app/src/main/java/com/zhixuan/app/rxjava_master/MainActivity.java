package com.zhixuan.app.rxjava_master;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private static String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
      if (!isTaskRoot()) {
        finish();
        return;
      }
    findViewById(R.id.txv).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }
    });
//      processUrlIpByOneFlatMap();
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

//            Observable.interval(1, TimeUnit.MILLISECONDS)
//                    .onBackpressureDrop()
//                    .observeOn(Schedulers.newThread())
//                    .subscribe(new Subscriber<Long>() {
//
//                        @Override
//                        public void onStart() {
//                            Log.w("TAG","start");
////                            request(1);
//                        }
//
//                        @Override
//                        public void onCompleted() {
//
//                        }
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.e("ERROR",e.toString());
//                        }
//
//                        @Override
//                        public void onNext(Long aLong) {
//                            Log.w("TAG","---->"+aLong);
//                            try {
//                                Thread.sleep(100);
////                                request(1);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });


            //注：之所以出现0-15这样连贯的数据，就是是因为observeOn操作符内部有一个长度为16的缓存区，它会首先请求16个事件缓存起来....
  }

    private Observable<String> processUrlIpByOneFlatMap() {
        return Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return createIpObservable(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<String> processUrlIpByTwoFlatMap() {
        return Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .toList()// if a Observable<list>
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> s) {
                        return Observable.from(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return createIpObservable(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void returnIpByList() {
        processUrlIpByTwoFlatMap()
                .toList() //to list
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> s) {
                        Log.e(TAG, "Consume Data <- " + s.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "throwable call()" + throwable.getMessage());
                    }
                });
    }

    private void returnIpOneByOne() {
        processUrlIpByTwoFlatMap()
                //processUrlIpByOneFlatMap()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "Consume Data <- "+s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "throwable call()" + throwable.getMessage());
                    }
                });
    }


    /**
     * 需求:获取urls的ip,返回所有urls的ips或者单个返回ip
     */
    private void observableFlatMap() {
        //==============把ip作为list返回
        //returnIpByList();
        //===============单个的返回
        returnIpOneByOne();

        //@TODO 如果某个url获取ip失败,该url之后的url都不会去获取ip了.原因(官方注释):
        //If the Observable calls this method (onError), it will not thereafter call onNext or onCompleted.

        //@TODO 可以不调用subscriber.onError(e);或者调用subscriber.onNext(your value);
    }


    private String getIPByUrl(String str) throws MalformedURLException, UnknownHostException {
        URL urls = new URL(str);
        String host = urls.getHost();
        String address = InetAddress.getByName(host).toString();
        int b = address.indexOf("/");
        return address.substring(b + 1);

    }


    private Observable<String> createIpObservable(final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String ip = getIPByUrl(url);
                    subscriber.onNext(ip);
                    Log.e(TAG, "Emit Data -> " + url + " : " + ip);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    //subscriber.onError(e);
                    subscriber.onNext(null);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    //subscriber.onError(e);
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io());
        //.subscribeOn(Schedulers.io()) 注意该方法在这里调用和放在使用该Observable的地方调 产生不同的影响
        //把注释去掉会使用不同的线程去执行,放在放在使用该Observable的地方调会共用一个线程去执行
    }
}
