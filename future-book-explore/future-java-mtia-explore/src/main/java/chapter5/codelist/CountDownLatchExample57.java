package chapter5.codelist;

import utils.Debug;
import utils.Tools;

import java.util.concurrent.CountDownLatch;

/**
 * @author Jay
 * 一个线程多次执行 CountDownlatch.countDown()示例
 *
 * 前文我们说过CountDownLatch 的构造器中的参数既可以表示先决操作的数量，也可以表示先决操作需要被执行的次数。
 * 在上述实战案例中，CountDownLatch 的构造器中的参数的含义就属于前者。
 * 而后者表示我们可以在一个线程中多次调用同一个CountDownLatch 实例的 countDown方法，以使相应实例的内部计数器值达到0，如清单5-7所示。
 *
 * 我们在创建CountDownLatch 实例latch的时候指定的构造器参数为4。
 * 尽管latch.countDown()一共会被子线程 workerThread执行10次，但是该程序的输出总是如下:It's done. data=4
 * 这里程序输出的data为4而不是10是由于:首先，latch.countDown()被workerThread执行了4次之后，main线程对latch.await()的调用就返回了，
 * 从而使该线程被唤醒。其次，workerThread在执行 latch.countDown()前所执行的操作（更新共享变量data)的结果
 * 对等待线程( main线程)从latch.await()返回之后的代码可见，因此 main线程被唤醒时能够读取到此前workerThread在 latch.countDown()调用
 * 返回前的操作结果——-data被更新为4。
 *
 * 这里，latch.countDown()被 workerThread 执行的次数大于4次并不会导致异常，也不会导致latch 内部状态（计数器值)的变更。
 *
 * 2022/7/14
 */
public class CountDownLatchExample57 {

    private static final CountDownLatch latch = new CountDownLatch(4);
    private static int data;

    public static void main(String[] args) throws InterruptedException {
        Thread workerThread = new Thread() {
            @Override
            public void run() {
                for (int i = 1; i < 10; i++) {
                    data = i;
                    latch.countDown();
                    // 使当前线程暂停（随机）一段时间
                    Tools.randomPause(1000);
                }

            };
        };
        workerThread.start();
        latch.await();
        Debug.info("It's done. data=%d", data);
    }

}
