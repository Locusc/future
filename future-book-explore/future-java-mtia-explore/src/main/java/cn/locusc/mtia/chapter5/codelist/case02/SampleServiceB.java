package cn.locusc.mtia.chapter5.codelist.case02;

import cn.locusc.mtia.utils.Tools;

import java.util.concurrent.CountDownLatch;

public class SampleServiceB extends AbstractService {

    public SampleServiceB(CountDownLatch latch) {
        super(latch);
    }

    @Override
    public void doStart() throws Exception {
        // 模拟实际操作耗时
        Tools.randomPause(2000);
        // 省略其他代码
    }

}
