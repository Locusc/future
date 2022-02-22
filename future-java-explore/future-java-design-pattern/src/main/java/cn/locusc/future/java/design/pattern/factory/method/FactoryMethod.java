package cn.locusc.future.java.design.pattern.factory.method;


import cn.locusc.future.java.design.pattern.factory.method.noodles.INoodles;

/**
 * @author Jay
 * 工厂方法模式
 * 2022/2/21
 */
public class FactoryMethod {

    public static void main(String[] args) {

        INoodlesFactory noodlesFactory = new LzINoodlesFactory();
        INoodles noodles = noodlesFactory.createNoodles();
        noodles.desc();
    }

}
