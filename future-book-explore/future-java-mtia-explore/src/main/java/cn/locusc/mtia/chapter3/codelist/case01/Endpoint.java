package cn.locusc.mtia.chapter3.codelist.case01;

/**
 * @author Jay
 * 表示下游部件的节点
 *
 * 这里Endpoint的 online实例变量是个volatile变量,它用来表示相应节点的服务状态:是否在线。
 * 所有负载均衡算法实现类的抽象父类AbstractLoadBalancer内部会维护一个心跳线程（ heartbeatThread)来定时检测下游部件各个节点的状态，
 * 并根据检测的结果来更新相应Endpoint的online实例变量,
 * 如清单3-12所示。这里心跳线程根据检测结果更新volatile变量online 的值，
 * 而具体的负载均衡算法实现类(如 WeightedRoundRobinLoadBalancer )则根据变量online 的值决定其动作（跳过还是不跳过相应节点，见清单3-10)，
 * 从而满足了要求3。这个过程涉及了volatile 关键字的场景一的运用。
 * 2022/7/4
 */
public class Endpoint {

    public final String host;
    public final int port;
    public final int weight;
    private volatile boolean online = true;

    public Endpoint(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + weight;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Endpoint other = (Endpoint) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        if (weight != other.weight)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Endpoint [host=" + host + ", port=" + port + ", weight=" + weight
                + ", online=" + online + "]\n";
    }

}
