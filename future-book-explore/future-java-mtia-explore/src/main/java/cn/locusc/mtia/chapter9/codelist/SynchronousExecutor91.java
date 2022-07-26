package cn.locusc.mtia.chapter9.codelist;

import java.util.concurrent.Executor;

/**
 * @author Jay
 * 使用Executor接口实现任务的同步执行
 * 9.2 Java Executor框架
 * Runnable接口和 Callable接口都是对任务处理逻辑的抽象,这种抽象使得我们无须关心任务的具体处理逻辑:
 * 不管是什么样的任务，其处理逻辑总是展现为一个具有统一签名的方法——Runnable.run()或者Callable.call().
 * java.util.concurrent.Executor接口则是对任务的执行进行的抽象，该接口仅定义了如下方法:
 * void execute(Runnable command)
 *
 * 其中，command参数代表需要执行的任务。Executor接口使得任务的提交方(相当于生产者）只需要知道它调用Executor.execute方法便可以使指定的任务被执行，
 * 而无须关心任务具体的执行细节:比如，任务是采用一个专门的工作者线程执行的，还是采用线程池执行的;采用什么样的线程池执行的;
 * 多个任务是以何种顺序被执行的。可见，Executor接口使得任务的提交能够与任务执行的具体细节解耦(Decoupling )。
 * 和对任务处理逻辑的抽象类似，对任务执行的抽象也能给我们带来信息隐藏（Information)和关注点分离( Separation Of Concern)的好处。
 *
 * 解耦任务的提交与任务的具体执行细节所带来的好处的一个例子是,它在一定程度上能够屏蔽任务同步执行与异步执行的差异。
 * 例如，对于同一个任务(Runnable实例),如果我们把它提交给一个 ThreadPoolExecutor(它实现了Executor接口)执行，
 * 那么该任务就是异步执行;如果把这个任务提交给如清单9-1所示的Executor实例执行，那么该任务就是同步执行。
 * 这个任务不管是同步执行还是异步执行，对于其提交方来说并没有太大差别，这就为更改任务的具体执行方式提供了灵活性和便利:
 * 更改任务的具体执行细节可能不会影响到任务的提交方，而这意味着更小的代码改动量和测试量。
 *
 * 可见，Executor接口一定程度上缩小了同步编程与异步编程的代码编写方式。
 *
 * Executor接口比较简单，功能也十分有限:首先，它只能为客户端代码执行任务，而无法将任务的处理结果返回给客户端代码;
 * 其次，Executor接口实现类内部往往会维护一些工作者线程，当我们不再需要一个 Executor实例的时候，
 * 往往需要主动将该实例内部维护的工作者线程停掉以释放相应的资源，而Executor接口并没有定义相应的方法。
 *
 * ExecutorService接口继承自Executor接口，它解决了上述问题。ExecutorService接口定义了几个submit方法，
 * 这些方法能够接受Callable接口或者Runnable接口表示的任务并返回相应的 Future实例,从而使客户端代码提交任务后可以获取任务的执行结果。
 * ExecutorService接口还定义了shutdown()方法和shutdownNow()方法来关闭相应的服务(比如关闭其维护的工作者线程)。
 * ThreadPoolExecutor是 ExecutorService的默认实现类。
 *
 * 2022/7/21
 */
public class SynchronousExecutor91 implements Executor {

    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
