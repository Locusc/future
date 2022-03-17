package cn.locusc.reactive.other.observer;

public interface Subject {

    /**
     * 将观察者注册到该主题的方法
     * @param observer 观察者
     */
    void registerObserver(Observer observer);

    /**
     * 将观察者从当前主题解除注册的方法
     * @param observer 要解除注册的观察者
     */
    void unregisterObserver(Observer observer);

    /**
     * 给该主题的观察者发送通知的方法
     * @param event 主题需要广播的事件
     */
    void notifyObservers(String event);

}
