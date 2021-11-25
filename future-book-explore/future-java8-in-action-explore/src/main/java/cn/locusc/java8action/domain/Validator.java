package cn.locusc.java8action.domain;

import cn.locusc.java8action.chapter8Efficient.ActionClass;

public class Validator {

    private final ActionClass.Jia82.Jia821.ValidationStrategy strategy;

    public Validator(ActionClass.Jia82.Jia821.ValidationStrategy v) {
        this.strategy = v;
    }

    public boolean validate(String s){
        return strategy.execute(s);
    }

}
