package cn.locusc.mtia.chapter7.codelist.diningphilosophers;

import cn.locusc.mtia.utils.Debug;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jay
 * 使用trylock(long TimeUnit)规避死锁
 * 规避死锁的第 3种方法是使用ReentrantLock.tryLock(long,TimeUnit)申请锁。
 * ReentrantLock.tryLock(long,TimeUnit)允许我们为锁申请这个操作指定一个超时时间。
 * 在超时时间内，如果相应的锁申请成功，那么该方法返回 true;如果在 tryLock(long,TimeUnit)执行的那一刻相应的锁正被其他线程持有，
 * 那么该方法会使当前线程暂停，直到这个锁被申请成功（此时该方法返回 true）或者等待时间超过指定的超时时间（此时该方法返回false )。
 * 因此，使用 tryLock(long,TimeUnit)来申请锁可以避免一个线程无限制地等待另外一个线程持有的资源，
 * 从而最终能够消除死锁产生的必要条件中的“占用并等待资源”。
 * 使用tryLock(long,TimeUnit)我们可以编写能够规避死锁的AbstractPhilosopher实现类FixedLockBasedPhilosopher，如清单7-8所示。
 *
 * FixedLockBasedPhilosopher类覆盖了其父类的 pickUpChopstick方法（实现拿起指定的筷子的功能)。
 * 这个 pickUpChopstick方法会调用指定筷子（ chopstick )对应的ReentrantLock实例的 tryLock(long,TimeUnit)来申请相应的锁。
 * 这里，我们指定一个哲学家(即 pickUpChopstick方法的执行线程）在等待其他哲学家放下其手中的(一根）筷子时最多只等待50毫秒，
 * 即避免了无限制的等待造成的“占用并等待资源”。
 * 2022/7/19
 */
public class FixedLockBasedPhilosopher78 extends BuggyLckBasedPhilosopher75 {

    public FixedLockBasedPhilosopher78(int id, Chopstick72 left, Chopstick72 right) {
        super(id, left, right);
    }

    @Override
    protected boolean pickUpChopstick(Chopstick72 chopstick) {
        final ReentrantLock lock = LOCK_MAP.get(chopstick);
        boolean pickedUp = false;
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.tryLock(50, TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                // 锁申请失败
                Debug.info("%s is trying to pick up %s on his %s,"
                                + "but it is held by other philosopher ...%n",
                        this, chopstick, chopstick == left ? "left" : "right");
                return false;
            }
        } catch (InterruptedException e) {
            // 若当前线程已经拿起另外一根筷子，则使其放下
            Chopstick72 theOtherChopstick = chopstick == left ? right : left;
            if (LOCK_MAP.get(theOtherChopstick).isHeldByCurrentThread()) {
                theOtherChopstick.putDown();
                LOCK_MAP.get(theOtherChopstick).unlock();
            }
            return false;
        }

        try {
            Debug.info("%s is picking up %s on his %s...%n",
                    this, chopstick, chopstick == left ? "left" : "right");
            chopstick.pickUp();
            pickedUp = true;
        } catch (Exception e) {
            // 不大可能走到这里
            if (lockAcquired) {
                lock.unlock();
            }
            pickedUp = false;
            e.printStackTrace();
        }
        return pickedUp;
    }

}
