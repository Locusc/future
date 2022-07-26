package cn.locusc.mtia.chapter3.codelist;

import cn.locusc.mtia.utils.Debug;

/**
 * @author Jay
 * 基于静态内部类的单例模式实现
 *
 * 考虑到双重检测锁定法实现上容易出错 ，我们可以采用另外一种同样可以实现延迟加
 * 载的效果且比较简单的一种方法.
 *
 * 我们知道类的静态变量被初次访问会触发Java虚拟机对该类进行初始化，即该类的静态变量的值会变为其初始值而不是默认值。
 * 因此，静态方法 getInstance()被调用的时候Java虚拟机会初始化这个方法所访问的内部静态类InstanceHolder。
 * 这使得InstanceHolder的静态变量 INSTANCE 被初始化，从而使StaticHolderSingleton类的唯一实例得以创建。
 * 由于类的静态变量只会创建一次，因此StaticHolderSingleton(单例类）只会被创建一次。
 * 2022/7/4
 */
public class StaticHolderSingleton318 {

    // 私有构造器
    private StaticHolderSingleton318() {
        Debug.info("StaticHolderSingleton inited.");
    }

    static class InstanceHolder {
        // 保存外部类的唯一实例
        static {
            Debug.info("InstanceHolder inited.");
        }
        final static StaticHolderSingleton318 INSTANCE = new StaticHolderSingleton318();
    }

    public static StaticHolderSingleton318 getInstance() {
        Debug.info("getInstance invoked.");
        return InstanceHolder.INSTANCE;
    }

    public void someService() {
        Debug.info("someService invoked.");
        // 省略其他代码
    }

    public static void main(String[] args) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Debug.info(StaticHolderSingleton318.InstanceHolder.class.getName());
                StaticHolderSingleton318.InstanceHolder.INSTANCE.someService();
            };
        };
        t.start();
    }

}
