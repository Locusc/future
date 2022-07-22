package chapter7.codelist;

import utils.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jay
 * 本程序演示嵌套监视器锁死（线程活性故障）现象, 嵌套监视器锁死 Demo
 *
 * 等待线程由于唤醒其所需的条件永远无法成立,或者其他线程无法唤醒这个线程而一直处于非运行状态（线程并未终止)导致其任务一直无法进展，
 * 那么我们就称这个线程被锁死(Lockout )。锁死就好比睡美人的故事中睡美人醒来的前提是她要得到王子的亲吻。
 * 但是如果王子无法亲吻她（比如王子“挂了”……)，那么睡美人将一直沉睡!
 *
 * 有些资料可能将锁死与死锁混为一谈，这样做表面看来似乎没有什么害处，毕竟锁死与死锁有着共同的外在表现——故障线程一直处于非运行状态而使得其任务无法进展。
 * 但是，锁死与死锁的产生条件是不同的，即便是在产生死锁的所有必要条件都不成立的情况下(此时死锁不可能产生)，锁死仍然可能出现。
 * 因此，“对付”死锁的办法未必能够用来“对付”锁死，将锁死与死锁区分开来是有必要的。按照锁死产生的条件来分，锁死包括信号丢失锁死和嵌套监视器锁死。
 *
 * 7.2.1 信号丢失锁死
 * 信号丢失锁死是由于没有相应的通知线程来唤醒等待线程而使等待线程一直处于等待状态的一种活性故障。
 * 信号丢失锁死的一个典型例子是等待线程在执行Object.wait()/Condition.await()前没有对保护条件进行判断，
 * 而此时保护条件实际上可能已然成立，然而此后可能并无其他线程更新相应保护条件涉及的共享变量使其成立并通知等待线程,
 * 这就使得等待线程一直处于等待状态,从而使其任务一直无法进展。这就是我们在第5章中强调Object.wait()/Condition.await()必须放在一个循环语句中的原因之一。
 * 信号丢失锁死的另外一个常见例子是CountDownLatch.countDown()调用没有放在 finally 块中导致CountDownLatch.await()的执行线程一直处于等待状态，
 * 从而使其任务一直无法进展。
 *
 * 7.2.2 嵌套监视器锁死
 * 嵌套监视器锁死( Nested Monitor Lockout)是嵌套锁导致等待线程永远无法被唤醒的一种活性故障.
 * 假设某个程序使用如图7-4所示的受保护方法及相应的通知方法来实现“等待/通知”。
 * 我们知道，等待线程在其执行到 monitorY.wait()的时候会被暂停并且其所持有的锁monitorY会被释放,
 * 但是等待线程所持有的外层锁monitorX并不会因此( Object.wait()调用）而被释放。
 * 通知线程在调用monitorY.notifyAll()来唤醒等待线程时需要持有相应的锁monitorY，
 * 但是由于monitorY所引导的临界区位于monitorX引导的临界区之内，因此通知线程必须先持有外层锁monitorX。
 * 而通知线程执行通知方法的时候，其所需申请的monitorX可能正好被等待线程所持有，因此通知线程无法唤醒等待线程。
 * 而等待线程只有在被唤醒之后(退出内层临界区）才能够释放其持有的外层锁monitorX。
 * 于是，通知线程始终无法获得锁monitorX，从而无法通过monitorY.notifyAll()调用来唤醒等待线程，这使得等待线程一直处于非运行状态（这里是BLOCKED状态)。
 * 这种由于嵌套锁导致通知线程始终无法唤醒等待线程的活性故障就被称为嵌套监视器锁死。
 *
 * 我们实际接触到的代码可能并不像图7-4所示的那样特征明显。清单7-17展示了一个嵌套监视器锁死Demo，
 * 这是一个简单的生产者—消费者实例,其中 main线程是生产者线程，而工作者线程(WorkerThread实例)是消费者线程。
 * 从表面上看，这个 Demo并没有符合图7-4所示的特征，但是运行这个Demo可以发现该程序很快就“冻住”了(可能没有任何输出)。
 * 而查看该程序的线程转储，我们并未发现有死锁。
 *
 * 从等待/通知的角度来看，这个生产者线程相当于通知线程，它无法生产“产品”就不会通知等待线程（消费者线程）队列非空，
 * 那么等待线程(Thread-0)就会一直处于等待状态，而等待线程一直处于等待状态则会导致其持有的内部锁（Ox00000000d72f9b80，
 * NestedMonitorLockoutDemo当前实例对应的内部锁)一直无法被释放。这样，生产者线程便永远无法生产“产品”，而消费者线程也永远处于等待状态。
 * 在这个Demo 中 ,ArrayBlockingQueue.take()/ArrayBlockingQueue.put(E)内部使用的锁连同
 * NestedMonitorLockoutDemo.doProcess方法/accept方法自身使用的内部锁事实上形成了图7-4所示的代码特征——在嵌套锁的内层临界区中调用Object.wait()/notify()/notifyAll)
 * 或者Condition.await()/signal()/signalAll()。因此，我们看到的“冻住”现象实际上是嵌套监视器锁死，而不是死锁!
 * 从死锁产生的必要条件角度出发，我们不难看出嵌套监视器锁死与死锁的区别。尽管上述Demo也存在嵌套锁，
 * 但是由于其中的两个线程( main线程和Thread-O)都是按照全局统一的顺序（先申请NestedMonitorLockoutDemo当前实例对应的内部锁，
 * 再申请ArrayBlockingQueue实例内部的显式锁)来申请锁的，这相当于采取前文提到的“锁排序法”来规避死锁，因此该Demo不可能出现死锁。
 * 尽管如此，该Demo仍然出现锁死。在本质上，嵌套监视器锁死是由于通知线程无法获得锁，导致其无法唤醒等待线程，
 * 最终使等待线程永远处于等待状态的活性故障;而死锁是由于所有故障线程都无法获得其所需的锁而导致的活性故障。
 * 在上述Demo 中,我们只需要将ArrayBlockingQueue.take()调用挪到doProcess方法之外就可以规避嵌套监视器锁死了，如下代码片段所示:
 *
 * protected synchronized void doProcess(string msg) throws InterruptedException {
 *  system.out.println("Process: " + msg) ;
 *  processed++;
 * }
 *
 * @override
 * class workerThread extends Thread {
 *  public void run(){
 *      try {
 *          string msg;
 *          while (true) {
 *              msg = queue.take();
 *              doProcess (msg);
 *          }
 *      } catch (InterruptedException e) {
 *          ;
 *      }
 * }
 *
 * 2022/7/20
 */
public class NestedMonitorLockoutDemo717 {

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    private int processed = 0;

    private int accepted = 0;

    public static void main(String[] args) throws InterruptedException {
        NestedMonitorLockoutDemo717 demo = new NestedMonitorLockoutDemo717();
        demo.start();
        int i = 0;
        while (i-- < 100000) {
            System.out.println(i);
            demo.accept("message: " + i);
            Tools.randomPause(100);
        }

    }

    /**
     * 同步方法持有的内部锁, 相当于该对象实例, 所以这里使用的是同一个内部锁
     */
    public synchronized void accept(String message) throws InterruptedException {
        // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
        queue.put(message);
        System.out.println(message);
        accepted++;
    }

    protected synchronized void doProcess() throws InterruptedException {
        // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
        String msg = queue.take();
        System.out.println("Process:" + msg);
        processed++;
    }

    public void start() {
        new WorkerThread().start();
    }

    public synchronized int[] getStat() {
        return new int[] { accepted, processed };
    }

    class WorkerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    doProcess();
                }
            } catch (InterruptedException e) {
                ;
            }
        }
    }

}
