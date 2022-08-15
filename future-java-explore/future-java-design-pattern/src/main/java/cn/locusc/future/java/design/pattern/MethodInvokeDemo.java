package cn.locusc.future.java.design.pattern;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class MethodInvokeDemo {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
            IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {

        Class<?> clazz = createClazz();

        Constructor<?> constructor = clazz.getConstructor(String.class);

        Object instance = newInstance(constructor,"improved");

        invokeByBeanInfo(clazz, instance);

        invokeByPropertyDescriptor(clazz, instance);
    }

    private static void invokeByBeanInfo(Class<?> clazz, Object instance) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(clazz);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        Optional<PropertyDescriptor> messagePd = Arrays.stream(propertyDescriptors)
                .filter(f -> "message".equals(f.getName()))
                .findFirst();

        messagePd.ifPresent(ip -> {
            Method readMethod = ip.getReadMethod();
            try {
                Object invoke = readMethod.invoke(instance);
                System.out.println(invoke);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private static void invokeByPropertyDescriptor(Class<?> clazz, Object instance) {
        Field[] fields = clazz.getDeclaredFields();
        Optional<Field> messageFd = Arrays.stream(fields)
                .filter(f -> "message".equals(f.getName()))
                .findFirst();

        messageFd.ifPresent(ip -> {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(ip.getName(), clazz);
                Method readMethod = propertyDescriptor.getReadMethod();
                Object invoke = readMethod.invoke(instance);
                System.out.println(invoke);
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private static Class<?> createClazz() throws ClassNotFoundException {
        return Class.forName("cn.locusc.future.java.design.pattern.domain.ReflectionClazz");
    }

    private static BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz);
    }

    private static Object newInstance(Constructor<?> constructor, Object... initargs)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(initargs);
    }

}
