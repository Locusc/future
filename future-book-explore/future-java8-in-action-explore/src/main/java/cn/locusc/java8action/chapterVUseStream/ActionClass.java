package cn.locusc.java8action.chapterVUseStream;

import cn.locusc.java8action.domain.Dish;
import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Jay
 * 5使用流
 * 2021/11/19
 */
public class ActionClass {

    public static void main(String[] args) {
        // 5.1筛选和切片
        // 5.1.1 用谓词筛选
        List<Dish> vegetarianMenu = Dish.getOfficial()
                .stream()
                // 方法引用检查菜肴是否适合素食者
                .filter(Dish::isVegetarian)
                .collect(toList());

        // 5.1.2 筛选各异的元素
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                // 根据流所生成元素的hashCode和equals方法实现）
                .distinct()
                .forEach(System.out::println);

        // 5.1.3 截短流
        // 意limit也可以用在无序流上，比如源是一个Set。这种情况下，limit的结果不会以任何顺序排列
        List<Dish> dishes = Dish.getOfficial().stream()
                .filter(d -> d.getCalories() > 300)
                .limit(3)
                .collect(toList());

        // 5.1.4 跳过元素
        List<Dish> skipDishes = Dish.getOfficial().stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(toList());

        // 练习
        // 将如何利用流来筛选前两个荤菜
        List<Dish> collect = Dish.getOfficial()
                .stream()
                .filter(dish -> dish.getType() == Dish.Type.MEAT)
                .limit(2)
                .collect(toList());

        // 5.2
        // 映射
        // 一个非常常见的数据处理套路就是从某些对象中选择信息。比如在SQL里，你可以从表中选择一列。
        // Stream API也通过map和flatMap方法提供了类似的工具。
        // 5.2.1 对流中每一个元素应用函数
        List<String> dishNames = Dish.getOfficial().stream()
                .map(Dish::getName)
                .collect(toList());
        // 函数接受一个单词，返回其长度
        List<String> words = Arrays.asList("Java 8", "Lambdas", "In", "Action");
        List<Integer> wordLengths = words.stream()
                .map(String::length)
                .collect(toList());
        // 找出每道菜的名称有多长
        List<Integer> dishNameLengths = Dish.getOfficial().stream()
                .map(Dish::getName)
                .map(String::length)
                .collect(toList());

        // 5.2.2 流的扁平化
        List<String> strings = Arrays.asList("Hello", "World");
        // 需要的效果 ["H","e","l", "o","W","r","d"]
        // 需要Stream<String> 实际返回是Stream<String[]>
        List<String[]> collect1 = strings.stream()
                .map(word -> word.split(""))
                .distinct()
                .collect(toList());

        // 1. 尝试使用map和Arrays.stream()
        // 结果[["G","o","o","d","b","y","e"],["W","o","r","l","d"]]
        String[] arrayOfWords = {"Goodbye", "World"};
        Stream<String> streamOfWords = Arrays.stream(arrayOfWords);
        List<String[]> collect2 = streamOfWords
                .map(word -> word.split(""))
                .distinct()
                .collect(toList());

        // 使用flatMap
        // 结果["H","e","l","o","W","r","d"]
        // 各个数组并不是分别映射成一个流，而是映射成流的内容。
        // 所有使用map(Arrays::stream)时生成的单个流都被合并起来，即扁平化为一个流
        List<String> collect3 = strings.stream()
                // 将每个单词转换为由其字母构成的数组
                .map(word -> word.split(""))
                // 将各个生成流扁平化为单个流
                .flatMap(Arrays::stream)
                .distinct()
                .collect(toList());
        System.out.println(JSON.toJSONString(collect3));

        // 练习
        // 1.给定一个数字列表，如何返回一个由每个数的平方构成的列表呢？例如，给定[1, 2, 3, 4,5]
        // 应该返回[1, 4, 9, 16, 25]。
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> collect4 = integers.stream()
                .map(integer -> integer * integer)
                .collect(toList());

        // 2.给定两个数字列表，如何返回所有的数对呢？例如，给定列表[1, 2, 3]和列表[3, 4]，应
        // 该返回[(1, 3), (1, 4), (2, 3), (2, 4), (3, 3), (3, 4)]。为简单起见，你可以用有两个元素的数组来代表数对。
        List<Integer> integers1 = Arrays.asList(1, 2, 3);
        List<Integer> integers2 = Arrays.asList(3, 4);
        List<int[]> collect5 = integers1.stream()
                .flatMap(i -> integers2.stream()
                        .map(j -> new int[]{i, j})
                )
                .collect(toList());
        System.out.println(JSON.toJSONString(collect5));

        // 如何扩展前一个例子，只返回总和能被3整除的数对呢？例如(2, 4)和(3, 3)是可以的。
        List<int[]> collect6 = integers1.stream()
                .flatMap(i -> integers2.stream()
                        .map(j -> new int[]{i, j})
                )
                .filter(i -> (i[0] + i[1]) % 3 == 0)
                .collect(toList());

        List<int[]> collect7 = integers1.stream()
                .flatMap(i -> integers2.stream()
                        .filter(j -> (j + i) % 3 == 0)
                        .map(j -> new int[]{i, j})
                )
                .collect(toList());
        System.out.println(JSON.toJSONString(collect6));
        System.out.println(JSON.toJSONString(collect7));

        // 5.3 查找与匹配
        // 另一个常见的数据处理套路是看看数据集中的某些元素是否匹配一个给定的属性。Stream
        // API通过allMatch、anyMatch、noneMatch、findFirst和findAny方法提供了这样的工具。
        // 5.3.1 检查谓词是否至少匹配一个元素
        // anyMatch方法返回一个boolean，因此是一个终端操作。
        List<Dish> menu = Dish.getOfficial();
        if(menu.stream().anyMatch(Dish::isVegetarian)) {
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }

        // 5.3.2 检查谓词是否匹配所有元素
        // allMatch方法的工作原理和anyMatch类似，但它会看看流中的元素是否都能匹配给定的谓词
        // 比如，你可以用它来看看菜品是否有利健康（即所有菜的热量都低于1000卡路里）：
        boolean isHealthyAllMatch = Dish.getOfficial()
                .stream()
                .allMatch(d -> d.getCalories() < 1000);

        // 和allMatch相对的是noneMatch。它可以确保流中没有任何元素与给定的谓词匹配。比如，
        boolean isHealthyNoneMatch = menu.stream()
                .noneMatch(d -> d.getCalories() >= 1000);

        // 5.3.3 查找元素
        // findAny方法将返回当前流中的任意元素。
        // Optional
        // isPresent()将在Optional包含值的时候返回true, 否则返回false。
        // ifPresent(Consumer<T> block)会在值存在的时候执行给定的代码块。我们在第3章
        //介绍了Consumer函数式接口；它让你传递一个接收T类型参数，并返回void的Lambda
        //表达式。
        // T get()会在值存在时返回值，否则抛出一个NoSuchElement异常。
        // T orElse(T other)会在值存在时返回值，否则返回一个默认值。
        Optional<Dish> any = menu.stream()
                .filter(Dish::isVegetarian)
                .findAny();

        // 如果包含一个值就打印它，否则什么都不做
        menu.stream()
                .filter(Dish::isVegetarian)
                .findAny()
                .ifPresent(System.out::println);

        // 5.3.4 根据条件查找第一个元素
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> any1 = someNumbers.stream()
                .map(x -> x * x)
                .filter(x -> x % 3 == 0)
                // .findAny()
                .findFirst();


        // 归约 将流归约成一个值
        // 函数式编程语言的术语来说，这称为折叠（fold），因为你可以将这个操作看成把一张长长的纸（你的流）反复折叠成一个小方块，而这就是折叠操作的结果
        // old 元素求和
        int sum = 0;
        for (int x :numbers) {
            sum += x;
        }
        // new reduce求和
        numbers.stream().reduce(0, (a, b) -> a + b);

    }

}
