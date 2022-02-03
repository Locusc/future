package cn.locusc.future.java.design.pattern.proxy;

import cn.locusc.future.java.design.pattern.domain.Person;

public class Jay implements Person {

    @Override
    public void doSomething() {
        System.out.println("jay doing something");
    }

}
