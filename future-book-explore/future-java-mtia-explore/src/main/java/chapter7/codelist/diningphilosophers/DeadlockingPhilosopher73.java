package chapter7.codelist.diningphilosophers;

import utils.Debug;

/**
 * @author Jay
 * 可能产生死锁的AbstractPhilosopher子类DeadlockingPhilosophe
 *
 * 接下来我们需要创建一个AbstractPhilosopher类的子类来实现吃饭动作，如清单7-3所示。
 * 由于Chopstick 是一个非线程安全对象，因此 AbstractPhilosopher.eat()在调用Chopstick.pickUp()/putDown()来模拟拿起筷子/放下筷子的时候需要加锁。
 * 再加上哲学家总是先拿起其左手边的筷子,然后才拿起其右手边的筷子，因此我们使用了一个嵌套的同步块并在相应的临界区中调用Chopstick.pickUp()。
 * 由于哲学家只有在拿到两根筷子的情况下才能够吃饭，
 * 因此AbstractPhilosophcr.doEat()调用必须放在嵌套同步块的内层同步块的临界区中（这样同时持有两根筷子这个条件才能成立)。
 *
 * 2022/7/19
 */
public class DeadlockingPhilosopher73 extends AbstractPhilosopher71 {

    public DeadlockingPhilosopher73(int id, Chopstick72 left, Chopstick72 right) {
        super(id, left, right);
    }

    @Override
    public void eat() {
        synchronized (left) {
            Debug.info("%s is picking up %s on his left...%n", this, left);
            left.pickUp();// 拿起左边的筷子
            synchronized (right) {
                Debug.info("%s is picking up %s on his right...%n", this, right);
                right.pickUp();// 拿起右边的筷子
                doEat();// 同时拿起两根筷子的时候才能够吃饭
                right.putDown();
            }
            left.putDown();
        }
    }

}
