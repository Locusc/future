package chapter6.codelist;

import utils.ReadOnlyIterator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay
 * 减少不可变对象所占的空间
 *
 * 我们也可以采取某些技术来减少不可变对象(尤其是比较大的不可变对象)所占用的内存空间。
 * 比如，创建不可变对象的时候尽可能让新的不可变对象与老的不可变对象共享部分内存空间，从而减少内存空间占用。
 * 在如清单6-6所示的例子中，BigImmutableObject的其中一个构造器允许我们指定一个现有的 BigImmutableObject实例(老的不可变对象)作为创建新实例的“模板”，
 * 该构造器会调用 BigImmutableObject.createRegistry方法。
 * BigImmutableObject.createRegistry方法会对指定的 BigImmutableObject 实例的 registry实例变量进行浅复制（Swallow Copy)得到一个新的HashMap，
 * 再对这个新的HashMap中需要更新的条目进行更新。更新后的HashMap实例会被作为新BigImmutableObject实例的registry实例变量的初始值(也是最终值)。
 * 由于 BigImmutableObject.createRegistry方法所创建的 HashMap实例是老的 BigImmutableObject实例的registry变量的一个浅复制对象，
 * 因此这两个HashMap实例会共用大部分存储空间（主要是 HashMap实例所引用的BigObject所占用的存储空间)。
 *
 * 基于上述原因，当被建模对象的状态变更比较频繁时，不可变对象也不见得就不能使用。
 * 此时，我们需要综合考虑被建模对象的规模、代码目标运行环境的 Java 虚拟机堆内存容量、系统对吞吐率和响应性的要求这几个因素。
 * 若这几个方面因素综合考虑都能满足要求，那么使用不可变对象建模也未尝不可。
 *
 * 虽然不可变对象自身的实例变量或者静态变量的值是不可改变的,但是这些变量所引用的对象本身的状态可能是可变的。
 * 例如，清单6-6所示的例子中 BigImmutableObject的实例变量registry值是不可变的，但是它所引用的HashMap对象的状态是可变的(比如可以更新其中一个条目)。
 * 此时，这些对象所包含的状态如果需要对外暴露的话，那么我们就需要注意这些对象状态也不能被更改。这通常有两种实现方法。
 * 一种是使用迭代器( Iterator ）)模式，即让相应的不可变对象实现Iterable接口，
 * 然后在该接口定义的 iterator方法中返回一个只读的Iterator实例(它不支持remove方法)。
 * 这样，不可变对象的客户端代码利用Iterator 实例，就可以对相应的不可变对象进行遍历操作，而不必关心也不能更改其内部结构。
 * 例如，第3章中的Candidate类(参见清单3-13）就采用了这种方法来阻止客户端代码更新其实例变量endpoints所引用的Set<Endpoint>实例。
 * 另外一种方法是防御性复制（Defensive Copy )。例如，清单6-6中的 iterator方法除了使用第一种方法创建一个只读的 Iterator实例，
 * 还通过调用Collections.unmodifiableSet方法来对HashMap 的entrySet进行防御性复制。
 *
 * 注意
 * ·当被建模现实实体的状态频繁变化的时候，不可变对象也不一定就不能使用。
 * ·不可变对象的使用对垃圾回收效率的影响既有消极的一面，也有积极的一面。
 * ·当一个不可变对象需要对外暴露某些状态的时候，可以使用迭代器（Iterator )模式和（或）防御性复制来阻止客户端代码对其状态进行修改。
 *
 * 不可变对象的典型应用场景
 * 不可变对象特别适用于以下场景。
 * 场景一 被建模对象的状态变化不频繁。
 * 正如上述案例所展示的，这种场景下可以设置一个工作者线程（例如上述案例中的配置管理线程）用于在被建模对象状态变化时创建新的不可变对象。
 * 而其他线程则仅读取不可变对象的状态。此场景下的一个小技巧是采用volatile关键字修饰引用不可变对象的变量,
 * 这样既可以避免使用锁（如synchronized)又可以保证可见性。第3章的第1个实战案例（负载均衡器）就属于该场景的应用。
 * 当然，上文也提到过被建模对象的状态变化频繁变化的情况下，也不见得就适合使用不可变对象。
 *
 * 场景二 同时对一组相关的数据进行写操作，因此需要保证原子性。
 * 此场景为了保证操作的原子性，通常的做法是使用锁。而此时应用不可变对象，我们既可以保障原子性又可以避免锁的使用，从而既简化了代码又提高了代码运行效率。
 * 第3章的第1个实战案例(负载均衡器）就属于该场景的应用。
 *
 * 场景三 使用不可变对象作为安全可靠的Map键(Key )。
 * 设someKey为任意一个状态可变对象，someValue为任意一个对象，map为 Map<K,V>接口的任意一个实例（比如HashMap 实例)。
 * 在map.put(someKey,someValue)被调用之后，如果someKey 的内部状态变化导致someKey.hashCode()的返回值产生变化，
 * 那么map.get(someKey)调用将无法返回someValue，即使在此期间无任何线程执行map.remove(someKey)!
 * 而如果 someKey是一个不可变对象，那么someKey.hashCode()返回值恒定，
 * 因此 map.get(someKey)调用总是可以返回someValue(除非中途map.remove(someKey)被调用过)。因此，不可变对象非常适宜用作 Map的键。
 *
 * 2022/7/18
 */
public class BigImmutableObject66 implements Iterable<Map.Entry<String, BigObject>> {

    private final HashMap<String, BigObject> registry;

    public BigImmutableObject66(HashMap<String, BigObject> registry) {
        this.registry = registry;
    }

    public BigImmutableObject66(BigImmutableObject66 prototype, String key,
                              BigObject newValue) {
        this(createRegistry(prototype, key, newValue));
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, BigObject> createRegistry(BigImmutableObject66 prototype, String key,
                                                             BigObject newValue) {
        // 从现有对象中复制（浅复制）字段
        HashMap<String, BigObject> newRegistry =
                (HashMap<String, BigObject>) prototype.registry.clone();

        // 仅更新需要更新的部分
        newRegistry.put(key, newValue);
        return newRegistry;
    }

    @Override
    public Iterator<Map.Entry<String, BigObject>> iterator() {
        // 对entrySet进行防御性复制
        final Set<Map.Entry<String, BigObject>> readOnlyEntries = Collections.unmodifiableSet(registry.entrySet());

        // 返回一个只读的Iterator实例
        return ReadOnlyIterator.with(readOnlyEntries.iterator());
    }

    public BigObject getObject(String key) {
        return registry.get(key);
    }

    public BigImmutableObject66 update(String key, BigObject newValue) {
        return new BigImmutableObject66(this, key, newValue);
    }

}

class BigObject {

    byte[] data = new byte[4 * 1024 * 1024];

    private int id;

    private final static AtomicInteger ID_Gen = new AtomicInteger(0);

    public BigObject() {
        id = ID_Gen.incrementAndGet();
    }

    @Override
    public String toString() {
        return "BigObject [id=" + id + "]";
    }
    // 省略其他代码

}
