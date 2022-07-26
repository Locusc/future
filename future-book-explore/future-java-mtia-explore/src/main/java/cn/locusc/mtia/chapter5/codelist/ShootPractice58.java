package cn.locusc.mtia.chapter5.codelist;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Jay
 * CyclicBarrier使用实例
 *
 * 有时候多个线程可能需要相互等待对方执行到代码中的某个地方（集合点)，这时这些线程才能够继续执行。
 * 这种等待类似于大家相约去爬山的情形:大家事先约定好时间和集合点，先到的人必须在集合点等待其他未到的人，只有所有参与人员到齐之后大家才能够出发去登山。
 * JDK 1.5开始引入了一个类java.util.concurrent.CyclicBarrier，该类可以用来实现这种等待。
 * CyclicBarrier类的类名中虽然包含Barrier这个单词,但是它和我们前面讲的内存屏障没有直接的关联。
 * 类名中Cyclic表示CyclicBarrier 实例是可以重复使用的。
 *
 * 使用CyclicBarrier实现等待的线程被称为参与方( Party )。
 * 参与方只需要执行CyclicBarrier.await()就可以实现等待。
 * 尽管从应用代码的角度来看，参与方是并发执行CyclicBarrier.await()的，
 * 但是，CyclicBarrier 内部维护了一个显式锁，这使得其总是可以在所有参与方中区分出一个最后执行CyclicBarrier.await()的线程，
 * 该线程被称为最后一个线程。除最后一个线程外的任何参与方执行CyclicBarrier.await()都会导致该线程被暂停(线程生命周期状态变为 WAITING)。
 * 最后一个线程执行CyclicBarrier.await()会使得使用相应CyclicBarrier实例的其他所有参与方被唤醒，
 * 而最后一个线程自身并不会被暂停。如果把参与方比作爬山例子中的登山者，
 * 那么参与方执行CyclicBarrier.await()而被暂停就相当于登山者到达指定的集合点等待其他登山者;
 * 最后一个线程执行CyclicBarrier.await()则相当于最后一个登山者到达集合点，这使得所有登山者都无须继续等待（被唤醒)而是可以一起出发去爬山（线程继续运行)。
 * 与CountDownLatch不同的是，CyclicBarrier实例是可重复使用的:所有参与方被唤醒的时候，任何线程再次执行CyclicBarrier.await()又会被暂停，
 * 直到这些线程中的最后一个线程执行了CyclicBarrier.await()。
 *
 * 下面看一个例子。该例子模拟了士兵参与打靶训练。所有参与训练的士兵( Soldier )被分为若干组(Rank )，
 * 其中每组被称为一排。一排中士兵的个数等于靶子的个数。每次只能够有一排士兵进行射击。
 * 一排中的士兵必须同时开始射击，并且射击完毕的士兵必须等待同排的其他所有士兵射击完毕后才能够整排地撤离射击点。
 * 一排中的士兵射击结束后腾出射击点和靶子,换另外一排中的士兵进行下一轮射击，如此交替进行，直到训练时间结束，如清单5-8所示。
 *
 *  //////////
 *
 * 我们使用了两个CyclicBarrier实例: startBarrier 和 shiftBarrier。
 * 其中 startBarrier 用于实现当前排的士兵在同--时刻开始射击,shiftBarrier用于实现当前排的士兵在该排所有士兵射击完毕后同时撤离打靶位置。
 * 由于一排中的士兵必须同时开始射击，因此一排中的任意一个士兵在其开始射击前必须等待同排中的其他士兵准备就绪,等到该排中所有士兵准备就绪的时候，
 * 该排中的所有士兵都开始射击。这种等待的模拟是通过执行startBarrier.await()实现的（见语句③)。
 * 虽然一排中的士兵是同时开始射击的，但是由于不同士兵的熟练程度不同，因此他们的射击结束时间是不同的。
 * 一排中先射击完毕的士兵必须等待同排中的其他士兵都射击完毕后才能撤离射击点。
 * 因此，先射击完毕的士兵需要原地等待，这种等待的模拟是通过调用shiftBarrier.await()实现的（见语句④)。
 * 另外，一排士兵射击结束后撤离射击点时下一排士兵可以进入射击点。这种打靶轮次的转换模拟是在语句①中实现的。
 * CyclicBarrier的其中一个构造器允许我们指定一个被称为barrierAction的任务（ Runnable 接口实例)。
 * barrierAction会被最后一个线程执行CyclicBarrier.await方法时执行，该任务执行结束后其他等待线程才会被唤醒。
 * 语句①就是利用了这一点而被放到了barrierAction.run()之中执行，从而确保了一排士兵射击结束后，下一排进行射击的士兵也随之确定了。
 *
 * 从以上输出可以看出:其一，一排中的士兵开始射击的时间点非常接近（时间相差仅几毫秒);
 * 其二，执行语句①的线程总是模拟最后一个结束射击的士兵的工作者线程（最后一个线程);其三，进行射击的排次是从О开始循环递增的。
 *
 *
 * 由于CyclicBarrier 内部实现是基于条件变量的,因此CyclicBarrier 的开销与条件变量的开销相似，其主要开销在可能产生的上下文切换。
 *
 * 扩展阅读CyclicBarrier的内部实现
 * CyclicBarrier内部使用了一个条件变量trip来实现等待/通知。
 * CyclicBarrier内部实现使用了分代 ( Generation)的概念用于表示CyclicBarrier 实例是可以重复使用的。
 * 除最后一个线程外的任何一个参与方都相当于一个等待线程，这些线程所使用的保护条件是“当前分代内,尚未执行await方法的参与方个数( parties )为0”。
 * 当前分代的初始状态是parties等于参与方总数（通过构造器中的 parties参数指定)。
 * CyclicBarrier.await()每被执行一次会使相应实例的parties值减少1。
 * 最后一个线程相当于通知线程，它执行CyclicBarrier.await()会使相应实例的parties值变为0，
 * 此时该线程会先执行 barrierAction.run()，然后再执行trip.signalAll()来唤醒所有等待线程。
 * 接着，开始下一个分代，即使得CyclicBarrier 的 parties值又重新恢复为其初始值。
 *
 * 设cyclicBarrier 为一个任意的 CyclicBarrier实例，
 * 任意一个参与方在执行cyclicBarrier.await()前所执行的任何操作对barrierAction.run()而言是可见的、有序的。
 * barrierAction.run()中所执行的任何操作对所有参与方在 cyclicBarrier.await()调用成功返回之后的代码而言是可见的、有序的。
 * 如图5-4所示(图中实线表示其一端连接的操作对箭头指向的代码是可见的、有序的, cn.locusc.mtia.chapter5\images\CyclicBarrier中的可见性和有序性保障.png)。
 *
 * CyclicBarrier的典型应用场景
 * CyclicBarrier 的典型应用场景包括以下几个，它们都可以在上述例子中找到影子。
 * 使迭代( Iterative)算法并发化。在并发化的迭代算法中，迭代操作是由多个工作者线程并行执行的。
 * CyclicBarrier 可用来实现执行迭代操作的任何一个工作者线程必须等待其他工作者线程也完成当前迭代操作的情况下才继续其下一轮的迭代操作，
 * 以便形成迭代操作的中间结果作为下一轮迭代的基础(输入)。因此，该应用场景从代码上反映出来的是，
 * CyclicBarrier.await()调用是在一个循环中执行的，正如清单5-8中语句④所在的循环语句所展示的那样。
 *
 * 在测试代码中模拟高并发。在编写多线程程序的测试代码时，我们常常需要使用有限的工作者线程来模拟高并发操作。
 * 为此，CyclicBarrier可用来实现这些工作者线程中的任意一个线程在执行其操作前必须等待其他线程也准备就绪
 * ，即使得这些工作者线程尽可能地在同一时刻开始其操作，正如上述例子我们使用CyclicBarrier来实现一排中的士兵在同一时刻开始射击。
 *
 * CyclicBarrier往往被滥用,其表现是在没有必要使用CyclicBarrier的情况下使用了CyclicBarrier。
 * 这种滥用的一个典型例子是利用CyclicBarrier的构造器参数barrierAction来指定一个任务，
 * 以实现一种等待线程结束的效果: barrierAction中的任务只有在目标线程结束后才能够被执行。
 * 事实上，这种情形下我们完全可以使用更加对口的 Thread.join()或者CountDownLatch来实现。
 * 因此，如果代码对CyclicBarrier.await()调用不是放在一个循环之中，并且使用CyclicBarrier的目的也不是为了模拟高并发操作，
 * 那么此时对CyclicBarrier 的使用可能是一种滥用。
 *
 * 2022/7/14
 */
public class ShootPractice58 {

    // 参与打靶训练的全部士兵
    final Soldier[][] rank;
    // 靶的个数，即每排中士兵的个数
    final int N;
    // 打靶持续时间（单位：秒）
    final int lasting;
    // 标识是否继续打靶
    volatile boolean done = false;
    // 用来指示进行下一轮打靶的是哪一排的士兵
    volatile int nextLine = 0;

    final CyclicBarrier shiftBarrier;

    final CyclicBarrier startBarrier;

    public ShootPractice58(int N, final int lineCount, int lasting) {
        this.N = N;
        this.lasting = lasting;
        // 五行四列
        this.rank = new Soldier[lineCount][N];
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < N; j++) {
                rank[i][j] = new Soldier(i * N + j);
            }
        }

        shiftBarrier = new CyclicBarrier(N, new Runnable() {
            @Override
            public void run() {
                // 更新下一轮打靶的排
                nextLine = (nextLine + 1) % lineCount;// 语句①
                Debug.info("Next turn is :%d", nextLine);
            }
        });
        // 语句②
        startBarrier = new CyclicBarrier(N);
    }

    public static void main(String[] args) throws InterruptedException {
        ShootPractice58 sp = new ShootPractice58(4, 5, 24);
        sp.start();
    }

    public void start() throws InterruptedException {
        // 创建并启动工作者线程
        Thread[] threads = new Thread[N];
        for (int i = 0; i < N; ++i) {
            threads[i] = new Shooting(i);
            threads[i].start();
        }
        // 指定时间后停止打靶
        Thread.sleep(lasting * 1000);
        stop();
        for (Thread t : threads) {
            t.join();
        }
        Debug.info("Practice finished.");
    }

    public void stop() {
        done = true;
    }

    class Shooting extends Thread {
        final int index;

        public Shooting(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            Soldier soldier;
            try {
                while (!done) {
                    soldier = rank[nextLine][index];
                    // 一排中的士兵必须同时开始射击
                    startBarrier.await();// 语句③
                    // 该士兵开始射击
                    soldier.fire();
                    // 一排中的士兵必须等待该排中的所有其他士兵射击完毕才能够离开射击点
                    shiftBarrier.await();// 语句④
                }
            } catch (InterruptedException e) {
                // 什么也不做
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }// run方法结束
    }// 类Shooting定义结束

    // 参与打靶训练的士兵
    static class Soldier {
        private final int seqNo;

        public Soldier(int seqNo) {
            this.seqNo = seqNo;
        }

        public void fire() {
            Debug.info(this + " start firing...");
            Tools.randomPause(5000);
            System.out.println(this + " fired.");
        }

        @Override
        public String toString() {
            return "Soldier-" + seqNo;
        }

    }// 类Soldier定义结束

}
