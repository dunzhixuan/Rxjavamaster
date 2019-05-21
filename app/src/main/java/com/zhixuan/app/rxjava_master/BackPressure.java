package com.zhixuan.app.rxjava_master;

import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/*
 * @See <a https://www.jianshu.com/p/ceb48ed8719d />
 * */
public class BackPressure {

  public static void main(String[] args) {
    //		  backPressure();
    //      buffer();
    backPressureDrop();
  }

  /*Observable在RxJava2.0中新的实现叫做Flowable， 同时旧的Observable也保留了。因为在 RxJava1.x 中，有很多事件不被能正确的背压，从而抛出MissingBackpressureException。
  举个简单的例子，在 RxJava1.x 中的 observeOn， 因为是切换了消费者的线程，因此内部实现用队列存储事件。
  在 Android 中默认的 buffersize 大小是16，因此当消费比生产慢时， 队列中的数目积累到超过16个，就会抛出MissingBackpressureException， 初学者很难明白为什么会这样，使得学习曲线异常得陡峭。
  而在2.0 中，Observable 不再支持背压，而Flowable 支持非阻塞式的背压。Flowable是RxJava2.0中专门用于应对背压（Backpressure）问题。
  所谓背压，即生产者的速度大于消费者的速度带来的问题，比如在Android中常见的点击事件，点击过快则经常会造成点击两次的效果。其中，Flowable默认队列大小为128。并且规范要求，所有的操作符强制支持背压。
  幸运的是， Flowable 中的操作符大多与旧有的 Observable 类似。*/
  private static void backPressure() {

    // 被观察者在主线程中，每1ms发送一个事件
    Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline())
        //         .subscribeOn(Schedulers.newThread())
        // 将观察者的工作放在新线程环境中
        //        .observeOn(Schedulers.newThread())
        // 观察者处理每1000ms才处理一个事件
        .subscribe(
            new Action1<Long>() {
              @Override
              public void call(Long aLong) {
                try {
                  Thread.sleep(1000);
                  System.out.print("-->sleep" + aLong);
                } catch (InterruptedException e) {
                  System.out.print("-->" + e);
                }
                //                Log.w("TAG", "---->" + aLong);
                System.out.print("-->" + aLong);
              }
            });

    // 抛出； Caused by: rx.exceptions.MissingBackpressureException

  }

  private static void buffer() {
    Observable.just("one", "two", "three", "four", "five") // 创建了一个有5个数字的被观察者
        // 3 means,  it takes max of three from its start index and create list
        // 1 means, it jumps one step every time
        .buffer(3, 1)
        .subscribe(
            new Action1<List<String>>() {
              @Override
              public void call(List<String> strings) {
                System.out.print(strings);
              }
            });

    /*
     * onBackpressureBuffer：把observable发送出来的事件做缓存，当request方法被调用的时候，
     * 给下层流发送一个item(如果给这个缓存区设置了大小，那么超过了这个大小就会抛出异常)。
     * onBackpressureDrop：将observable发送的事件抛弃掉，直到subscriber再次调用request（n）方法的时候，就发送给它这之后的n个事件。
     * */
    // Observable.just(1).onBackpressureBuffer().onBackpressureDrop().subscribe();
  }

  // <a https://www.jianshu.com/p/2c4799fa91a4 />
  private static void backPressureDrop() {
    Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline())
        .onBackpressureBuffer(1000) // 设置一个大小为1000的缓存区
        .observeOn(Schedulers.newThread())
        .subscribe(
            new Subscriber<Long>() {

              @Override
              public void onStart() {
                System.out.print("start" + "\n");
              }

              @Override
              public void onCompleted() {}

              @Override
              public void onError(Throwable e) {
                System.out.print("ERROR" + e.toString() + "\n");
              }

              @Override
              public void onNext(Long aLong) {
                System.out.print("-->" + aLong + "\n");
                try {
                  Thread.sleep(100);
                  // request(1);
                  // request(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            });


      Observable.interval(1, TimeUnit.MILLISECONDS,Schedulers.trampoline())
              .onBackpressureDrop() //此处不起作用,必须在真机上才能模拟出效果
              .observeOn(Schedulers.newThread())
              .subscribe(new Subscriber<Long>() {

                  @Override
                  public void onStart() {
                      System.out.print("start" + "\n");
//                        request(1);
                  }

                  @Override
                  public void onCompleted() {

                  }
                  @Override
                  public void onError(Throwable e) {
                      System.out.print("ERROR" + e.toString() + "\n");
                  }

                  @Override
                  public void onNext(Long aLong) {
                      System.out.print("-->" + aLong + "\n");
                      try {
                          Thread.sleep(100);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  }
              });
    // 注意：默认缓存大小：16
  }
}
