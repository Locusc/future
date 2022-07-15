package chapter4.codelist;

import utils.DESEncryption;
import utils.Tools;

/**
 * @author Jay
 * 实际测量 WT/ST Demo
 * java -Dx.prepause=40000 -Dx.postpause=10000
 *
 * Ucpu为目标CPU使用率（О<Ucpu≤ 1 ),WT ( Wait Time)为程序花费在等待（例如等待IO操作结果)上的时长，
 * ST( Service Time)为程序实际占用处理器执行计算的时长。
 * 在实践中,我们可以使用jvisualvm提供的监控数据计算出WT/ST的值。
 * 2022/7/13
 */
public class WTSTMeasureDemo implements Runnable {
    final long waitTime;

    public WTSTMeasureDemo(long waitTime) {
        this.waitTime = waitTime;
    }

    public static void main(String[] args) throws Exception {
        main0(args);
    }

    public static void main0(String[] args) throws Exception {
        final int argc = args.length;
        int nThreads = argc > 0 ? Integer.valueOf(args[0]) : 1;
        long waitTime = argc >= 1 ? Long.valueOf(args[0]) : 4000L;
        WTSTMeasureDemo demo = new WTSTMeasureDemo(waitTime);
        Thread[] threads = new Thread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new Thread(demo);
        }
        long s = System.currentTimeMillis();
        Tools.startAndWaitTerminated(threads);
        long duration = System.currentTimeMillis() - s;
        long serviceTime = duration - waitTime;
        System.out.printf(
                "WT/ST: %-4.2f, waitTime：%dms, serviceTime：%dms, duration：%4.2fs%n",
                waitTime * 1.0f / serviceTime,
                waitTime, serviceTime,
                duration * 1.0f / 1000);
    }

    @Override
    public void run() {
        try {
            // 模拟I/O操作
            Thread.sleep(waitTime);

            // 模拟实际执行计算
            String result = null;
            for (int i = 0; i < 400000; i++) {
                result = DESEncryption.encryptAsString(
                        "it is a cpu-intensive task" + i,
                        "12345678");
            }
            System.out.printf("result:%s%n", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
