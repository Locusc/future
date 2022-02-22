package cn.locusc.future.java.design.pattern.factory.method;

import cn.locusc.future.java.design.pattern.factory.method.noodles.INoodles;

/**
 * 工厂方法模式
 */
public interface INoodlesFactory {

     INoodles createNoodles();

}