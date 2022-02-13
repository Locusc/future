package cn.locusc.future.java.design.pattern.proxy.dynamic;

import cn.locusc.future.java.design.pattern.proxy.statiz.IRentingHouse;
import cn.locusc.future.java.design.pattern.proxy.statiz.RentingHouseImpl;

/**
 * @author Jay
 * JDK动态代理, 实现方法增强
 * 2022/2/12
 */
public class JdkProxy {

    public static void main(String[] args) {

        // 委托对象---委托方
        IRentingHouse rentingHouse = new RentingHouseImpl();

        // 从代理对象工厂获取代理对象
        IRentingHouse jdkProxy = (IRentingHouse) ProxyFactory.getInstance().getJdkProxy(rentingHouse);

        jdkProxy.rentHouse();

    }
}
