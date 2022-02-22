package cn.locusc.future.java.design.pattern.factory;

import cn.locusc.future.java.design.pattern.domain.AbsComputer;

/**
 * @author Jay
 * 工厂模式
 * 2022/2/2
 */
public class FactoryMode {

    public static void main(String[] args) {
        // 考虑使用工厂模式
        // 当客户程序不需要知道要使用对象的创建过程
        // 客户程序使用的对象存在变动的可能, 或者根本就不知道使用哪一个具体的对象.
        AbsComputer absComputer = ComputerFactory.createComputer("hp");
        absComputer.start();
    }

}
