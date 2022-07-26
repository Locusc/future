package cn.locusc.mtia.chapter3.codelist.case03;

import cn.locusc.mtia.utils.Tools;

public class CaseRunner3_3 {

    public static void main(String[] args) throws InterruptedException {
        final AlarmMgr324 alarmMgr324 = AlarmMgr324.INSTANCE;
        Thread[] threads = new Thread[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < threads.length; i++) {
            // 模拟多个线程调用alarmMgr.init();
            threads[i] = new Thread() {
                @Override
                public void run() {
                    alarmMgr324.init();
                }
            };
        }

        // 启动并等待指定的线程结束
        Tools.startAndWaitTerminated(threads);
    }

}
