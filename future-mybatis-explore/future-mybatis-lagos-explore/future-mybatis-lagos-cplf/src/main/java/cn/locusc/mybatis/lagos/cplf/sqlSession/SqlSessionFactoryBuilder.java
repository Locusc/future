package cn.locusc.mybatis.lagos.cplf.sqlSession;

import cn.locusc.mybatis.lagos.cplf.config.XMLConfigBuilder;
import cn.locusc.mybatis.lagos.cplf.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) throws DocumentException, PropertyVetoException {
        // 1.使用dom4j解析配置文件, 将解析出来的内容封装到Configuration中
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);

        // 2.创建sqlSessionFactory 工厂类: 生产sqlSession(会话对象)
        return new DefaultSqlSessionFactory(configuration);
    }
}
