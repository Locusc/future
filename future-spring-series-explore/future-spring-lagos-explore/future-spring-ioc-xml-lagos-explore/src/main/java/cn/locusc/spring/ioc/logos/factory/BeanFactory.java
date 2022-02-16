package cn.locusc.spring.ioc.logos.factory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jay
 * 工厂类, 生产对象(使用反射技术)
 * 2022/2/8
 */
public class BeanFactory {

    /**
     * 任务一: 读取解析xml, 通过反射技术实例化对象并且存储使用
     * 任务二: 对外提供获取实例对象的接口(根据id获取)
     */
    private static Map<String, Object> map = new HashMap<>(); // 存储对象

    static {
        // 任务一: 读取解析xml, 通过反射技术实例化对象并且存储使用
        // 加载xml
        InputStream resourceAsStream = BeanFactory.class.getClassLoader()
                .getResourceAsStream("applicationContext.xml");
        // 解析xml xPath表达式 为了快速定位xml中的元素
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> beanList = rootElement.selectNodes("//bean");
            for (int i = 0; i < beanList.size(); i++) {
                Element element = beanList.get(i);
                // 处理每个bean元素, 获取到该元素的id和class属性
                // accountDao
                String id = element.attributeValue("id");
                // cn.locusc.spring.ioc.transfer.logos.dao.impl.JdbcAccountDaoImpl
                String clazz = element.attributeValue("class");

                // 通过反射技术实例化对象
                Class<?> aClass = Class.forName(clazz);
                // 实例化之后的对象
                Object o = aClass.newInstance();
                // 存储到map中待用
                map.put(id, o);
            }

            // 实例化完成之后维护对象的依赖关系, 检查哪些对象需要传值进入, 根据它的配置, 我们传入相应的值
            // 有property子元素的bean就有传值需求
            List<Element> propertyList = rootElement.selectNodes("//property");
            for (int i = 0; i < propertyList.size(); i++) {
                // 解析property, 获取父元素
                // <property name="AccountDao" ref="accountDao"></property>
                Element element = propertyList.get(i);

                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                // 找到当前需要被处理依赖关系的bean
                Element parent = element.getParent();
                // 调用父元素对象的反射功能
                String parentId = parent.attributeValue("id");
                Object parentObject = map.get(parentId);
                // 遍历父对象中的所有方法, 找到"set"+"name"
                Method[] methods = parentObject.getClass().getMethods();
                for (int j = 0; j < methods.length; j++) {
                    Method method = methods[j];
                    System.out.println(method.getName());
                    // 该方法就是setAccountDao(AccountDao accountDao)
                    if(method.getName().equalsIgnoreCase("set" + name)) {
                        method.invoke(parentObject, map.get(ref));
                    }
                }

                // 把处理之后的parentObject重新放到map中
                map.put(parentId, parentObject);
            }
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // 任务二: 对外提供获取实例对象的接口(根据id获取)
    public static Object getBean(String id){
        return map.get(id);
    }

}
