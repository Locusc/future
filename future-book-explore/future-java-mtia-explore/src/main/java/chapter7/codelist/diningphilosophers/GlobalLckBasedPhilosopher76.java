package chapter7.codelist.diningphilosophers;

import utils.Debug;

/**
 * @author Jay
 * 使用粗粒度的锁规避死锁
 * 一个线程在已经持有一个锁的情况下再次申请这个锁(比如，一个类的一个同步方法调用该类的另外一个同步方法）并不会导致死锁，
 * 这是因为Java中的锁（包括内部锁和显式锁）都是可重入的(Reentrant )，这种情形下线程再次申请这个锁是可以成功的。
 *
 * 弄清楚死锁产生的必要条件也就不难想到规避死锁的方法——我们只要消除死锁产生的任意一个必要条件就可以规避死锁了。
 * 由于锁具有排他性并且锁只能够由其持有线程主动释放，因此由锁导致的死锁只能够从消除“占用并等待资源”和消除“循环等待资源”这两个方向人手。
 * 相应地，下面我们介绍基于这两个思路规避死锁的方法。
 *
 * 粗锁法（ Coarsen-grained Lock )——使用粗粒度的锁代替多个锁。从消除“占用并等待资源”出发我们不难想到的一种方法就是，
 * 采用一个粒度较粗的锁来替代原先的多个粒度较细的锁，这样涉及的线程都只需要申请一个锁从而避免了死锁。
 * 按照这个思路，我们可以编写一个能够规避死锁的AbstractPhilosopher 实现类GlobalLckBasedPhilosopher，如清单7-6所示。
 * GlobalLckBasedPhilosopher.eat()会使用一个静态变量GLOBAL_LOCK作为锁。
 * 这样，所有哲学家线程( GlobalLckBasedPhilosopher 的实例)在拿起筷子前都必须持有GLOBAL_LOCK对应的内部锁。
 * 此时,由于每个哲学家线程仅需要申请一个锁就可以吃饭，因此死锁产生的必要条件“占用并等待资源”和“循环等待资源”就都不成立了，从而避免了死锁。
 *
 * 粗锁法的缺点是它明显地降低了并发性并可能导致资源浪费。例如﹐GlobalLckBasedPhilosopher.eat()采用粗锁法的结果是一次只能够有一个哲学家能够吃饭，
 * 一个哲学家在吃饭的时候其他哲学家只能在思考或者等待筷子!而实际上，一个哲学家在吃饭的时候仅占用了两根筷子，
 * 剩下的三根筷子其实还够供另外一个哲学家使用!因此,粗锁法的适用范围比较有限。
 *
 * 2022/7/19
 */
public class GlobalLckBasedPhilosopher76 extends AbstractPhilosopher71 {

    // GLOBAL_LOCK必须使用static修饰
    private final static Object GLOBAL_LOCK = new Object();

    public GlobalLckBasedPhilosopher76(int id, Chopstick72 left, Chopstick72 right) {
        super(id, left, right);
    }

    @Override
    public void eat() {
        synchronized (GLOBAL_LOCK) {
            Debug.info("%s is picking up %s on his left...%n", this, left);
            left.pickUp();
            Debug.info("%s is picking up %s on his right...%n", this, right);
            right.pickUp();
            doEat();
            right.putDown();
            left.putDown();
        }
    }

}
