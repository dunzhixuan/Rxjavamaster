package com.zhixuan.app.rxjava_master;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class interval {
  private static Subscription subscription;

  public static void main(String[] args) {
    interval();
    //    unsubscribe();
  }

  private static void interval() {
    Observable observable =
        Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()).take(129);

    subscription =
        observable
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.newThread())
            .subscribe(
                new Subscriber<Long>() {

                  @Override
                  public void onCompleted() {}

                  @Override
                  public void onError(Throwable e) {
                    System.out.println(e.toString());
                  }

                  @Override
                  public void onNext(Long aLong) {
                    System.out.println(aLong);
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  }
                });

    subscription.unsubscribe();

    try {
      Thread.sleep(100000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void unsubscribe() {
    subscription.unsubscribe();
  }
}
