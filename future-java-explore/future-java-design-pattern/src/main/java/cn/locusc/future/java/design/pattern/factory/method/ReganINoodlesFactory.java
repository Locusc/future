package cn.locusc.future.java.design.pattern.factory.method;

import cn.locusc.future.java.design.pattern.factory.method.noodles.INoodles;
import cn.locusc.future.java.design.pattern.factory.method.noodles.ReganNoodles;

public class ReganINoodlesFactory implements INoodlesFactory {

    @Override
    public INoodles createNoodles() {
        return new ReganNoodles();
    }

}
