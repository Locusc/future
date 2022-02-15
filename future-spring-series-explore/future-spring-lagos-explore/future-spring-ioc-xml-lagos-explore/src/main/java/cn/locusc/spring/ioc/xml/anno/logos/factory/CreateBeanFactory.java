package cn.locusc.spring.ioc.xml.anno.logos.factory;

import cn.locusc.spring.ioc.xml.anno.logos.utils.ConnectionUtils;

public class CreateBeanFactory {

    public static ConnectionUtils getInstanceStatic() {
        return new ConnectionUtils();
    }

    public ConnectionUtils getInstance() {
        return new ConnectionUtils();
    }
}
