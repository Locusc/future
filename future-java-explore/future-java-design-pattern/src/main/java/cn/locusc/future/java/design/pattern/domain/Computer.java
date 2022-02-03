package cn.locusc.future.java.design.pattern.domain;

public class Computer {

    // 显示器
    private String display;
    // 主机
    private String mainUnit;
    // 鼠标
    private String mouse;
    // 键盘
    private String keyboard;

    public String getDisplay() {
        return display;
    }
    public void setDisplay(String display) {
        this.display = display;
    }
    public String getMainUnit() {
        return mainUnit;
    }
    public void setMainUnit(String mainUnit) {
        this.mainUnit = mainUnit;
    }
    public String getMouse() {
        return mouse;
    }
    public void setMouse(String mouse) {
        this.mouse = mouse;
    }
    public String getKeyboard() {
        return keyboard;
    }
    public void setKeyboard(String keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public String toString() {
        return "Computer{" + "display='" + display + '\'' + ", mainUnit='"
                + mainUnit + '\'' + ", mouse='" + mouse + '\'' + ", keyboard='" + keyboard +
                '\'' + '}';
    }

}
