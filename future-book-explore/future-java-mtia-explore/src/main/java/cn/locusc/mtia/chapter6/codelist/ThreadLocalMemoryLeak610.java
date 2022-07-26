package cn.locusc.mtia.chapter6.codelist;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Jay
 * ThreadLocal内存泄露代码示例
 * pageNum 274
 *
 * 线程特有对象的典型应用场景
 * 场景一需要使用非线程安全对象，但又不希望因此而引入锁。如果多个线程需要使用非线程安全的对象，
 * 而我们又不希望该对象被多个线程共享（因为共享往往意味着需要引入锁以保证线程安全），
 * 此时可以使用线程特有对象，使得各个线程拥有其特有的非线程安全对象实例。清单6-7所示的例子就属于该场景的应用。
 *
 * 场景二―使用线程安全对象，但希望避免其使用的锁的开销和相关问题。
 * 线程安全的对象虽然可以被多个线程共享，但是由于其可能使用了锁来保证线程安全，
 * 而某些情况下我们可能不希望看到锁的开销以及由锁可能引起的相关问题（如死锁）。
 * 此时，我们可以将线程安全的对象当作非线程安全的对象来看待。因此，这种场景就转化成场景一。
 * 只不过此时使用线程特有对象的主要意图在于避免锁的开销，当然线程安全也是有保障的。如清单6-8所示的代码展示了这种使用场景。
 *
 * 场景三隐式参数传递（ Implicit Parameter Passing )。线程特有对象在一个具体的线程中，它是线程全局可见的。
 * 一个类的方法中设置的线程特有对象对于该方法调用的任何其他方法（包括其他类的方法）都是可见的。
 * 这就可以形成隐式传递参数的效果，即一个类的方法调用另一个类的方法时,前者向后者传递数据可以借助ThreadLocal而不必通过方法参数传递。
 * 不过，也有的观点认为隐式参数传递使得系统难于理解。隐式参数传递的实现通常是使用一个只包括静态方法的类或者单例类（包装类）来封装对线程特有对象的访问，
 * 其他相应访问线程特有对象的代码只需要调用包装类的静态方法或者实例方法即可以访问线程特有对象。
 *
 * 场景四特定于线程的单例（ Singleton)模式。
 * 广为使用的单例模式所实现的效果是在一个Java 虚拟机中的一个类加载器下某个类有且仅有一个实例。
 * 如果我们希望对于某个类每个线程有且仅有该类的一个实例，那么就可以使用线程特有对象。
 * 例如，在如清单6-7所示的代码中，doPost方法的多个执行线程各自只会创建一个SimpleDateFormat实例。
 *
 * 2022/7/18
 */
public class ThreadLocalMemoryLeak610 extends HttpServlet {

    private static final long serialVersionUID = 4364376277297114653L;

    final static ThreadLocal<Counter> counterHolder = new ThreadLocal<Counter>() {
        @Override
        protected Counter initialValue() {
            return new Counter();
        }
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
        try (PrintWriter pwr = resp.getWriter()) {
            pwr.printf("Thread %s,counter:%d",
                    Thread.currentThread().getName(),
                    counterHolder.get().getAndIncrement());
        }
    }

    protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        counterHolder.get().getAndIncrement();
        // 省略其他代码
    }

}

// 非线程安全
class Counter {
    private int i = 0;
    public int getAndIncrement() {
        return i++;
    }
}
