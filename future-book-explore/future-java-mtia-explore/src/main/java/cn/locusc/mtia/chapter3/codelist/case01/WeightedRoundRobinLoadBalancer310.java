package cn.locusc.mtia.chapter3.codelist.case01;

/**
 * @author Jay
 * 加权轮询负载均衡算法实现类
 * 2022/7/4
 */
public class WeightedRoundRobinLoadBalancer310 extends AbstractLoadBalancer {

    // 私有构造器
    private WeightedRoundRobinLoadBalancer310(Candidate candidate) {
        super(candidate);
    }

    // 通过该静态方法创建该类的实例
    public static LoadBalancer39 newInstance(Candidate candidate)
            throws Exception {
        WeightedRoundRobinLoadBalancer310 lb =
                new WeightedRoundRobinLoadBalancer310(candidate);
        lb.init();
        return lb;

    }

    // 在该方法中实现相应的负载均衡算法
    @Override
    public Endpoint nextEndpoint() {
        Endpoint selectedEndpoint = null;
        int subWeight = 0;
        int dynamicTotoalWeight;
        final double rawRnd = super.random.nextDouble();
        int rand;

        // 读取volatile变量candidate
        final Candidate candiate = super.candidate;
        dynamicTotoalWeight = candiate.totalWeight;
        for (Endpoint endpoint : candiate) {
            // 选取节点以及计算总权重时跳过非在线节点
            if (!endpoint.isOnline()) {
                dynamicTotoalWeight -= endpoint.weight;
                continue;
            }
            rand = (int) (rawRnd * dynamicTotoalWeight);
            subWeight += endpoint.weight;
            if (rand <= subWeight) {
                selectedEndpoint = endpoint;
                break;
            }
        }
        return selectedEndpoint;
    }

}
