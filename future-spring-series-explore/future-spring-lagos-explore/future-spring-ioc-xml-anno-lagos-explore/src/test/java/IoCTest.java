import cn.locusc.spring.ioc.logos.dao.AccountDao;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IoCTest {

    @Test
    public void testIoC() {
        // 通过读取classpath下的xml文件来启动容器(xml模式SE应用下推荐)
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        // ApplicationContext applicationContext = new FileSystemXmlApplicationContext("文件的绝对路径");

        // &符号获取FactoryBean的类型
        Object companyBean = applicationContext.getBean("&companyBean");
        System.out.println("==========companyBean:" + companyBean);

        AccountDao accountDao = (AccountDao)applicationContext.getBean("accountDao");
        System.out.println(accountDao);

        Object connectionUtils = applicationContext.getBean("connectionUtils");
        System.out.println(connectionUtils);

        applicationContext.close();
    }

    /**
     * 测试Bean对象的延迟加载
     */
    @Test
    public void testBeanLazy() {
        // 启动容器(容器初始化)
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        // getBean获取bean对象使用
        Object lazyResult = applicationContext.getBean("lazyResult");
        System.out.println(lazyResult);

        applicationContext.close();
    }

}
