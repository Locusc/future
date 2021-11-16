package cn.locusc.java8action.chapterI;

import cn.locusc.java8action.domain.Apple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Jay
 * 默认方法
 * 2021/11/15
 */
public class DefaultMethod14 {

    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        // 串行
        List<Apple> heavyApples1 =
                inventory.stream().filter((Apple a) -> a.getWeight() > 150)
                        .collect(toList());

        List<Apple> heavyApples2 =
                inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150)
                        .collect(toList());

        // java8新增了默认方法
        // 避免增加新的接口方法后需要所有实现类实现改方法
        // 接口如今可以包含实现类没有提供实现的方法签名
        // 缺失的方法主体随接口提供了（因此就有了默认实现），而不是由实现类提供。
        // 这就给接口设计者提供了一个扩充接口的方式，而不会破坏现有的代码
        // 使用default关键字修饰 列如sort方法
        heavyApples2.sort(Comparator.comparing(Apple::getWeight));

    }

}
