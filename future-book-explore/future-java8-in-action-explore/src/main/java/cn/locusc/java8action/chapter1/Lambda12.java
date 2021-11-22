package cn.locusc.java8action.chapter1;

import cn.locusc.java8action.domain.Apple;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Jay
 * Java 中的函数
 * 2021/11/15
 */
@Data
public class Lambda12 {

    /**
     * 1.2.1 方法和 Lambda 作为一等公民
     */
    public void Lambda121() {
        // java < 8
        File[] files = new File("*").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isHidden();
            }
        });

        // java >= 8
        File[] files1 = new File("*").listFiles(File::isHidden);
    }

    /**
     * 1.2.2 传递代码：一个例子
     */
    static class Lambda122 {

        // old
        public static List<Apple> filterGreenApples(List<Apple> inventory){
            List<Apple> result = new ArrayList<>();
            for (Apple apple : inventory) {
                if ("green".equals(apple.getColor())) {
                    result.add(apple);
                }
            }
            return result;
        }

        public static List<Apple> filterHeavyApples(List<Apple> inventory){
            List<Apple> result = new ArrayList<>();
            for (Apple apple : inventory) {
                if (apple.getWeight() > 150) {
                    result.add(apple);
                }
            }
            return result;
        }

        // new
        // Predicate 谓语；谓词；断言
        public static boolean isGreenApple(Apple apple) {
            return "green".equals(apple.getColor());
        }

        public static boolean isHeavyApple(Apple apple) {
            return apple.getWeight() > 150;
        }

        public interface Predicate<T> {
            boolean test(T t);
        }

        public static List<Apple> filterApples(List<Apple> inventory,
                                        Predicate<Apple> p) {
            ArrayList<Apple> result = new ArrayList<>();
            for (Apple apple : inventory) {
                if (p.test(apple)) {
                    result.add(apple);
                }
            }
            return result;
        }

        // 使用
        public static void main(String[] args) {
            List<Apple> inventory = new ArrayList<>();
            Apple apple = new Apple();
            inventory.add(apple);

            filterApples(inventory, Lambda122::isGreenApple);
            filterApples(inventory, Lambda122::isHeavyApple);
        }

    }

    /**
     * 1.2.2 从传递方法到 Lambda
     */
    static class Lambda123 {

        public interface dynamicFilter {
            <T> Collection<T> filter(Collection<T> c, Predicate<T> p);
        }

        static <T> Collection<T> filter(Collection<T> c,
                                        Collection<T> b,
                                        Predicate<T> p) {
            c.forEach(value -> {
                if (p.test(value)) {
                    b.add(value);
                }
            });
            return b;
        }

        public static void main(String[] args) {
            List<Apple> inventory = new ArrayList<>();
            Apple apple = new Apple();
            apple.setColor("green");
            apple.setWeight(160);

            Apple apple1 = new Apple();
            apple1.setColor("brown");
            apple1.setWeight(80);

            inventory.add(apple);
            inventory.add(apple1);

            Lambda122.filterApples(inventory, (Apple a) -> "green".equals(a.getColor()));
            Lambda122.filterApples(inventory, (Apple a) -> a.getWeight() > 150 );
            Lambda122.filterApples(inventory, (Apple a) -> a.getWeight() < 80 ||
                    "brown".equals(a.getColor()));

            Collection<Apple> filter = filter(inventory, new ArrayList<>(), (Apple a) -> a.getWeight() > 150);

            System.out.println(JSON.toJSONString(filter));
        }
    }

}
