package cn.locusc.mtia.chapter10.codelist;

import static org.junit.Assert.assertTrue;
import cn.locusc.mtia.chapter4.codelist.case02.LogReader49Thread;
import cn.locusc.mtia.chapter4.codelist.case02.RecordSet48;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Jay
 * LogReaderThread单元测试示例JUnit代码
 *
 * 提高多线程程序的可测试性(Testability ）可以从以下几个方面入手。
 * 抽象(Abstraction)与实现(Implementation)分离。
 * 抽象与实现分离是面向对象编程的基本原则——面向接口编程,它不仅可以提高代码的可读性和可扩展性，
 * 同时也能提高代码的可测试性。例如,第4章的第2个实战案例(响应延时统计)充分体现了这一点，该案例中使用的抽象与实现如表10-1所示。
 * cn.locusc.mtia.chapter10\images\10-1-响应演示统计程序中抽象与实现分离.jpg
 *
 * 数据与数据来源分离。程序所处理的数据可以来自用户输入、文件、数据库以及网络等，而对数据的处理逻辑代码应该只关心数据本身，
 * 而不应该关心数据的来源。这种数据与其来源的分离可被看作抽象与实现分离的一个具体应用，它可以降低耦合性(Coupling )，
 * 并提高代码的灵活性和可测试性。例如，在第4章的第2个实战案例(响应延时统计）中，尽管该程序的输入数据来自文件（接口日志文件),
 * 但是负责读取日志文件记录的实现类LogReaderThread(代码参见清单4-10 )本身并不直接使用File或者FileInputStream而是使用InputStream来表示
 * 其输入, 如下代码片段所示
 * public LogReaderThread(Inputstream in, int inputBufferSize, int batchSize){
 *  super(in, inputBuffersize, batchsize);
 * }
 * 这使得在对LogReaderThread 进行单元测试的时候，我们可以根本不借助文件而是直接使用一个普通对象来表示输入数据(一组日志记录),如清单10-1所示。
 * 2022/7/22
 */
public class LogReaderThreadTest101 {

    private LogReader49Thread logReader;
    private StringBuilder sdb;

    @Before
    public void setUp() throws Exception {
        sdb = new StringBuilder();
        sdb.append("2016-03-30 09:33:04.644|SOAP|request|SMS|sendSms|OSG|ESB|00200000000|192.168.1.102|13612345678|136712345670");
        sdb.append("\n2016-03-30 09:33:04.688|SOAP|response|SMS|sendSmsRsp|ESB|OSG|00200000000|192.168.1.102|13612345678|136712345670");
        sdb.append("\n2016-03-30 09:33:04.732|SOAP|request|SMS|sendSms|ESB|NIG|00210000001|192.168.1.102|13612345678|136712345670");
        sdb.append("\n2016-03-30 09:33:04.772|SOAP|response|SMS|sendSmsRsp|NIG|ESB|00210000004|192.168.1.102|13612345678|136712345670\n");

        InputStream in = new ByteArrayInputStream(sdb.toString().getBytes("UTF-8"));
        logReader = new LogReader49Thread(in, 1024, 4);
        logReader.start();
    }

    @After
    public void tearDown() throws Exception {
        logReader.interrupt();
    }

    @Test
    public void testNextBatch() {
        try {
            RecordSet48 rs = logReader.nextBatch();
            StringBuilder contents = new StringBuilder();
            String record;
            while (null != (record = rs.nextRecord())) {
                contents.append(record).append("\n");
            }
            assertTrue(contents.toString().equals(sdb.toString()));
        } catch (InterruptedException ignored) {
        }
    }

}
