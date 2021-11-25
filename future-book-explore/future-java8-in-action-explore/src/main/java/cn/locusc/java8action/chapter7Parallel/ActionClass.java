package cn.locusc.java8action.chapter7Parallel;

import cn.locusc.java8action.domain.Accumulator;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Jay
 * 第7章 并行数据处理与性能
 * 本章内容
 *  用并行流并行处理数据
 *  并行流的性能分析
 *  分支/合并框架
 *  使用Spliterator分割流
 * 2021/11/24
 *
 * 小结
 *  内部迭代让你可以并行处理一个流，而无需在代码中显式使用和协调不同的线程。
 *  虽然并行处理一个流很容易，却不能保证程序在所有情况下都运行得更快。并行软件的
 * 行为和性能有时是违反直觉的，因此一定要测量，确保你并没有把程序拖得更慢。
 *  像并行流那样对一个数据集并行执行操作可以提升性能，特别是要处理的元素数量庞大，
 * 或处理单个元素特别耗时的时候。
 *  从性能角度来看，使用正确的数据结构，如尽可能利用原始流而不是一般化的流，几乎
 * 总是比尝试并行化某些操作更为重要。
 *  分支/合并框架让你得以用递归方式将可以并行的任务拆分成更小的任务，在不同的线程
 * 上执行，然后将各个子任务的结果合并起来生成整体结果。
 *  Spliterator定义了并行流如何拆分它要遍历的数据。
 */
public class ActionClass {

    /**
     * 7.1 并行流
     * 并行流就是一个把内容分成多个数据块 并用不同的线程分别处理每个数据块的流
     */
    public static class Jia71 {

        // 接受数字n作为参数，并返回从1到给定参数的所有数字的和
        public static long sequentialSumMapToInt(long n) {
            return Stream.iterate(1L, i -> i + 1).limit(n)
                    .mapToLong(l -> l)
                            .parallel().sum();
        }

        // 接受数字n作为参数，并返回从1到给定参数的所有数字的和
        public static long sequentialSumReduce(long n) {
            // 生成自然数无限流
            return Stream.iterate(1L, i -> i + 1)
                    // 限制到前n个数
                    .limit(n)
                    // 对所有数字求和来归纳流
                    .reduce(0L, Long::sum);
        }

        // 用更为传统的Java术语来说，这段代码与下面的迭代等价
        // n足够大时 使用并行需要考虑是否对结果变量进行同步
        // 用多少线程，谁负责生成数 谁来做加法
        public static long iterativeSum(long n) {
            long result= 0;
            for (long i = 1L; i <= n; i++) {
                result += i;
            }
            return result;
        }

        /**
         * 7.1.1 将顺序流转换为并行流
         */
        public static class Jia711 {

            // 可以把流转换成并行流，从而让前面的函数归约过程并行运行
            // 对顺序流调用parallel方法
            public static long parallelSum(long n) {
                return Stream.iterate(1L, i -> i + 1)
                        .limit(n)
                        // 将流转换为并行流
                        .parallel()
                        .reduce(0L, Long::sum);
            }

            // 顺序流调用parallel方法并不意味着流本身有任何实际的变化
            // 设一个boolean标志 表示调用parallel之后进行的所有操作都并行执行
            // 类似地，你只需要对并行流调用sequential方法就可以把它变成顺序流
            // 结合起来以更细化地控制并行执行和顺序执行
            // 最后一次parallel或sequential调用会影响整个流水线
            // 流水线会并行执行，因为最后调用的是它
            public static void example() {
                Stream.generate(Math::random)
                        .limit(5)
                        .filter(i -> i % 1 == 0)
                        .sequential()
                        .mapToDouble(i -> i)
                        .parallel()
                        .reduce(Double::sum);
            }

            public static void main(String[] args) {
                // 并行流内部使用了默认的ForkJoinPool
                // 默认的线程数量就是处理器数量 由下面的方法获取
                System.out.println(Runtime.getRuntime().availableProcessors());

                // 可以通过下面设置修改线程池大小
                // 全局设置 影响所有的并行流
                // 一般来说让ForkJoinPool的大小默认为处理器数量
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
            }
        }

        /**
         * 7.1.2 测量流性能
         */
        public static class Jia712 {

            // 我们声称并行求和方法应该比顺序和迭代方法性能好 应该通过测量判断
            // 测量对前n个自然数求和的函数的性能
            public static long measureSumPerf(Function<Long, Long> adder, long n) {
                long fastest = Long.MAX_VALUE;
                for (int i = 0; i < 10; i++) {
                    long start = System.nanoTime();
                    Long sum = adder.apply(n);
                    long duration = (System.nanoTime() - start) / 1_000_000;
                    System.out.println("Result: " + sum);
                    if (duration < fastest) fastest = duration;
                }
                return fastest;
            }

            public static long rangedSum(long n) {
                return LongStream.rangeClosed(1, n)
                        .reduce(0L, Long::sum);
            }

            public static long parallelRangedSum(long n) {
                return LongStream.rangeClosed(1, n)
                        .parallel()
                        .reduce(0L, Long::sum);
            }

            public static void main(String[] args) {
                // Sequential sum done in:108 msecs
                System.out.println(
                        "Sequential sum done in:" +
                                measureSumPerf(Jia71::sequentialSumReduce, 10_000_000) + " msecs"
                );

                // Sequential sum done in:78 msecs
                System.out.println(
                        "Sequential sum done in:" +
                                measureSumPerf(Jia71::sequentialSumMapToInt, 10_000_000) + " msecs"
                );

                // 用传统for循环的迭代版本快很多 因为它更为底层 不需要对原始类型做任何装箱或拆箱操作
                // Iterative sum done in:3 msecs
                System.out.println("Iterative sum done in:" +
                        measureSumPerf(Jia71::iterativeSum, 10_000_000) + " msecs"
                );

                // Parallel sum done in: 140 msecs
                System.out.println("Parallel sum done in: " +
                        measureSumPerf(Jia711::parallelSum, 10_000_000) + " msecs"
                );

                // 总结
                // 求和方法的并行版本比顺序版本要慢很多
                //  iterate生成的是装箱的对象，必须拆箱成数字才能求和；
                //  我们很难把iterate分成多个独立块来并行执行。
                // iterate很难分割成能够独立执行的小块，因为每次应用这个函数都要依赖前一次应用的结果

                // iterate本质上是顺序的 是一个不易并行化的操作
                // 数字列表在归纳过程开始时没有准备好，因而无法有效地把流划分为小块来并行处理
                // 把流标记成并行，你其实是给顺序处理增加了开销，它还要把每次求和操作分到一个不同的线程上


                // 使用更有针对性的方法
                // LongStream.rangeClosed直接产生原始类型的long数字，没有装箱拆箱的开销。
                // LongStream.rangeClosed会生成数字范围，很容易拆分为独立的小块

                // Parallel sum done in: 4 msecs
                // 数值流避免了非针对性流那些没必要的自动装箱和拆箱操作
                // 选择适当的数据结构往往比并行化算法更重要
                System.out.println("Parallel sum done in: " +
                        measureSumPerf(Jia712::rangedSum, 10_000_000) + " msecs"
                );

                // 应用并行流
                //并行化过程本身需要对流做递归划分，把每
                //个子流的归纳操作分配到不同的线程，然后把这些操作的结果合并成一个值。但在多个内核之间
                //移动数据的代价也可能比你想的要大，所以很重要的一点是要保证在内核中并行执行工作的时间
                //比在内核之间传输数据的时间长
                // Parallel range sum done in:1 msecs
                System.out.println("Parallel range sum done in:" +
                        measureSumPerf(Jia712::parallelRangedSum, 10_000_000) +
                        " msecs"
                );
            }
        }

        /**
         * 7.1.3 正确使用并行流
         */
        public static class Jia713 {

            // 累加操作本质上是顺序的
            public static long sideEffectSum(long n) {
                Accumulator accumulator = new Accumulator();
                LongStream.rangeClosed(1, n).forEach(accumulator::add);
                return accumulator.total;
            }

            // 每次访问total都会出现数据竞争
            // 如果用同步来修复 将失去并行的意义
            public static long sideEffectParallelSum(long n) {
                Accumulator accumulator = new Accumulator();
                LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
                return accumulator.total;
            }

            public static void main(String[] args) {
                //Result: 5019
                //Result: 4596
                //Result: 4409
                //Result: 4765...
                //是由于多个线程在同时访问累加器，执行total += value
                //不是一个原子操作(顺序不能被打乱，不能被切割只执行一部分)
                //根源在于，forEach中调用的方法有副作用
                //它会改变多个线程共享的对象的可变状态
                System.out.println("SideEffect parallel sum done in: " +
                                Jia712.measureSumPerf(Jia713::sideEffectParallelSum, 100L) +" msecs");
            }
        }

        /**
         * 7.1.4 高效使用并行流
         */
        public static class Jia714 {
            // 1.用适当的基准来检查其性能
            // 2.留意装箱和拆箱 可以使用原始类型流来避免这种操作(IntStream、LongStream、DoubleStream)
            // 3. 有些操作本身在并行流上的性能就比顺序流差 特别是依赖元素顺序的操作(limit,findFirst)
            // 可以调用unordered方法来把有序流变成无序流

            // 4.考虑流的操作流水线的总计算成本 N.元素总数 Q.元素通过流水线的大致处理成本
            // N*Q就是这个对成本的一个粗略的定性估计 Q值较高就意味着使用并行流时性能好的可能性比较大

            // 5.对于较小的数据量 并行处理少数几个元素的好处抵不上并行化造成的额外开销
            // 6.要考虑流背后的数据结构是否易于分解 Spliterator可以掌控分解过程

            // 7.流自身的特点 流水线中的中间操作修改流的方式，都可能会改变分解过程的性能。
            // 例如，一个SIZED流可以分成大小相等的两部分，这样每个部分都可以比较高效地并行处
            // 理，但筛选操作可能丢弃的元素个数却无法预测，导致流本身的大小未知

            // 8.终端操作中合并步骤的代价是大是小（例如Collector中的combiner方法）。
            // 组合每个子流产生的部分结果所付出的代价就可能会超出通过并行流得到的性能提升
        }

    }

    /**
     * 7.2 分支/合并框架
     * 分支/合并框架的目的是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任
     * 务的结果合并起来生成整体结果。它是ExecutorService接口的一个实现，它把子任务分配给
     * 线程池（称为ForkJoinPool）中的工作线程
     */
    public static class Jia72 {

        /**
         * 7.2.1 使用 RecursiveTask
         * if (任务足够小或不可分) {
         *  顺序计算该任务
         * } else {
         *  将任务分成两个子任务
         *  递归调用本方法，拆分每个子任务，等待所有子任务完成
         *  合并每个子任务的结果
         * }
         *
         * {@link ForkJoinSumCalculator}
         */
        public static class Jia721 {

            // 并行对前n个自然数求和
            public static long forkJoinSum(long n) {
                long[] numbers = LongStream.rangeClosed(1, n).toArray();
                ForkJoinSumCalculator task = new ForkJoinSumCalculator(numbers);
                return new ForkJoinPool().invoke(task);
            }

            public static void main(String[] args) {

                // 用多个ForkJoinPool是没有什么意义的
                // 一般来说把它实例化一次，然后把实例保存在静态字段中，使之成为单例

                //当把ForkJoinSumCalculator任务传给ForkJoinPool时，这个任务就由池中的一个线程
                //执行，这个线程会调用任务的compute方法。该方法会检查任务是否小到足以顺序执行，如果不
                //够小则会把要求和的数组分成两半，分给两个新的ForkJoinSumCalculator，而它们也由
                //ForkJoinPool安排执行。因此，这一过程可以递归重复，把原任务分为更小的任务，直到满足
                //不方便或不可能再进一步拆分的条件（本例中是求和的项目数小于等于10 000）。这时会顺序计
                //算每个任务的结果，然后由分支过程创建的（隐含的）任务二叉树遍历回到它的根。接下来会合
                //并每个子任务的部分结果，从而得到总任务的结果

                // ForkJoin sum done in: 23 msecs 10_000_000
                System.out.println("ForkJoin sum done in: " + Jia71.Jia712.measureSumPerf(
                        Jia721::forkJoinSum, 20) + " msecs" );
            }
        }

        /**
         * 7.2.2 使用分支/合并框架的最佳做法
         */
        public static class Jia722 {
            // 虽然分支/合并框架还算简单易用，不幸的是它也很容易被误用。以下是几个有效使用它的最佳做法。
            //  对一个任务调用join方法会阻塞调用方，直到该任务做出结果。因此，有必要在两个子
            //任务的计算都开始之后再调用它。否则，你得到的版本会比原始的顺序算法更慢更复杂，
            //因为每个子任务都必须等待另一个子任务完成才能启动。
            //  不应该在RecursiveTask内部使用ForkJoinPool的invoke方法。相反，你应该始终直
            //接调用compute或fork方法，只有顺序代码才应该用invoke来启动并行计算。
            //  对子任务调用fork方法可以把它排进ForkJoinPool。同时对左边和右边的子任务调用
            //它似乎很自然，但这样做的效率要比直接对其中一个调用compute低。这样做你可以为
            //其中一个子任务重用同一线程，从而避免在线程池中多分配一个任务造成的开销。
            //  调试使用分支/合并框架的并行计算可能有点棘手。特别是你平常都在你喜欢的IDE里面
            //看栈跟踪（stack trace）来找问题，但放在分支合并计算上就不行了，因为调用compute
            //的线程并不是概念上的调用方，后者是调用fork的那个。
            //  和并行流一样，你不应理所当然地认为在多核处理器上使用分支/合并框架就比顺序计
            //算快。我们已经说过，一个任务可以分解成多个独立的子任务，才能让性能在并行化时
            //有所提升。所有这些子任务的运行时间都应该比分出新任务所花的时间长；一个惯用方
            //法是把输入/输出放在一个子任务里，计算放在另一个里，这样计算就可以和输入/输出
            //同时进行。此外，在比较同一算法的顺序和并行版本的性能时还有别的因素要考虑。就
            //像任何其他Java代码一样，分支/合并框架需要“预热”或者说要执行几遍才会被JIT编
            //译器优化。这就是为什么在测量性能之前跑几遍程序很重要，我们的测试框架就是这么
            //做的。同时还要知道，编译器内置的优化可能会为顺序版本带来一些优势（例如执行死
            //码分析——删去从未被使用的计算）。
            //对于分支/合并拆分策略还有最后一点补充：你必须选择一个标准，来决定任务是要进一步
            //拆分还是已小到可以顺序求值。我们会在下一节中就此给出一些提示。
        }

        /**
         * 7.2.3 工作窃取
         */
        public static class Jia723 {
            //作窃取算法用于在池中的工作线程之间重新分配和平衡任务。图7-5展示
            //了这个过程。当工作线程队列中有一个任务被分成两个子任务时，一个子任务就被闲置的工作线
            //程“偷走”了。如前所述，这个过程可以不断递归，直到规定子任务应顺序执行的条件为真
        }

    }

    /**
     * 7.3 Spliterator(可分迭代器 split-table-iterate)
     */
    public static class Jia73 {

        // T: 遍历的元素的类型
        public interface Spliterator<T> {
            // 行为类似于普通的Iterator
            // 因为它会按顺序一个一个使用Spliterator中的元素
            // 并且如果还有其他元素要遍历就返回true
            boolean tryAdvance(Consumer<? super T> action);
            // 可以把一些元素划出去分给第二个Spliterator（由该方法返回），让它们两个并行处理
            Spliterator<T> trySplit();
            // 估计还剩下多少元素要遍历，因为即使不那么确切，能快速算出来是一个值也有助于让拆分均匀一点。
            long estimateSize();
            // 特性
            int characteristics();
        }

        /**
         * 7.3.1 拆分过程
         * 查看图《递归拆分过程》和《Spliterator的特性》
         */
        public static class Jia731 {

        }

        /**
         * 7.3.2 实现你自己的 Spliterator
         */
        public static class Jia732 {

            static final String SENTENCE =
                    " Nel mezzo del cammin di nostra vita " +
                            "mi ritrovai in una selva oscura" +
                            " ché la dritta via era smarrita ";

            // 一个迭代式字数统计方法
            public static int countWordsIteratively(String s) {
                int counter = 0;
                boolean lastSpace = true;
                // 逐个遍历String中的所有字符
                for (char c : s.toCharArray()) {
                    if(Character.isWhitespace(c)) {
                        lastSpace = true;
                    } else {
                        // 上一个字符是空格
                        // 而当前遍历的字符不是空格时 将单词计数器加一
                        if(lastSpace) {
                            counter ++;
                        }
                        lastSpace = false;
                    }
                }
                return counter;
            }

            /**
             * 用来在遍历Character流时计数的类
             */
            static class WordCounter {
                // 计算到目前为止计算过的数字
                private final int counter;
                // 判断上一个遇到的是不是空格
                private final boolean lastSpace;

                public WordCounter(int counter, boolean lastSpace) {
                    this.counter = counter;
                    this.lastSpace = lastSpace;
                }

                // 和迭代算法一样。accumulate方法一个个遍历Character
                public WordCounter accumulate(Character c) {
                    if(Character.isWhitespace(c)) {
                        return lastSpace ? this : new WordCounter(counter, true);
                    } else {
                        // 上一个字符是空格 而当前遍历的字符不是空格时 将单词计数器加一
                        return lastSpace ? new WordCounter(counter + 1, false) : this;
                    }
                }

                // 合并两个wordCounter 把其计数器加起来
                public WordCounter combine(WordCounter wordCounter) {
                    // 仅需要计数器的总和 无需关心lastSpace
                    return new WordCounter(counter + wordCounter.counter,
                            wordCounter.lastSpace);
                }

                public int getCounter() {
                    return counter;
                }

            }

            static class WordCounterSpliterator implements java.util.Spliterator<Character> {

                private final String string;

                private int currentChar = 0;

                public WordCounterSpliterator(String string) {
                    this.string = string;
                }

                // 行为类似于普通的Iterator
                // 因为它会按顺序一个一个使用Spliterator中的元素
                // 并且如果还有其他元素要遍历就返回true

                //  tryAdvance方法把String中当前位置的Character传给了Consumer，并让位置加一。
                //作为参数传递的Consumer是一个Java内部类，在遍历流时将要处理的Character传给了
                //一系列要对其执行的函数。这里只有一个归约函数，即WordCounter类的accumulate
                //方法。如果新的指针位置小于String的总长，且还有要遍历的Character， 则
                //tryAdvance返回true。
                @Override
                public boolean tryAdvance(Consumer<? super Character> action) {
                    // 当前处理的字符
                    action.accept(string.charAt(currentChar++));
                    // 如果还有字符要处理 则返回true
                    return currentChar < string.length();
                }

                // 可以把一些元素划出去分给第二个Spliterator（由该方法返回），让它们两个并行处理
                // 它定义了拆分要遍历的数据结构的逻辑
                // 首先要设定不再进一步拆分的下限。这里用了一个非常低的下
                //限——10个Character，仅仅是为了保证程序会对那个比较短的String做几次拆分。
                //在实际应用中，就像分支/合并的例子那样，你肯定要用更高的下限来避免生成太多的
                //任务。如果剩余的Character数量低于下限，你就返回null表示无需进一步拆分。相
                //反，如果你需要执行拆分，就把试探的拆分位置设在要解析的String块的中间。但我
                //们没有直接使用这个拆分位置，因为要避免把词在中间断开，于是就往前找，直到找到
                //一个空格。一旦找到了适当的拆分位置，就可以创建一个新的Spliterator来遍历从
                //当前位置到拆分位置的子串；把当前位置this设为拆分位置，因为之前的部分将由新
                //Spliterator来处理，最后返回
                @Override
                public java.util.Spliterator<Character> trySplit() {
                    int currentSize = string.length() - currentChar;
                    if(currentSize < 10) {
                        // 返回null表示要解析的String已经足够的小，可以顺序处理
                        return null;
                    }
                    // 将试探拆分位置设定为要解析的String的中间
                    for (int splitPos = currentSize / 2 + currentChar;
                            splitPos < string.length(); splitPos ++) {
                        // 让拆分位置前进直到下一个空格
                        if (Character.isWhitespace(string.charAt(splitPos))) {
                            // 创建一个新的WordCounterSpliterator来解析String
                            // 从开始到拆分位置的部分
                            java.util.Spliterator<Character> spliterator =
                                    new WordCounterSpliterator(string.substring(currentChar, splitPos));

                            // 将这个WordCounterSpliterator的起始位置设为拆分位置
                            currentChar = splitPos;
                            return spliterator;
                        }
                    }
                    return null;
                }

                // 估计还剩下多少元素要遍历，因为即使不那么确切，能快速算出来是一个值也有助于让拆分均匀一点。
                // String的总长度和当前遍历的位置的差
                @Override
                public long estimateSize() {
                    return string.length() - currentChar;
                }

                // Spliterator是ORDERED（顺序就是String中各个Character的次序）
                // SIZED（estimatedSize方法的返回值是精确的）
                // SUBSIZED（trySplit方法创建的其他Spliterator也有确切大小）
                // NONNULL（String中不能有为 null 的 Character ）
                // IMMUTABLE （在解析 String 时不能再添加Character，因为String本身是一个不可变类）的
                @Override
                public int characteristics() {
                    return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
                }

            }


                // 归约Character流
            private static int countWords(Stream<Character> stream) {
                WordCounter wordCounter = stream.reduce(
                        new WordCounter(0, true),
                        WordCounter::accumulate,
                        WordCounter::combine
                );
                return wordCounter.getCounter();
            }

            public static void main(String[] args) {
                // 19
                System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");

                // 1. 以函数式风格重写单词计数器
                Stream<Character> stream = IntStream.range(0, SENTENCE.length())
                        .mapToObj(SENTENCE::charAt);
                // System.out.println("Found " + countWords(stream) + " words");

                // 2. 让WordCounter并行工作
                // 结果为25 19为正确答案
                // 因为原始的String被并行流的拆分器在任意位置拆分，所以有时一个词会被分为两个词 数两次
                System.out.println("Found " + countWords(stream.parallel()) + " words");


                // 确保String不是在随机位置拆开的，而只能在词尾拆开
                // 必须为Character实现一个Spliterator，它只能在两个词之间拆开String 然后创建并行流
                // 3. 运用WordCounterSpliterator
                java.util.Spliterator<Character> spliterator = new WordCounterSpliterator("jay chan is the best");
                Stream<Character> streamSupport = StreamSupport.stream(spliterator, true);
                // （延迟绑定）可以在第一次遍历、第一次拆分或第一次查询估计大小时绑定元素的数据源，而不是在创建时就绑定
                System.out.println("Found " + countWords(streamSupport) + " words");
            }
        }

    }

}
