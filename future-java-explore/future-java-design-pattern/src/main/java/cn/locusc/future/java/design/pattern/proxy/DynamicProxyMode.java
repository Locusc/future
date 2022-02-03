package cn.locusc.future.java.design.pattern.proxy;

import cn.locusc.future.java.design.pattern.domain.Person;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyMode implements InvocationHandler {

    // 声明被代理的对象
    private Person person;

    // 构造函数
    public DynamicProxyMode(Person person) {
        this.person = person;
    }

    // 获取代理对象
    public Object getTarget() {
        return Proxy.newProxyInstance(
                person.getClass().getClassLoader(),
                person.getClass().getInterfaces(),
                this
        );
    }

    public static void main(String[] args) {
        System.out.println("不使用代理类, 调用doSomething");
        Person person = new Jay();
        person.doSomething();

        System.out.println("-------------------------");

        System.out.println("使用代理类, 调用doSomething");
        Person proxy = (Person) new DynamicProxyMode(new Jay()).getTarget();
        proxy.doSomething();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("对原方法进行了前置增强");
        // 原方法执行
        Object invoke = method.invoke(person, args);
        System.out.println("对原方法进行了后置增强");
        return invoke;
    }
}
