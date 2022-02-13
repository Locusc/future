package cn.locusc.future.java.design.pattern.singleton;

/**
 * @author Jay
 * 饿汉式(立即加载)
 * 2022/2/12
 */
public class HungrySingleton {

    // 构造方法私有化
    private HungrySingleton() {}

    // 将自身实例化对象设置为一个属性，并用static、final修饰
    private static final HungrySingleton instance = new HungrySingleton();

    // 静态方法返回该实例
    public static HungrySingleton getInstance() {
        return instance;
    }
}
