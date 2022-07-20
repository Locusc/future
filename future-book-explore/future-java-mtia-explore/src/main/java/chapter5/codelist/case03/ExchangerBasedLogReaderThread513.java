package chapter5.codelist.case03;

import chapter4.codelist.case02.AbstractLogReader49;
import chapter4.codelist.case02.RecordSet48;

import java.io.InputStream;
import java.util.concurrent.Exchanger;

/**
 * @author Jay
 * 使用 Exchanger 作为传输通道实例
 *
 *
 *
 * 2022/7/15
 */
public class ExchangerBasedLogReaderThread513 extends AbstractLogReader49 {

    private final Exchanger<RecordSet48> exchanger;

    private volatile RecordSet48 nextToFill;

    private RecordSet48 consumedBatch;


    public ExchangerBasedLogReaderThread513(InputStream in, int inputBufferSize,
                                            int batchSize) {
        super(in, inputBufferSize, batchSize);
        exchanger = new Exchanger<RecordSet48>();
        nextToFill = new RecordSet48(batchSize);
        consumedBatch = new RecordSet48(batchSize);
    }

    @Override
    protected RecordSet48 getNextToFill() {
        return nextToFill;
    }

    @Override
    protected RecordSet48 nextBatch() throws InterruptedException {
        consumedBatch = exchanger.exchange(consumedBatch);
        if (consumedBatch.isEmpty()) {
            consumedBatch = null;
        }
        return consumedBatch;
    }

    @Override
    protected void publish(RecordSet48 recordSet) throws InterruptedException {
        nextToFill = exchanger.exchange(recordSet);
    }

}
