package cn.locusc.mtia.chapter6.codelist;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Jay
 *
 * 使用ThreadLocal实现线程安全示例
 * 设tlVar为任意一个线程局部变量。初始状态下，tlVar并没有与之关联的线程特有对象。
 * 当一个线程初次执行tlVar.get()的时候，tlVar.get()会调用tlVar.initialValue()。
 * tlVar.initialValue()的返回值就会成为tlVar所关联的当前线程(即tlVar.get()的执行线程)的线程特有对象。
 * 这个线程后续再次执行tlVar.get()所返回的线程特有对象始终都是同一个对象(即tlVar.initialValue()的返回值)，
 * 除非这个线程中途执行了tlVar.set(T)。由于ThreadLocal的initialValue方法的返回值为null，
 * 因此要设置线程局部变量关联的初始线程特有对象。我们需要创建ThreadLocal的子类(通常是匿名子类)并在子类中覆盖( Override )initialValue方法，
 * 然后在该方法中返回初始线程特有对象。从Java 8开始，ThreadLocal引入了一个名为 withInitial的静态方法，
 * 该方法使得我们能够用一个Lambda表达式(返回值)作为相应线程局部变量所关联的初始线程特有对象。
 * 例如，清单6-7中的线程局部变量SDF的初始值可写作ThreadLocal.withInitial(() ->new SimpleDateFormat("yyyy-MM-dd"))。
 *
 * 使用ThreadLocal，我们可以将清单6-5中的非线程安全Servlet改造成线程安全的，如清单6-7所示。
 * 在这个例子中，ThreadLocal 不仅使我们在无须借助锁的情况下实现了线程安全，
 * 还减少了对象创建的次数——doPost方法的各个执行线程各自仅创建各自的一个SimpleDateFormat实例。
 * 相反，如果我们直接在 doPost方法中创建并使用SimpleDateFormat实例的话固然可以确保线程安全，
 * 但是那样就意味着每次执行 doPost方法都会导致新的SimpleDateFormat实例被创建。
 *
 * 线程局部变量通常是会被声明为某个类的静态变量，正如清单6-7所示。这是因为:如果把线程局部变量声明为某个类的实例变量,
 * 那么每创建该类的一个实例都会导致新的ThreadLocal 实例被创建。这就可能导致当前线程中同一个类型的线程特有对象会被多次创建。
 * 而这即便不会导致错误,也会导致重复创建对象带来的浪费。
 * 注意 ThreadLocal 实例通常会被作为某个类的静态字段使用。
 *
 * 2022/7/18
 */
@WebServlet("/threadLocalExample")
public class ServletWithThreadLocal extends HttpServlet {

    private static final long serialVersionUID = -9179908895742969397L;

    final static ThreadLocal<SimpleDateFormat> SDF =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final SimpleDateFormat sdf = SDF.get();
        String strExpiryDate = req.getParameter("expirtyDate");
        try (PrintWriter pwr = resp.getWriter()) {
            sdf.parse(strExpiryDate);
            // 省略其他代码
            pwr.printf("[%s]expirtyDate:%s", Thread.currentThread().getName(), strExpiryDate);
        } catch (ParseException e) {
            throw new ServletException(e);
        } // try结束
    }

}
