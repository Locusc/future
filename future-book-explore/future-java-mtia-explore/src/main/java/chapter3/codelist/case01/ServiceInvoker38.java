package chapter3.codelist.case01;

import utils.Debug;

/**
 * @author Jay
 * 调用下游部件服务
 * 2022/7/4
 */
public class ServiceInvoker38 {

    // 保存当前类的唯一实例
    private static final ServiceInvoker38 INSTANCE = new ServiceInvoker38();

    // 负载均衡器实例，使用volatile变量保障可见性 场景二
    private volatile LoadBalancer39 loadBalancer39;

    // 私有构造器
    private ServiceInvoker38() {
        // 什么也不做
    }

    /**
     * 获取当前类的唯一实例
     */
    public static ServiceInvoker38 getInstance() {
        return INSTANCE;
    }

    /**
     * 根据指定的负载均衡器派发请求到特定的下游部件。
     * 接着，我们为ServiceInvoker 设置一个实例变量loadBalancer用来保存LoadBalancer实例（即具体的负载均衡算法)。
     * 这里，我们使用volatile关键字修饰loadBalancer，就是属于volatile关键字的场景二的运用:
     * ServiceInvoker的 dispatchRequest方法会通过调用getLoadBalancer()方法来读取volatile变量 loadBalancer，
     * 该方法运行在业务线程（即 Web服务器的工作者线程）中。
     * 当系统的启动线程（即 main线程）或者配置管理线程（负责配置数据的刷新）更新了变量loadBalancer 的值之后，
     * 所有业务线程在无须使用锁的情况下也能够读取到更新后的 loadBalancer变量值，这实现了对负载均衡算法的动态调整，即满足了要求2。
     * @param request 待派发的请求
     */
    public void dispatchRequest(Request request) {
        // 这里读取volatile变量loadBalancer
        Endpoint endpoint = getLoadBalancer39().nextEndpoint();

        if (null == endpoint) {
            // 省略其他代码

            return;
        }

        // 将请求发给下游部件
        dispatchToDownstream(request, endpoint);

    }

    // 真正将指定的请求派发给下游部件
    private void dispatchToDownstream(Request request, Endpoint endpoint) {
        Debug.info("Dispatch request to " + endpoint + ":" + request);
        // 省略其他代码
    }

    public LoadBalancer39 getLoadBalancer39() {
        // 读取负载均衡器实例
        return loadBalancer39;
    }

    public void setLoadBalancer39(LoadBalancer39 loadBalancer39) {
        // 设置或者更新负载均衡器实例
        this.loadBalancer39 = loadBalancer39;
    }

}
