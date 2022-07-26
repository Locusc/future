package cn.locusc.mtia.chapter4.codelist.case02;

import cn.locusc.mtia.utils.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Jay
 * 日志文件读取线程。
 * 2022/7/13
 */
public abstract class AbstractLogReader49 extends Thread {

    protected final BufferedReader logFileReader;
    // 表示日志文件是否读取结束
    protected volatile boolean isEOF = false;
    protected final int batchSize;

    public AbstractLogReader49(InputStream in, int inputBufferSize, int batchSize) {
        logFileReader = new BufferedReader(new InputStreamReader(in),
                inputBufferSize);
        this.batchSize = batchSize;
    }

    protected RecordSet48 getNextToFill() {
        return new RecordSet48(batchSize);
    }

    /* 留给子类实现的抽象方法 */
    // 获取下一个记录集
    protected abstract RecordSet48 nextBatch()
            throws InterruptedException;

    // 发布指定的记录集
    protected abstract void publish(RecordSet48 recordBatch)
            throws InterruptedException;

    @Override
    public void run() {
        RecordSet48 recordSet48;
        boolean eof = false;
        try {
            while (true) {
                recordSet48 = getNextToFill();
                recordSet48.reset();
                eof = doFill(recordSet48);
                publish(recordSet48);
                if (eof) {
                    if (!recordSet48.isEmpty()) {
                        publish(new RecordSet48(1));
                    }
                    isEOF = eof;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Tools.silentClose(logFileReader);
        }
    }

    protected boolean doFill(final RecordSet48 recordSet48) throws IOException {
        final int capacity = recordSet48.capacity;
        String record;
        for (int i = 0; i < capacity; i++) {
            record = logFileReader.readLine();
            if (null == record) {
                return true;
            }
            // 将读取到的日志记录存入指定的记录集
            recordSet48.putRecord(record);
        }
        return false;
    }

}
