package cn.locusc.elastic.job;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

public class ElasticJobSimpleDemo {

    public static void main(String[] args) {
        // 配置分布式协调服务（注册中心）Zookeeper
        ZookeeperConfiguration zookeeperConfiguration =
                new ZookeeperConfiguration("localhost:2181", "data-archive-job");

        // 配置任务（时间事件、定时任务业务逻辑、调度器）
        ZookeeperRegistryCenter zookeeperRegistryCenter =
                new ZookeeperRegistryCenter(zookeeperConfiguration);

        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder("archive-job", "*/2 * * * * ?", 3)
                .shardingItemParameters("0=bachelor,1=master,2=doctor")
                .build();

        SimpleJobConfiguration simpleJobConfiguration =
                new SimpleJobConfiguration(jobCoreConfiguration, ElasticJobArchiveDemo.class.getName());

        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration)
                .overwrite(true)
                .build();

        JobScheduler jobScheduler = new JobScheduler(zookeeperRegistryCenter, liteJobConfiguration);

        jobScheduler.init();
    }

}
