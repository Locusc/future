package cn.locusc.mybatis.lagos.cplf.config;

import cn.locusc.mybatis.lagos.cplf.io.Resources;
import cn.locusc.mybatis.lagos.cplf.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {

    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    /**
     * 该方法就是将使用dom4j对配置文件进行解析, 封装configuration
     * @param inputStream 字节输入流
     * @return cn.locusc.mybatis.lagos.cplf.pojo.Configuration
     */
    public Configuration parseConfig(InputStream inputStream) throws DocumentException, PropertyVetoException {
        Document document = new SAXReader().read(inputStream);
        //<configuration>
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");

        Properties properties = new Properties();
        for (Element element : list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");

            properties.setProperty(name, value);
        }

        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("diverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        configuration.setDataSource(comboPooledDataSource);

        //mapper.xml解析: 拿到路径-字节输入流-dom4j解析
        List<Element> list1 = rootElement.selectNodes("//mapper");
        for (Element element : list1) {
            String mapperPath = element.attributeValue("resource");
            InputStream resourceAsInputStream = Resources.getResourceAsInputStream(mapperPath);

            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(resourceAsInputStream);
        }
        return configuration;
    }

}
