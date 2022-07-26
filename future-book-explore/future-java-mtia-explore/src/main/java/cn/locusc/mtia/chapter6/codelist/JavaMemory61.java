package cn.locusc.mtia.chapter6.codelist;

import cn.locusc.mtia.utils.Debug;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay
 * Java运行时空间示例代码
 *
 * 了解Java运行时存储空间的有关知识有助于我们更好地理解多线程编程。
 * Java运行时( Java Runtime)空间可以分为堆(Heap)空间、栈(Stack)空间和非堆 (Non-Heap )空间。
 * 其中,堆空间和非堆空间是可以被多个线程共享的,而栈空间则是线程的私有空间，每个线程都有其栈空间，并且一个线程无法访问其他线程的栈空间。
 *
 * 堆空间(（Heap space)用于存储对象，即创建一个实例的时候该实例所需的存储空间是在堆空间中进行分配的，
 * 堆空间本身是在Java 虚拟机启动的时候分配的一段可以动态扩容的内存空间。因此，类的实例变量是存储在堆空间中的。
 * 由于堆空间是线程之间的共享空间，因此实例变量以及引用型实例变量所引用的对象是可以被多个线程共享的。
 * 不管引用对象的变量的作用域如何（局部变量、实例变量和静态变量)，对象本身总是存储在堆空间中的。
 * 堆空间也是垃圾回收器（Garbage Collector )工作的场所，即堆空间中没有可达引用的对象(不再被使用的对象）所占用的存储空间会被垃圾回收器回收。
 * 堆空间通常可以进一步划分为年轻代( Young Generation )和年老代( Old/Tenured Generation )。
 * 对象所需的存储空间是在年轻代中进行分配的。垃圾回收器对年轻代中的对象进行的垃圾回收被称为次要回收(Minor Collection )。
 * 次要回收中“幸存”下来（即没有被回收掉）的对象最终可能被移入(改变对象所在的存储空间)年老代。
 * 垃圾回收器对年老代中的对象进行的垃圾回收被称为主要回收( Major Collection )。
 *
 * 栈空间( Stack Space )是为线程的执行而准备的一段固定大小的内存空间，每个线程都有其栈空间'。
 * 栈空间是在线程创建的时候分配的。线程执行（调用)一个方法前，Java虚拟机会在该线程的栈空间中为这个方法调用创建一个栈帧(Frame)。
 * 栈帧用于存储相应方法的局部变量、返回值等私有数据。
 * 可见，局部变量的变量值存储在栈空间中。基础类型( Primitive Type）变量和引用类型(Reference Type）变量的变量值都是直接存储在栈帧中的”。
 * 引用型变量的值相当于被引用对象的内存地址，而引用型变量所引用的对象仍然在堆空间中。
 * 也就是说，对于引用型局部变量，栈帧中存储的是相应对象的内存地址而不是对象本身!由于一个线程无法访问另外一个线程的栈空间，
 * 因此，线程对局部变量以及对只能通过当前线程的局部变量才能访问到的对象进行的操作具有固有 ( Inherent)的线程安全性。
 *
 * 非堆空间(Non-Heap Space )用于存储常量以及类的元数据(（Meta-data )等，它也是在Java 虚拟机启动的时候分配的一段可以动态扩容的内存空间。
 * 类的元数据包括类的静态变量、类有哪些方法以及这些方法的元数据（包括名称、参数和返回值等)。非堆空间也是多个线程之间共享的存储空间。
 * 类的静态变量在非堆空间中的存储方式与局部变量在栈空间的存储方式相似，即这些空间中仅存储变量的值本身，而引用型变量所引用的对象仍然存储在堆空间中。
 *
 * cn.locusc.mtia.chapter6\images\Java运行时存储空间示意图.png
 *
 * 提示
 * 堆空间、非堆空间是线程间可共享的空间，这表现为实例变量和静态变量是线程间可共享的;
 * 栈空间是线程的私有空间，这表现为局部变量是无法被多个线程共享的。
 * 线程对局部变量以及对只能通过当前线程的局部变量才能访问到的对象进行的操作具有固有( Inherent )的线程安全性。
 *
 * 2022/7/18
 */
public class JavaMemory61 {

    public static void main(String[] args) {
        String msg = args.length > 0 ? args[0] : null;
        ObjectX objectX = new ObjectX();
        objectX.greet(msg);
    }

    static class ObjectX implements Serializable {
        private static final long serialVersionUID = 8554375271108416940L;
        private static AtomicInteger ID_Generator = new AtomicInteger(0);
        private Date timeCreated = new Date();
        private int id;

        public ObjectX() {
            this.id = ID_Generator.getAndIncrement();
        }

        public void greet(String message) {
            String msg = toString() + ":" + message;
            Debug.info(msg);
        }

        @Override
        public String toString() {
            return "[" + timeCreated + "] ObjectX [" + id + "]";
        }
    }

}
