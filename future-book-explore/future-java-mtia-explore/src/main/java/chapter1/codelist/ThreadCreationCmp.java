package chapter1.codelist;

import utils.Tools;

public class ThreadCreationCmp {

    public static void main(String[] args) {
        Thread t;
        CountingTask countingTask = new CountingTask();

        // 获取处理器个数
        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();

        // 直接创建线程
        for (int i = 0; i < 2 * numberOfProcessors; i++) {
            t = new Thread(countingTask);
            t.start();
        }

        // 以子类的方式创建线程
        for (int i = 0; i < 2 * numberOfProcessors; i++) {
            t = new CountingThread();
            t.start();
        }
    }

    static class Counter {
        private int count;

        public void increment() {
            count++;
        }

        public int value() {
            return count;
        }
    }

    static class CountingTask implements Runnable {

        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                this.doSomething();
                counter.increment();
            }
            System.out.println(Thread.currentThread().getName());
            System.out.println("CountingTask :" + counter.value());

        }

        private void doSomething() {
            Tools.randomPause(80);
        }

    }

    static class CountingThread extends Thread {

        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                this.doSomething();
                counter.increment();
            }
            System.out.println(Thread.currentThread().getName());
            System.out.println("CountingThread :" + counter.value());
        }

        private void doSomething() {
            Tools.randomPause(80);
        }
    }

}
