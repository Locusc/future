package cn.locusc.future.java.design.pattern.factory;

import cn.locusc.future.java.design.pattern.domain.AbsComputer;

/**
 * @author Jay
 * 工厂模式
 * 2022/2/2
 */
public class FactoryMode {

    public static void main(String[] args) {
        AbsComputer absComputer = ComputerFactory.createComputer("hp");
        absComputer.start();
    }

}
