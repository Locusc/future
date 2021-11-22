package cn.locusc.java8action.chapter1;

import cn.locusc.java8action.domain.Apple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jay
 * 默认方法
 * 2021/11/15
 * 小结:
 *  请记住语言生态系统的思想，以及语言面临的“要么改变，要么衰亡”的压力。虽然Java
 * 可能现在非常有活力，但你可以回忆一下其他曾经也有活力但未能及时改进的语言的命
 * 运，如COBOL。
 *  Java 8中新增的核心内容提供了令人激动的新概念和功能，方便我们编写既有效又简洁的
 * 程序。
 *  现有的Java编程实践并不能很好地利用多核处理器。
 *  函数是一等值；记得方法如何作为函数式值来传递，还有Lambda是怎样写的。
 *  Java 8中Streams的概念使得Collections的许多方面得以推广，让代码更为易读，并允
 * 许并行处理流元素。
 *  你可以在接口中使用默认方法，在实现类没有实现方法时提供方法内容。
 *  其他来自函数式编程的有趣思想，包括处理null和使用模式匹配。
 */
public class DefaultMethod14 {

    public static void main(String[] args) {
        // List<Apple> inventory = new ArrayList<>();
        // 串行
        // List<Apple> heavyApples1 =
        //        inventory.stream().filter((Apple a) -> a.getWeight() > 150)
        //                 .collect(toList());

        // List<Apple> heavyApples2 =
        //        inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150)
        //                 .collect(toList());

        // java8新增了默认方法
        // 避免增加新的接口方法后需要所有实现类实现改方法
        // 接口如今可以包含实现类没有提供实现的方法签名
        // 缺失的方法主体随接口提供了（因此就有了默认实现），而不是由实现类提供。
        // 这就给接口设计者提供了一个扩充接口的方式，而不会破坏现有的代码
        // 使用default关键字修饰 列如sort方法
        List<Apple> apples = new ArrayList<>();
        apples.sort(Comparator.comparing(Apple::getWeight));
    }

}
