package chapter2.codelist;

import utils.Tools;

public class RaceConditionDemo22 {

    public static void main(String[] args) {

        // 客户端线程数
        int numberOfThreads = args.length > 0 ? Short.parseShort(args[0]) : Runtime
                .getRuntime().availableProcessors();

        Thread[] workerThreads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            workerThreads[i] = new WorkerThread(i, 10);
        }

        // 待所有线程创建完毕后，再一次性将其启动，以便这些线程能够尽可能地在同一时间内运行
        for (Thread ct : workerThreads) {
            ct.start();
        }

    }

    // 模拟业务线程
    static class WorkerThread extends Thread {

        private final int requestCount;

        public WorkerThread(int id, int requestCount) {
            super("worker-" + id);
            this.requestCount = requestCount;
        }

        @Override
        public void run() {
            int i = requestCount;
            String requestID;
            RequestIDGenerator21 requestIDGen = RequestIDGenerator21.getInstance();
            while (i-- > 0) {
                // 生成Request ID
                requestID = requestIDGen.nextID();
                this.processRequest(requestID);
            }
        }

        // 模拟请求处理
        private void processRequest(String requestID) {
            // 模拟请求处理耗时
            Tools.randomPause(50);
            System.out.printf("%s got requestID: %s %n",
                    Thread.currentThread().getName(), requestID);
        }
    }
}
