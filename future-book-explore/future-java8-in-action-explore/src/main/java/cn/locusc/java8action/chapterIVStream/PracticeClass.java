package cn.locusc.java8action.chapterIVStream;

import cn.locusc.java8action.domain.Dish;

/**
 * @author Jay
 * 流 练习
 * 小结:
 *  流是“从支持数据处理操作的源生成的一系列元素”。
 *  流利用内部迭代：迭代通过filter、map、sorted等操作被抽象掉了。
 *  流操作有两类：中间操作和终端操作。
 *  filter和map等中间操作会返回一个流，并可以链接在一起。可以用它们来设置一条流
 * 水线，但并不会生成任何结果。
 *  forEach和count等终端操作会返回一个非流的值，并处理流水线以返回结果。
 *  流中的元素是按需计算的。
 * 2021/11/19
 */
public class PracticeClass {

    public static void main(String[] args) {
        // 流水线中最后一个操作count返回一个long，这是一个非Stream的值。因此它是打印当前筛选的菜肴提取菜名时打印出来
        // 一个终端操作。所有前面的操作，filter、distinct、limit，都是连接起来的，并返回一个Stream，因此它们是中间操作
        long count = Dish.getOfficial()
                .stream()
                .filter(dish -> dish.getCalories() > 300)
                .distinct()
                .limit(3)
                .count();

        System.out.println(count);

    }

}
