package com.zhixuan.app.rxjava_master;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxThread {

  public static void main(String[] args) {
  }

  private void rxThread() {

    Observable.just("")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            new Action1<String>() {
              @Override
              public void call(String s) {}
            });

    /* subscribeOn表示subcribe这个动作所发生的线程 ，只能执行一次，多次执行,只执行第一次*/
    /* observeOn表示observer（suscriber执行操作所在的线程）可以多次执行*/

    /*https://www.jianshu.com/p/12638513424f*/
    /*https://www.jianshu.com/p/1ed30bb39e76*/
    Observable.just("1").subscribeOn(Schedulers.trampoline()); /*创建一个新的线程*/
    Observable.just("1").subscribeOn(Schedulers.computation()); /*适用于和CPU有关的任务，可以充分利用CPU的计算资源*/
  }

}
