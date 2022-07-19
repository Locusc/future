package chapter3.codelist.case01;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Jay
 * 负载均衡算法抽象实现类，所有负载均衡算法实现类的父类
 *
 * Candidate实例的状态包括下游部件的服务器节点列表( endpoints)以及这些节点的总权重( totalWeight )。
 * 如果下游部件的服务器节点需要变更，例如要增加一个服务器节点或者有个节点的权重需要调整，
 * 那么，我们需要同时更新服务器节点列表以及相应的总权重。
 * 这里所谓的“同时”意味着这个更新操作必须是一个原子操作，否则其他线程可能看到总权重与服务器节点列表中各个节点的权重总和不一致的情形。
 * 如果 Candidate类的状态是可变的,那么为了保障这个操作的原子性,我们往往需要借助锁。
 * 而在这个案例中,Candidate是个不可变对象，
 * 因此这个更新操作通过创建一个新的Candidate实例并以该实例为参数调用AbstractLoadBalancer.updateCandidate方法（参见清单3-12)即可实现。
 * AbstractLoadBalancer类内部会维护一个volatile实例变量candidate来引用Candidate实例,如下代码片段所示:
 *
 * 这里，candidate实例变量是配置管理线程（负责执行updateCandidate方法）和业务线程所共享的对象。
 * volatile关键字保障了对实例变量candidate的写操作的原子性，从而保障整个更新操作（更新下游部件的节点以及总权重)的原子性。
 * 另外，volatile关键字还保障了这种更新的结果对于业务线程的可见性。
 *
 * 从上述例子中可以看出，不可变对象可以使我们在无须借助锁的情况下实现线程安全，从而避免了锁可能产生的问题以及开销。
 *
 * 有时创建严格意义上的不可变对象比较难,此时不妨考虑使用等效或者近似的不可变对象，这也同样有利于发挥不可变对象的优势。
 * 所谓“等效或者近似”，就是尽可能地满足不可变对象所需的条件。例如，上述案例中涉及的Endpoint类(参见清单3-11)就是一个等效不可变对象。
 *
 * 不可变对象的使用能够对垃圾回收效率产生影响，其影响既有消极的也有积极的。
 * 由于基于不可变对象的设计中系统状态的变更是通过创建新的不可变对象实例来实现的,
 * 因此，当系统的状态频繁变更或者不可变对象所占用的内存空间比较大时，不可变对象的不断创建会增加垃圾回收的负担。
 * 但是，不可变对象的使用也可能有利于降低垃圾回收的开销。
 * 这是因为创建不可变对象往往导致堆空间年轻代( Young Generation )中的对象（新创建的不可变实例)引用年老代( Old Generation)中的对象。
 * 而这种对象引用方式，相比于使用状态可变的对象所导致的年老代对象引用年轻代对象的引用方式,更加有利于减少垃圾回收的开销:
 * 修改一个状态可变对象的实例变量值的时候，如果这个对象已经位于年老代中，那么在垃圾回收器进行下一轮次要回收(Minor Collection )的时候，
 * 年老代中包含这个对象的卡片( Card，年老代中存储对象的存储单位，一个 Card 的大小为512字节)中的所有对象都必须被扫描一遍，
 * 以确定年老代中是否有对象对待回收的对象持有引用。因此，年老代对象持有对年轻代对象的引用会导致次要回收的开销增加。
 *
 * 2022/7/4
 */
public abstract class AbstractLoadBalancer implements LoadBalancer39 {

    private final static Logger LOGGER = Logger.getAnonymousLogger();
    // 使用volatile变量替代锁（有条件替代）
    protected volatile Candidate candidate;

    // 在3.8.3节的实战案例中，我们使用final修饰AbstractLoadBalancer类(见清单3-12)的 java.util.Random型实例变量random（随机数生成器)，
    // 这不仅仅是因为该变量一经初始化就无须更新，更为重要的是由于我们要保障线程安全:random变量的初始化是在一个线程（ main线程）进行中，
    // 而其使用(通过super.random.nextDouble()调用来生成随机数）是在另外一种线程（业务线程，即 nextEndpoint()方法的执行线程）中进行的(参见清单3-10 ),
    // 因此我们必须保障业务线程读取到random变量的值是初始值(而不是默认值null)，并且该值所引用的Random实例是初始化完毕的。
    protected final Random random;
    // 心跳线程
    private Thread heartbeatThread;

    public AbstractLoadBalancer(Candidate candidate) {
        if (null == candidate || 0 == candidate.getEndpointCount()) {
            throw new IllegalArgumentException("Invalid candidate " + candidate);
        }
        this.candidate = candidate;
        random = new Random();
    }

    public synchronized void init() throws Exception {
        if (null == heartbeatThread) {
            heartbeatThread = new Thread(new HeartbeatTask(), "LB_Heartbeat");
            heartbeatThread.setDaemon(true);
            heartbeatThread.start();
        }
    }

    /**
     * page num 135
     */
    @Override
    public void updateCandidate(final Candidate candidate) {
        if (null == candidate || 0 == candidate.getEndpointCount()) {
            throw new IllegalArgumentException("Invalid candidate " + candidate);
        }
        // 更新volatile变量candidate
        this.candidate = candidate;
    }

    /*
     * 留给子类实现的抽象方法
     *
     * @see chapter3.codelist.case01.LoadBalancer39.nextEndpoint
     */
    @Override
    public abstract Endpoint nextEndpoint();

    protected void monitorEndpoints() {
        // 读取volatile变量
        final Candidate currCandidate = candidate;
        boolean isTheEndpointOnline;

        // 检测下游部件状态是否正常
        for (Endpoint endpoint : currCandidate) {
            isTheEndpointOnline = endpoint.isOnline();
            if (doDetect(endpoint) != isTheEndpointOnline) {
                endpoint.setOnline(!isTheEndpointOnline);
                if (isTheEndpointOnline) {
                    LOGGER.log(java.util.logging.Level.SEVERE, endpoint
                            + " offline!");
                } else {
                    LOGGER.log(java.util.logging.Level.INFO, endpoint
                            + " is online now!");
                }
            }
        }// for循环结束

    }

    // 检测指定的节点是否在线
    private boolean doDetect(Endpoint endpoint) {
        boolean online = true;
        // 模拟待测服务器随机故障
        int rand = random.nextInt(1000);
        if (rand <= 500) {
            online = false;
        }
        return online;
    }

    private class HeartbeatTask implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    // 检测节点列表中所有节点是否在线
                    monitorEndpoints();
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                // 什么也不做
            }
        }
    }// HeartbeatTask类结束

}
