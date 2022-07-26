package cn.locusc.mtia.chapter10.codelist;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.L_Result;

/**
 * @author Jay
 *
 * 一个非线程安全的计数器
 * 10.2.4多线程程序的单元测试:JcStress
 * JCStress是OpenJDK下的一个试验性项目,它可以用来编写多线程程序的单元测试8。
 * JCStress非常直观地体现了多线程程序测试的本质—―对特定的共享状态进行并发操作，
 * 然后检查实际共享状态（结果）是否符合我们的期望。
 * 相应地，JCStress提供了一组注解( Annotation)和工具类（参见表10-2)，这极大地简化了测试代码编写。
 *
 * 针对清单10-3所示的计数器类Counter,我们可以设计一个简单的测试用例,如表10-3所示。
 *
 * 在上述测试用例中，我们用@State来注解类StateObject，这表示该类包含了该测试用例所访问的共享状态——测试目标对象Counter类的实例。
 * 我们用@Actor来注解actor1、actor2方法，这表示这些方法要对共享状态进行并发操作。用@Actor注解的方法可以声明类型为代表共享状态的对象的参数。
 * 此外，这些方法还可以声明代表测试结果数据(比如LongResult1 )的参数用于向JCStress 报告结果数据。
 * JCStress 会采用一个线程池来执行这些并发操作，并且为了提高触发多线程Bug 的概率,默认情况下每个并发操作会被执行5轮。
 * 一个测试用例内所有用@Actor注解的方法都被执行一遍算一轮并发操作执行结束，
 * 每轮并发操作执行结束之后该测试用例内所有用@Arbiter注解的方法就会被执行一次。因此，通常我们会在@Arbiter所注解的方法中收集测试结果数据。
 * 例如在上述代码中，我们在 actor3方法中声明了一个LongResultl参数用于向JCStress 提供测试结果数据°。
 * 由于JCStress所提供的表示测试结果的工具类仅支持int、double、boolean这类基础数据类型，
 * 因此在收集测试结果数据的时候我们可能需要将结果数据转换为基础类型数据。而JCStress则根据测试用例中@Outcome注解的内容来对测试结果进行解读，
 * 即判定并记录相应的结果是否可以接受。
 *
 * 2022/7/22
 */
@JCStressTest
@Description("测试Counter的线程安全性")
@Outcome(id = "[2]", expect = Expect.ACCEPTABLE, desc = "OK")
@Outcome(id = "[1]", expect = Expect.FORBIDDEN, desc = "丢失更新或者读脏数据")
class CounterTest104 {
    @State
    public static class StateObject {
        final Counter103 counter = new Counter103();
    }

    @Actor
    public void actor1(StateObject sh) {
        sh.counter.increment();
    }

    @Actor
    public void actor2(StateObject sh) {
        sh.counter.increment();
    }

    @Arbiter
    public void actor3(L_Result r, StateObject sh) {
        r.r1 = sh.counter.vaule();
    }
}
