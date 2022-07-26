package cn.locusc.mtia.chapter6.codelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jay
 * 保障对外包装对象的遍历操作的线程安全
 *
 * 装饰器（ Decorator )模式可以用来实现线程安全，其基本思想是为非线程安全对象创建一个相应的线程安全的外包装对象( Wrapper )，
 * 客户端代码不直接访问非线程安全对象而是访问其外包装对象。外包装对象与相应的非线程安全对象具有相同的接口，
 * 因此客户端代码使用外包装对象的方式与直接使用相应的非线程安全对象的方式相同,而外包装对象内部通常会借助锁,
 * 以线程安全的方式调用相应非线程安全对象的同签名方法来实现其对外暴露的各个方法。
 *
 * java.util.Collections.synchronizedX(其中，X可以是Set、List、Map等）方法就是使
 * 用装饰器模式将指定的非线程安全的集合对象对外暴露为线程安全的对象(外包装对象)。
 * Collections.synchronizedX方法的参数允许我们指定一个非线程安全的集合对象，该方法的返回值是指定集合对象的外包装对象。
 * 这些对象也被称为同步集合( SynchronizedCollection )。
 * 例如，Collections.synchronizedMap方法可以根据指定的非线程安全Map接口实现类（比如HashMap )返回一个相应的外包装对象（同样也是Map接口实例)。
 *
 * 使用装饰器模式来实现线程安全的一个好处就是关注点分离( Separation of Concern )。
 * 在这种设计中,实现同一组功能的对象有两个版本——非线程安全版和线程安全版。
 * 例如,对于Map接口定义的功能，我们有一个非线程安全版的 HashMap和一个线程安全版的Collections.synchronizedMap(new HashMap())。
 * 这会带来若干好处:首先，这使得我们可以根据实际需要选择最合适的实现类。
 * 比如，如果只有一个线程需要使用Map接口，那么我们可以选择HashMap，
 * 这样可以避免相应的同步集合( Collections.synchronizedMap的返回值)中使用的锁所产生的开销。
 * 其次，在非线程安全版的类里我们可以只关注功能本身，而不必关注线程安全问题，即我们能够以单线程的方式去开发非线程安全版的类。
 * 这不仅降低了开发难度,还提高了可测试性。而线程安全版的类仅需要关注线程安全问题;
 * 至于功能部分，它可以委托给相应的非线程安全版的类，即通过调用相应非线程安全版类的相应方法来实现功能，这同样也能够提高可测试性。
 *
 * 使用装饰器模式来实现线程安全也存在一些缺点，例如Collections.synchronizedX方法返回的同步集合存在如下弊端。
 * 首先，这些同步集合的iterator方法返回的 Iterator实例并不是线程安全的。
 * 为了保障对同步集合的遍历操作的线程安全性，我们需要对遍历操作进行加锁，如清单6-12所示。
 *
 * 从清单6-12中可以看出，对同步集合进行遍历操作的时候，我们需要以被遍历同步集合对象本身作为内部锁。
 * 这样做实质上是利用了内部锁的排他性，从而阻止了遍历过程中其他线程改变了同步集合的内部结构。
 * 因此，这种遍历是不利于提高并发性的。另外，对遍历操作进行加锁时,我们选用的内部锁必须和相应的同步集合内部用于保障其自身线程安全所使用的锁保持一致。
 * 也就是说，这一定程度上要求我们必须知道同步集合对象内部的一些细节，显然这是有悖于面向对象编程中的信息封装( Information Hiding)原则的。
 *
 * 其次，这些同步集合在其实现线程安全的时候通常是使用一个粗粒度的锁，即使用一个锁来保护其内部所有的共享状态。
 * 因此，使用这些同步集合虽然可以确保线程安全，但是也可能导致锁的高争用,从而导致较大的上下文切换的开销。
 * 2022/7/18
 */
public class SyncCollectionSafeTraversal612 {

    final List<String> syncList = Collections.synchronizedList(new ArrayList<>());

    // ...

    public void dump() {
        Iterator<String> iterator = syncList.iterator();
        synchronized (syncList) {
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }
    }

}
