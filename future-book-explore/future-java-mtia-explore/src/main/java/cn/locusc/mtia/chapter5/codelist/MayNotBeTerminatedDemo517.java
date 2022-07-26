package cn.locusc.mtia.chapter5.codelist;

import cn.locusc.mtia.utils.Debug;

/**
 * @author Jay
 * 线程中断标记不能作为线程停止的demo
 *
 * Thread.interrupt() 将该线程的中断标记设置为true
 * Thread.currentThread().isInterrupted() 获取该线程的中断标记值
 * Thread.interrupted() 获取并重置只中断标记值(返回当前线程的中断标记值,并将当前线程中断标记重置为false)
 *
 * 实际上，由于发起线程在执行workerThread.interrupt()的时候workerThread可能正在执行task.run()，
 * 而task.run()中的代码可能会清除(“吞没”)线程中断标记，从而使得workerThread依旧无法终止，如清单5-17所示。
 *
 * 但是，workerThread却依然未终止。由此可见，从通用的角度来看，
 * 我们不能使用线程中断标记作为线程停止标记，而需要使用一个专门的实例变量来作为线程停止标记。
 * 2022/7/15
 */
public class MayNotBeTerminatedDemo517 {

    public static void main(String[] args) throws InterruptedException {
        TaskRunner514 tr = new TaskRunner514();
        tr.init();

        tr.submit(() -> {
            Debug.info("before doing task");
            try {
                System.out.println(Thread.currentThread().isInterrupted()); // true
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // 注意: 依照惯例，抛出InterruptedException异常的方法，通常会在其抛出该异常时将当前线程的线程中断标记重置为false。
                // 什么也不做:这会导致线程中断标记被清除
                System.out.println(Thread.currentThread().isInterrupted()); // false
            }
            Debug.info("after doing task");
        });

        tr.workerThread.interrupt();

        // main0(args);
    }

    public static void main0(String[] args) throws InterruptedException {
        TerminatableTaskRunner518 tr = new TerminatableTaskRunner518();
        tr.init();

        tr.submit(() -> {
            Debug.info("before doing task");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // 注意: 依照惯例，抛出InterruptedException异常的方法，通常会在其抛出该异常时将当前线程的线程中断标记重置为false。
                // 什么也不做:这会导致线程中断标记被清除
            }
            Debug.info("after doing task");
        });

        tr.cancelTask();
    }
}
