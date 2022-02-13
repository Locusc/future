package cn.locusc.future.java.design.pattern.proxy.dynamic;

import cn.locusc.future.java.design.pattern.proxy.statiz.IRentingHouse;
import cn.locusc.future.java.design.pattern.proxy.statiz.RentingHouseImpl;

/**
 * @author Jay
 * 基于Cglib的动态代理
 * 2022/2/12
 */
public class CglibProxy {

    public static void main(String[] args) {

        // 委托对象---委托方
        IRentingHouse rentingHouse = new RentingHouseImpl();

        // 从代理对象工厂获取代理对象
        // 获取rentingHouse对象的代理对象,
        // Enhancer类似于JDK动态代理中的Proxy
        // 通过实现接口MethodInterceptor能够对各个方法进行拦截增强, 类似于JDK动态代理中的InvocationHandler
        // 使用工厂来获取代理对象
        RentingHouseImpl cglibProxy = (RentingHouseImpl) ProxyFactory.getInstance().getCglibProxy(rentingHouse);

        cglibProxy.rentHouse();

    }

}
