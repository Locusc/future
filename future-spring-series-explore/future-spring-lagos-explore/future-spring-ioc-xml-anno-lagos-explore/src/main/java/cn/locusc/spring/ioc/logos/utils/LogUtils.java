package cn.locusc.spring.ioc.logos.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogUtils {

    @Pointcut("execution(* cn.locusc.spring.ioc.logos.service.impl.TransferServiceImpl.*(..))")
    public void pt1() { }

    /**
     * 业务逻辑开始之前执行
     */
    @Before("pt1()")
    public void beforeMethod(JoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            System.out.println(arg);
        }
        System.out.println("业务逻辑开始执行之前执行......");
    }

    /**
     * 业务逻辑结束时执行(无论异常与否)
     */
    @After("pt1()")
    public void afterMethod() {
        System.out.println("业务逻辑结束时执行......");
    }

    /**
     * 业务逻辑异常时执行
     */
    @AfterThrowing("pt1()")
    public void exceptionMethod() {
        System.out.println("业务逻辑异常时执行......");
    }

    /**
     * 业务逻辑正常时执行
     */
    @AfterReturning(value = "pt1()", returning = "retValue")
    public void successMethod(Object retValue) {
        System.out.println("业务逻辑正常时执行......");
    }

    /**
     * 环绕通知 环绕通知不应和其他几种通知一起使用
     */
    // @Around("pt1()")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint) {
        System.out.println("环绕通知中的beforeMethod");
        Object proceed = null;
        try {
            // 控制原有业务逻辑是否执行
            proceed = proceedingJoinPoint.proceed(); // method.invoke
        } catch (Throwable throwable) {
            System.out.println("环绕通知中的exceptionMethod");
        } finally {
            System.out.println("环绕通知中的afterMethod");
        }
        return proceed;
    }
}
