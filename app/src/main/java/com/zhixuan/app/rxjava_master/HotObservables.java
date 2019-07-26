package com.zhixuan.app.rxjava_master;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class HotObservables {

  public static void main(String[] args) {
    // 1、hot Observable
//    Observable.just(new Person());
    // 2、cold Observable
//    interval();
    // 3、Cold Observable 转换成 Hot Observable
//    coldToHot();

    ConnectableObservable<Long> observable = interval2();
    observable.connect();
    try {
      Thread.sleep(50L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    observable.observeOn(Schedulers.newThread()).subscribe(
            new Subscriber<Long>() {
              @Override
              public void onNext(Long aLong) {
                System.out.println("Long" + aLong);
              }

              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                System.out.println(e.toString());
              }

            });

    try {
      Thread.sleep(100L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void interval() {
    Observable.interval(1, TimeUnit.SECONDS, Schedulers.trampoline()).take(1000);
  }

  static class Person {
    Person() {
      System.out.println("执行了构造函数");
    }
  }

  public static void coldToHot() {
    ConnectableObservable<Long> connectableObservable =
        Observable.create(
                new Observable.OnSubscribe<Long>() {
                  @Override
                  public void call(Subscriber<? super Long> subscriber) {
                    Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .take((int) Long.MAX_VALUE).subscribe(subscriber::onNext);
                  }
                })
            .publish();
    connectableObservable.connect();
    try {
      Thread.sleep(50L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    connectableObservable.observeOn(Schedulers.newThread()).subscribe(new Subscriber<Long>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        System.out.println(e.toString());
      }

      @Override
      public void onNext(Long aLong) {
        System.out.println(aLong);
      }
    });

    try {
      Thread.sleep(100L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // TODO 为何这样不行

    //    ConnectableObservable<Long> connectableObservable =
    //        Observable.interval(1, TimeUnit.MILLISECONDS,
    // Schedulers.trampoline()).take(Long.MAX_VALUE).publish();
  }

  public static ConnectableObservable<Long> interval2() {
    ConnectableObservable<Long> connectableObservable =
            Observable.create(
                    new Observable.OnSubscribe<Long>() {
                      @Override
                      public void call(Subscriber<? super Long> subscriber) {
                        Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.computation())
                                .take((int) Long.MAX_VALUE).subscribe(subscriber::onNext);
                      }
                    })
                    .publish();

    //TODO 为何这样不行
//    ConnectableObservable<Long> connectableObservable =
//        Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()).take(Long.MAX_VALUE).publish();
    return connectableObservable;
  }
}
