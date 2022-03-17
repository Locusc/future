package cn.locusc.reactive.other.observer;

import cn.locusc.reactive.other.observer.impl.CustomObserver1;
import cn.locusc.reactive.other.observer.impl.CustomObserver2;
import cn.locusc.reactive.other.observer.impl.CustomSubject;

public class ObserverTest {

    public static void main(String[] args) {

        Subject subject = new CustomSubject();
        Observer o1 = new CustomObserver1();
        Observer o2 = new CustomObserver2();

        subject.registerObserver(o1);
        subject.registerObserver(o2);

        subject.notifyObservers("事件1");
        subject.notifyObservers("事件2");

        System.out.println("=====================");

        subject.unregisterObserver(o1);

        subject.notifyObservers("事件3");

    }

}
