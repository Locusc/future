package cn.locusc.mtia.chapter2.codelist;

import cn.locusc.mtia.utils.Tools;

/**
 * @author Jay
 *
 * 约定:
 *    对于同一个共享变量而言,一个线程更新了该变量的值之后, 他线程能够读取
 * 到这个更新后的值, 那么这个值就被称为该变量的相对新值, 如果读取这个共享变
 * 的线程在读取并使用该变量的时候其 线程无法更新该变量的值, 那么该线程读取到
 * 的相对新值就被称为该变量的最新值
 *
 * 可见性的保障仅仅意味着一个线程能够读取到共享变量的相对新值
 * 而不能保障改线程能够读取相应变量的最新值
 *
 * 2022/5/30
 */
public class VisibilityDemo27 {

    public static void main(String[] args) throws InterruptedException {
        TimeConsumingTask timeConsumingTask = new TimeConsumingTask();
        Thread thread = new Thread(timeConsumingTask);
        thread.start();

        // 指定的时间内任务没有执行结束的话, 就将其取消
        Thread.sleep(30000);
        timeConsumingTask.cancel();
    }

    static class TimeConsumingTask implements Runnable {

//        private boolean toCancel = false;

        // 提示JIT编译器被修饰变量可能被多个线程共享
        // 以阻止JIT编译器做出可能导致程序运行不正常的优化
        // 另一个作用就是读取一个volatile关键字修饰的变量会使响应的处理器执行冲刷处理器缓存的动作
        // 从而保证了可见性
        private volatile boolean toCancel = false;

        @Override
        public void run() {
            while (!toCancel) {
                if (this.doExecute()) {
                    break;
                }

                if(toCancel) {
                    System.out.println("Task was canceled.");
                } else {
                    System.out.println("Task done.");
                }
            }

            // JIT优化后
//            if (!toCancel) {
//                while (true) {
//                    if (this.doExecute()) {
//                        break;
//                    }
//                }
//            }
        }

        private boolean doExecute() {
            boolean isDone = false;
            System.out.println("executing...");

            // 模拟实际操作的时间消耗
            Tools.randomPause(50);
            // 省略其他代码

            return isDone;
        }

        public void cancel() {
            toCancel = true;
            System.out.println(this + "cancel.");
        }

    }

}
