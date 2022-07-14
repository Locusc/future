package chapter3.codelist;

/**
 * @author Jay
 * 基于双重检查锁定的正确单例模式实现
 *
 * 尽管第1次检查(操作①)对变量 instance的访问没有加锁从而使竞态仍然可能存在，但是乍一看，它似乎既避免了锁的开销又保障了线程安全:
 * 一个线程T,执行到操作①的时候发现 instance为null，而此刻另外一个线程Tz可能恰好刚执行完操作③而使instance值不为null;
 * 接着T,获得锁而执行临界区代码的时候会再次判断instance值是否为null(第2次检查)，此时由于该线程是在临界区内读取共享变量instance的，
 * 因此T可以发现此刻 instance值已经不为 null，于是，T不会操作③（创建实例)，从而避免了再次创建一个实例。
 * 当然，仅仅从可见性的角度分析结论确实如此。但是，在一些情形下为了确保线程安全光考虑可见性是不够的，我们还需要考虑重排序的因素。
 * 我们知道操作③可以分解为以下伪代码所示的几个独立子操作:
 * objRef = allocate(IncorrectDCLSingletion.class); //子操作①:分配对象所需的存储空间
 * invokeConstructor(objRef) ;//子操作②:初始化objRef引用的对象
 * instance = objRef;  //子操作③:将对象引用写入共享变量
 * 根据锁的重排序规则⒉和规则1(参见3.7节),临界区内的操作可以在临界区内被重排序。
 * 因此，JIT编译器可能将上述的子操作重排序为:子操作①-子操作③→子操作②，
 * 即在初始化对象之前将对象的引用写入实例变量instance(正如我们在第2章清单2-10所示的 Demo中所看到的现象)。
 * 由于锁对有序性的保障是有条件的(参见3.2.1 节)，而操作①(第1次检查）读取instance变量的时候并没有加锁，
 * 因此上述重排序对操作①的执行线程是有影响的:该线程可能看到一个未初始化（或未初始化完毕）的实例，即变量instance 的值不为 null，
 * 但是该变量所引用的对象中的某些实例变量的变量值可能仍然是默认值，而不是构造器中设置的初始值。
 * 也就是说，一个线程在执行操作①的时候发现instance不为null，于是该线程就直接返回这个instance变量所引用的实例，
 * 而这个实例可能是未初始化完毕的，这就可能导致程序出错!
 *
 *
 * 在分析清楚问题的原因之后，解决方法也就不难想到:只需要将instance变量采用volatile修饰即可。这实际上是利用了volatile关键字的以下两个作用。
 * 保障可见性:一个线程通过执行操作③修改了instance变量值，其他线程可以读取到相应的值（通过执行操作①)。
 * 保障有序性:由于volatile能够禁止 volatile变量写操作与该操作之前的任何读、写操作进行重排序，
 * 因此，用volatile修饰 instance相当于禁止JIT编译器以及处理器将子操作②(对对象进行初始化的写操作）重排序到子操作③（将对象引用写人共享变量的写操作），
 * 这保障了一个线程读取到instance变量所引用的实例时该实例已经初始化完毕。
 * 通过volatile关键字对上述两点的保障，双重检测锁定所要实现的效果才得以正确实现，如清单3-17所示。
 * 2022/7/4
 */
public class DCLSingleton317 {

    /*
     * 保存该类的唯一实例，使用volatile关键字修饰instance。
     */
    private static volatile DCLSingleton317 instance;

    /*
     * 私有构造器使其他类无法直接通过new创建该类的实例
     */
    private DCLSingleton317() {
        // 什么也不做
    }

    /**
     * 创建并返回该类的唯一实例 <BR>
     * 即只有该方法被调用时该类的唯一实例才会被创建
     *
     * @return
     */
    public static DCLSingleton317 getInstance() {
        if (null == instance) {// 操作①：第1次检查
            synchronized (DCLSingleton317.class) {
                if (null == instance) {// 操作②：第2次检查
                    instance = new DCLSingleton317();// 操作③
                }
            }
        }
        return instance;
    }

    public void someService() {
        // 省略其他代码
    }

}
