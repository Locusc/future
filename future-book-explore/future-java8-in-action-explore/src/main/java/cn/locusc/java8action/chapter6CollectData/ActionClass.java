package cn.locusc.java8action.chapter6CollectData;

import cn.locusc.java8action.domain.Currency;
import cn.locusc.java8action.domain.Dish;
import cn.locusc.java8action.domain.Transaction;
import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author Jay
 * 用流收集数据
 * 本章内容
 *  用Collectors类创建和使用收集器
 *  将数据流归约为一个值
 *  汇总：归约的特殊情况
 *  数据分组和分区
 *  开发自己的自定义收集器
 *
 *
 * 小结
 *  collect是一个终端操作，它接受的参数是将流中元素累积到汇总结果的各种方式（称
 * 为收集器）。
 *  预定义收集器包括将流元素归约和汇总到一个值，例如计算最小值、最大值或平均值。
 * 这些收集器总结在表6-1中。
 *  预定义收集器可以用groupingBy对流中元素进行分组，或用partitioningBy进行分区。
 *  收集器可以高效地复合起来，进行多级分组、分区和归约。
 *  你可以实现Collector接口中定义的方法来开发你自己的收集器。
 * 2021/11/22
 */
public class ActionClass {

    /**
     * 此区分Collection、Collector和collect
     */
    public static void main(String[] args) {

        // 由Transaction构成的List，并且想按照名义货币进行分组
        // old
        // 建立累积交易分组的Map
        Map<Currency, List<Transaction>> transactionsByCurrencies =
                new HashMap<>();
        List<Transaction> transactions = new ArrayList<>();
        // 迭代Transaction的List
        for (Transaction transaction : transactions) {
            // 提取Transaction的货币
            Currency currency = transaction.getCurrency();
            List<Transaction> transactionsForCurrency =
                    transactionsByCurrencies.get(currency);
            // 如果分组Map中没有这种货币的条目，就创建一个
            if (transactionsForCurrency == null) {
                transactionsForCurrency = new ArrayList<>();
                transactionsByCurrencies
                        .put(currency, transactionsForCurrency);
            }
            // 将当前遍历的Transaction加入同一货币的Transaction的List
            transactionsForCurrency.add(transaction);
        }

        // Stream分组
        Map<Currency, List<Transaction>> collect = transactions
                .stream()
                .collect(groupingBy(Transaction::getCurrency));
    }

    /**
     * 6.1.1 收集器用作高级归约
     */
    public static class Jia611 {

        public static void main(String[] args) {
            // 更易复合和重用
            // Collector会对元素应用一个转换函数
            // 并将结果累积在一个数据结构中，从而产生这一过程的最终输出
            // 最直接和最常用的收集器是toList静态方法，它会把流中所有的元素收集到一个List中
            List<Transaction> transactions = new ArrayList<>();
            transactions.stream().collect(Collectors.toList());
        }

    }

    /**
     * 6.1.2 预定义收集器
     */
    public static class Jia612 {

        public static void main(String[] args) {
            // 从Collectors类提供的工厂方法（例如groupingBy）创建的收集器
            // 主要提供了三大功能
            //  将流元素归约和汇总为一个值
            //  元素分组
            //  元素分区
            // 分组的特殊情况“分区”，即使用谓词（返回一个布尔值的单参数函数）作为分组函数。
        }

    }

    /**
     * 6.2 归约和汇总
     * 1-3 对单个值的归约汇总
     */
    public static class Jia62 {

        public static void main(String[] args) {
            // 再宽泛一点来说，但凡要把流中所有的项目合并成一个结果时就可以用。这个结果可以
            // 是任何类型，可以复杂如代表一棵树的多级映射，或是简单如一个整数——也许代表了菜单的热量总和
            final List<Dish> official = Dish.getOfficial();
            final Long collect = official.stream().collect(Collectors.counting());
            // 相当于
            final long count = official.stream().count();
            System.out.println("Collectors.counting():" + collect + "count():" + count);
        }

        /**
         * 6.2.1 查找流中的最大值和最小值
         */
        public static class Jia621 {

            public static void main(String[] args) {
                List<Dish> menu = Dish.getOfficial();
                final Optional<Integer> reduce = menu.stream()
                        .map(Dish::getCalories)
                        .reduce(Integer::max);

                // 找出菜单中热量最高的菜
                // Collectors.maxBy和/Collectors.minBy，来计算流中的最大或最小值
                final Comparator<Dish> dishComparator = Comparator.comparingInt(Dish::getCalories);
                final Optional<Dish> collect = menu.stream()
                        .collect(maxBy(dishComparator));


            }
        }

        /**
         * 6.2.2 汇总
         */
        public static class Jia622 {

            public static void main(String[] args) {
                // 求出菜单列表的总热量
                List<Dish> menu = Dish.getOfficial();
                // Collectors.summingInt
                // 接受一个把对象映射为求和所需int的函数，并返回一个收集器
                // Collectors.summingLong和Collectors.summingDouble方法的作用完全一样
                final Integer collect = menu.stream().collect(summingInt(Dish::getCalories));
                System.out.println(collect);
                // 有Collectors.averagingInt，连同对应的averagingLong和averagingDouble可以计算数值的平均数
                final Double collect1 = menu.stream().collect(averagingInt(Dish::getCalories));

                // 使用summarizingInt工厂
                // 方法返回的收集器。例如，通过一次summarizing操作你可以就数出菜单中元素的个数，并得
                // 到菜肴热量总和、平均值、最大值和最小值：
                final IntSummaryStatistics collect2 = menu.stream().collect(summarizingInt(Dish::getCalories));
                System.out.println(collect2);

                // 相应的summarizingLong和summarizingDouble工厂方法有相关的LongSummaryStatistics和DoubleSummaryStatistics类型，适用于收集的属性是原始类型long或double的情况
            }
        }

        /**
         * 6.2.3 连接字符串
         */
        public static class Jia623 {

            public static void main(String[] args) {
                // joining工厂方法返回的收集器会把对流中每一个对象应用toString方法得到的所有字符串连接成一个字符串
                // 把菜单中所有菜肴的名称连接起来
                List<Dish> menu = Dish.getOfficial();
                final String collect = menu.stream().map(Dish::getName).collect(joining());
                // 如果Dish类有一个toString方法来返回菜肴的名称，那你无需用提取每一道菜名称的
                // 函数来对原流做映射就能够得到相同的结果 【没验证出来】
                // String shortMenu = menu.stream().collect(joining());
                System.out.println(collect);

                // joining工厂方法有一个重载版本可以接受元素之间的分界符
                // 一个逗号分隔的菜肴名称列表
                final String collect1 = menu.stream().map(Dish::getName).collect(joining(", "));
                System.out.println(collect1);
            }
        }

        /**
         * 6.2.4 广义的归约汇总
         * 以上的收集器都是是一个可以用reducing工厂方法定义的归约过程的特殊
         * Collectors.reducing工厂方法是所有这些特殊情况的一般化
         */
        public static class Jia624 {

            public static void main(String[] args) {
                // 以用reducing方法创建的收集器来计算你菜单的总热量
                List<Dish> menu = Dish.getOfficial();
                // 三参数reduce
                final Integer collect = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
                // 以使用下面这样单参数形式的reducing来找到热量最高的菜
                // 单参数reduce
                // 单参数reducing工厂方法创建的收集器看作三参数方法的特殊情况
                // 流中的第一个项目作为起点，把恒等函数（即一个函数仅仅是返回其输入参数）作为一个转换函数。这
                // 也意味着，要是把单参数reducing收集器传递给空流的collect方法，收集器就没有起点
                // 它将因此而返回一个Optional<Dish>对象
                final Optional<Dish> collect1 = menu.stream().collect(reducing(
                        (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2
                ));


                // Stream接口的collect和reduce方法有何不同，因为两种方法通常会获得相同的结果
                // 例如，使用reduce方法来实现toListCollector所做的工作

                //这个解决方案有两个问题：一个语义问题和一个实际问题。语义问题在于，reduce方法
                //旨在把两个值结合起来生成一个新值，它是一个不可变的归约。与此相反，collect方法的设
                //计就是要改变容器，从而累积要输出的结果。这意味着，上面的代码片段是在滥用reduce方
                //法，因为它在原地改变了作为累加器的List。你在下一章中会更详细地看到，以错误的语义
                //使用reduce方法还会造成一个实际问题：这个归约过程不能并行工作，因为由多个线程并发
                //修改同一个数据结构可能会破坏List本身。在这种情况下，如果你想要线程安全，就需要每
                //次分配一个新的List，而对象分配又会影响性能。这就是collect方法特别适合表达可变容
                //器上的归约的原因，更关键的是它适合并行操作，本章后面会谈到这一点。
                final Stream<Integer> stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();
                stream.reduce(
                        new ArrayList<>(),
                        (List<Integer> l, Integer e) -> {
                            l.add(e);
                            return l;
                        },
                        (List<Integer> l1, List<Integer> l2) -> {
                          l1.addAll(l2);
                          return l1;
                        }
                );

                // 1. 收集框架的灵活性：以不同的方法执行同样的操作
                // 进一步简化前面使用reducing收集器的求和例子 利用Integer的方法引用
                int totalCalories = menu.stream().collect(reducing(0,
                        Dish::getCalories,
                        Integer::sum));


                // 归约操作: 利用累积函数，把一个初始化为起始值的累加器，和把转换函数应用到流中每个元素上得到的结果不断迭代合并起来
                // counting收集器也是类似地利用三参数reducing工厂方法实现的
                //    public static <T> Collector<T, ?, Long>
                //    counting() {
                //        return reducing(0L, e -> 1L, Long::sum);
                //    }

                // 不使用收集器也能执行相同操作
                final Integer integer = menu.stream().map(Dish::getCalories).reduce(Integer::sum).get();
                // 更简洁的方法是把流映射到一个IntStream，然后调用sum方法，你也可以得到相同的结果
                final int sum = menu.stream().mapToInt(Dish::getCalories).sum();

                // 2. 根据情况选择最佳解决方案
                // 函数式编程通常提供了多种方法来执行同一个操作
                // 收集器在某种程度上比Stream接口上直接提供的方法用起来更复杂，但好处在于它们能提供更高水平的抽象和概括，也更容易重用和自定义
                // 尽可能为手头的问题探索不同的解决方案，但在通用的方案里面，始终选择最专门化的一个
            }
        }

    }

    /**
     * 6.3 分组
     */
    public static class Jia63 {

        public static void main(String[] args) {

            // 菜单中的菜按照类型进行分类
            final List<Dish> menu = Dish.getOfficial();
            final Map<Dish.Type, List<Dish>> collect = menu.stream().collect(groupingBy(Dish::getType));

            // 分类函数不一定像方法引用那样可用
            // 因为你想用以分类的条件可能比简单的属性访问器要复杂
            // 热量不到400卡路里的菜划分为“低热量”（diet），热量400到700
            // 卡路里的菜划为“普通”（normal），高于700卡路里的划为“高热量”（fat）。
            final Map<Dish.CaloricLevel, List<Dish>> collect1 = menu.stream().collect(
                    groupingBy(dish -> {
                        if (dish.getCalories() <= 400) return Dish.CaloricLevel.DIET;
                        else if (dish.getCalories() <= 700) return
                                Dish.CaloricLevel.NORMAL;
                        else return Dish.CaloricLevel.FAT;
                    })
            );
        }

        /**
         * 6.3.1 多级分组
         */
        public static class Jia631 {

            public static void main(String[] args) {
                final List<Dish> menu = Dish.getOfficial();
                final Map<Dish.Type, Map<Dish.CaloricLevel, List<Dish>>> collect = menu.stream().collect(
                        groupingBy(Dish::getType,
                                groupingBy(dish -> {
                                    if (dish.getCalories() <= 400) return Dish.CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return
                                            Dish.CaloricLevel.NORMAL;
                                    else return Dish.CaloricLevel.FAT;
                                })
                        )
                );

                System.out.println(JSON.toJSONString(collect));
            }

        }

        /**
         * 6.3.2 按子组收集数据
         * collectingAndThen 对收集器返回值的进行处理
         * mapping 根据分组的子流构建新的集合
         */
        public static class Jia632 {

            public static void main(String[] args) {
                // 传递给第一个groupingBy的第二个收集器可以是任何类型，而不一定是另一个groupingBy
                // 数一数菜单中每类菜有多少个
                final List<Dish> menu = Dish.getOfficial();
                final Map<Dish.Type, Long> collect = menu.stream().collect(
                        groupingBy(Dish::getType, counting())
                );
                System.out.println(JSON.toJSONString(collect));

                // groupingBy(f) = groupingBy(f, toList())
                // 查找菜单中热量最高的菜肴的收集器改一改，按照菜的类型分类
                //这个Map中的值是Optional，因为这是maxBy工厂方法生成的收集器的类型，但实际上，
                //如果菜单中没有某一类型的Dish，这个类型就不会对应一个Optional. empty()值，
                //而且根本不会出现在Map的键中。groupingBy收集器只有在应用分组条件后，第一次在
                //流中找到某个键对应的元素时才会把键加入分组Map中。这意味着Optional包装器在这
                //里不是很有用，因为它不会仅仅因为它是归约收集器的返回类型而表达一个最终可能不
                //存在却意外存在的值。
                final Map<Dish.Type, Optional<Dish>> collect1 = menu.stream().collect(
                        groupingBy(Dish::getType, maxBy(
                                Comparator.comparing(Dish::getCalories)
                        ))
                );
                // 相当于
                final Map<Dish.Type, Optional<Dish>> collect6 = menu.stream().collect(
                        groupingBy(Dish::getType,
                                mapping(Function.identity(),
                                        maxBy(Comparator.comparingInt(Dish::getCalories)))
                        )
                );
                System.out.println(JSON.toJSONString(collect1));

                // 1. 把收集器的结果转换为另一种类型
                // 收集器返回的结果转换为另一种类型，你可以使用
                // Collectors.collectingAndThen工厂方法返回的收集器
                final Map<Dish.Type, Dish> collect2 = menu.stream().collect(
                        groupingBy(Dish::getType,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(Dish::getCalories)),
                                        Optional::get
                                )
                        )
                );
                // 相当于
                final Map<Dish.Type, Dish> collect3 = menu.stream().collect(toMap(Dish::getType, Function.identity(), BinaryOperator.maxBy(Comparator.comparing(Dish::getCalories))));
                System.out.println(JSON.toJSONString(collect3));

                // 6.6图片 嵌套收集器来获得多重效果
                // 收集器用虚线表示，因此groupingBy是最外层，根据菜肴的类型把菜单流分组，得到三
                //个子流。
                // groupingBy收集器包裹着collectingAndThen收集器，因此分组操作得到的每个子流
                //都用这第二个收集器做进一步归约。
                // collectingAndThen收集器又包裹着第三个收集器maxBy。
                // 随后由归约收集器进行子流的归约操作，然后包含它的collectingAndThen收集器会对
                //其结果应用Optional:get转换函数。
                // 对三个子流分别执行这一过程并转换而得到的三个值，也就是各个类型中热量最高的
                //Dish，将成为groupingBy收集器返回的Map中与各个分类键（Dish的类型）相关联的值。

                // 2. 与groupingBy联合使用的其他收集器的例子
                // 通过groupingBy工厂方法的第二个参数传递的收集器将会对分到同一组中的所有流元素执行进一步归约操作
                // 求出所有菜肴热量总和的收集器
                final Map<Dish.Type, Integer> collect4 = menu.stream()
                        .collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
                // 然而常常和groupingBy联合使用的另一个收集器是mapping方法生成的。这个方法接受两
                //个参数：一个函数对流中的元素做变换，另一个则将变换的结果对象收集起来。其目的是在累加
                //之前对每个输入元素应用一个映射函数，这样就可以让接受特定类型元素的收集器适应不同类型
                //的对象。
                // 对于每种类型的Dish，菜单中都有哪些CaloricLevel。我们可以把groupingBy和mapping收集器结合起来
                final Map<Dish.Type, Set<Dish.CaloricLevel>> collect5 = menu.stream().collect(
                        groupingBy(Dish::getType, mapping(
                                dish -> {
                                    if (dish.getCalories() <= 400) return Dish.CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return Dish.CaloricLevel.NORMAL;
                                    else return Dish.CaloricLevel.FAT;
                                },
                                toSet()
                        ))
                );

                // 注意在上一个示例中，对于返回的Set是什么类型并没有任何保证。但通过使用toCollection，
                // 你就可以有更多的控制。例如，你可以给它传递一个构造函数引用来要求HashSet：
                menu.stream().collect(
                        groupingBy(Dish::getType, mapping(
                                dish -> {
                                    if (dish.getCalories() <= 400) return Dish.CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return Dish.CaloricLevel.NORMAL;
                                    else return Dish.CaloricLevel.FAT;
                                },
                                toCollection(HashSet::new))
                        )
                );

                final Map<Dish.Type, ArrayList<String>> collect7 = menu.stream().collect(
                        groupingBy(
                                Dish::getType,
                                mapping(Dish::getName, toCollection(ArrayList::new))
                        )
                );

            }
        }

    }

    /**
     * 6.4 分区
     * 分区是分组的特殊情况：由一个谓词（返回一个布尔值的函数）作为分类函数，它称分区函数
     * 分区函数返回一个布尔值，这意味着得到的分组Map的键类型是Boolean，于是它最多可以分为两组——true是一组，false是一组
     */
    public static class Jia64 {

        public static void main(String[] args) {
            // 把菜单按照素食和非素食分开
            final List<Dish> menu = Dish.getOfficial();
            final Map<Boolean, List<Dish>> collect = menu.stream().collect(partitioningBy(Dish::isVegetarian));
            System.out.println(JSON.toJSONString(collect));

            // Map中键为true的值，就可以找出所有的素食菜肴了
            final List<Dish> dishes = collect.get(true);

            // 用同样的分区谓词，对菜单List创建的流作筛选，然后把结果收集到另外一个List中也可以获得相同的结果
            final List<Dish> collect1 = menu.stream().filter(Dish::isVegetarian).collect(toList());
        }

        /**
         * 6.4.1 分区的优势
         */
        public static class Jia641 {
            final List<Dish> menu = Dish.getOfficial();
            // partitioningBy工厂方法有一个重载版本，可以像下面这样传递第二个收集器
            final Map<Boolean, Map<Dish.Type, List<Dish>>> collect2 = menu.stream().collect(
                    partitioningBy(Dish::isVegetarian,
                            groupingBy(Dish::getType))
            );

            // 重用前面的代码来找到素食和非素食中热量最高的菜
            final Map<Boolean, Dish> collect3 = menu.stream().collect(
                    partitioningBy(Dish::isVegetarian,
                            collectingAndThen(
                                    maxBy(Comparator.comparingInt(Dish::getCalories)),
                                    Optional::get
                            )
                    )
            );

            // 使用partitioningBy进行多级分区
            //(1) menu.stream().collect(partitioningBy(Dish::isVegetarian,
            // partitioningBy (d -> d.getCalories() > 500)));
            //(2) menu.stream().collect(partitioningBy(Dish::isVegetarian,
            // partitioningBy (Dish::getType)));
            //(3) menu.stream().collect(partitioningBy(Dish::isVegetarian,
            // counting()));
            //(1) 这是一个有效的多级分区，产生以下二级Map：
            //{ false={false=[chicken, prawns, salmon], true=[pork, beef]},
            // true={false=[rice, season fruit], true=[french fries, pizza]}}
            //(2) 这无法编译，因为partitioningBy需要一个谓词，也就是返回一个布尔值的函数。
            //方法引用Dish::getType不能用作谓词。
            //(3) 它会计算每个分区中项目的数目，得到以下Map：
            //{false=5, true=4}
        }

        /**
         * 6.4.2 将数字按质数(只有两个正因数 1和本身)和非质数分区
         */
        public static class Jia642 {

            public boolean isPrime(int candidate) {
                // 产生一个自然数范围，从2开始，直至但不包括待测数
                return IntStream.range(2, candidate)
                        // 如果待测数字不能被流中任何数字整除则返回true
                        .noneMatch(i -> candidate % i == 0);
            }

            // 是仅测试小于等于待测数平方根的因子
            public static boolean isPrimeUpgrade(int candidate) {
                final int candidateRoot = (int) Math.sqrt(candidate);
                return IntStream.rangeClosed(2, candidateRoot)
                        // 如果待测数字不能被流中任何数字整除则返回true
                        .noneMatch(i -> candidate % i == 0);
            }

            public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
                return IntStream.rangeClosed(2, n)
                        .boxed()
                        .collect(
                                partitioningBy(Jia642::isPrimeUpgrade)
                        );
            }

            public static void main(String[] args) {
                final Map<Boolean, List<Integer>> booleanListMap = partitionPrimes(3);
                System.out.println(JSON.toJSONString(booleanListMap));
            }
        }
    }

    /**
     * 6.5 收集器接口
     * Collector接口包含了一系列方法，为实现具体的归约操作（即收集器）提供了范本
     */
    public static class Jia65 {

        // ToListCollector是IDENTITY_FINISH的
        //因为用来累积流中元素的List已经是我们要的最终结果，用不着进一步转换了，但它并不是UNORDERED，因为用在有序
        //流上的时候，我们还是希望顺序能够保留在得到的List中。最后，它是CONCURRENT的，但我们刚才说过了，仅仅在背后的数据源无序时才会并行处理
        public static class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

            // 1. 建立新的结果容器：supplier(n.供应商)方法
            // 创建并返回新的可变结果容器的函数。
            // 必须返回一个结果为空的Supplier，也就是一个无参数函数
            // 在调用时它会创建一个空的累加器实例,供数据收集过程使用
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            // 将值折叠到可变结果容器中的函数。
            // 2. 将元素添加到结果容器：accumulator(n.累加器)方法
            // 返回执行归约操作的函数
            //当遍历到流中第n个元素时，这个函数执行时会有两个参数：
            //保存归约结果的累加器（已收集了流中的前 n1 个项目），还有第n个元素本身。
            //该函数将返回void，因为累加器是原位更新，即函数的执行改变了它的内部状态以体现遍历的
            //元素的效果。对于ToListCollector，这个函数仅仅会把当前项目添加至已经遍历过的项目的列表：
            @Override
            public BiConsumer<List<T>, T> accumulator() {
                // return List:add
                return (list, item) -> list.add(item);
            }

            // 接受两个部分结果并将其合并的函数。组合器函数可以将状态从一个参数折叠到另一个参数并返回该参数，或者可以返回一个新的结果容器。
            // 4. 合并两个结果容器：combiner(n.combiner.组合器.合成仪)方法
            //四个方法中的最后一个——combiner方法会返回一个供归约操作使用的函数，它定义了对
            //流的各个子部分进行并行处理时，各个子部分归约所得的累加器要如何合并。对于toList而言，
            //这个方法的实现非常简单，只要把从流的第二个部分收集到的项目列表加到遍历第一部分时得到的列表后面就行了
            // 这个方法可以对流进行并行归约
            // toList()的combiner方法
            //public BinaryOperator<List<T>> combiner() {
            //                return ((list1, list2) -> {
            //                    list1.addAll(list2);
            //                    return list1;
            //                });
            //            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return ((list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });
            }

            // 从中间累积类型执行最终转换
            // 3. 对结果容器应用最终转换：finishe(n.修整器)r方法
            //在遍历完流后，finisher方法必须返回在累积过程的最后要调用的一个函数，以便将累加
            //器对象转换为整个集合操作的最终结果。通常，就像ToListCollector的情况一样，累加器对
            //象恰好符合预期的最终结果，因此无需进行转换。所以finisher方法只需返回identity函数：
            @Override
            public Function<List<T>, List<T>> finisher() {
                return Function.identity();
            }

            // 返回{@code Collector.Characteristics}的{@code Set}，指示此收集器的特征。这个集合应该是不可变的。
            // 提示列表，告诉collect方法在执行归约操作的时候可以应用哪些优化（比如并行化）。
            // 5. characteristics(n.特征；特点；品质)方法
            //会返回一个不可变的Characteristics集合，它定义了收集器的行为——
            //尤其是关于流是否可以并行归约，以及可以使用哪些优化的提示。
            //Characteristics是一个包含三个项目的枚举
            // UNORDERED——归约结果不受流中项目的遍历和累积顺序的影响。
            // CONCURRENT——accumulator函数可以从多个线程同时调用，且该收集器可以并行归
            //约流。如果收集器没有标为UNORDERED，那它仅在用于无序数据源时才可以并行归约。
            // IDENTITY_FINISH——这表明完成器方法返回的函数是一个恒等函数，可以跳过。这种
            //情况下，累加器对象将会直接用作归约过程的最终结果。这也意味着，将累加器A不加检
            //查地转换为结果R是安全的。
            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(
                        EnumSet.of(Characteristics.IDENTITY_FINISH,
                                Characteristics.CONCURRENT)
                );
            }
        }

        public static void main(String[] args) {
            // Collector接口
            //public interface Collector<T, A, R> {
            // Supplier<A> supplier();
            // BiConsumer<A, T> accumulator();
            // Function<A, R> finisher();
            // BinaryOperator<A> combiner();
            // Set<Characteristics> characteristics();
            //}

            // T是流中要收集的项目的泛型
            // A是累加器的类型, 累加器是在收集过程中用于积累部分结果的对象
            // R是收集操作得到的对象(通常但并不一定是集合)的类型

            // 例如实现一个ToListCollector<T>类，将Stream<T>中的所有元素收集到一个List<T>里
            // 查看上面的ToListCollector类
        }

        /**
         * 6.5.1 理解 Collector 接口声明的方法
         */
        public static class Jia651 {
            // 查看前面的ToListCollector类
            // 【详情查看目录下图片-使用combiner方法来并行化归约过程】
            // 原始流会以递归方式拆分为子流，直到定义流是否需要进一步拆分的一个条件为非（如
            //果分布式工作单位太小，并行计算往往比顺序计算要慢，而且要是生成的并行任务比处
            //理器内核数多很多的话就毫无意义了）。
            // 现在，所有的子流都可以并行处理，即对每个子流应用图6-7所示的顺序归约算法。
            // 最后，使用收集器combiner方法返回的函数，将所有的部分结果两两合并。这时会把原
            //始流每次拆分时得到的子流对应的结果合并起来。


        }

        /**
         * 6.5.2 全部融合到一起
         */
        public static class Jia652 {
            // 查看前面的ToListCollector类
            //这个实现与Collectors.toList方法并不完全相同，但区别仅仅是一些小的优化。
            //这些优化的一个主要方面是Java API所提供的收集器在需要返回空列表时使用了Collections.emptyList()
            //这个单例（singleton）。这意味着它可安全地替代原生Java，来收集菜单流中的所有Dish的列表
            public static void main(String[] args) {
                // 区别在于标准的Java API所提供的收集器在需要返回空列表时使用了Collections.emptyList()这个单例（singleton）
                // 这个实现和标准的构造之间的其他差异在于toList是一个工厂，而ToListCollector必须用new来实例化
                final Stream<Dish> menuStream = Dish.getOfficial().stream();
                // 自定义
                final List<Dish> collect = menuStream.collect(new ToListCollector<Dish>());
                // 标准的
                final List<Dish> collect1 = menuStream.collect(toList());

                // 进行自定义收集而不去实现Collector
                //对于IDENTITY_FINISH的收集操作，还有一种方法可以得到同样的结果而无需从头实现新
                //的Collectors接口。Stream有一个重载的collect方法可以接受另外三个函数——supplier、
                //accumulator和combiner，其语义和Collector接口的相应方法返回的函数完全相同
                menuStream.collect(
                        ArrayList::new,
                        List::add,
                        List::addAll
                );
                // <R> R collect(Supplier<R> supplier,
                //               BiConsumer<R, ? super T> accumulator,
                //               BiConsumer<R, R> combiner);
                // 这种方式虽然比前一个写法更为紧凑和简洁，却不那么易读
                // 以恰当的类来实现自己的自定义收集器有助于重用并可避免代码重复
                // 这个collect方法不能传递任何Characteristics，所以它永远都是一个IDENTITY_FINISH和
                // CONCURRENT但并非UNORDERED的收集器
            }
        }

    }

    /**
     * 6.6 开发你自己的收集器以获得更好的性能
     */
    public static class Jia66 {
        // 6.4用Collectors类提供的一个方便的工厂方法创建了一个收集器，它将前n个自然数划分为质数和非质数
        // 为了更好的性能 开发一个自定义收集器
        public static void main(String[] args) {
            System.out.println((int) Math.sqrt(27));
        }

        // 将前n个自然数按质数和非质数分区
        public Map<Boolean, List<Integer>> partitionPrimes(int n) {
            return IntStream.rangeClosed(2, n).boxed()
                    .collect(partitioningBy(candidate -> isPrime(candidate)));
        }

        // 限制除数不超过被测试数的平方根
        public boolean isPrime(int candidate) {
            int candidateRoot = (int) Math.sqrt((double) candidate);
            return IntStream.rangeClosed(2, candidateRoot)
                    .noneMatch(i -> candidate % i == 0);
        }

        /**
         * 6.6.1 仅用质数做除数
         */
        public static class Jia661 {
            // 优化: 测试数是不是能够被质数整除
            // 要是除数本身都不是质数就用不着测了 可以仅仅用被测试数之前的质数来测试
            // 然而我们目前所见的预定义收集器的问题，也就是必须自己开发一个收集器的原因在于，在收集过程中是没有办法访问部分结果的。
            // 这意味着，当测试某一个数字是否是质数的时候，你没法访问目前已经找到的其他质数的列表
            public static boolean isPrime(List<Integer> primes, int candidate) {
                return primes.stream().noneMatch(i -> candidate % i == 0);
            }

            //还应该应用先前的优化，仅仅用小于被测数平方根的质数来测试。因此，你需要想办法
            //在下一个质数大于被测数平方根时立即停止测试。不幸的是，Stream API中没有这样一种方法。
            //你可以使用filter(p -> p <= candidateRoot)来筛选出小于被测数平方根的质数。但filter
            //要处理整个流才能返回恰当的结果。如果质数和非质数的列表都非常大，这就是个问题了。你用
            //不着这样做；你只需在质数大于被测数平方根的时候停下来就可以了。因此，我们会创建一个名
            //为takeWhile的方法，给定一个排序列表和一个谓词，它会返回元素满足谓词的最长前缀
            public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
                int i = 0;
                for (A item : list) {
                    // 检查列表中的当前项目是否满足谓词
                    if (!p.test(item)) {
                        // 如果不满足，返回该项目之前的前缀子列表
                        return list.subList(0, i);
                    }
                    i++;
                }
                // 列表中的所有项目都满足谓词，因此返回列表本身
                return list;
            }

            // 利用该方法, 只用不大于被测数平方根的质数去测试了
            // 这个takeWhile实现是即时的。理想情况下，我们会想要一个延迟求值的
            // takeWhile，这样就可以和noneMatch操作合并
            public static boolean isPrimeUpgrade(List<Integer> primes, int candidate) {
                int candidateRoot = (int) Math.sqrt(candidate);
                return takeWhile(primes, i -> i <= candidateRoot)
                        .stream()
                        .noneMatch(p -> candidate % p == 0);
            }

            // 新的isPrime方法在手，你就可以实现自己的自定义收集器了
            // 1. 第一步：定义Collector类的签名
            // public interface Collector<T, A, R>
            // 其中T、A和R分别是流中元素的类型、用于累积部分结果的对象类型，以及collect操作最
            // 终结果的类型。这里应该收集Integer流，而累加器和结果类型则都是Map<Boolean,
            // List<Integer>>（和先前代码清单6-6中分区操作得到的结果Map相同），键是true和false，
            // 值则分别是质数和非质数的List
            public static class PrimeNumbersCollector
                    implements Collector<
                    // 流中元素的类型
                    Integer,
                    // 累加器类型
                    Map<Boolean, List<Integer>>,
                    // collect操作的结果类型
                    Map<Boolean, List<Integer>>
                    > {

                // 2. 第二步：实现归约过程
                // supplier方法会返回一个在调用时创建累加器的函数
                // 创建了用作累加器的Map，还为true和false两个键下面初始化了对应的空列表
                // 在收集过程中会把质数和非质数分别添加到这里
                @Override
                public Supplier<Map<Boolean, List<Integer>>> supplier() {
                    return () -> new HashMap<Boolean, List<Integer>>(){
                        {
                            put(true, new ArrayList<>());
                            put(false, new ArrayList<>());
                        }
                    };
                }

                // 2. 第二步：实现归约过程
                // 将待测试是否为质数的数以及迄今找到的质数列表
                // 调用isPrime方法，将待测试是否为质数的数以及迄今找到的质数列表传递给它
                // 调用的结果随后被用作获取质数或非质数列表的键
                // 在把新的被测数添加到恰当列表中
                @Override
                public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
                    return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
                        // 根据isPrime的结果，获取质数或非质数列表
                        acc.get(isPrimeUpgrade(acc.get(true), candidate))
                         // 将被测数添加到相应的列表中
                        .add(candidate);
                    };
                }

                // 3. 第三步：让收集器并行工作（如果可能）
                // 在并行收集时把两个部分累加器合并起来
                // 将第二个Map中质数和非质数列表中的所有数字合并到第一个Map的对应列表
                // 实际上这个收集器是不能并行使用的，因为该算法本身是顺序的
                // 这意味着永远不会调用combiner方法
                // 可以把它的实现留空(更好的做法是抛出一个UnsupportedOperationException异常)
                @Override
                public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
                    return (Map<Boolean, List<Integer>> map1,
                            Map<Boolean, List<Integer>> map2) -> {
                        map1.get(true).addAll(map2.get(true));
                        map1.get(false).addAll(map2.get(false));
                        return map1;
                    };
                }

                // 4. 第四步：finisher方法和收集器的characteristics方法
                // accumulator正好就是收集器的结果，用不着进一步转换，那么finisher方法就返回identity函数
                @Override
                public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
                    return Function.identity();
                }

                // 既不是CONCURRENT也不是UNORDERED，但却是IDENTITY_FINISH的
                @Override
                public Set<Characteristics> characteristics() {
                    return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
                }
            }

            public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
                return IntStream.rangeClosed(2, n).boxed()
                        .collect(new Jia661.PrimeNumbersCollector());
            }

            public static void main(String[] args) {
                // 新的自定义收集器来代替6.4节中用partitioningBy工厂方法创建的那个，并获得完全相同的结果了
                System.out.println(partitionPrimesWithCustomCollector(10));
            }
        }

        /**
         * 6.6.2 比较收集器性能
         */
        public static class Jia662 {

            // PrimeNumbersCollector核心逻辑的三个函数传给collect方法的重载版本来获得同样的结果：
            // 避免为实现Collector接口创建一个全新的类；得到的代码更紧凑，虽然可能可读性会差一点，可重用性会差一点
            public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
                return IntStream.rangeClosed(2, n).boxed()
                        .collect(
                                () -> new HashMap<Boolean, List<Integer>>() {{
                                   put(true, new ArrayList<>());
                                   put(false, new ArrayList<>());
                                }},
                                (acc, candidate) -> {
                                    acc.get(Jia661.isPrimeUpgrade(acc.get(true), candidate))
                                            .add(candidate);
                                },
                                (map1, map2) -> {
                                    map1.get(true).addAll(map2.get(true));
                                    map1.get(false).addAll(map2.get(false));
                                });
            }

            public static void main(String[] args) {
                long fastest = Long.MAX_VALUE;
                // 运行测试10次
                for (int i = 0; i <10; i++) {
                    long start = System.nanoTime();
                    // 将前一百万个自然数按质数和非质数分区
                    // Jia64.Jia642.partitionPrimes(1_000_000);
                    // Jia661.partitionPrimesWithCustomCollector(1_000_000);
                    Jia662.partitionPrimesWithCustomCollector(1_000_000);
                    // 取运行时间的毫秒值
                    long duration = (System.nanoTime() - start) / 1_000_000;
                    // 检查这个执行是否是最快的一个
                    if(duration < fastest) {
                        fastest = duration;
                    }
                    System.out.println(
                            "Fastest execution done in " + fastest + " msecs");
                }
            }
        }

    }

}
