package cn.locusc.zookeeper.surface.original;

import lombok.SneakyThrows;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jay
 * zookeeper原生使用
 * 2022/5/9
 */
public class NativeZookeeperSurface implements Watcher {

    private final String ZK_PERSISTENT = "/zk-persistent";

    private final String ZK_EPHEMERAL = "/zk-ephemeral";

    private final String ZK_PERSISTENT_SEQUENTIAL = "/zk-persistent_sequential";

    private static final CountDownLatch countDownLatch = new CountDownLatch(5);

    private static ZooKeeper zookeeper;

    /**
     * 客户端可以通过创建一个zk实例来连接zk服务器
     * new Zookeeper(connectString,sessionTimeOut,watcher)
     * connectString: 连接地址,IP,端口
     * sessionTimeOut: 会话超时时间, 单位毫秒
     * watcher: 监听器(当特定事件触发监听时,zk会通过watcher通知到客户端)
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper("127.0.0.1:2181", 5000, new NativeZookeeperSurface());
        System.out.println(String.format("zookeeper status: %s", zookeeper.getState()));
        countDownLatch.await();
    }

    /**
     * 监听方法
     * 回调方法: 处理来自服务器端的watcher通知
     */
    @SneakyThrows
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
            System.out.println("zookeeper was sync connected");
            // this.createZookeeperNode();

            // 获取节点数据的方法
            // this.obtainNodeValue();
            // 获取节点的子节点列表方法
            // this.obtainChildNodeValue();

            // 更新数据节点内容的方法
            // this.obtainNodeValue();
            // this.updateNodeValue();
            // this.obtainNodeValue();

            // 删除节点
            this.deleteNodeSync();
        } else if(Event.EventType.NodeChildrenChanged.equals(watchedEvent.getType())) {
            // 子节点列表发生改变时, 服务器端会发生noteChildrenChanged事件通知
            // 要重新获取子节点列表, 同时注意: 通知是一次性的，需要反复注册监听
            this.obtainChildNodeValue();
        }
    }

    /**
     * 创建节点
     * path  :节点创建的路径
     * data[]:节点创建要保存的数据，是个byte类型的
     * acl   :节点创建的权限信息(4种类型)
     *       ANYONE_ID_UNSAFE: 表示任何人
     *       AUTH_IDS: 此ID仅可用于设置ACL。它将被客户机验证的ID替换。
     *       OPEN_ACL_UNSAFE: 是一个完全开放的ACL(常用) --> world:anyone
     *       CREATOR_ALL_ACL: 此ACL授予创建者身份验证ID的所有权限
     * createMode:创建节点的类型(4种类型)
     *       PERSISTENT:持久节点
     *	     PERSISTENT_SEQUENTIAL:持久顺序节点
     *       EPHEMERAL:临时节点
     *       EPHEMERAL_SEQUENTIAL:临时顺序节点
     * String node = zookeeper.create(path,data,acl,createMode);
     */
    private void createZookeeperNode() throws KeeperException, InterruptedException {
        // 持久节点
        String persistent = zookeeper.create(
                ZK_EPHEMERAL,
                "content of persistent".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT
        );
        System.out.println(persistent);

        // 临时节点
        String ephemeral = zookeeper.create(
                ZK_PERSISTENT,
                "content of ephemeral".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL
        );
        System.out.println(ephemeral);

        // 持久顺序节点
        String persistentSequential = zookeeper.create(
                ZK_PERSISTENT_SEQUENTIAL,
                "content of persistent sequential".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL
        );
        System.out.println(persistentSequential);
    }

    /**
     * 获取节点数据
     * path:获取数据的路径
     * watch:是否开启监听
     * stat:节点状态信息
     * null:表示获取最新版本的数据
     * zk.getData(path, watch, stat);
     */
    private void obtainNodeValue() throws KeeperException, InterruptedException {
        byte[] data = zookeeper.getData(ZK_PERSISTENT, Boolean.FALSE, null);
        System.out.println(new String(data));
    }

    /**
     * 获取子节点数据
     * path:路径
     * watch:是否要启动监听, 当子节点列表发生变化, 会触发监听
     * zooKeeper.getChildren(path, watch);
     */
    private void obtainChildNodeValue() throws KeeperException, InterruptedException {
        List<String> children = zookeeper.getChildren(ZK_PERSISTENT, Boolean.TRUE);
        System.out.println(children);
    }

    /**
     * path: 路径
     * data: 要修改的内容 byte[]
     * version: 为-1,表示对最新版本的数据进行修改
     * zooKeeper.setData(path, data,version);
     */
    private void updateNodeValue() throws KeeperException, InterruptedException {
        Stat stat = zookeeper.setData(
                ZK_PERSISTENT,
                "change data from updateNodeValue()".getBytes(),
                -1
        );
    }

    /**
     * zooKeeper.exists(path,watch): 判断节点是否存在
     * zookeeper.delete(path,version): 删除节点
     */
    private void deleteNodeSync() throws KeeperException, InterruptedException {
        String format = String.format("%s%s", ZK_PERSISTENT, "/c1");
        Stat exists = zookeeper.exists(format, false);

        if(exists == null) {
            return;
        }

        zookeeper.delete(format, -1);

        Stat stat = zookeeper.exists(format, false);
        System.out.println(stat == null ? "delete success" : "delete failed");
    }
    
}
