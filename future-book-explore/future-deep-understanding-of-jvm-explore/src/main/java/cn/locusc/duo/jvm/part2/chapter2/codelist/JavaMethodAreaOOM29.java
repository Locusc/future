package cn.locusc.duo.jvm.part2.chapter2.codelist;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Jay
 * VM Args：-XX:PermSize=10M -XX:MaxPermSize=10M
 * 借助CGLib使得方法区出现内存溢出异常
 *
 * 我们再来看看方法区的其他部分的内容，方法区的主要职责是用于存放类型的相关信息，如类
 * 名、访问修饰符、常量池、字段描述、方法描述等。对于这部分区域的测试，基本的思路是运行时产
 * 生大量的类去填满方法区，直到溢出为止。虽然直接使用Java SE API也可以动态产生类（如反射时的
 * GeneratedConstructorAccessor和动态代理等），但在本次实验中操作起来比较麻烦。在代码清单2-8里
 * 笔者借助了CGLib
 * [3]直接操作字节码运行时生成了大量的动态类。
 * 值得特别注意的是，我们在这个例子中模拟的场景并非纯粹是一个实验，类似这样的代码确实可
 * 能会出现在实际应用中：当前的很多主流框架，如Spring、Hibernate对类进行增强时，都会使用到
 * CGLib这类字节码技术，当增强的类越多，就需要越大的方法区以保证动态生成的新类型可以载入内
 * 存。另外，很多运行于Java虚拟机上的动态语言（例如Groovy等）通常都会持续创建新类型来支撑语
 * 言的动态性，随着这类动态语言的流行，与代码清单2-9相似的溢出场景也越来越容易遇到。
 *
 * 在JDK 7中的运行结果：
 * Caused by: java.lang.OutOfMemoryError: PermGen space
 * at java.lang.ClassLoader.defineClass1(Native Method)
 * at java.lang.ClassLoader.defineClassCond(ClassLoader.java:632)
 * at java.lang.ClassLoader.defineClass(ClassLoader.java:616)
 * ... 8 more
 *
 * 方法区溢出也是一种常见的内存溢出异常，一个类如果要被垃圾收集器回收，要达成的条件是比
 * 较苛刻的。在经常运行时生成大量动态类的应用场景里，就应该特别关注这些类的回收状况。这类场
 * 景除了之前提到的程序使用了CGLib字节码增强和动态语言外，常见的还有：大量JSP或动态产生JSP
 * 文件的应用（JSP第一次运行时需要编译为Java类）、基于OSGi的应用（即使是同一个类文件，被不同
 * 的加载器加载也会视为不同的类）等。
 * 在JDK 8以后，永久代便完全退出了历史舞台，元空间作为其替代者登场。在默认设置下，前面
 * 列举的那些正常的动态创建新类型的测试用例已经很难再迫使虚拟机产生方法区的溢出异常了。不过
 * 为了让使用者有预防实际应用里出现类似于代码清单2-9那样的破坏性的操作，HotSpot还是提供了一
 * 些参数作为元空间的防御措施，主要包括：
 * ·-XX：MaxMetaspaceSize：设置元空间最大值，默认是-1，即不限制，或者说只受限于本地内存
 * 大小。
 * ·-XX：MetaspaceSize：指定元空间的初始空间大小，以字节为单位，达到该值就会触发垃圾收集
 * 进行类型卸载，同时收集器会对该值进行调整：如果释放了大量的空间，就适当降低该值；如果释放
 * 了很少的空间，那么在不超过-XX：MaxMetaspaceSize（如果设置了的话）的情况下，适当提高该
 *  值。
 * ·-XX：MinMetaspaceFreeRatio：作用是在垃圾收集之后控制最小的元空间剩余容量的百分比，可
 * 减少因为元空间不足导致的垃圾收集的频率。类似的还有-XX：Max-MetaspaceFreeRatio，用于控制最
 * 大的元空间剩余容量的百分比。
 *
 * 2022/7/29
 */
public class JavaMethodAreaOOM29 {

    public static void main(String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    return proxy.invokeSuper(obj, args);
                }
            });
            enhancer.create();
        }
    }

    static class OOMObject {
    }
}
