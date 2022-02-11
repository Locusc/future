package chapter1.codelist;

import org.junit.Test;

public class WelcomeApp {

    @Test
    public void threadTest1() {
        // 创建线程
        Thread welcomeThread = new WelcomeThread();
        // 启动线程
        welcomeThread.start();
        // 输出"当前线程"的线程名称
        System.out.printf("1.Welcome! I`m %s.%n",  Thread.currentThread().getName());
    }

    @Test
    public void threadTest2() {
        // 创建线程
        Thread welcomeThread = new Thread(new WelcomeTask());
        // 启动线程
        welcomeThread.start();
        // 输出"当前线程"的线程名称
        System.out.printf("1.Welcome! I`m %s.%n",  Thread.currentThread().getName());
    }

    @Test
    public void threadTest3() {
        // 创建线程
        Thread welcomeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.printf("2.Welcome! I`m %s.%n",  Thread.currentThread().getName());
            }
        });
        // 启动线程
        welcomeThread.start();
        // 这里直接调用线程的run方法, 仅是出于演示的目的
        welcomeThread.run();
        // 输出"当前线程"的线程名称
        System.out.printf("1.Welcome! I`m %s.%n",  Thread.currentThread().getName());
    }

    static class WelcomeThread extends Thread {
        @Override
        public void run() {
            System.out.printf("2.Welcome! I`m %s.%n",  Thread.currentThread().getName());
        }
    }

    static class WelcomeTask implements Runnable {
        // 在该方法中实现线程的任务处理逻辑
        @Override
        public void run() {
            // 输出"当前线程"的线程名称
            System.out.printf("2.Welcome! I`m %s.%n",  Thread.currentThread().getName());
        }
    }

}
