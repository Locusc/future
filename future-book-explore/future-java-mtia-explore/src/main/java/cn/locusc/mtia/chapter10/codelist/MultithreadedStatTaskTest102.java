package cn.locusc.mtia.chapter10.codelist;

import cn.locusc.mtia.chapter4.codelist.case02.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Jay
 *
 * 依赖注入 ( Dependency Injection)。在抽象与实现分离的基础上我们可以进一步实现依赖注人。
 * 所谓依赖注入就是指一个对象关联（通常是通过实例变量）另外一个对象（依赖）的时候，
 * 该对象并不直接创建其依赖对象，而是通过第三方向其提供（注入）相应对象而实现的。
 * 依赖注人使得我们对一个对象进行单元测试时可以使用一个测试桩（Stub)对象来替代该对象的真实依赖，
 * 从而简化了单元测试。例如，在第4章的第2个实战案例（响应延时统计）中，
 * AbstractStatTask类(参见清单4-5)的一个构造器允许我们在创建实例时指定其依赖StatProcessor实例，
 * 该构造器使得我们在单元测试时可以不使用StatProcessor接口的现有实现类RecordProcessor而是指定一个测试桩对象。
 *
 * 关注点分离( Separation Of Concern )。在多线程程序中，
 * 将程序中的功能型关注点( Functional Concern）与线程相关的性能关注点( Performance Concern)分离可以极大地提高代码的可测试性。
 * 例如，第4章第2个实战案例(响应延时统计)实现的多线程程序的核心功能是，根据指定的日志记录统计外部系统的响应延时情况。
 * 该核心功能由本身完全是依照单线程模型来写的RecordProcessor类(代码见本书配套下载资源）实现的，
 * 因此RecordProcessor类完全可以按照单线程的方式进行单独测试（比如使用Junit )，其测试通过则意味着该程序的核心功能测试通过。
 * 而该程序直接与线程打交道的代码只有MultithreadedStatTask 类（参见清单4-7)和 LogReaderThread类(参见清单4-10)，
 * 这就使得对这些类进行单元测试时我们只需要关心这些代码本身所需完成的处理而无须关心该程序的核心功能(它应该落实在 RecordProcessor类的单元测试上），
 * 从而降低了测试难度。清单10-2展示了MultithreadedStatTask类的单元测试用例代码。
 * MultithreadedStatTask的一个构造器允许我们指定一个StatProcessor接口（其作用参见表10-1）实现:
 *
 * public MultithreadedstatTask(int sampleInterval,statProcessor recordProcessor)
 * 在此，由于MultithreadedStatTask 类才是我们的单元测试目标，因此在创建MultithreadedStatTask实例mt的时候，
 * 我们并不指定StatProcessor接口的真实实现类RecordProcessor(参见表10-1)，而是使用测试桩类FakeProcessor的实例。
 *
 * MultithreadedStatTask.createLogReader()会创建MultithreadedStatTask读取日志记录所需的AbstractLogReader实例。
 * 此时，由于AbstractLogReader 实例的真实实现类 LogReaderThread（我们已经使用清单10-1中的测试用例对其进行了单元测试）并非我们的测试目标，
 * 因此我们并不直接创建MultithreadedStatTask 实例，而是创建MultithreadedStatTask 的一个匿名子类，
 * 并在该匿名类中覆盖MultithreadedStatTask.createLogReader(),
 * 使其返回的AbstractLogReader实例为一个测试桩类实例( AbstractLogReader类的一个匿名子类)。
 *
 * 这里，我们为单元测试目标类MultithreadedStatTask所依赖的其他对象（包括StatProcessor实例和AbstractLogReader实例）
 * 都创建了相应的测试桩（ Stub )对象，从而降低了测试难度。相反，如果该程序的核心功能是直接夹杂在MultithreadedStatTask类之中的，
 * 那么我们对MultithreadedStatTask类进行单元测试时势必隐含着对核心功能的测试。
 *
 * 使工作者线程数可以配置。多线程 Bug 的触发往往与程序的并发程度有关，因此使程序中的工作者线程数量可以配置，
 * 便于我们在测试中动态调整线程数以提高或者降低并发程度。
 *
 * 2022/7/22
 */
public class MultithreadedStatTaskTest102 {

    private MultithreadedStatTask47 mst;
    private int recordCount = 0;
    private String[] records;

    @Before
    public void setUp() throws Exception {
        records = new String[4];
        records[0] = "2016-03-30 09:33:04.644|SOAP|request|SMS|sendSms|OSG|ESB|00200000000|192.168.1.102|13612345678|136712345670";
        records[1] = "2016-03-30 09:33:04.688|SOAP|response|SMS|sendSmsRsp|ESB|OSG|00200000000|192.168.1.102|13612345678|136712345670";
        records[2] = "2016-03-30 09:33:04.732|SOAP|request|SMS|sendSms|ESB|NIG|00210000001|192.168.1.102|13612345678|136712345670";
        records[3] = "2016-03-30 09:33:04.772|SOAP|response|SMS|sendSmsRsp|NIG|ESB|00210000004|192.168.1.102|13612345678|136712345670";
        mst = createTask(10, 3, "sendSms", "*");
    }

    @After
    public void tearDown() throws Exception {
        recordCount = 0;
    }

    @Test
    public void testRun() {
        // 只关心MultithreadedStatTask本身（与多线程有关）
        mst.run();
        assertTrue(records.length == recordCount);
    }

    private MultithreadedStatTask47 createTask(
            int sampleInterval,
            int traceIdDiff, String expectedOperationName,
            String expectedExternalDeviceList) throws Exception {

        // Stub对象
        final AbstractLogReader49 logReader = new AbstractLogReader49(
                new ByteArrayInputStream(new byte[] {}), 1024, 4) {
            boolean eof = false;
            RecordSet48 consumedBatch = new RecordSet48(super.batchSize);

            @Override
            protected RecordSet48 getNextToFill() {
                return null;
            }

            @Override
            protected RecordSet48 nextBatch() {
                if (eof) {
                    return null;
                }
                for (String r : records) {
                    consumedBatch.putRecord(r);
                }
                eof = true;
                return consumedBatch;
            }

            @Override
            protected void publish(RecordSet48 recordBatch) {
                // 什么也不做
            }

            @Override
            public void run() {
                // 什么也不做
            }
        };

        // 返回MultithreadedStatTask的匿名子类
        return new MultithreadedStatTask47(sampleInterval, new FakeProcessor()) {
            @Override
            protected AbstractLogReader49 createLogReader() {
                // 并不返回AbstractLogReader类的真实实现类LogReaderThread，而是一个Stub类实例
                return logReader;
            }
        };// 不使用StatProcessor的真实实现类RecordProcessor，而是使用Stub类FakeProcessor
    }// createTask结束

    // Stub类
    class FakeProcessor implements StatProcessor {
        @Override
        public void process(String record) {
            recordCount++;
        }

        @Override
        public Map<Long, DelayItem> getResult() {
            // 不关心该方法，故返回空的Map
            return Collections.emptyMap();
        }
    }// FakeProcessor结束

}
