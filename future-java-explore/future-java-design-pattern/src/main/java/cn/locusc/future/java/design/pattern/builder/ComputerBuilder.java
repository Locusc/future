package cn.locusc.future.java.design.pattern.builder;

import cn.locusc.future.java.design.pattern.domain.Computer;

public class ComputerBuilder {

    private Computer computer = new Computer();

    public void installDisplay(String display) {
        computer.setDisplay(display);
    }

    public void installMainUnit(String mainUnit) {
        computer.setMainUnit(mainUnit);
    }

    public void installMouse(String mouse) {
        computer.setMouse(mouse);
    }

    public void installKeyboard(String keyboard) {
        computer.setKeyboard(keyboard);
    }

    public Computer getComputer() {
        return computer;
    }

}
