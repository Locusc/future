package cn.locusc.reactive.other.observer.impl;

import cn.locusc.reactive.other.observer.Observer;
import cn.locusc.reactive.other.observer.Subject;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CustomSubject implements Subject {

    /**
     * 用于封装订阅了该主题的观察者
     * 线程安全的
     */
    private final Set<Observer> observers = new CopyOnWriteArraySet<>();

    @Override
    public void registerObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        observers.forEach(o -> o.observe(event));
    }

}
