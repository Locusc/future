package chapter6.codelist.so;

import chapter3.codelist.case01.Endpoint;

import java.util.Comparator;

/**
 * @author Jay
 * 无状态对象实例
 *
 * 对象（ Object)就是操作和数据的封装。对象所包含的数据就被称为该对象的状态( State )，它包括存储在实例变量或者静态变量之中的数据。
 * 一个对象的状态也可能包含该对象引用的其他对象的实例变量或者静态变量中的数据。相应地，实例变量、静态变量也被称为状态变量( State Variable )。
 * 如果一个类的同一个实例被多个线程共享并不会使这些线程存在共享状态（ Shared State )，那么这个类及其任意一个实例就被称为无状态对象( Stateless Object )。
 * 反之，如果一个类的同一个实例被多个线程共享，会使这些线程存在共享状态，那么这个类及其任意一个实例就被称为有状态对象( Stateful Object )。
 * 无状态对象不含任何实例变量，且不包含任何静态变量或者其包含的静态变量都是只读的（常量)。有状态对象又可以分为状态可变对象和状态不可变对象。
 * 所谓状态可变就是，对象在其生命周期中,其状态变量的值可以发生变化。
 *
 * 我们知道线程安全问题产生的前提是多个线程之间存在共享数据。
 * 因此，实现线程安全的一种自然的方法就是避免在多个线程之间共享数据。使用无状态对象就是这样一种自然的办法:
 * 一个线程执行无状态对象的任意一个方法来完成某个计算的时候，该计算的瞬时状态（中间结果)仅体现在局部变量和(或)只有当前执行线程能够访问的对象的状态上。
 * 因此，一个线程执行无状态对象的任何方法都不会对访问该无状态对象的其他线程产生任何干扰作用。
 * 所以，无状态对象具有固有的线程安全性，它可以被多个线程共享，而这些线程在执行该对象的任何方法时都无须使用同步机制。
 *
 * 下面看一个无状态对象使用实例。
 * 假设我们要对第3章的第1个实战案例(负载均衡模块)中的服务器节点(Endpoint类，参见清单3-11)进行排序的话，
 * 那么我们可以创建一个Comparator实例来表示相应的排序规则，如清单6-2所示。
 *
 * DefaultEndpointComparator的实例就是一个无状态对象:
 * DefaultEndpointComparator.compare方法执行时所产生的瞬时状态仅体现为局部变量以及只有执行线程才能访问的对象（ Endpoint 实例)。
 * 在此基础上我们可以实现排序，如清单6-3所示。
 * 一个DefaultEndpointComparator实例可以被 EndpointView.retrieveServerList()的多个执行线程共享（通过静态变量DEFAULT_COMPARATOR )，
 * 而这些线程无须使用锁等同步机制。
 *
 * 2022/7/18
 */
public class DefaultEndpointComparator62 implements Comparator<Endpoint> {

    @Override
    public int compare(Endpoint server1, Endpoint server2) {
        int result = 0;
        boolean isOnline1 = server1.isOnline();
        boolean isOnline2 = server2.isOnline();
        // 优先按照服务器是否在线排序
        if (isOnline1 == isOnline2) {
            // 被比较的两台服务器都在线（或不在线）的情况下进一步比较服务器权重
            result = compareWeight(server1.weight, server2.weight);
        } else {
            // 在线的服务器排序靠前
            if (isOnline1) {
                result = -1;
            }
        }
        return result;
    }

    private int compareWeight(int weight1, int weight2) {
        if (weight1 == weight2) {
            return 0;
        } else if (weight1 < weight2) {
            // 按权重降序排列
            return 1;
        } else {
            return -1;
        }
    }

}
