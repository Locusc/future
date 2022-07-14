package chapter3.codelist;

import utils.stf.*;

/**
 * @author Jay
 * volatile 对有序性的保障 Demo
 * 2022/7/4
 */
@ConcurrencyTest(iterations = 200000)
public class VolatileOrderingDemo36 {
    private int dataA = 0;
    private long dataB = 0L;
    private String dataC = null;
    private volatile boolean ready = false;

    @Actor
    public void writer() {
        dataA = 1;
        dataB = 10000L;
        dataC = "Content...";
        ready = true;
    }

    @Observer({
            @Expect(desc = "Normal", expected = 1),
            @Expect(desc = "Impossible", expected = 2),
            @Expect(desc = "ready not true", expected = 3)
    })
    public int reader() {
        int result = 0;
        boolean allISOK;
        if (ready) {
            allISOK = (1 == dataA) && (10000L == dataB) && "Content...".equals(dataC);
            result = allISOK ? 1 : 2;
        } else {
            result = 3;
        }
        return result;
    }

    public static void main(String[] args) throws InstantiationException,
            IllegalAccessException {

        // 调用测试工具运行测试代码
        TestRunner.runTest(VolatileOrderingDemo36.class);
    }

}
