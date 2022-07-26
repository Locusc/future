package cn.locusc.mtia.chapter6.codelist;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author Jay
 *
 * 使用Filter规避ThreadLocal内存泄露示例代码
 *
 * 对于同一个ThreadLocal实例，ThreadLocal.remove()能够奏效的前提是，其执行线程与ThreadLocal.get()/set(T)的执行线程必须是同一个线程。
 * 由于ThreadLocal.get()/remove()/set(T)这几个方法都是针对当前线程（即这些方法的执行线程）的，
 * 因此即使是针对同一个ThreadLocal实例,我们也无法通过在一个线程中调用ThreadLocal.remove()来将另外一个线程的线程特有对象从其所属的Entry中剥离。
 * 换而言之，我们无法通过在一个线程中执行ThreadLocal.remove()来规避另外一个线程因使用ThreadLocal而导致的伪内存泄露?!
 *
 * 因此，在 Web应用中，为了规避ThreadLocal 可能导致的内存泄漏、伪内存泄漏，
 * 我们通常需要在javax.servlet.Filter接口实现类的doFilter方法中调用ThreadLocal.remove()。
 * 这其实是利用Filter 的一个重要特征（以便ThreadLocal.remove()调用能够奏效):
 * Web服务器对同一个HTTP请求进行处理时，Filter.doFilter方法的执行线程与Servlet 的执行线程(即Servlet.service方法的执行线程）是同一个线程。
 * 如清单6-11所示的Filter可用于规避ThreadLocalMemoryLeak类(见清单6-10)中定义的线程局部变量counterHolder可能导致的内存泄漏、伪内存泄漏。
 *
 * 在上述Filter.doFilter方法中，我们在FilterChain.doFilter调用之后，即请求处理结束之后调用ThreadLocal.remove()。
 * 在 Web应用中使用线程特有对象可能导致线程特有对象的“退化”:在上述例子中，为了避免ThreadLocal 导致的伪内存泄漏(或内存泄漏),
 * 我们在每个请求处理结束后都将该请求的处理线程的线程特有对象（Counter 实例）清理掉。
 * 因此，不同的请求即使是先后由同一个（任意的）服务器工作者线程来负责处理的，
 * 这个（任意的）线程每次执行ThreadLocalMemoryLeak.doGet方法（以对请求进行处理)的时候都会创建新的 Counter实例。
 * 这就意味着:首先，不同的服务器工作者线程不会访问相同的 Counter实例，即Counter实例不会被多个服务器工作者线程共享,
 * 这说明该例子对Counter的使用方式(线程局部变量)与直接将Counter实例定义为一个静态变量( final static Counter COUNTER=new Counter();)还是不同的。
 * 其次,这些服务器工作者线程所访问的线程特有对象( Counter实例）实际上已“退化”成“请求特有对象”——每一个请求都对应一个Counter实例。
 *
 * 2022/7/18
 */
@WebFilter("/memoryLeak")
public class ThreadLocalCleanupFilter611 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
        ThreadLocalMemoryLeak610.counterHolder.remove();
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        // 什么也不做
    }

    @Override
    public void destroy() {
        // 什么也不做
    }

}
