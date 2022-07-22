package chapter7.codelist.diningphilosophers;

import utils.Debug;

/**
 * @author Jay
 * 使用锁排序规避死锁
 * 锁排序法( Lock Ordering)——相关线程使用全局统一的顺序申请锁。
 * 假设有多个线程需要申请资源（锁){Lock，Lock,，…，Lockw}，
 * 那么我们只需要让这些线程依照一个全局(相对于使用这种资源的所有线程而言)统一的顺序去申请这些资源，
 * 就可以消除“循环等待资源”这个条件，从而规避死锁。
 * 例如，在哲学家就餐问题中每个哲学家都是依照“先拿起左手边的筷子，再拿起右手边的筷子”这种局部顺序来拿筷子的。之所以称这种顺序为“局部”，
 * 是因为一个哲学家右手边的筷子恰恰是另外一个哲学家左手边的筷子。因此，从全局的角度来看这种拿筷子的顺序实际上是各个线程各自为政使用不同的顺序，
 * 从而使“循环等待资源”得以成立。为了消除“循环等待资源”这个死锁产生的必要条件，我们可以让所有的哲学家（线程)使用全局统一的顺序去拿起两根筷子，
 * 比如先拿编号( id）值较小的，再拿编号值较大的筷子。这种方法实际上是对资源（筷子或者访问筷子所需的锁)进行排序。
 * 一般地,我们可以使用对象的身份hashcode( Identity Hash Code,即System.identityHashCode(Object)的返回值)来作为资源的排序依据。
 * 依照这个思路，我们可以编写能够规避死锁的AbstractPhilosopher'实现类FixedPhilosopher ,如清单7-7所示。
 *
 * 在 FixedPhilosopher类中，我们先在该类的构造器中根据Chopstick实例的身份hashcode对左手边的筷子和右手边的筷子进行排序，
 * 排序的结果记为一根筷子( one)和另外一根筷子( theOther )。
 * 在 eat方法中，我们在调用Chopstick.pickUp()/putDown()的时候分别用one和 theOther 去替代left(左手边筷子）和right(右手边筷子)进行加锁。
 * 这样就确保每个哲学家线程都是使用全局统一的顺序去申请资源（Chopstick 对应的内部锁)，从而消除了“循环等待资源”这个条件而规避了死锁。
 * 由于使用不同的对象调用System.identityHashCode(Object)仍然可能返回相同的身份hashcode(尽管这种可能性极低)，
 * 因此在eat方法中我们仍然考虑到这些情形（即left和 right对应的身份hashcode相同)。
 * 此时，我们转而使用粗锁法——使用FixedPhilosopher类对象本身(Java平台中的类本身也是一种对象）作为全局锁对Chopstick.pickUp()/putDown()调用进行加锁。
 *
 * chapter7.codelist.diningphilosophers.DiningPhilosopherProblem74
 * #createPhilosopher(java.lang.String, int, chapter7.codelist.diningphilosophers.Chopstick72[])
 * 该方法对传入的筷子进行取模, 在FixedPhilosopher77中获取hash值时, 相当于构成了一个hash环
 * 这里存在一个相对位置的变化, one和other相对于不同的线程可能是left/right或者right/left, 但是由于hash环的原因left会一直是chopstick-1
 * right会一直是chopstick-0
 * [2022-07-19 15:40:34.595][INFO][Philosopher-0]:Philosopher-0 is thinking...
 *
 * [2022-07-19 15:41:54.260][INFO][Philosopher-1]:Philosopher-1 is thinking...
 *
 * [2022-07-19 15:41:54.279][INFO][Philosopher-0]:Philosopher-0 is picking up chopstick-1 on his right...
 *
 * [2022-07-19 15:41:54.280][INFO][Philosopher-0]:Philosopher-0 is picking up chopstick-0 on his left...
 *
 * [2022-07-19 15:41:54.280][INFO][Philosopher-0]:Philosopher-0 is eating...
 *
 * [2022-07-19 15:41:54.286][INFO][Philosopher-0]:Philosopher-0 is thinking...
 *
 * [2022-07-19 15:41:54.286][INFO][Philosopher-1]:Philosopher-1 is picking up chopstick-1 on his left...
 *
 * [2022-07-19 15:41:54.287][INFO][Philosopher-1]:Philosopher-1 is picking up chopstick-0 on his right...
 *
 * [2022-07-19 15:41:54.287][INFO][Philosopher-1]:Philosopher-1 is eating...
 *
 * [2022-07-19 15:41:54.295][INFO][Philosopher-1]:Philosopher-1 is thinking...
 * 2022/7/19
 */
public class FixedPhilosopher77 extends AbstractPhilosopher71 {

    private final Chopstick72 one;
    private final Chopstick72 theOther;

    public FixedPhilosopher77(int id, Chopstick72 left, Chopstick72 right) {
        super(id, left, right);
        // 对资源（锁）进行排序
        int leftHash = System.identityHashCode(left);
        int rightHash = System.identityHashCode(right);
        if (leftHash < rightHash) {
            one = left;
            theOther = right;
        } else if (leftHash > rightHash) {
            one = right;
            theOther = left;
        } else {
            // 两个对象的identityHashCode值相等是可能的，尽管这个几率很小
            one = null;
            theOther = null;
        }
    }

    @Override
    public void eat() {
        if (null != one) {
            synchronized (one) {
                Debug.info("%s is picking up %s on his %s...%n", this, one,
                        one == left ? "left" : "right");
                one.pickUp();
                synchronized (theOther) {
                    Debug.info("%s is picking up %s on his %s...%n", this,
                            theOther, theOther == left ? "left" : "right");
                    theOther.pickUp();
                    doEat();
                    theOther.putDown();
                }
                one.putDown();
            }
        } else {
            // 退化为使用粗锁法
            synchronized (FixedPhilosopher77.class) {
                Debug.info("%s is picking up %s on his left...%n", this, left);
                left.pickUp();

                Debug.info("%s is picking up %s on his right...%n", this, right);
                right.pickUp();
                doEat();
                right.putDown();

                left.putDown();
            }
        }// if语句结束
    }// eat方法结束

}
