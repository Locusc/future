package cn.locusc.java8action.appendix;

import cn.locusc.java8action.domain.Dish;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

/**
 * @author Jay
 * 附录C
 * 如何以并发方式在同一个流
 * 上执行多种操作
 * Java 8中，流有一个非常大的（也可能是最大的）局限性，使用时，对它操作一次仅能得到
 * 一个处理结果。实际操作中，如果你试图多次遍历同一个流，结果只有一个，那就是遭遇下面这
 * 样的异常：
 * java.lang.IllegalStateException: stream has already been operated upon or closed
 * 虽然流的设计就是如此，但我们在处理流时经常希望能同时获取多个结果。譬如，你可能会
 * 用一个流来解析日志文件，就像我们在5.7.3节中所做的那样，而不是在某个单一步骤中收集多个
 * 数据。或者，你想要维持菜单的数据模型，就像我们第4章到第6章用于解释流特性的那个例子，
 * 你希望在遍历由“佳肴”构成的流时收集多种信息。
 * 换句话说，你希望一次性向流中传递多个Lambda表达式。为了达到这一目标，你需要一个
 * fork类型的方法，对每个复制的流应用不同的函数。更理想的情况是你能以并发的方式执行这
 * 些操作，用不同的线程执行各自的运算得到对应的结果。
 * 不幸的是，这些特性目前还没有在Java 8的流实现中提供。不过，本附录会为你展示一种方
 * 法，利用一个通用API①，即Spliterator，尤其是它的延迟绑定能力，结合BlockingQueues
 * 和Futures来实现这一大有裨益的特性
 * 2021/12/5
 */
public class C {


    /**
     * C.1 复制流
     * 要达到在一个流上并发地执行多个操作的效果，你需要做的第一件事就是创建一个
     * StreamForker，这个StreamForker会对原始的流进行封装，在此基础之上你可以继续定义你
     * 希望执行的各种操作。我们看看下面这段代码。
     */
    public static class C1 {

        //代码清单C-1 定义一个StreamForker，在一个流上执行多个操作
        static class StreamForker<T> {
            private final Stream<T> stream;
            private final Map<Object, Function<Stream<T>, ?>> forks =
                    new HashMap<>();

            public StreamForker(Stream<T> stream) {
                this.stream = stream;
            }

            public StreamForker<T> fork(Object key, Function<Stream<T>, ?> f) {
                //使用一个键对流上的函数进行索引
                forks.put(key, f);
                // 返回this从而保证多次流畅地
                return this;
            }

            public Results getResults() {
                ForkingStreamConsumer<T> consumer = build();
                try {
                    stream.sequential().forEach(consumer);
                } finally {
                    consumer.finish();
                }
                return consumer;
            }

            //ForkingStreamConsumer同时实现了前面定义的Results接口和Consumer接口。随着我
            //们进一步剖析它的实现细节，你会看到它主要的任务就是处理流中的元素，将它们分发到多个
            //BlockingQueues中处理，BlockingQueues的数量和通过fork方法提交的操作数是一致的。
            //注意，我们很明确地知道流是顺序处理的，不过，如果你在一个并发流上执行forEach方法，它
            //的元素可能就不是顺序地被插入到队列中了。finish方法会在队列的末尾插入特殊元素表明该
            //队列已经没有更多需要处理的元素了。build方法主要用于创建ForkingStreamConsumer，详
            //细内容请参考下面的代码清单。

            //使用build方法创建ForkingStreamConsumer
            private ForkingStreamConsumer<T> build() {
                ArrayList<BlockingQueue<T>> queues = new ArrayList<>();
                Map<Object, Future<?>> actions = forks.entrySet().stream().reduce(
                        new HashMap<Object, Future<?>>(),
                        (map, e) -> {
                            map.put(e.getKey(),
                                    getOperationResult(queues, e.getValue()));
                            return map;
                        },
                        (m1, m2) -> {
                            m1.putAll(m2);
                            return m1;
                        }
                );
                return new ForkingStreamConsumer<>(queues, actions);
            }

            //代码清单C-2中，你首先创建了我们前面提到的由BlockingQueues组成的列表。紧接着，
            //你创建了一个Map，Map的键就是你在流中用于标识不同操作的键，值包含在Future中，Future
            //中包含了这些操作对应的处理结果。BlockingQueues的列表和Future组成的Map会被传递给
            //ForkingStreamConsumer的构造函数。每个Future都是通过getOperationResult方法创建
            //的，代码清单如下。

            //使用getOperationResult方法创建Future
            private Future<?> getOperationResult(List<BlockingQueue<T>> queues,
                                                 Function<Stream<T>, ?> f) {
                BlockingQueue<T> queue = new LinkedBlockingQueue<>();
                //创建一个队列，并将其添加到创建一个Splite- 队列的列表中
                queues.add(queue);
                //创建一个Splite- 队列的列表中rator，遍历队列中的元素
                Spliterator<T> spliterator = new BlockingQueueSpliterator<>(queue);
                //创建一个流，将Spliterator作为数据源
                Stream<T> source = StreamSupport.stream(spliterator, false);
                //创建一个Future对象，以异步方式计算在流上执行特定函数的结果
                return CompletableFuture.supplyAsync(() -> f.apply(source) );
            }

            public interface Results {
                public <R> R get(Object key);
            }

            //getOperationResult方法会创建一个新的BlockingQueue，并将其添加到队列的列表。
            //这个队列会被传递给一个新的BlockingQueueSpliterator对象，后者是一个延迟绑定的
            //Spliterator，它会遍历读取队列中的每个元素；我们很快会看到这是如何做到的。

            //接下来你创建了一个顺序流对该Spliterator进行遍历，最终你会创建一个Future在流上
            //执行某个你希望的操作并收集其结果。这里的Future使用CompletableFuture类的一个静态工
            //厂方法创建，CompletableFuture实现了Future接口。这是Java 8新引入的一个类，我们在第
            //11章对它进行过详细的介绍。

            //开发 ForkingStreamConsumer 和 BlockingQueueSpliterator

            //还有两个非常重要的部分你需要实现，分别是前面提到过的ForkingStreamConsumer类和
            //BlockingQueueSpliterator类。你可以用下面的方式实现前者。
            static class ForkingStreamConsumer<T> implements Consumer<T>, Results {
                static final Object END_OF_STREAM = new Object();
                private final List<BlockingQueue<T>> queues;
                private final Map<Object, Future<?>> actions;
                ForkingStreamConsumer(List<BlockingQueue<T>> queues,
                                      Map<Object, Future<?>> actions) {

                    this.queues = queues;
                    this.actions = actions;
                }

                @Override
                public void accept(T t) {
                    //将流中遍历的元素添加到所有的队列中
                    queues.forEach(q -> q.add(t));
                }

                void finish() {
                    // 将最后一个元素添加到队列中，表明该流已经结束
                    accept((T) END_OF_STREAM);
                }

                // 等待Future完成相关的计算，返回由特定键标识的处理结果
                @Override
                public <R> R get(Object key) {
                    try {
                        return  ((Future<R>) actions.get(key)).get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            //这个类同时实现了Consumer和Results接口，并持有两个引用，一个指向由BlockingQueues组成的列表，另一个是执行了由Future构成的Map结构，它们表示的是即将在流上执行
            //的各种操作。
            //Consumer接口要求实现accept方法。这里，每当ForkingStreamConsumer接受流中的一
            //个元素，它就会将该元素添加到所有的BlockingQueues中。另外，当原始流中的所有元素都添
            //加到所有队列后，finish方法会将最后一个元素添加所有队列。BlockingQueueSpliterators
            //碰到最后这个元素时会知道队列中不再有需要处理的元素了。
            //Results接口需要实现get方法。一旦处理结束，get方法会获得Map中由键索引的Future，
            //解析处理的结果并返回。
            //最后，流上要进行的每个操作都会对应一个BlockingQueueSpliterator。每个BlockingQueueSpliterator都持有一个指向BlockingQueues的引用，这个BlockingQueues是由
            //ForkingStreamConsumer 生成的，你可以用下面这段代码清单类似的方法实现一个
            //BlockingQueueSpliterator。
            static class BlockingQueueSpliterator<T> implements Spliterator<T> {
                private final BlockingQueue<T> q;
                BlockingQueueSpliterator(BlockingQueue<T> q) {
                    this.q = q;
                }
                @Override
                public boolean tryAdvance(Consumer<? super T> action) {
                    T t;
                    while (true) {
                        try {
                            t = q.take();
                            break;
                        } catch (InterruptedException e) { }
                    }
                    if (t != ForkingStreamConsumer.END_OF_STREAM) {
                        action.accept(t);
                        return true;
                    }
                    return false;
                }
                @Override
                public Spliterator<T> trySplit() {
                    return null;
                }
                @Override
                public long estimateSize() {
                    return 0;
                }
                @Override
                public int characteristics() {
                    return 0;
                }
            }

            //这段代码实现了一个Spliterator，不过它并未定义如何切分流的策略，仅仅利用了流的
            //延迟绑定能力。由于这个原因，它也没有实现trySplit方法。
            //由于无法预测能从队列中取得多少个元素，所以estimatedSize方法也无法返回任何有意
            //义的值。更进一步，由于你没有试图进行任何切分，所以这时的估算也没什么用处。
            //这一实现并没有体现表7-2中列出的Spliterator的任何特性，因此characteristic方法
            //返回0。
            //这段代码中提供了实现的唯一方法是tryAdvance，它从BlockingQueue中取得原始流中的
            //元素，而这些元素最初由ForkingSteamConsumer添加。依据getOperationResult方法创建
            //Spliterator同样的方式，这些元素会被作为进一步处理流的源头传递给Consumer对象（在流
            //上要执行的函数会作为参数传递给某个fork方法调用）。tryAdvance方法返回true通知调用方
            //还有其他的元素需要处理，直到它发现由ForkingSteamConsumer添加的特殊对象，表明队列
            //中已经没有更多需要处理的元素了。图C-2展示了StreamForker及其构建模块的概述。


            //图C-2 StreamForker及其合作的构造块 这幅图中，左上角的StreamForker中包含一个Map结构，以方法的形式定义了流上要执行
            //的操作，这些方法分别由对应的键索引。右边的ForkingStreamConsumer为每一种操作的对象
            //维护了一个队列，原始流中的所有元素会被分发到这些队列中。
            //图的下半部分，每一个队列都有一个BlockingQueueSpliterator从队列中提取元素作为
            //各个流处理的源头。最后，由原始流复制创建的每个流，都会被作为参数传递给某个处理函数，
            //执行对应的操作。至此，你已经实现了StreamForker所有组件，可以开始工作了。
        }

        //这里的fork方法接受两个参数。
        // Function参数，它对流进行处理，将流转变为代表这些操作结果的任何类型。
        // key参数，通过它你可以取得操作的结果，并将这些键/函数对累积到一个内部的Map中

        //图C-1 StreamForker详解
        //这里用户定义了希望在流上执行的三种操作，这三种操作通过三个键索引标识。StreamForker会遍历原始的流，并创建它的三个副本。这时就可以并行地在复制的流上执行这三种操
        //作，这些函数运行的结果由对应的键进行索引，最终会填入到结果的Map。

        //所有由fork方法添加的操作的执行都是通过getResults方法的调用触发的，该方法返回一
        //个Results接口的实现，具体的定义如下：
        //这一接口只有一个方法，你可以将fork方法中使用的key对象作为参数传入，方法会返回该
        //键对应的操作结果
        public interface Results {
            public <R> R get(Object key);
        }

        /**
         * C.1.1 使用 ForkingStreamConsumer 实现 Results 接口
         *
         */
        public static class C11 {

        }

        //C.1.3 将 StreamForker 运用于实战
        //我们将StreamForker应用到第4章中定义的menu数据模型上，希望对它进行一些处理。通
        //过复制原始的菜肴（dish）流，我们想以并发的方式执行四种不同的操作，代码清单如下所示。
        //这尤其适用于以下情况：你想要生成一份由逗号分隔的菜肴名列表，计算菜单的总热量，找出热
        //量最高的菜肴，并按照菜的类型对这些菜进行分类。
        public static void main(String[] args) {
            //将StreamForker运用于实战
            Stream<Dish> menuStream = Dish.getOfficial().stream();
            StreamForker.Results results = new StreamForker<Dish>(menuStream)
                    .fork("shortMenu", s -> s.map(Dish::getName)
                            .collect(joining(", ")))
                    .fork("totalCalories", s -> s.mapToInt(Dish::getCalories).sum())
                    .fork("mostCaloricDish", s -> s.collect(reducing(
                            (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2))
                            .get())
                    .fork("dishesByType", s -> s.collect(groupingBy(Dish::getType)))
                    .getResults();

            String shortMenu = results.get("shortMenu");
            int totalCalories = results.get("totalCalories");
            Dish mostCaloricDish = results.get("mostCaloricDish");
            Map<Dish.Type, List<Dish>> dishesByType = results.get("dishesByType");
            System.out.println("Short menu: " + shortMenu);
            System.out.println("Total calories: " + totalCalories);
            System.out.println("Most caloric dish: " + mostCaloricDish);
            System.out.println("Dishes by type: " + dishesByType);

            //StreamForker提供了一种使用简便、结构流畅的API，它能够复制流，并对每个复制的流
            //施加不同的操作。这些应用在流上以函数的形式表示，可以用任何对象的方式标识，在这个例子
            //里，我们选择使用String的方式。如果你没有更多的流需要添加，可以调用StreamForker的
            //getResults方法，触发所有定义的操作开始执行，并取得StreamForker.Results。由于这些
            //操作的内部实现就是异步的，getResults方法调用后会立刻返回，不会等待所有的操作完成，
            //拿到所有的执行结果才返回。
            //你可以通过向StreamForker.Results接口传递标识特定操作的键取得某个操作的结果。
            //如果该时刻操作已经完成，get方法会返回对应的结果；否则，该方法会阻塞，直到计算结束，
            //取得对应的操作结果。
            //正如我们所预期的，这段代码会产生下面这些输出：
            //Short menu: pork, beef, chicken, french fries, rice, season fruit, pizza,
            // prawns, salmon
            //Total calories: 4300
            //Most caloric dish: pork
            //Dishes by type: {OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork,
            // beef, chicken], FISH=[prawns, salmon]}
        }
    }
    
}
