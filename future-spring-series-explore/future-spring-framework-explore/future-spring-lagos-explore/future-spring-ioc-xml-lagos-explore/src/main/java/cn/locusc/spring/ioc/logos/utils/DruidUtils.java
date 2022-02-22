package cn.locusc.spring.ioc.logos.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author 应癫
 */
public class DruidUtils {

    private DruidUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://39.107.96.199:3306/mercy");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("wdnmd123");

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
