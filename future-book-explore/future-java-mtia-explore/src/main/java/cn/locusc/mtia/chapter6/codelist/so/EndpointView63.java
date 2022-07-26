package cn.locusc.mtia.chapter6.codelist.so;

import cn.locusc.mtia.chapter3.codelist.case01.Endpoint;
import cn.locusc.mtia.utils.Debug;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Jay
 * 对服务器节点进行排序
 *
 * 无状态对象具有线程安全性，这有两层含义:首先，无状态对象的客户端代码在调用该对象的任何方法时都无须加锁。
 * 例如EndpointView.retrieveServerList()在访问DefaultEndpointComparator实例的时候无须加锁。
 * 其次，无状态对象自身的方法实现也无须使用锁。例如，DefaultEndpointComparator.compare方法中没有使用任何锁。
 *
 * 2022/7/18
 */
public class EndpointView63 {

    static final Comparator<Endpoint> DEFAULT_COMPARATOR;

    static {
        DEFAULT_COMPARATOR = new DefaultEndpointComparator62();
    }

    // 省略其他代码

    public Endpoint[] retrieveServerList(Comparator<Endpoint> comparator) {
        Endpoint[] serverList = doRetrieveServerList();
        Arrays.sort(serverList, comparator);
        return serverList;
    }

    public Endpoint[] retrieveServerList() {
        return retrieveServerList(DEFAULT_COMPARATOR);
    }

    private Endpoint[] doRetrieveServerList() {
        // 模拟实际代码
        Endpoint[] serverList = new Endpoint[] {
                new Endpoint("192.168.1.100", 8080, 5),
                new Endpoint("192.168.1.101", 8081, 3),
                new Endpoint("192.168.1.102", 8082, 2),
                new Endpoint("192.168.1.103", 8080, 4) };
        serverList[0].setOnline(false);
        serverList[3].setOnline(false);
        return serverList;
    }

    public static void main(String[] args) {
        EndpointView63 endpointView = new EndpointView63();
        Endpoint[] serverList = endpointView.retrieveServerList();
        Debug.info(Arrays.toString(serverList));
    }

}
