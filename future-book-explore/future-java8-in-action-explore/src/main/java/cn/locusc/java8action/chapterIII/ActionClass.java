package cn.locusc.java8action.chapterIII;

import cn.locusc.java8action.chapterII.PracticeClass;
import cn.locusc.java8action.domain.Apple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static cn.locusc.java8action.chapterII.PracticeClass.filter;

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
    }

}
