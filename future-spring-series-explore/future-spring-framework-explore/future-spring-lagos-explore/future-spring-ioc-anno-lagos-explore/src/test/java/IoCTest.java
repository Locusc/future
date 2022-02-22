import cn.locusc.spring.ioc.logos.SpringConfig;
import cn.locusc.spring.ioc.logos.dao.AccountDao;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IoCTest {

    @Test
    public void testIoC() {
        // SE应用注解启动方式
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        // ApplicationContext applicationContext = new FileSystemXmlApplicationContext("文件的绝对路径");

        AccountDao accountDao = (AccountDao)applicationContext.getBean("accountDao");
        System.out.println(accountDao);
    }
}
