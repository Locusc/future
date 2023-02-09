package cn.locusc.duo.jvm.part4.chapter3.codelist;

/**
 * @author Jay
 * 死锁代码样例
 * 2023/1/9
 */
public class ThreadDeadLockTestCase49 {

    static class SysAddRunnable implements Runnable {
        int a,b;
        public SysAddRunnable(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public void run() {
            synchronized (Integer.valueOf(b)) {
                synchronized (Integer.valueOf(a)) {
                    System.out.println(a + b);
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(new SysAddRunnable(1,2)).start();
            new Thread(new SysAddRunnable(2,1)).start();
        }
    }

}
