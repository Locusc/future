package cn.locusc.future.java.design.pattern.factory;

import cn.locusc.future.java.design.pattern.domain.AbsComputer;
import cn.locusc.future.java.design.pattern.domain.HpComputer;
import cn.locusc.future.java.design.pattern.domain.LegendComputer;

public class ComputerFactory {

    public static AbsComputer createComputer(String type) {

        AbsComputer absComputer = null;

        switch (type) {
            case "legend":
                absComputer = new LegendComputer();
                break;
            case "hp":
                absComputer = new HpComputer();
        }

        return absComputer;
    }

}
