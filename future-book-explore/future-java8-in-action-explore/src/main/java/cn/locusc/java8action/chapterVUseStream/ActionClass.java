package cn.locusc.java8action.chapterVUseStream;

import cn.locusc.java8action.domain.Dish;
import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Jay
 * 5使用流
 * 小结
 * 这一章很长，但是很有收获！现在你可以更高效地处理集合了。事实上，流让你可以简洁地
 * 表达复杂的数据处理查询。此外，流可以透明地并行化。以下是你应从本章中学到的关键概念。
 *  Streams API可以表达复杂的数据处理查询。常用的流操作总结在表5-1中。
 *  你可以使用filter、distinct、skip和limit对流做筛选和切片。
 *  你可以使用map和flatMap提取或转换流中的元素。
 *  你可以使用findFirst和 findAny方法查找流中的元素。你可以用allMatch、
 * noneMatch和anyMatch方法让流匹配给定的谓词。
 *  这些方法都利用了短路：找到结果就立即停止计算；没有必要处理整个流。
 *  你可以利用reduce方法将流中所有的元素迭代合并成一个结果，例如求和或查找最大
 * 元素。
 *  filter和map等操作是无状态的，它们并不存储任何状态。reduce等操作要存储状态才
 * 能计算出一个值。sorted和distinct等操作也要存储状态，因为它们需要把流中的所
 * 有元素缓存起来才能返回一个新的流。这种操作称为有状态操作。
 *  流有三种基本的原始类型特化：IntStream、DoubleStream和LongStream。它们的操
 * 作也有相应的特化。
 *  流不仅可以从集合创建，也可从值、数组、文件以及iterate与generate等特定方法
 * 创建。
 *  无限流是没有固定大小的流。
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


        // 5.4 归约 将流归约成一个值
        // 函数式编程语言的术语来说，这称为折叠（fold），因为你可以将这个操作看成把一张长长的纸（你的流）反复折叠成一个小方块，而这就是折叠操作的结果

        // 5.4.1 old 元素求和
        int sum = 0;
        for (int x :numbers) {
            sum += x;
        }
        // new reduce求和
        numbers.stream().reduce(0, (a, b) -> a + b);
        // 方法引用
        numbers.stream().reduce(0, Integer::sum);
        // 相乘
        numbers.stream().reduce(0, (a, b) -> a * b);
        // 没有任何元素的情况返回一个Optional<Integer>
        // 没有初始值 表明和可能不存在
        Optional<Integer> reduce = numbers.stream().reduce((a, b) -> (a + b));

        // 5.4.2 最大值和最小值
        // 最大值
        Optional<Integer> reduce1 = numbers.stream().reduce(Integer::max);
        // 最小值
        Optional<Integer> reduce2 = numbers.stream().reduce(Integer::min);
        numbers.stream().reduce((x, y) -> x < y ? x : y);
        // 练习 样用map和reduce方法数一数流中有多少个菜
        // map和reduce的连接通常称为map-reduce模式 因为它很容易并行化
        Optional<Integer> reduce3 = Dish.getOfficial()
                .stream()
                .map(value -> 1)
                .reduce(Integer::sum);
        // or
        long count = Dish.getOfficial().stream().count();
        // or
        Dish.getOfficial().size();
        //归约方法的优势与并行化
        //相比于前面写的逐步迭代求和，使用reduce的好处在于，这里的迭代被内部迭代抽象掉
        //了，这让内部实现得以选择并行执行reduce操作。而迭代式求和例子要更新共享变量sum，
        //这不是那么容易并行化的。如果你加入了同步，很可能会发现线程竞争抵消了并行本应带来的
        //性能提升！这种计算的并行化需要另一种办法：将输入分块，分块求和，最后再合并起来。但
        //这样的话代码看起来就完全不一样了。你在第7章会看到使用分支/合并框架来做是什么样子。
        //但现在重要的是要认识到，可变的累加器模式对于并行化来说是死路一条。你需要一种新的模
        //式，这正是reduce所提供的。你还将在第7章看到，使用流来对所有的元素并行求和时，你的
        //代码几乎不用修改：stream()换成了parallelStream()。
        //int sum = numbers.parallelStream().reduce(0, Integer::sum);
        //但要并行执行这段代码也要付一定代价，我们稍后会向你解释：传递给reduce的Lambda
        //不能更改状态（如实例变量），而且操作必须满足结合律才可以按任意顺序执行。

        // 流操作：无状态和有状态
        //你已经看到了很多的流操作。乍一看流操作简直是灵丹妙药，而且只要在从集合生成流的
        //时候把Stream换成parallelStream就可以实现并行。
        //当然，对于许多应用来说确实是这样，就像前面的那些例子。你可以把一张菜单变成流，
        //用filter选出某一类的菜肴，然后对得到的流做map来对卡路里求和，最后reduce得到菜单
        //的总热量。这个流计算甚至可以并行进行。但这些操作的特性并不相同。它们需要操作的内部
        //状态还是有些问题的。
        //诸如map或filter等操作会从输入流中获取每一个元素，并在输出流中得到0或1个结果。
        //这些操作一般都是无状态的：它们没有内部状态（假设用户提供的Lambda或方法引用没有内
        //部可变状态）。
        //但诸如reduce、sum、max等操作需要内部状态来累积结果。在上面的情况下，内部状态
        //很小。在我们的例子里就是一个int或double。不管流中有多少元素要处理，内部状态都是有界的。
        //相反，诸如sort或distinct等操作一开始都和filter和map差不多——都是接受一个
        //流，再生成一个流（中间操作），但有一个关键的区别。从流中排序和删除重复项时都需要知
        //道先前的历史。例如，排序要求所有元素都放入缓冲区后才能给输出流加入一个项目，这一操
        //作的存储要求是无界的。要是流比较大或是无限的，就可能会有问题（把质数流倒序会做什么
        //呢？它应当返回最大的质数，但数学告诉我们它不存在）。我们把这些操作叫作有状态操作。



        // 5.6 数值流
        // 每个Integer都必须拆箱成一个原始类型 暗含的装箱成本。
        int calories = menu.stream()
                .map(Dish::getCalories)
                .reduce(0, Integer::sum);
        // 理想是Stream有一个直接的sum方法 问题在于map方法会生成一个Stream<T>
        // 把各种菜加起来是没有任何意义的
        // Stream提供了原始类型流特化，专门支持处理数值流的方法 目的是解决装箱造成的复杂性
        // 5.6.1 原始类型流特化
        // 1. 映射到数值流 如果流是空的，sum默认返回0
        int sum1 = menu.stream()
                .mapToInt(Dish::getCalories)
                .sum();

        // 2. 转换回对象流 数值流 -> 非特化流
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        // 将数值范围装箱成为一个一般流时，boxed尤其有用
        Stream<Integer> boxed = intStream.boxed();

        // 3. 默认值OptionalInt
        // OptionalInt、OptionalDouble和OptionalLong
        OptionalInt max = menu.stream()
                .mapToInt(Dish::getCalories)
                .max();
        int i = max.orElse(1);
        System.out.println(i);


        // 5.6.2 数值范围
        // 生成1和100之间的所数字
        // range是不包含结束值的 rangeClosed则包含结束值
        // 也就是range不包含结束的100 rangeClosed包含
        IntStream evenNumbers = IntStream.rangeClosed(1, 100);
        // 一个从1到100的偶数流
        IntStream intStream1 = IntStream.rangeClosed(1, 100)
                .filter(n -> n % 2 == 0);
        // 从 1 到 100 有50个偶数
        System.out.println(intStream1.count());

        // 5.6.3 数值流应用：勾股数
        // 1. 勾股数 a * a + b * b = c * c
        // 2. 表示三元数 如new int[]{3, 4, 5}
        // 3. 筛选成立的组合 表示整数filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
        // 4. 生成三元组
        // stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
        // .map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
        // 5. 生成b值 这里假设
        // IntStream.rangeClosed(1, 100)
        //        .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
        //        .boxed()
        //        .map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
        // 6. 生成值
        //创建一个从1到100的数值范围来生成a的值。对每
        //个给定的a值，创建一个三元数流。要是把a的值映射到三元数流的话，就会得到一个由流构成的
        //流。flatMap方法在做映射的同时，还会把所有生成的三元数流扁平化成一个流。这样你就得到
        //了一个三元数流。还要注意，我们把b的范围改成了a到100。没有必要再从1开始了，否则就会造
        //成重复的三元数，例如(3,4,5)和(4,3,5)。
        Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, 100)
                        .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                        .mapToObj(b ->
                                new int[]{a, b, (int) Math.sqrt(a * a + b * b)})
                );

        pythagoreanTriples.limit(5).forEach(t ->
                System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

        // to be better
        Stream<double[]> pythagoreanTriplesBetter = IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, 100)
                        .mapToObj(b ->
                                new double[]{a, b, Math.sqrt(a * a + b * b)})
                        .filter(t -> t[2] % 1 == 0)
                );
        pythagoreanTriplesBetter.limit(5).forEach(t ->
                System.out.println(t[0] + ", " + t[1] + ", " + t[2]));


        // 5.7 构建流
        // 将介绍如何从值序列、数组、文件来创建流，甚至由生成函数来创建无限流
        // 5.7.1 由值创建流 用静态方法Stream.of，通过显式值创建一个流
        Stream<String> stream = Stream.of("Java 8 ", "Lambdas ", "In ", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);
        // 使用empty得到一个空流
        Stream<String> emptyStream = Stream.empty();

        // 5.7.2 由数组创建流 以使用静态方法Arrays.stream从数组创建一个流
        // 例如你可以将一个原始类型int的数组转换成一个IntStream
        int[] nums = {2, 3, 5, 7, 11, 13};
        int sumNums = Arrays.stream(nums).sum();

        // 5.7.3 由文件生成流
        // 例如，一个很有用的方法是Files.lines，它会返回一个由指定文件中的各行构成的字符串流
        // 文件中有多少各不相同的词
//        long uniqueWords = 0;
//        try(Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
//            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
//                    .distinct()
//                    .count();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        // 5.7.4 由函数生成流：创建无限流
        //Stream API提供了两个静态方法来从函数生成流：Stream.iterate和Stream.generate。
        //这两个操作可以创建所谓的无限流：不像从固定集合创建的流那样有固定大小的流。由iterate
        //和generate产生的流会用给定的函数按需创建值，因此可以无穷无尽地计算下去！一般来说，
        //应该使用limit(n)来对这种流加以限制，以避免打印无穷多个值。
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);

        // 斐波纳契元组序列
        Stream.iterate(new int[]{0, 1}, n -> new int[]{n[1], n[0] + n[1]})
                .limit(20)
                .map(t -> t[0])
                .forEach(System.out::println);

        // 2. 生成 generate不是依次对每个新生成的值应用函数的
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

        // generate生成斐波纳契元组序列
        //前面的代码创建了一个IntSupplier的实例。此对象有可变的状态：它在两个实例变量中
        //记录了前一个斐波纳契项和当前的斐波纳契项。getAsInt在调用时会改变对象的状态，由此在
        //每次调用时产生新的值。相比之下，使用iterate的方法则是纯粹不变的：它没有修改现有状态，
        //但在每次迭代时会创建新的元组。你将在第7章了解到，你应该始终采用不变的方法，以便并行
        //处理流，并保持结果正确。请注意，因为你处理的是一个无限流，所以必须使用limit操作来显
        //式限制它的大小；否则，终端操作（这里是forEach）将永远计算下去。同样，你不能对无限流
        //做排序或归约，因为所有元素都需要处理，而这永远也完不成！
        IntSupplier fib = new IntSupplier(){

            private int previous = 0;
            private int current = 1;

            @Override
            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrevious;
            }

        };

        IntStream.generate(fib).limit(10).forEach(System.out::println);

    }

}
