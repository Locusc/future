package cn.locusc.dubbo.api.components;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperClientComponent {

    private final CuratorFramework client;

    private static final ZookeeperClientComponent zookeeperClientComponent;

    public ZookeeperClientComponent(CuratorFramework client) {
        this.client = client;
    }

    static {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        zookeeperClientComponent = new ZookeeperClientComponent(client);
        client.start();
    }

    public static CuratorFramework client() {
        return zookeeperClientComponent.client;
    }

}
