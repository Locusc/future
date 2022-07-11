package cn.locusc.seata.common;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    /**
     * 使⽤druid连接池
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    /**
     * 设置数据源代理-,完成分⽀事务注册/事务提交与回滚等操作
     * 启动扫描配置类,分别加载每个⼯程的启动类中
     */
    // @Primary //设置⾸选数据源对象
    // @Bean("dataSource")
    public DataSourceProxy dataSource(DataSource druidDataSource) {
        return new DataSourceProxy(druidDataSource);
    }

}
