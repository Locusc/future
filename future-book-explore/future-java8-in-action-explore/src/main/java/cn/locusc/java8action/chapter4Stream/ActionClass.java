package cn.locusc.java8action.chapter4Stream;

import cn.locusc.java8action.domain.Dish;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Jay
 * 引入流
 * 2021/11/19
 */
public class ActionClass {

    public static void main(String[] args) {
        List<Dish> menu = Dish.getDish();
        // 4.1 流是什么
        // 按照calorie排序
        // jdk1.7
        ArrayList<Dish> lowCaloricDishes = new ArrayList<>();
        for(Dish d: menu){
            if(d.getCalories() < 400){
                lowCaloricDishes.add(d);
            }
        }
        lowCaloricDishes.sort(new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return o1.getCalories().compareTo(o2.getCalories());
            }
        });
        // 处理排序后得菜名列表
        ArrayList<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish d : lowCaloricDishes) {
            lowCaloricDishesName.add(d.getName());
        }

        // jdk1.8
        List<String> lowCaloricDishesName8 = menu.stream()
                .filter(d -> d.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(toList());

        // 利用多核架构并行执行
        List<String> lowCaloricDishesName8Parallel = menu.parallelStream()
                .filter(d -> d.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(toList());

        // 4.2 简介 流: 支持 数据处理操作 的 源 生成的 元素序列
        // 元素序列——就像集合一样，流也提供了一个接口，可以访问特定元素类型的一组有序
        //值。因为集合是数据结构，所以它的主要目的是以特定的时间/空间复杂度存储和访问元
        //素（如ArrayList 与 LinkedList）。但流的目的在于表达计算，比如你前面见到的
        //filter、sorted和map。集合讲的是数据，流讲的是计算。我们会在后面几节中详细解
        //释这个思想。
        // 源——流会使用一个提供数据的源，如集合、数组或输入/输出资源。 请注意，从有序集
        //合生成流时会保留原有的顺序。由列表生成的流，其元素顺序与列表一致。
        // 数据处理操作——流的数据处理功能支持类似于数据库的操作，以及函数式编程语言中
        //的常用操作，如filter、map、reduce、find、match、sort等。流操作可以顺序执
        //行，也可并行执行。
        //此外，流操作有两个重要的特点。
        // 流水线——很多流操作本身会返回一个流，这样多个操作就可以链接起来，形成一个大
        //的流水线。这让我们下一章中的一些优化成为可能，如延迟和短路。流水线的操作可以
        //看作对数据源进行数据库式查询。
        // 内部迭代——与使用迭代器显式迭代的集合不同，流的迭代操作是在背后进行的。我们
        //在第1章中简要地提到了这个思想，下一节会再谈到它。

        // 从menu获得流
        List<String> threeHighCaloricDishNames =
                Dish.getOfficial()
                    // 建立操作流水线：
                    .stream()
                    // 首先选出高热量的菜肴
                    .filter(d -> d.getCalories() > 300)
                    // 获取菜名
                    .map(Dish::getName)
                    // 只选择头三个
                    .limit(3)
                    // 将结果保存在另一个List中
                    .collect(toList());

        System.out.println(threeHighCaloricDishNames);

        // 4.3 流与集合
        // 只能遍历一次 只能消费一次
        // IllegalStateException: stream has already been operated upon or closed
        List<String> title = Arrays.asList("Java8", "In", "Action");
        Stream<String> s = title.stream();
        // s.forEach(System.out::println);
        s.forEach(System.out::println);
        // 内部迭代与外部迭代
        // 集合讲的是数据，流讲的是计算。
        // 使用Collection接口需要用户去做迭代（比如用for-each），这称为外部迭代。 相反Streams库使用内部迭代
        // 集合: 用for-each循环外部迭代
        ArrayList<String> names = new ArrayList<>();
        List<Dish> officialDish = Dish.getOfficial();
        // for-each还隐藏了迭代中的一些复杂性, for-each结构是一个语法糖
        // 显式顺序迭代菜单列表
        for (Dish dish : officialDish) {
            // 提取名称并将其添加到累加器
            names.add(dish.getName());
        }
        // 集合: 用背后的迭代器做外部迭代
        Iterator<Dish> iterator = officialDish.iterator();
        while (iterator.hasNext()) {
            Dish next = iterator.next();
            names.add(next.getName());
        }
        // 流: 外部迭代
        List<String> collect = officialDish.stream()
                // 用 getName 方 法参数化map，提取菜名
                .map(Dish::getName)
                // 开始执行操作流水线；没有迭代
                .collect(toList());

        // 4.4 流操作
        //  filter、map和limit可以连成一条流水线；
        //  collect触发流水线执行并关闭它。、
        // 中间操作一般都可以合并起来，在终端操作时一次性全部处理。
        List<String> dishNames =
                Dish.getOfficial()
                        // 从菜单获得流
                        .stream()
                        // 中间操作
                        .filter(d -> {
                            System.out.println("filtering" + d.getName());
                            return d.getCalories() > 300;
                        })
                        // 中间操作
                        .map(d -> {
                            System.out.println("mapping" + d.getName());
                            return d.getName();
                        })
                        // 中间操作
                        .limit(3)
                        // 将Stream转换为List 终端操作
                        // 除非流水线上触发一个终端操作，否则中间操作不会执行任何处理
                        // 没有.collect(toList()); 上面并不会执行
                        .collect(toList());
        // 中间操作
        System.out.println(dishNames);

        // 终端操作
        Dish.getOfficial().stream().forEach(System.out::println);

    }
}
