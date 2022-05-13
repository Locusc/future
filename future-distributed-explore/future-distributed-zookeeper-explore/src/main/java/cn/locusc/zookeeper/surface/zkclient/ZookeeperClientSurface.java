package cn.locusc.zookeeper.surface.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author Jay
 * 借助zkclient完成会话的创建
 * 2022/5/10
 */
public class ZookeeperClientSurface {

    private static ZkClient zkClient;

    private final String ZK_CLIENT = "/zk-client";

    /**
     * 创建一个zkClient实例就可以完成连接, 完成会话的创建
     * serverString: 服务器连接地址
     * 注意: zkClient通过对zookeeperAPI内部封装, 将这个异步创建会话的过程同步化了
     */
    public static void main(String[] args) {
        zkClient = new ZkClient("127.0.0.1:2181");
    }

    /**
     * createParents: 是否要创建父节点,如果值为true,那么就会递归创建节点
     */
    private void createZookeeperNode(String path) {
        // 创建节点
        zkClient.createPersistent(path, true);
    }

    /**
     * 获取节点值
     */
    private Object obtainZookeeperNodeValue(String path) {
        return zkClient.readData(path);
    }

    /**
     * 递归删除节点
     */
    private void deleteNodeRecursive(String path) {
        zkClient.deleteRecursive(path);
    }

    /**
     * 删除节点
     */
    private void deleteNode(String path) {
        zkClient.delete(path);
    }

    /**
     * 注册节点监听事件
     * 客户端可以对一个不存在的节点进行子节点变更的监听
     * 只要该节点的子节点列表发生变化, 或者该节点本身被创建或者删除, 都会触发监听
     * s: parentPath
     * list: 变化后子节点列表
     */
    private void watcherZookeeperNode(String path) {
        zkClient.subscribeChildChanges(path, (parentPath, list) ->
                System.out.println(parentPath + "的子节点列表发生了变化, 变化后的子节点列表为"+ list)
        );
    }

    /**
     * 注册节点数据监听事件
     */
    private void watcherZookeeperNodeValue(String path) {
        zkClient.subscribeDataChanges(path, new IZkDataListener() {

            /**
             * 当节点数据内容发生变化时，执行的回调方法
             * s: path
             * o: 变化后的节点内容
             */
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println(s + "该节点内容被更新，更新的内容" + o);
            }

            /**
             * 当节点被删除时，会执行的回调方法
             * s: path
             */
            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println(s + "该节点被删除");
            }
        });
    }

    /**
     * 判断节点是否存在
     */
    private void existsZookeeperNode(String path) {
        zkClient.exists(path);
    }

    /**
     * 更新节点内容
     */
    private void updateNodeValue(String path, Object value) {
        zkClient.writeData(path, value);
    }

}
