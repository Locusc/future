package cn.locusc.java8action.chapterIII;

import cn.locusc.java8action.domain.Apple;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

/**
 * @author Jay
 * Lambda表达式
 * 2021/11/16
 */
public class ActionClass {

    public static void process(Runnable r){
        r.run();
    }

    // 环绕模式 都环绕着进行准备/清理的同一段冗余代码
    public static String processFile() throws IOException {
        try (BufferedReader br =
                     new BufferedReader(new FileReader("data.txt"))) {
            return br.readLine();
        }
    }

    // 使用函数式接口来传递行为
    @FunctionalInterface
    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }

    public static String processFile(BufferedReaderProcessor p) throws
            IOException {
        try (BufferedReader br =
                     new BufferedReader(new FileReader("data.txt"))) {
            // 执行一个行为
            return p.process(br);
        }
    }

    // ########################################
    // Predicate
    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for(T e: list){
            if(p.test(e)){
                result.add(e);
            }
        }
        return result;
    }

    // Consumer
    public static <T> void forEach(List<T> list, Consumer<T> c) {
        for (T t : list) {
            c.accept(t);
        }
    }

    // Function
    public static <T, R> List<R> map(List<T> list, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        for (T s : list) {
            result.add(f.apply(s));
        }
        return result;
    }

    public static String testString = "testString";

    public static void main(String[] args) throws IOException {
        List<Apple> inventory = Apple.getApples();

        // anonymousMethod
        Comparator<Apple> byWeightAnonymousMethod = new Comparator<Apple>() {
            public int compare(Apple a1, Apple a2){
                return a1.getWeight().compareTo(a2.getWeight());
            }
        };

        // lambdaFunc
        Comparator<Apple> byWeightLambdaFunc =
                (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

        List<Apple> greenApples =
                filter(inventory, (Apple a) -> "green".equals(a.getColor()));


        // 函数式接口
        Runnable r1 = () -> System.out.println("Hello World 1");

        Runnable r2 = new Runnable(){
            public void run(){
                System.out.println("Hello World 2");
            }
        };
        process(r1);
        process(r2);
        process(() -> System.out.println("Hello World 3"));


        // 环绕执行模式
        // 行为参数化
        // 传递 Lambda
        processFile((BufferedReader br) -> br.readLine() + br.readLine());

        // Predicate
        // 需要表示一个涉及类型T的布尔表达式时
        List<String> strings = Arrays.asList("1", "2", "3");
        Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
        filter(strings, nonEmptyStringPredicate);

        // 无装箱
        IntPredicate evenNumbers = (int i) -> i % 2 == 0;
        evenNumbers.test(1000);

        // 装箱
        Predicate<Integer> oddNumbers = (Integer i) -> i % 2 == 0;
        oddNumbers.test(1000);

        // Consumer
        // 需要访问类型T的对象，并对其执行某些操作，就可以使用这个接口
        forEach(strings, (String i) -> System.out.println(i));

        // Function
        // 需要定义一个Lambda，将输入对象的信息映射到输出，就可以使用这个接口
        map(strings, (String s) -> s.length());

        // 显式捕获异常
        Function<BufferedReader, String> f = (BufferedReader b) -> {
            try {
                return b.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        // 同样的 Lambda，不同的函数式接口
        Comparator<Apple> c1 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
        ToIntBiFunction<Apple, Apple> c2 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
        BiFunction<Apple, Apple, Integer> c3 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

        // 个Lambda的主体是一个语句表达式，它就和一个返回void的函数描述符兼容（当然需要参数列表也兼容）
        Predicate<String> p = s -> strings.add(s);
        Consumer<String> b = s -> strings.add(s);

        // Object o = () -> {System.out.println("Tricky example"); };
        // Lambda表达式的上下文是Object（目标类型）。但Object不是一个函数式接口。
        // 为了解决这个问题，你可以把目标类型改成Runnable，它的函数描述符是() -> void：
        Runnable r = () -> {System.out.println("Tricky example");};

        // 类型推断
        // Java编译器会从上下文（目标类型）推断出用什么函数式接
        // 口来配合Lambda表达式，这意味着它也可以推断出适合Lambda的签名，因为函数描述符可以通
        // 过目标类型来得到。这样做的好处在于，编译器可以了解Lambda表达式的参数类型，这样就可
        // 以在Lambda语法中省去标注参数类型。换句话说，Java编译器会像下面这样推断Lambda的参数
        // 类型：①
        filter(inventory, a -> "green".equals(a.getColor()));

        // 使用局部变量 实例变量都存储在堆中 局部变量则保存在栈上
        // 如果Lambda可以直接访问局部变量，而且Lambda是在一个线程中使用的，则使用Lambda的线程，可能会在分配该变量的线程将这个变量收回之后，去访问该变量
        // Java在访问自由局部变量时，实际上是在访问它的副本，而不是访问原始变量。
        // 局部变量必须隐式或者显式final修饰 避免线程问题
        // 这里会提示错误 Lambda表达式引用的局部变量必须是最终的（final）或事实上最终的
        int portNumber = 1337;
        // Runnable runnable = () -> System.out.println(portNumber);
        portNumber = 31337;

    }


    // 方法引用
    public static class MethodReference {

        public static void main(String[] args) {

            List<Apple> inventory = Apple.getApples();


        }

    }

}
