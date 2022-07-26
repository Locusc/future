package cn.locusc.mtia.chapter4.codelist.case02;

import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jay
 * 日志读取线程实现类
 * 2022/7/13
 */
public class LogReader49Thread extends AbstractLogReader49 {

    // 线程安全的队列
    final BlockingQueue<RecordSet48> channel = new ArrayBlockingQueue<RecordSet48>(2);

    public LogReader49Thread(InputStream in, int inputBufferSize, int batchSize) {
        super(in, inputBufferSize, batchSize);
    }

    @Override
    public RecordSet48 nextBatch()
            throws InterruptedException {
        RecordSet48 batch;
        // 从队列中取出一个记录集
        batch = channel.take();
        if (batch.isEmpty()) {
            batch = null;
        }
        return batch;
    }

    @Override
    protected void publish(RecordSet48 recordBatch) throws InterruptedException {
        // 记录集存入队列
        channel.put(recordBatch);
    }

}
