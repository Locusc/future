package cn.locusc.mtia.chapter2.codelist;

import cn.locusc.mtia.utils.Tools;

/**
 * @author Jay
 *
 * 线程终止与可见性
 * 与线程的启动, 停止与可见性类似
 * Java语言规范保证一个线程终止后该线程对共享变量的更新对于调用该线程
 * join方法的线程而言是可见的.
 *
 * 2022/5/30
 */
public class ThreadJoinVisibility29 {

    // 线程间的共享变量
    static int data = 0;

    /**
     * 线程thread运行时将共享变量data的值更新为1, 因此main线程对
     * 线程thread的join方法调用结束后, 该线程读取的共享变量data值为1这一点是有保证的
     */
    public static void main(String[] args) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                // 使当前线程休眠R毫秒(R的值为随机数)
                Tools.randomPause(50);

                // 更新data的值
                data = 1;
            }
        };

        thread.start();

        // 等待线程thread结束后, main线程才继续运行
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // 读取并打印变量data的值
        System.out.println(data);
    }

}
