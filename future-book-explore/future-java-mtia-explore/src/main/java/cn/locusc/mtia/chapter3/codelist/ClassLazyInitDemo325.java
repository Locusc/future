package cn.locusc.mtia.chapter3.codelist;

import cn.locusc.mtia.utils.Debug;

/**
 * @author Jay
 * 类的延迟初始化 Demo
 *
 * 可见，访问Collaborator类本身(语句①)仅仅使该类被Java 虚拟机加载，而并没有使其被初始化(此时,从输出上看我们并没有看到static初始化块被调用)。
 * 从“Collaboratorinitializing...”在 number 的初始值1之前被输出可以看出，
 * 当一个线程（这里是 main线程）初次访问类Collaborator的静态变量（语句②)时这个类才被初始化。
 * static关键字在多线程环境下有其特殊的涵义，
 * 它能够保证一个线程即使在未使用其他同步机制的情况下也总是可以读取到一个类的静态变量的初始值(而不是默认值)。
 * 但是，这种可见性保障仅限于线程初次读取该变量。
 * 如果这个静态变量在相应类初始化完毕之后被其他线程更新过，那么一个线程要读取该变量的相对新值仍然需要借助锁、volatile关键字等同步机制
 *
 * 2022/7/12
 */
public class ClassLazyInitDemo325 {

    public static void main(String[] args) {
        Debug.info(Collaborator.class.hashCode());// 语句①
        Debug.info(Collaborator.number);// 语句②
        Debug.info(Collaborator.flag);
    }

    static class Collaborator {
        static int number = 1;
        static boolean flag = true;
        static {
            Debug.info("Collaborator initializing...");
        }
    }

}
