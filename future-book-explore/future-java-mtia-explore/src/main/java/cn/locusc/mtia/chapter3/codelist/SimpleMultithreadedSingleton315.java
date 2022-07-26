package cn.locusc.mtia.chapter3.codelist;

/**
 * @author Jay
 * 简单加锁实现的单例模式实现
 * 2022/7/4
 */
public class SimpleMultithreadedSingleton315 {

    // 保存该类的唯一实例
    private static SimpleMultithreadedSingleton315 instance = null;

    /*
     * 私有构造器使其他类无法直接通过new创建该类的实例
     */
    private SimpleMultithreadedSingleton315() {
        // 什么也不做
    }

    /**
     * 创建并返回该类的唯一实例 <BR>
     * 即只有该方法被调用时该类的唯一实例才会被创建
     */
    public static SimpleMultithreadedSingleton315 getInstance() {
        synchronized (SimpleMultithreadedSingleton315.class) {
            if (null == instance) {
                instance = new SimpleMultithreadedSingleton315();
            }
        }
        return instance;
    }

    public void someService() {
        // 省略其他代码
    }

}
