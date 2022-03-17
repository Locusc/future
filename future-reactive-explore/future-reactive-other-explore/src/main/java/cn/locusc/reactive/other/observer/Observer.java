package cn.locusc.reactive.other.observer;

public interface Observer {

    /**
     * 让主题回调的方法，通知当前观察者事件
     * @param event 通知的事件
     */
    void observe(String event);

}
