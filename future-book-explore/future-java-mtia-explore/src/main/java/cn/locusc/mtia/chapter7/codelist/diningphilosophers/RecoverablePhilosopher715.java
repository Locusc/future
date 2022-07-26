package cn.locusc.mtia.chapter7.codelist.diningphilosophers;

import cn.locusc.mtia.utils.Debug;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jay
 * 清单7-15支持死锁恢复的AbstractPhilosopher子类 RecoverablePhilosopher
 *
 * 清单7-5中的哲学家模型由于是通过ReentrantLock.lock()申请显式锁的，
 * 因此它无法响应中断，也就无法支持死锁的自动恢复。因此为了展示死锁恢复的效果，我们需要将其改造为如清单7-15所示的代码。
 * 2022/7/19
 */
public class RecoverablePhilosopher715 extends BuggyLckBasedPhilosopher75 {

    public RecoverablePhilosopher715(int id, Chopstick72 left, Chopstick72 right) {
        super(id, left, right);
    }

    @Override
    protected boolean pickUpChopstick(Chopstick72 chopstick) {
        final ReentrantLock lock = LOCK_MAP.get(chopstick);
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            // 使当前线程释放其已持有的锁
            Debug.info("%s detected interrupt.", Thread.currentThread().getName());
            Chopstick72 theOtherChopstick = chopstick == left ? right : left;
            theOtherChopstick.putDown();
            LOCK_MAP.get(theOtherChopstick).unlock();
            return false;
        }
        try {
            Debug.info(
                    "%s is picking up %s on his %s...%n",
                    this, chopstick, chopstick == left ? "left" : "right");

            chopstick.pickUp();
        } catch (Exception e) {
            // 不大可能走到这里
            e.printStackTrace();
            lock.unlock();
            return false;
        }
        return true;
    }

}
