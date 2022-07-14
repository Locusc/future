package chapter3.codelist;

import utils.Debug;

/**
 * @author Jay
 * 基于枚举类型的单例模式实现示例代码
 *
 * 正确实现延迟加载的单例模式还有一种更为简单的方法，那就是利用枚举（ Enum)类型，
 *
 * 这里，枚举类型Singleton相当于一个单例类，其字段INSTANCE值相当于该类的唯一实例。
 * 这个实例是在Singleton.INSTANCE初次被引用的时候才被初始化的。
 * 仅访问Singleton本身（比如上述的Singleton.class.getName()调用)并不会导致Singleton的唯一实例被初始化。
 * 2022/7/4
 */
public class EnumBasedSingletonExample319 {

    public static void main(String[] args) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Debug.info(Singleton.class.getName());
                Singleton.INSTANCE.someService();
            };
        };
        t.start();
    }

    public static enum Singleton {
        INSTANCE;
        // 私有构造器
        Singleton() {
            Debug.info("Singleton inited.");
        }

        public void someService() {
            Debug.info("someService invoked.");
            // 省略其他代码
        }
    }

}
