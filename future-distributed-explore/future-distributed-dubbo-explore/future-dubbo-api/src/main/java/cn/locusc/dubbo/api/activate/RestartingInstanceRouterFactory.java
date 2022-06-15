package cn.locusc.dubbo.api.activate;

import cn.locusc.dubbo.api.components.ZookeeperClientComponent;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jay
 * 动态设置路由
 * 比如在发布前需要对发布的路由做降级
 * 降级后的路由无法访问, 发布完成之后删除zookeeper节点重新访问
 * 2022/6/8
 */
@Activate
public class RestartingInstanceRouterFactory implements RouterFactory {

    @Override
    public Router getRouter(URL url) {
        return new RestartingInstanceRouter(url);
    }

    private static class RestartingInstanceRouter implements Router {

        private final ReadyRestartInstances readyRestartInstances;
        private final URL url;

        private RestartingInstanceRouter(URL url) {
            this.url = url;
            this.readyRestartInstances = ReadyRestartInstances.create();
        }

        @Override
        public URL getUrl() {
            return url;
        }

        @Override
        public <T> List<Invoker<T>> route(List<Invoker<T>> list, URL url, Invocation invocation) throws RpcException {
            // 如果没有在重启列表中 才会加入到后续调用列表
            return list.stream()
                    .filter(i -> !readyRestartInstances.hasRestartingInstance(
                            i.getUrl().getParameter("remote.application"),
                            i.getUrl().getIp())
                    )
                    .collect(Collectors.toList());
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean isForce() {
            return false;
        }

        @Override
        public int getPriority() {
            return 0;
        }
    }

    private static class ReadyRestartInstances implements PathChildrenCacheListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(ReadyRestartInstances.class);

        private static final String LISTEN_PATHS = "/locusc/dubbo/restart/instances";

        private final CuratorFramework curatorFramework;

        // 当节点变化时 给这个集合赋值 重启机器的信息列表
        private volatile Set<String> restartInstances = new HashSet<>();

        private ReadyRestartInstances(CuratorFramework curatorFramework) {
            this.curatorFramework = curatorFramework;
        }

        public static ReadyRestartInstances create() {

            final CuratorFramework zookeeperClient = ZookeeperClientComponent.client();

            try {
                // 检查监听路径是否存在
                final Stat stat = zookeeperClient.checkExists().forPath(LISTEN_PATHS);
                // 如果监听路径不存在 则创建
                if (stat == null) {
                    zookeeperClient.create().creatingParentsIfNeeded().forPath(LISTEN_PATHS);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("确保基础路径存在");
            }

            final ReadyRestartInstances instances = new ReadyRestartInstances(zookeeperClient);

            // 创建一个NodeCache
            PathChildrenCache nodeCache = new PathChildrenCache(zookeeperClient, LISTEN_PATHS, false);

            // 给节点缓存对象 加入监听
            nodeCache.getListenable().addListener(instances);

            try {
                nodeCache.start();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("启动路径监听失败");
            }

            return instances;
        }

        /**
         * 返回应用名和主机拼接后的字符串
         */
        private String buildApplicationAndInstanceString(String applicationName, String host) {
            return applicationName + "_" + host;
        }

        /**
         * 增加重启实例的配置信息方法
         */
        public void addRestartingInstance(String applicationName, String host) throws Exception {
            curatorFramework.create().creatingParentsIfNeeded().forPath(LISTEN_PATHS + "/" + buildApplicationAndInstanceString(applicationName, host));
        }

        /**
         * 删除重启实例的配置信息方法
         */
        public void removeRestartingInstance(String applicationName, String host) throws Exception {
            curatorFramework.delete().forPath(LISTEN_PATHS + "/" + buildApplicationAndInstanceString(applicationName, host));
        }

        /**
         * 判断节点信息是否存在于restartInstances
         */
        public boolean hasRestartingInstance(String applicationName, String host) {
            return restartInstances.contains(buildApplicationAndInstanceString(applicationName, host));
        }

        @Override
        public void childEvent(CuratorFramework curatorFramework,
                               PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
            // 查询出监听路径下所有的目录配置信息
            final List<String> restartingInstances = curatorFramework.getChildren().forPath(LISTEN_PATHS);
            // 给restartInstances
            if (CollectionUtils.isEmpty(restartingInstances)) {
                this.restartInstances = Collections.emptySet();
            } else {
                this.restartInstances = new HashSet<>(restartingInstances);
            }
        }

    }

}
