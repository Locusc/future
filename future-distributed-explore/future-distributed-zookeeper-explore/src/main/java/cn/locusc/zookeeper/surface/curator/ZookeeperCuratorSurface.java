package cn.locusc.zookeeper.surface.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class ZookeeperCuratorSurface {

    private static final String ZK_CURATOR_C1 = "/zk-curator/c1";

    public static void main(String[] args) throws Exception {
        CuratorFramework fluentZkSession = createFluentZkSession();
        createZookeeperNode(ZK_CURATOR_C1, fluentZkSession);
    }

    /**
     * 创建zookeeper会话
     */
    private static CuratorFramework createZkSession() {
        ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(
                1000,
                3
        );

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .newClient("127.0.0.1:2181", exponentialBackoffRetry);

        curatorFramework.start();

        return curatorFramework;
    }

    /**
     * 创建zookeeper会话, fluent方式
     */
    private static CuratorFramework createFluentZkSession() {
        ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(
                1000,
                3
        );

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(exponentialBackoffRetry)
                .namespace("base")
                .build();

        curatorFramework.start();

        return curatorFramework;
    }

    /**
     * 节点递归创建
     */
    private static String createZookeeperNode(String path, CuratorFramework curatorFramework) throws Exception {
        return curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path, "create curator node".getBytes());
    }

    /**
     * 获取节点的数据内容
     */
    private byte[] obtainNodeValue(String path, CuratorFramework curatorFramework) throws Exception {
        return curatorFramework.getData()
                .forPath(path);
    }

    /**
     * 获取节点的状态信息
     */
    private byte[] obtainNodeStat(String path, CuratorFramework curatorFramework) throws Exception {
        return curatorFramework.getData()
                .storingStatIn(new Stat())
                .forPath(path);
    }

    /**
     * 更新节点内容
     * 如果更新一个不存在的版本 抛出BadVersionException
     */
    private int updateNodeValue(String path, CuratorFramework curatorFramework, byte[] value) throws Exception {
        return curatorFramework.setData()
                .withVersion(-1)
                .forPath(path, value)
                .getVersion();
    }

    /**
     * 删除节点
     */
    private Void deleteNode(String path, CuratorFramework curatorFramework) throws Exception {
        return curatorFramework.delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .forPath(path);
    }

}
