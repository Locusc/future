package cn.locusc.mtia.chapter3.codelist.case01;

import cn.locusc.mtia.utils.ReadOnlyIterator;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Jay
 * 不可变对象(Immutable Object）是指一经创建其状态就保持不变的对象。不可变对象也具有固有的线程安全性，因此不可变对象也可以像无状态对象那样被多个线程共享,而这些线程访问这些共享对象的时候无须加锁。当不可变对象所建模的现实实体的状态发生变化时，系统通过创建新的不可变对象实例来进行反映。
 * 一个严格意义上的不可变对象要同时满足以下所有条件。
 * 类本身使用final修饰:这是为了防止通过创建子类来改变其定义的行为。
 *
 * 所有字段都是用final修饰的:使用final修饰不仅仅是从语义上说明被修饰字段的值不可改变;
 * 更重要的是这个语义在多线程环境下保证了被修饰字段的初始化安全，即 final修饰的字段在对其他线程可见时，它必定是初始化完成的。
 *
 * 对象在此初始化过程中没有逸出(Escape):防止其他类（如该类的内部匿名类)在对象初始化过程中修改其状态。
 * 任何字段，若其引用了其他状态可变的对象（如集合、数组等），则这些字段必
 * 须是private修饰的，并且这些字段值不能对外暴露。若有相关方法要返回这些字段值，则应该进行防御性复制(Defensive Copy )。
 *
 * 2022/7/18
 */
public final class Candidate implements Iterable<Endpoint> {

    // 下游部件节点列表
    private final Set<Endpoint> endpoints;
    // 下游部件节点的总权重
    public final int totalWeight;

    public Candidate(Set<Endpoint> endpoints) {
        int sum = 0;
        for (Endpoint endpoint : endpoints) {
            sum += endpoint.weight;
        }
        this.totalWeight = sum;
        this.endpoints = endpoints;
    }

    public int getEndpointCount() {
        return endpoints.size();
    }

    @Override
    public final Iterator<Endpoint> iterator() {
        return ReadOnlyIterator.with(endpoints.iterator());
    }

    @Override
    public String toString() {
        return "Candidate [endpoints=" + endpoints + ", totalWeight=" + totalWeight
                + "]";
    }

}
