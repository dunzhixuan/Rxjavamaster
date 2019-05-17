package com.zhixuan.app.rxjava_master;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    interval();
  }

  /** ************* 创建Observable **************** */

  /** 1、create */
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

  /** interval */
  private static void interval() {

    Observable.interval(1, TimeUnit.SECONDS)
        .subscribe(
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
}
