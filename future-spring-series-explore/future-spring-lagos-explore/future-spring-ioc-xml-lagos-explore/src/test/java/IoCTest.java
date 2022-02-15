import cn.locusc.spring.ioc.xml.anno.logos.dao.AccountDao;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IoCTest {

    @Test
    public void testIoC() {
        // 通过读取classpath下的xml文件来启动容器(xml模式SE应用下推荐)
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        // ApplicationContext applicationContext = new FileSystemXmlApplicationContext("文件的绝对路径");

        AccountDao accountDao = (AccountDao)applicationContext.getBean("accountDao");
        System.out.println(accountDao);

        Object connectionUtils = applicationContext.getBean("connectionUtils");
        System.out.println(connectionUtils);

        applicationContext.close();
    }
}
