package cn.locusc.java8action.chapter2;

import cn.locusc.java8action.domain.Apple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jay
 * 练习
 * 2021/11/16
 */
public class PracticeClass {

    // self
    public interface ApplePredicate {
        String toString (Apple apple);
    }

    public static class AppleWeightStringPredicate implements ApplePredicate {
        public String toString(Apple apple){
            return String.valueOf(apple.getWeight());
        }
    }

    public static void prettyPrintApple(List<Apple> inventory, ApplePredicate p){
        for(Apple apple: inventory) {
            String output = p.toString(apple);
            System.out.println(output);
        }
    }

    // official
    public interface AppleFormatter{
        String accept(Apple a);
    }

    public static class AppleFancyFormatter implements AppleFormatter {
        public String accept(Apple apple){
            String characteristic = apple.getWeight() > 150 ? "heavy" :
                    "light";
            return "A " + characteristic +
                    " " + apple.getColor() +" apple";
        }
    }

    public static class AppleSimpleFormatter implements AppleFormatter{
        public String accept(Apple apple){
            return "An apple of " + apple.getWeight() + "g";
        }
    }

    public static void prettyPrintApple(List<Apple> inventory,
                                        AppleFormatter formatter){
        for(Apple apple: inventory){
            String output = formatter.accept(apple);
            System.out.println(output);
        }
    }

    // simplify
    public interface Predicate<T> {
        boolean test(T t);
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for(T e: list){
            if(p.test(e)){
                result.add(e);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Apple> apples = Apple.getApples();

        // 类 对象传递
        prettyPrintApple(apples, new AppleWeightStringPredicate());
        prettyPrintApple(apples, new AppleFancyFormatter());


        // simplify
        // 匿名类
        // 不易读 造成指向判断错误
        ActionClass.filterApples(apples, new ActionClass.ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return "red".equals(apple.getColor());
            }
        });

        // lambda表达式
        ActionClass.filterApples(apples, (Apple apple) -> "red".equals(apple.getColor()));

        filter(apples, (Apple apple) -> "red".equals(apple.getColor()));

        List<Integer> nums = Arrays.asList(1, 2, 3);
        filter(nums, (Integer j) ->  j % 2 == 0);
    }
}
