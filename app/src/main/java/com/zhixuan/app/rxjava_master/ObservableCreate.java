package com.zhixuan.app.rxjava_master;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ObservableCreate {

  /*
   * 创建操作符有以下几个:
   * create
   * just
   * form
   * interval
   * range
   * repeat
   * defer
   * */
  public static void main(String[] args) {
    //        interval();
    //    				range();
    //    				repeat();
    defer();
    //    backPressure1();
  }

  /** ************* 创建Observable **************** */

  /** 1、create --Rxjava2 */
  private static void create() {
    final Person person = new Person();

    Subscription subscription =
        Observable.create(
                new Observable.OnSubscribe<Person>() {
                  @Override
                  public void call(Subscriber<? super Person> subscriber) {
                    subscriber.onNext(person);
                    subscriber.onCompleted();
                  }
                })
            .subscribe(
                new Subscriber<Person>() {
                  // ①
                  @Override
                  public void onStart() {
                    super.onStart();
                  }

                  @Override
                  public void onNext(Person person) {}

                  @Override
                  public void onCompleted() {}

                  @Override
                  public void onError(Throwable e) {}
                });

    // ②
    subscription.isUnsubscribed();
    subscription.unsubscribe();

    // 注：Subscriber和Observer区别
    // ①：多定义了一个onStart方法，它会在 subscribe 刚开始，而事件还未发送之前被调用，可以用于做一些准备工作，例如数据的清零或重置、启动背压
    // ②：unsubscribe:解除监听,在 subscribe() 之后， Observable 会持有 Subscriber 的引用，这个引用如果不能及时被释放，将有内存泄露的风险。
    // ③：RxJavaHooks.onCreate(f)：通过Hook机制加入call方法,Hook机制:@see <a
    // href="https://www.jianshu.com/p/c431ad21f071">Android Hook 机制之简单实战</a>

    // onNext
    Observable.just("1")
        .subscribe(
            new Action1<String>() {
              @Override
              public void call(String s) {}
            });

    // onCompleted
    Action0 action0 =
        new Action0() {
          @Override
          public void call() {}
        };

    Observable.just("1")
        .subscribe(
            new Action1<String>() {
              @Override
              public void call(String s) {}
            },
            new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {}
            },
            action0);

    Observable.just("1")
        .map(
            new Func1<String, Integer>() {
              @Override
              public Integer call(String s) {
                return null;
              }
            })
        .subscribe(
            new Action1<Integer>() {
              @Override
              public void call(Integer integer) {}
            });
  }

  /** 2、just */
  private void just() {
    // 应用场景：快速创建 被观察者对象（Observable） & 发送10个以下事件
    Observable.just("1", "2");
    Observable.just(new String[] {});
    Observable.just(new Person());
    Observable.just(null);

    // 注：just操作符将单个参数发送的内容通过ScalarSynchronousObservable转换为一个新的Observable对象;
    // 而将多个参数发送的内容转换为一个数组，然后将数组通过from操作符进行发送
  }

  /** 3、from */
  private static void from() {
    // 应用场景:遍历数组
    String[] a = new String[] {"1", "2"};
    Observable.from(a)
        .subscribe(
            new Action1<String>() {
              @Override
              public void call(String s) {
                System.out.println(s);
              }
            });

    // 应用场景:遍历集合
    ArrayList<Person> peoples = new ArrayList<>();
    peoples.add(new Person("小王", 21));
    peoples.add(new Person("小张", 21));
    peoples.add(new Person("小李", 21));
    Observable.from(peoples)
        .subscribe(
            new Action1<Person>() {
              @Override
              public void call(Person p) {
                System.out.println("peoplesname==" + p.getName());
              }
            });

    // 注:①和just相辅相成,从源码上来看，当数据源是数组时，用from
    // ②：Observable将数组中的元素逐个进行发送，在发送过程中转换为Observable对象。
    // ③：from还可以将一个Futrue或Iterable转为Observable发射出去，这块用的不多，暂时先放到后面，讲完Rxjava打算讲讲多线程，到时候再回过来看一下这种情况；
    // Future模式:@See <a href="https://www.jianshu.com/p/949d44f3d9e3">Java多线程 - Future模式</a>

  }

  /** 4 、interval */
  private static void interval() {

    // 创建以1秒为事件间隔发送整数序列的Observable
    Observable observable = Observable.interval(1, TimeUnit.SECONDS);
    observable.subscribe(
        new Action1<Long>() {
          @Override
          public void call(Long aLong) {
            // ①
            System.out.println(aLong);
          }
        });

    Observable.interval(1, TimeUnit.SECONDS, Schedulers.trampoline())
        .subscribe(
            new Action1<Long>() {
              @Override
              public void call(Long aLong) {
                System.out.println(aLong);
              }
            });

    Observable.interval(5, 1, TimeUnit.SECONDS, Schedulers.trampoline())
        .take(10)
        .subscribe(
            new Action1<Long>() {
              @Override
              public void call(Long aLong) {
                System.out.println(aLong);
              }
            });

    // ①：发现不能正常的1、2、3、4、5这样，是因为interval内默认是Schedulers.computation()线程
    // ②：Rxjava2中对 interval 扩展了一个操作符 intervalRange
  }

  /*5 、range*/
  private static void range() {
    // 发射一个范围内的有序整数序列，并且我们可以指定范围的起始和长度
    Observable.range(0, 5)
        .subscribe(
            new Action1<Integer>() {
              @Override
              public void call(Integer integer) {
                System.out.print(integer);
              }
            });

    //    Observable.range(1, 9, Schedulers.trampoline()).subscribeOn(Schedulers.io()).subscribe();
  }

  /*6、repeat*/
  private static void repeat() {
    // repeat操作符,创建一个以N次重复发送数据的Observable
    Observable.range(1, 5)
        .repeat(3)
        .subscribe(
            new Action1<Integer>() {
              @Override
              public void call(Integer integer) {
                System.out.print(integer);
              }
            });
  }

  /*7、 defer*/
  private static void defer() {

    Observable observable =
        Observable.just(1)
            .map(
                new Func1<Integer, Integer>() {
                  @Override
                  public Integer call(Integer o) {
                    System.out.println("1");
                    return 1;
                  }
                });
    observable.subscribe(
        new Action1<Integer>() {
          @Override
          public void call(Integer integer) {
            System.out.print("2");
          }
        });
    observable.subscribe(
        new Action1() {
          @Override
          public void call(Object o) {
            System.out.print("3");
          }
        });

    // 注：内部也调用了onCreate方法，只是defer操作符传递的OnSubscribe是OnSubscribeDefer
    // OnSubscribeDefer也是继承自OnSubscribe，那么他的call方法肯定也是在订阅的时候被调用
    // （就是说订阅的时候才创建这个observable，并且每次订阅都会创建一个新的observable）

    // defer 就相当于懒加载，只有等observable 与observer建立了订阅关系时，observable才会建立
    // 所以可以实现延迟订阅@See <a https://www.jianshu.com/p/c83996149f5b />
  }

  private static void backPressure1() {
    Observable.interval(1, TimeUnit.SECONDS)
        .subscribe(
            new Action1<Long>() {
              @Override
              public void call(Long aLong) {
                // ①
                System.out.println(aLong);
              }
            });

    // 被观察者在主线程中，每1ms发送一个事件
    //        Observable.interval(1, TimeUnit.MILLISECONDS)
    //                // .subscribeOn(Schedulers.newThread())
    //                // 将观察者的工作放在新线程环境中
    ////        .observeOn(Schedulers.newThread())
    //                // 观察者处理每1000ms才处理一个事件
    //                .subscribe(
    //                        new Action1<Long>() {
    //                            @Override
    //                            public void call(Long aLong) {
    //                                try {
    //                                    Thread.sleep(1000);
    //                                    System.out.print("-->sleep" + aLong);
    //                                } catch (InterruptedException e) {
    //                                    System.out.print("-->" + e);
    //                                }
    ////                Log.w("TAG", "---->" + aLong);
    //                                System.out.print("-->" + aLong);
    //                            }
    //                        });
  }
}
