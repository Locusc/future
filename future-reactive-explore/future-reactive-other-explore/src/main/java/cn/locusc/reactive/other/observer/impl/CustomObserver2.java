package cn.locusc.reactive.other.observer.impl;

import cn.locusc.reactive.other.observer.Observer;

public class CustomObserver2 implements Observer {

    @Override
    public void observe(String event) {
        System.out.println("观察者2 -- " + event);
    }

}
