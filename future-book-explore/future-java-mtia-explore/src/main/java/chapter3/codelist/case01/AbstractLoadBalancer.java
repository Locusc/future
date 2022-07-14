package chapter3.codelist.case01;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Jay
 * 负载均衡算法抽象实现类，所有负载均衡算法实现类的父类
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
