package cn.locusc.mtia.chapter6.codelist.so;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Jay
 * 该类是一个错误的Servlet类（非线程安全）
 * 无状态对象的一个典型应用就是Java EE中的Servlet.Servlet是一个实现javax.servlet.Servlet 接口的托管( Managed )类，而不是一个普通的类。
 * 所谓托管类，是指Servlet类实例的创建、初始化以及销毁的整个对象生命周期完全是由Java Web 服务器（例如Tomcat)控制的，
 * 而服务器为每一个Servlet类最多只生成一个实例，该唯-一实例会被用于处理服务器接收到的多个请求。
 * 即一个Servlet类的一个(唯一的）实例会被多个线程共享，并且服务器调用Servlet.service方法时并没有加锁，
 * 因此使Servlet实例成为无状态对象有利于提高服务器的并发性。
 * 这也是Servlet类一般不包含实例变量或者静态变量的原因:
 * 一旦 Servlet类包含实例变量或者静态变量，我们就需要考虑是否使用锁以保障其线程安全。
 * 例如，清单6-5展示了一个错误（非线程安全）的Servlet类。
 * 该Servlet类为了避免重复创建SimpleDateFormat实例的开销而将SimpleDateFormat 实例作为Servlet类的一个实例变量sdf，
 * 然而该Servlet对 sdf的访问又没有加锁，从而导致 sdf.parse(String)
 * 调用解析出来的日期可能是一个客户端根本没有提交过的错误日期!
 * 2022/7/18
 */
public class UnsafeServlet extends HttpServlet {

    private static final long serialVersionUID = -2772996404655982182L;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String strExpiryDate = req.getParameter("expirtyDate");
        try {
            sdf.parse(strExpiryDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 省略其他代码
    }

}
