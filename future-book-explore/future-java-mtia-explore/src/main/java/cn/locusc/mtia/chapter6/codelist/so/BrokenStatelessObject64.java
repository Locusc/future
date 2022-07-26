package cn.locusc.mtia.chapter6.codelist.so;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jay
 * 多个线程访问本身不包含状态的对象也可能存在共享状态示例
 *
 * 正如清单6-2的代码所展示的那样，无状态对象（以及该类的任何一个上层类）是不包含任何实例变量或者任何可更新的静态变量的。
 * 但是，有时候我们可能很难找到一个像 DefaultEndpointComparator实例那样“纯粹”的无状态对象——一个类即使不包含任何实例变量或者静态变量，
 * 执行这个类方法的多个线程仍然可能存在共享状态，如清单6-4所示。
 *
 * 尽管 BrokenStatelessObject类自身不包含任何实例变量或者静态变量，但是BrokenStatelessObjec.doSomething 方法的多个执行线程仍然可能存在共享状态。
 * BrokenStatelessObjec.doSomething方法中使用的UnsafeSingleton是一个非线程安全单例类(该类仅有一个实例UnsafeSingleton.INSTANCE)。
 * 因此, BrokenStatelessObjec.doSomething方法的多个执行线程其实是在共享同一个UnsafeSingleton实例，
 * 而UnsafeSingleton类的实例变量state1就成为这些线程的共享状态。
 * 尽管BrokenStatelessObjec.doSomething方法的多个执行线程各自都访问各自的UnsafeStatefullObject实例，
 * 但是UnsafeStatefullObject的静态变量cache 会成为这些线程的共享状态。
 * 因此，即使一个类不包含任何实例变量或者静态变量，执行该类方法的多个线程也仍然可能存在共享状态。
 * 此时，这个类在调用其他类的方法时仍然可能需要使用锁。例如，BrokenStatelessObjec.doSomething方法可能需要改写为:
 * synchronized (this) {
 *     str = sfo.doSomething(s, i);
 * }
 *
 * 注意
 * 无状态对象不包含任何实例变量或者可更新静态变量（包括来自相应类的上层类的实例变量或者静态变量)。
 * 但是，一个类不包含任何实例变量或者静态变量却不一定是无状态对象。
 * 特殊情况下，不包含任何实例变量或者静态变量的类，其方法实现时仍然需要借助锁来保障线程安全。
 *
 * 从面向对象编程的角度来看，无状态对象由于不包含任何状态，因此同一个类的多个无状态对象之间是没有差别的。
 * 既然如此，我们又为何要使用对象（无状态对象）而不是使用一个仅包括静态方法的类呢?
 * 这个问题还是得从面向对象编程中的抽象（Abstraction )与实现( Implementation)这两个层次来回答。
 * 例如，Arrays.sort(T[]a, Comparator<? super T> c)允许我们指定一个Comparator接口实例（ c ）用于指定数组元素的排序规则。
 * 这里的Comparator接口就是对排序规则的抽象，而sort方法的调用方所传递的具体Comparator实例则代表实现——一个具体的排序规则。
 * 显然，我们在调用这个sort方法的时候必须传递一个Comparator 接口实现类的一个实例(对象),而无法传递一个类(尽管类本身在Java平台中也是一种对象)。
 *
 * 无状态对象可以被多个线程共享，而其客户端代码及其自身的方法实现又无须使用锁，从而避免了锁可能产生的问题（例如死锁)以及开销。
 * 因此，无状态对象有利于提高并发性。然而，有时候设计出一个纯粹的无状态对象可能有些难度。
 * 另外，即便是纯粹的无状态对象，随着代码的维护，它也可能逐渐演变成其内部实现需要借助锁等线程同步机制的“非纯粹”的无状态对象:
 * 无状态对象的一些方法可能在代码维护过程中需要访问一些非线程安全对象，而这些对象的访问可能导致这些方法的执行线程存在共享状态。
 *
 * 2022/7/18
 */
public class BrokenStatelessObject64 {

    public String doSomething(String s) {
        UnsafeSingleton us = UnsafeSingleton.INSTANCE;
        int i = us.doSomething(s);
        UnsafeStatefullObject sfo = new UnsafeStatefullObject();
        String str;
        synchronized (this) {
            str = sfo.doSomething(s, i);
        }
        return str;
    }

    static class UnsafeStatefullObject {

        static Map<String, String> cache = new HashMap<>();

        public String doSomething(String s, int len) {
            String result = cache.get(s);
            if (null == result) {
                result = md5sum(result, len);
                cache.put(s, result);
            }
            return result;
        }

        public String md5sum(String s, int len) {
            // 生成md5摘要
            // 省略其他代码
            return s;
        }
    }

    enum UnsafeSingleton {
        INSTANCE;

        public int state1;

        public int doSomething(String s) {
            // 省略其他代码

            // 访问state1
            return 0;
        }
    }

}
