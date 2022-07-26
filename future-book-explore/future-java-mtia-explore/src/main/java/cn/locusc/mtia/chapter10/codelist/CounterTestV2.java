package cn.locusc.mtia.chapter10.codelist;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.L_Result;

/**
 * @author Jay
 * 计数器Counter JCStress测试用例简化版
 * 有了上述基础，我们就能够将上述测试用例进一步简化:直接采用@State来注解测试用例类本身，如清单10-5所示。
 *
 * 并发操作多轮执行结束后，JCStress 能够生成测试报告，如图10-7所示。
 * 从该报告中可以看出某些情况下测试的结果为1而不是我们所期望的2,可见 Counter类并非线程安全。
 *
 * JCStress的优点在于其简单性。JCStress 的使用非常简明，使用JCStress 编写单元测试代码，我们几乎不需要调用JCStress 的任何API。
 * 这使得测试代码的开发者能够更加专注于测试用例本身的实现而不是与测试工具本身有关的细节以及API。
 *
 * JCStress的缺点表现在以下几个方面。
 *
 * 文档的缺乏。JCStress 的相关文档比较少，不过这点一定程度上可以被其简单性
 * 所弥补。另外，JCStress 工程的子目录tests-custom下有不少针对JDK标准库类的测试用例，阅读这些测试用例的源码是学习JCStress的一种有效途径。
 *
 * 与其他工具的集成。JCStress目前并没有与JUnit集成。JCStress本身并不提供与Eclipse的集成，
 * 不过JCStress能够以Maven项目的形式被集成到Eclipse工程之中。
 * 本书配套下载资源中的 Maven工程( JCStress-tests )有简明文档( Readme.doc )介绍了如何在Eclipse 中配置和使用JCStress。
 *
 * 不便于测试代码本身的调试。JCStress并不是直接执行我们所编写的测试用例类，而是执行相应的自动生成的类。
 * 例如，针对清单10-4中的测试用例CounterTest类，JCStress所执行的是一个名为CounterTest_jcstress 的类，
 * 该类是在Maven构建的时候由自动生成的代码自动编译而成的。因此，JCStress不便于测试代码本身的调试。
 *
 * 选用多线程程序测试工具、框架时的一个重要考量是简单性-—测试代码的开发者能够更加专注于测试用例本身的实现而不是与测试工具本身有关的细节以及API，
 * 这正是本书介绍JCStress的原因之一。
 *
 * 2022/7/22
 */
@JCStressTest
@State
@Description("测试Counter的线程安全性")
@Outcome(id = "[2]", expect = Expect.ACCEPTABLE, desc = "OK")
@Outcome(id = "[1]", expect = Expect.FORBIDDEN, desc = "丢失更新或者读脏数据")
public class CounterTestV2 {
    final Counter103 counter = new Counter103();
    @Actor
    public void actor1() {
        counter.increment();
    }

    @Actor
    public void actor2() {
        counter.increment();
    }

    @Arbiter
    public void actor3(L_Result r) {
        r.r1 = counter.vaule();
    }
}
