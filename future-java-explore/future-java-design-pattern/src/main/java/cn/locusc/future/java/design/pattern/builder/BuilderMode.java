package cn.locusc.future.java.design.pattern.builder;

import cn.locusc.future.java.design.pattern.domain.Computer;

/**
 * @author Jay
 * 构造者模式
 * 2022/2/2
 */
public class BuilderMode {

    public static void main(String[] args) {
        ComputerBuilder computerBuilder = new ComputerBuilder();
        computerBuilder.installDisplay("显示器");
        computerBuilder.installMainUnit("主机");
        computerBuilder.installMouse("鼠标");
        computerBuilder.installKeyboard("键盘");

        Computer computer = computerBuilder.getComputer();
        System.out.println(computer.toString());
    }

}
