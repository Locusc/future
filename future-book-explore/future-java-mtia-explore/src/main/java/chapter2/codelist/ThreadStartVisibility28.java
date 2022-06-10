package chapter2.codelist;

import utils.Tools;

/**
 * @author Jay
 * 线程的启动, 停止与可见性
 * Java语言规范(JLS java language specification)保证
 * 父线程在启动子线程之前对共享变量的更新对于子线程来说是可见的
 * 2022/5/30
 */
public class ThreadStartVisibility28 {

    // 线程间的共享变量
    static int data = 0;

    /**
     * 如果我们把上述程序中的语句②注释掉, 则由于main线程在启动其子线程thread
     * 之前将共享变量data的值更新为1(见语句①), 因此子线程thread所读取到的共享变量data
     * 的值一定为1, 这是由于父线程在子线程启动前对共享变量的更新对子线程的可见性是有
     * 保证的; 如果我们没有将语句②注释掉，那么由于父线程在子线程启动之后对共享变量的
     * 更新对子线程的可见性是没有保证, 因此子线程thread此时读取到的共享变量data
     * 值可能为2, 也可能仍然为1, 这就解释了为什么多次运行上述程序可以发现其输出可能
     * 是"1", 也可能是"2"
     */
    public static void main(String[] args) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                // 使当前线程休眠R毫秒(R的值为随机数)
                Tools.randomPause(50);

                // 读取并打印变量data的值
                System.out.println(data);
            }
        };

        // 在子线程thread启动前更新变量data的值
        data = 1;// 语句①
        thread.start();

        // 使当前线程休眠R毫秒（R的值为随机数）
        Tools.randomPause(50);

        // 在子线程thread启动后更新变量data的值
        data = 2;// 语句②
    }

}
