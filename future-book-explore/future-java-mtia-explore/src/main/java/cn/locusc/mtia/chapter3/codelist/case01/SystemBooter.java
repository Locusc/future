package cn.locusc.mtia.chapter3.codelist.case01;

import java.util.HashSet;
import java.util.Set;

public class SystemBooter {

    public static void main(String[] args) throws Exception {
        SystemBooter sysBooter = new SystemBooter();
        ServiceInvoker38 rd = ServiceInvoker38.getInstance();

        LoadBalancer39 lb = sysBooter.createLoadBalancer();

        // 在main线程中设置负载均衡器实例
        rd.setLoadBalancer39(lb);

    }

    // 根据系统配置创建负载均衡器实例
    private LoadBalancer39 createLoadBalancer() throws Exception {
        LoadBalancer39 lb;
        Candidate candidate = new Candidate(loadEndpoints());
        lb = WeightedRoundRobinLoadBalancer310.newInstance(candidate);
        return lb;
    }

    private Set<Endpoint> loadEndpoints() {
        Set<Endpoint> endpoints = new HashSet<Endpoint>();

        // 模拟从数据库加载以下信息
        endpoints.add(new Endpoint("192.168.101.100", 8080, 3));
        endpoints.add(new Endpoint("192.168.101.101", 8080, 2));
        endpoints.add(new Endpoint("192.168.101.102", 8080, 5));
        endpoints.add(new Endpoint("192.168.101.103", 8080, 7));
        return endpoints;
    }

}
