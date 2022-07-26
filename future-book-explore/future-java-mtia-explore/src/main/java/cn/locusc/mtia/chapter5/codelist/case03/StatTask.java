package cn.locusc.mtia.chapter5.codelist.case03;

import cn.locusc.mtia.chapter4.codelist.case02.AbstractLogReader49;
import cn.locusc.mtia.chapter4.codelist.case02.MultithreadedStatTask47;

import java.io.InputStream;

public class StatTask extends MultithreadedStatTask47 {

    public StatTask(InputStream in, int sampleInterval, int traceIdDiff,
                    String expectedOperationName, String expectedExternalDeviceList) {
        super(in, sampleInterval, traceIdDiff, expectedOperationName,
                expectedExternalDeviceList);
    }

    @Override
    protected AbstractLogReader49 createLogReader() {
        return new ExchangerBasedLogReaderThread513(in, inputBufferSize, batchSize);
    }

}