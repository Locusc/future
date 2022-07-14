package chapter3.codelist.case02;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * @author Jay
 * Sevlet Filter中更新统计指标
 * 2022/7/12
 */
@WebFilter("/echo")
public class CountingFilter322 implements Filter {

    final Indicator321 indicator321 = Indicator321.getInstance();

    public CountingFilter322() {
        // 什么也不做
    }

    @Override
    public void destroy() {
        // 什么也不做
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        StatusExposingResponse httpResponse = new StatusExposingResponse(
                (HttpServletResponse) servletResponse);

        filterChain.doFilter(servletRequest, servletResponse);

        int statusCode = httpResponse.getStatus();
        if (0 == statusCode || 2 == statusCode / 100) {
            indicator321.newRequestProcessed();
        } else {
            indicator321.requestProcessedFailed();
        }
    }

    public static class StatusExposingResponse extends HttpServletResponseWrapper {

        private int httpStatus;

        public StatusExposingResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        @Override
        public int getStatus() {
            return httpStatus;
        }

    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        // 什么也不做
    }

}
