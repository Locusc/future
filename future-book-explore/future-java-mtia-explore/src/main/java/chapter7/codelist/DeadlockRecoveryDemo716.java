package chapter7.codelist;

import chapter7.codelist.diningphilosophers.DiningPhilosopherProblem74;

/**
 * @author Jay
 * 死锁自动恢复demo
 *
 * 运行上述程序，我们可以发现当死锁产生的时候，DeadlockDetector 能够自动侦测到并试图进行自动恢复。
 * 但是，恢复之后故障又很快重新出现了，接着又是自动恢复……由此可见，死锁自动恢复的实际意义并不大。
 *
 * 首先，死锁的自动恢复有赖于死锁的线程能够响应中断。以 RecoverablePhilosopher（清单7-15)为例，
 * 如果我们在代码开发与维护过程中能够意识到它是可能导致死锁的，
 * 那么我们应该采取的措施是规避死锁（防患未然）而不是使其支持死锁的自动恢复（为亡羊补牢做准备);
 * 相反，如果我们未能事先意识到死锁这个问题，那么这个类的相关方法可能根本无法响应中断，
 * 或者能够响应中断但是其响应的结果却未必是DeadlockDetector所期望的——释放其已持有的资源。
 *
 * 其次，自动恢复尝试可能导致新的问题。
 * 例如，如果 RecoverablePhilosopher（清单7-15）对中断的响应方式是仅仅保留中断标记而并不释放其已持有的资源，
 * 即RecoverablePhilosopher.pickUpChopstick 方法对InterruptedException异常的处理逻辑仅仅
 * 是调用Thread.currentThread().interrupt()以保留中断标记,那么尝试对这样的死锁线程进行恢复非但不能达到预期效果，
 * 反而会造成相应线程一直在尝试申请锁而一直无法申请成功，即产生活锁
 *
 * 2022/7/19
 */
public class DeadlockRecoveryDemo716 {

    public static void main(String[] args) throws Exception {
        // 创建并启动死锁检测与恢复线程
        new DeadlockDetector714().start();
        // 指定RecoverablePhilosopher为哲学家模型实现类
        System.setProperty("x.philo.impl", "RecoverablePhilosopher715");
        // 启动哲学家就餐问题模拟程序
        DiningPhilosopherProblem74.main(args);
    }

}
