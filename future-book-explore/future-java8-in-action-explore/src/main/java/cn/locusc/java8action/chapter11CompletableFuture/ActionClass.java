package cn.locusc.java8action.chapter11CompletableFuture;

import cn.locusc.java8action.domain.Discount;
import cn.locusc.java8action.domain.Quote;
import cn.locusc.java8action.domain.Shop;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jay
 * CompletableFuture:组合式异步编程
 * 本章内容:
 *  创建异步计算，并获取计算结果
 *  使用非阻塞操作提升吞吐量
 *  设计和实现异步API
 *  如何以异步的方式使用同步的API
 *  如何对两个或多个异步操作进行流水线和合并操作
 *  如何处理异步操作的完成状态
 *
 * 小结:
 *  执行比较耗时的操作时，尤其是那些依赖一个或多个远程服务的操作，使用异步任务可
 * 以改善程序的性能，加快程序的响应速度。
 *  你应该尽可能地为客户提供异步API。使用CompletableFuture类提供的特性，你能够
 * 轻松地实现这一目标。
 *  CompletableFuture类还提供了异常管理的机制，让你有机会抛出/管理异步任务执行
 * 中发生的异常。
 *  将同步API的调用封装到一个CompletableFuture中，你能够以异步的方式使用其结果。
 *  如果异步任务之间相互独立，或者它们之间某一些的结果是另一些的输入，你可以将这
 * 些异步任务构造或者合并成一个。
 *  你可以为CompletableFuture注册一个回调函数，在Future执行完毕或者它们计算的
 * 结果可用时，针对性地执行一些程序。
 *  你可以决定在什么时候结束程序的运行，是等待由CompletableFuture对象构成的列表
 * 中所有的对象都执行完毕，还是只要其中任何一个首先完成就中止程序的运行。
 * 2021/11/30
 */
public class ActionClass {

    private final static Executor executor = Executors.newFixedThreadPool(
            // 创建一个线程池，线程池中线程的数目为100和商店数目二者中较小的一个值
            Math.min(Shop.getShops().size(), 100),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    // 使用守护线程——这种方式不会阻止程序的关停
                    //你现在正创建的是一个由守护线程构成的线程池。Java程序无法终止或者退出一个正
                    //在运行中的线程，所以最后剩下的那个线程会由于一直等待无法发生的事件而引发问题。与此相
                    //反，如果将线程标记为守护进程，意味着程序退出时它也会被回收。这二者之间没有性能上的差异
                    t.setDaemon(true);
                    return t;
                }
            }
    );

    /**
     * 实现并发，而非并行，或者你的主要目标是在同一个CPU上执行几个松耦合的任务，
     * 充分利用CPU的核，让其足够忙碌，从而最大化程序的吞吐量，那么你
     * 其实真正想做的是避免因为等待远程服务的返回，或者对数据库的查询，而阻塞线程的执行，
     * 浪费宝贵的计算资源，因为这种等待的时间很可能相当长。通过本章中你会了解，Future接口，
     * 尤其是它的新版实现CompletableFuture，是处理这种情况的利器
     */
    public static void main(String[] args) {

    }

    /**
     * 11.1 Future 接口
     * Future接口在Java 5中被引入，设计初衷是对将来某个时刻会发生的结果进行建模
     * 它建模了一种异步计算，返回一个执行运算结果的引用，当运算结束后，这个引用被返回给调用方
     *
     */
    public static class Jia111 {

        public static Double doSomeLongComputation() {
            return 0.00;
        }
        public static Double doSomethingElse() {
            return 0.00;
        }

        public static void main(String[] args) {
            // 使用Future以异步的方式执行一个耗时的操作
            // 创建ExecutorService，通过它你可以向线程池提交任务
            ExecutorService executor = Executors.newCachedThreadPool();
            // 向ExecutorService提交一个Callable对象
            Future<Double> future = executor.submit(new Callable<Double>() {
                @Override
                public Double call() {
                    // 以异步方式在新的线程中执行耗时的操作
                    return doSomeLongComputation();
                }
            });
            // 异步操作进行的同时，你可以做其他的事情
            doSomethingElse();
            try {
                // 获取异步操作的结果，如果最终被阻塞，无法得到结果，那么在最多等待1秒钟之后退出
                Double result = future.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //这种编程方式让你的线程可以在ExecutorService以并发方式调
            //用另一个线程执行耗时操作的同时，去执行一些其他的任务。接着，如果你已经运行到没有异步
            //操作的结果就无法继续任何有意义的工作时，可以调用它的get方法去获取操作的结果。如果操
            //作已经完成，该方法会立刻返回操作的结果，否则它会阻塞你的线程，直到操作完成，返回相应
            //的结果。
        }



        /**
         * 11.1.1 Future 接口的局限性
         * 我们很难表述Future结果之间的依赖性；从文字描述上这很简单，“当长时
         * 间计算任务完成时，请将该计算的结果通知到另一个长时间运行的计算任务，这两个计算任务都
         * 完成后，将计算的结果与另一个查询操作结果合并”。但是，使用Future中提供的方法完成这样
         * 的操作又是另外一回事。这也是我们需要更具描述能力的特性的原因，比如下面这些。
         *  将两个异步计算合并为一个——这两个异步计算之间相互独立，同时第二个又依赖于第
         * 一个的结果。
         *  等待Future集合中的所有任务都完成。
         *  仅等待Future集合中最快结束的任务完成（有可能因为它们试图通过不同的方式计算同
         * 一个值），并返回它的结果。
         *  通过编程方式完成一个Future任务的执行（即以手工设定异步操作结果的方式）。
         *  应对Future的完成事件（即当Future的完成事件发生时会收到通知，并能使用Future
         * 计算的结果进行下一步的操作，不只是简单地阻塞等待操作的结果）
         *
         * Stream和CompletableFuture的设计都遵循
         * 了类似的模式：它们都使用了Lambda表达式以及流水线的思想。从这个角度，你可以说
         * CompletableFuture和Future的关系就跟Stream和Collection的关系一样
         */
        public static class Jia1111 { }

        /**
         * 11.1.2 使用 CompletableFuture 构建异步应用
         * 我们会创建一个名为“最佳价格查询器” （best-price-finder）的应用，
         * 它会查询多个在线商店，依据给定的产品或服务找出最低的价格。
         * 这个过程中，你会学到几个重要的技能。
         *
         *  首先，你会学到如何为你的客户提供异步API（如果你拥有一间在线商店的话，这是非常
         * 有帮助的）。
         *  其次，你会掌握如何让你使用了同步API的代码变为非阻塞代码。你会了解如何使用流水
         * 线将两个接续的异步操作合并为一个异步计算操作。这种情况肯定会出现，比如，在线
         * 商店返回了你想要购买商品的原始价格，并附带着一个折扣代码——最终，要计算出该
         * 商品的实际价格，你不得不访问第二个远程折扣服务，查询该折扣代码对应的折扣比率。
         *  你还会学到如何以响应式的方式处理异步操作的完成事件，以及随着各个商店返回它的
         * 商品价格，最佳价格查询器如何持续地更新每种商品的最佳推荐，而不是等待所有的商
         * 店都返回他们各自的价格（这种方式存在着一定的风险，一旦某家商店的服务中断，用
         * 户可能遭遇白屏）。
         *
         * 同步API与异步API
         * 同步API其实只是对传统方法调用的另一种称呼：你调用了某个方法，调用方在被调用方
         * 运行的过程中会等待，被调用方运行结束返回，调用方取得被调用方的返回值并继续运行。即
         * 使调用方和被调用方在不同的线程中运行，调用方还是需要等待被调用方结束运行，这就是阻
         * 塞式调用这个名词的由来。
         * 与此相反，异步API会直接返回，或者至少在被调用方计算完成之前，将它剩余的计算任
         * 务交给另一个线程去做，该线程和调用方是异步的——这就是非阻塞式调用的由来。执行剩余
         * 计算任务的线程会将它的计算结果返回给调用方。返回的方式要么是通过回调函数，要么是由
         * 调用方再次执行一个“等待，直到计算完成”的方法调用。这种方式的计算在I/O系统程序设
         * 计中非常常见：你发起了一次磁盘访问，这次访问和你的其他计算操作是异步的，你完成其他
         * 的任务时，磁盘块的数据可能还没载入到内存，你只需要等待数据的载入完成。
         */
        public static class Jia1112 { }
    }

    /**
     * 11.2 实现异步 API
     */
    public static class Jia112 {

        public static class Shop {

            public Shop(String bestShop) {
            }

            //为了实现最佳价格查询器应用，让我们从每个商店都应该提供的API定义入手。首先，商店
            //应该声明依据指定产品名称返回价格的方法：
            public double getPrice(String product) {
                return calculatePrice(product);
            }

            // 模拟1秒钟延迟的方法
            //该方法的内部实现会查询商店的数据库，但也有可能执行一些其他耗时的任务，比如联系其
            //他外部服务（比如，商店的供应商，或者跟制造商相关的推广折扣）。我们在本章剩下的内容中，
            //采用delay方法模拟这些长期运行的方法的执行，它会人为地引入1秒钟的延迟
            public static void delay() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //getPrice方法会调用delay方法，并返回一个随机计算的值，代码
            //清单如下所示。返回随机计算的价格这段代码看起来有些取巧。它使用charAt，依据产品的名
            //称，生成一个随机值作为价格
            public static double calculatePrice(String product) {
                throw new RuntimeException("product not available");
                //delay();
                //return new Random().nextDouble() * product.charAt(0) + product.charAt(1);
            }

            //很明显，这个API的使用者（这个例子中为最佳价格查询器）调用该方法时，它依旧会被
            //阻塞。为等待同步事件完成而等待1秒钟，这是无法接受的，尤其是考虑到最佳价格查询器对
            //网络中的所有商店都要重复这种操作。本章接下来的小节中，你会了解如何以异步方式使用同
            //步API解决这个问题。但是，出于学习如何设计异步API的考虑，我们会继续这一节的内容，假
            //装我们还在深受这一困难的烦扰：你是一个睿智的商店店主，你已经意识到了这种同步API会
            //为你的用户带来多么痛苦的体验，你希望以异步API的方式重写这段代码，让用户更流畅地访
            //问你的网站。
        }

        /**
         * 11.2.1 将同步方法转换为异步方法
         */
        public static class Jia1121 {

            public static class Shop {
                public Shop(String bestShop) {

                }

                // 为了实现这个目标，你首先需要将getPrice转换为getPriceAsync方法，并修改它的返回值
                // 新的CompletableFuture类提供了大量的方法可以代替原始的Future实现
                public Future<Double> getPriceAsync(String product) {
                    // 创建CompletableFuture对象，它会包含计算的结果
                    CompletableFuture<Double> futurePrice = new CompletableFuture<>();
                    new Thread(() -> {
                        // 在另一个线程中以异步方式执行计算
                        double price = Jia112.Shop.calculatePrice(product);
                        // 需长时间计算的任务结束并得出结果时，设置Future的返回值
                        futurePrice.complete(price);
                    }).start();
                    // 无需等待还没结束的计算，直接返回Future对象
                    return futurePrice;
                }
            }

            public static void main(String[] args) {
                // 使用异步API
                Shop shop = new Shop("BestShop");
                long start = System.nanoTime();
                // 查询商店，试图取得商品的价格
                Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
                long invocationTime = ((System.nanoTime() - start) / 1_000_000);
                System.out.println("Invocation returned after " + invocationTime
                        + " msecs");

                // 执行更多任务，比如查询其他商店
                Jia111.doSomethingElse();

                // 在计算商品价格的同时
                try {
                    // 从Future对象中读取价格，如果价格未知，会发生阻塞
                    double price = futurePrice.get();
                    System.out.printf("Price is %.2f%n", price);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
                System.out.println("Price returned after " + retrievalTime + " msecs");

                //Invocation returned after 114 msecs
                //Price is 145.98
                //Price returned after 1188 msecs

                //你一定已经发现getPriceAsync方法的调用返回远远早于最终价格计算完成的时间。在11.4
                //节中，你还会知道我们有可能避免发生客户端被阻塞的风险。实际上这非常简单，Future执行
                //完毕可以发送一个通知，仅在计算结果可用时执行一个由Lambda表达式或者方法引用定义的回
                //调函数
            }
        }

        /**
         * 11.2.2 错误处理
         * 如果价格计算过程中产生了错误 用于提示错误的异常会被限制
         * 在试图计算商品价格的当前线程的范围内，最终会杀死该线程，
         * 而这会导致等待get方法返回结果的客户端永久地被阻塞。
         *
         * 客户端可以使用重载版本的get方法，它使用一个超时参数来避免发生这样的情况
         * 也因为如此，你不会有机会发现计算商品价格的线程内到底发生了什么问题才引发了这样的失效。
         *
         * CompletableFuture的completeExceptionally方法
         * 将导致CompletableFuture内发生问题的异常抛出
         */
        public static class Jia1122 {

            public static class Shop {

                public Shop(String bestShop) { }

                //客户端现在会收到一个ExecutionException异常，该异常接收了一个包含失败原因的
                //Exception参数，即价格计算方法最初抛出的异常
                public Future<Double> getPriceAsync(String product) {
                    CompletableFuture<Double> futurePrice = new CompletableFuture<>();
                    new Thread(() -> {
                        double price = 0;
                        try {
                            price = Jia112.Shop.calculatePrice(product);
                            // 如果价格计算正常结束，完成Future操作并设置商品价格
                            futurePrice.complete(price);
                        } catch (Exception ex) {
                            // 否则就抛出导致失败的异常，完成这次Future操作
                            futurePrice.completeExceptionally(ex);
                        }
                    }).start();
                    return futurePrice;
                }

                //CompletableFuture类自
                //身提供了大量精巧的工厂方法，使用这些方法能更容易地完成整个流程，还不用担心实现的细节。
                //比如，采用supplyAsync方法后，你可以用一行语句重写代码

                //supplyAsync方法接受一个生产者（Supplier）作为参数，返回一个CompletableFuture
                //对象，该对象完成异步执行后会读取调用生产者方法的返回值。生产者方法会交由ForkJoinPool
                //池中的某个执行线程（Executor）运行，但是你也可以使用supplyAsync方法的重载版本，传
                //递第二个参数指定不同的执行线程执行生产者方法。一般而言，向CompletableFuture的工厂
                //方法传递可选参数，指定生产者方法的执行线程是可行的，
                //这种方式直接存在了错误处理机制
                public Future<Double> getPriceAsyncImproves(String product) {
                    return CompletableFuture.supplyAsync(() -> Jia112.Shop.calculatePrice(product));
                }
            }

            public static void main(String[] args) {
                Shop shop = new Shop("BestShop");
                Future<Double> futurePrice = shop.getPriceAsyncImproves("my favorite product");
                try {
                    double price = futurePrice.get();
                    System.out.printf("Price is %.2f%n", price);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 11.3 让你的代码免受阻塞之苦
     */
    public static class Jia113 {

        public static void main(String[] args) {
            // 验证findPrices的正确性和执行性能
            // 对这4个商的查询是顺序进行的，并且一个查询操作会阻塞另一个
            // [BestPrice price is 197.71, LetsSaveBig price is 191.74, MyFavoriteShop price is 138.24, BuyItAll price is 151.76]
            // Done in 4168 msecs
            Shop.findShopPerformance("myPhone27S", Shop.findPrices());
        }

        /**
         * 11.3.1 使用并行流对请求进行并行操作
         */
        public static class Jia1131 {
            public static void main(String[] args) {
                // 对findPrices进行并行操作
                // [BestPrice price is 207.01, LetsSaveBig price is 205.40, MyFavoriteShop price is 214.76, BuyItAll price is 143.66]
                // Done in 1062 msecs
                Shop.findShopPerformance("myPhone27S", Shop.findPricesParallel());
            }
        }

        /**
         * 11.3.2 使用 CompletableFuture 发起异步请求
         */
        public static class Jia1132 {

            // 得到一个List<CompletableFuture<String>>，列表中的每个
            // CompletableFuture对象在计算完成后都包含商店的String类型的名称
            public static Function<String, List<CompletableFuture<String>>> findPricesCF() {
                return name -> Shop.getShops().stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> String.format("%s price is %.2f",
                                        shop.getName(), shop.getPrice(name))
                                )
                        )
                        .collect(Collectors.toList());
            }

            public static Function<String, List<String>> findPricesCFM() {
                return name -> {
                    List<CompletableFuture<String>> priceFutures = Shop.getShops().stream()
                            // 使用CompletableFuture以异步方式计算每种商品的价格
                            .map(shop -> CompletableFuture.supplyAsync(
                                    () -> String.format("%s price is %.2f",
                                            shop.getName(), shop.getPrice(name))
                                    )
                            )
                            .collect(Collectors.toList());

                    // 对List中的所有future对象执行join操作，一个接一个地等待它们运行结束
                    // 注意: CompletableFuture类中的join方法和Future接口中的get有相同的含义
                    // 并且也声明在Future接口中，它们唯一的不同是join不会抛出任何检测到的异常
                    // 使用它你不再需要使用try/catch语句块让你传递给第二个map方法的Lambda表达式变得过于臃肿
                    return priceFutures.stream()
                            // 等待所有异步操作结束
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                };
            }

            public static void main(String[] args) {
                //[BestPrice price is 181.19, LetsSaveBig price is 155.23, MyFavoriteShop price is 128.83, BuyItAll price is 146.36]
                //Done in 1095 msecs 时间仍比并行慢 书上大约是并行的两倍

                //图11-4【为什么Stream的延迟特性会引起顺序执行以及如何避免.jpg】
                //的上半部分展示了使用单一流水线处理流的过程，我们看到，执行的流程（以虚线标
                //识）是顺序的。事实上，新的CompletableFuture对象只有在前一个操作完全结束之后，才能
                //创建。与此相反，图的下半部分展示了如何先将CompletableFutures对象聚集到一个列表中
                //（即图中以椭圆表示的部分），让对象们可以在等待其他对象完成操作之前就能启动
                Shop.findShopPerformance("myPhone27S", findPricesCFM());
            }

        }

        /**
         * 11.3.3 寻找更好的方案
         */
        public static class Jia1133 {

            public static void main(String[] args) {
                // 并行流的版本工作得非常好，那是因为它能并行地执行四个任务，所以它几乎能为每个商家
                // 书中是4个 但是我的电脑是四核八线程
                //分配一个线程。但是，如果你想要增加第九个商家到商店列表中，
                //[BestPrice price is 193.89, LetsSaveBig price is 157.70, MyFavoriteShop price is 171.91, BuyItAll price is 125.29, BuyItAll price is 211.00, BuyItAll price is 125.99, BuyItAll price is 133.95, BuyItAll price is 226.63, CostCo price is 195.53]
                //Done in 9104 msecs
                Shop.findShopPerformance("myPhone27S", Shop.findPrices());

                //[BestPrice price is 141.86, LetsSaveBig price is 123.76, MyFavoriteShop price is 161.17, BuyItAll price is 129.92, BuyItAll price is 218.92, BuyItAll price is 201.05, BuyItAll price is 202.84, BuyItAll price is 195.52, CostCo price is 143.69]
                //Done in 2073 msecs
                Shop.findShopPerformance("myPhone27S", Shop.findPricesParallel());

                //[BestPrice price is 166.76, LetsSaveBig price is 197.22, MyFavoriteShop price is 183.57, BuyItAll price is 219.39, BuyItAll price is 205.92, BuyItAll price is 184.66, BuyItAll price is 201.31, BuyItAll price is 133.90, CostCo price is 158.69]
                //Done in 2127 msecs
                Shop.findShopPerformance("myPhone27S", Jia1132.findPricesCFM());

                //并行流和异步调用不分伯仲 究其原因都一样：它们内部
                //采用的是同样的通用线程池，默认都使用固定数目的线程，具体线程数取决于Runtime.
                //getRuntime().availableProcessors()的返回值。然而，CompletableFuture具有一定的
                //优势，因为它允许你对执行器（Executor）进行配置，尤其是线程池的大小，让它以更适合应
                //用需求的方式进行配置，满足程序的要求，而这是并行流API无法提供的。让我们看看你怎样利
                //用这种配置上的灵活性带来实际应用程序性能上的提升。
            }

        }

        /**
         * 11.3.4 使用定制的执行器
         * 《Java并发编程实战》（http://mng.bz/979c）一书中，Brian Goetz和合著者们为线程池大小
         * 的优化提供了不少中肯的建议。这非常重要，如果线程池中线程的数量过多，最终它们会竞争
         * 稀缺的处理器和内存资源，浪费大量的时间在上下文切换上。反之，如果线程的数目过少，正
         * 如你的应用所面临的情况，处理器的一些核可能就无法充分利用。Brian Goetz建议，线程池大
         * 小与处理器的利用率之比可以使用下面的公式进行估算：
         * Nthreads = NCPU * UCPU * (1 + W/C)
         * 其中：
         * ❑ NCPU是处理器的核的数目，可以通过Runtime.getRuntime().availableProcessors()得到
         * ❑ UCPU是期望的CPU利用率（该值应该介于0和1之间）
         * ❑ W/C是等待时间与计算时间的比率
         */
        public static class Jia1134 {

            //你的应用99%的时间都在等待商店的响应，所以估算出的W/C比率为100。这意味着如果你
            //期望的CPU利用率是100%，你需要创建一个拥有400个线程的线程池。实际操作中，如果你创建
            //的线程数比商店的数目更多，反而是一种浪费，因为这样做之后，你线程池中的有些线程根本没
            //有机会被使用。出于这种考虑，我们建议你将执行器使用的线程数，与你需要查询的商店数目设
            //定为同一个值，这样每个商店都应该对应一个服务线程。不过，为了避免发生由于商店的数目过
            //多导致服务器超负荷而崩溃，你还是需要设置一个上限，比如100个线程
            //为“最优价格查询器”应用定制的执行器
            private final static Executor executor = Executors.newFixedThreadPool(
                    // 创建一个线程池，线程池中线程的数目为100和商店数目二者中较小的一个值
                    Math.min(Shop.getShops().size(), 100),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            // 使用守护线程——这种方式不会阻止程序的关停
                            //你现在正创建的是一个由守护线程构成的线程池。Java程序无法终止或者退出一个正
                            //在运行中的线程，所以最后剩下的那个线程会由于一直等待无法发生的事件而引发问题。与此相
                            //反，如果将线程标记为守护进程，意味着程序退出时它也会被回收。这二者之间没有性能上的差异
                            t.setDaemon(true);
                            return t;
                        }
                    }
            );

            public static Function<String, List<String>> findPricesCFME() {
                return name -> {
                    List<CompletableFuture<String>> priceFutures = Shop.getShops().stream()
                            .map(shop -> CompletableFuture.supplyAsync(
                                    () -> String.format("%s price is %.2f",
                                            shop.getName(), shop.getPrice(name))
                                    , executor)
                            )
                            .collect(Collectors.toList());

                    return priceFutures.stream()
                            // 等待所有异步操作结束
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                };
            }


            public static void main(String[] args) {
                // 可以将执行器作为第二个参数传递给supplyAsync工厂方法了
                //[BestPrice price is 220.46, LetsSaveBig price is 182.06, MyFavoriteShop price is 136.86, BuyItAll price is 132.61, BuyItAll price is 183.57, BuyItAll price is 142.28, BuyItAll price is 211.22, BuyItAll price is 150.11, CostCo price is 213.70]
                //Done in 1089 msecs
                Shop.findShopPerformance("myPhone27S", findPricesCFME());
                //一般而言，这种状态会一直持续，直到商店的数目达到我们之前计算的阈值400。
                //这个例子证明了要创建更适合你的应用特性的执行器，利用CompletableFutures向其提交任
                //务执行是个不错的主意。处理需大量使用异步操作的情况时，这几乎是最有效的策略

                // 并行——使用流还是CompletableFutures？
                //目前为止，你已经知道对集合进行并行计算有两种方式：要么将其转化为并行流，利用map
                //这样的操作开展工作，要么枚举出集合中的每一个元素，创建新的线程，在CompletableFuture内对其进行操作。后者提供了更多的灵活性，你可以调整线程池的大小，而这能帮助
                //你确保整体的计算不会因为线程都在等待I/O而发生阻塞。
                //我们对使用这些API的建议如下。
                //❑如果你进行的是计算密集型的操作，并且没有I/O，那么推荐使用Stream接口，因为实
                //现简单，同时效率也可能是最高的（如果所有的线程都是计算密集型的，那就没有必要
                //创建比处理器核数更多的线程）。
                //❑反之，如果你并行的工作单元还涉及等待I/O的操作（包括网络连接等待），那么使用
                //CompletableFuture灵活性更好，你可以像前文讨论的那样，依据等待/计算，或者
                //W/C的比率设定需要使用的线程数。这种情况不使用并行流的另一个原因是，处理流的
                //流水线中如果发生I/O等待，流的延迟特性会让我们很难判断到底什么时候触发了等待。
            }
        }

    }

    /**
     * 11.4 对多个异步任务进行流水线操作
     * 详情查看Discount类
     * 让我们假设所有的商店都同意使用一个集中式的折扣服务。该折扣服务提供了五个不同的折
     * 扣代码，每个折扣代码对应不同的折扣率
     *
     * 我们还假设所有的商店都同意修改getPrice方法的返回格式。
     * getPrice现在以ShopName:price:DiscountCode的格式返回一个String类型的值
     * 我们的示例实现中会返回一个随机生成的Discount.Code，以及已经计算得出的随机价格：
     */
    public static class Jia114 {

        /**
         * 11.4.1 实现折扣服务
         * 详情查看Discount类和Quote类
         */
        public static class Jia1141 { }

        /**
         * 11.4.2 使用 Discount 服务
         * 由于Discount服务是一种远程服务，你还需要增加1秒钟的模拟延迟，代码如下所示。和在
         * 11.3节中一样，首先尝试以最直接的方式（坏消息是，这种方式是顺序而且同步执行的）重新实
         * 现findPrices，以满足这些新增的需求
         */
        public static class Jia1142 {

            // 第一个操作将每个shop对象转换成了一个字符串，该字符串包含了该 shop中指定商品的
            //价格和折扣代码。
            // 第二个操作对这些字符串进行了解析，在Quote对象中对它们进行转换。
            // 最终，第三个map会操作联系远程的Discount服务，计算出最终的折扣价格，并返回该
            //价格及提供该价格商品的shop。
            public static Function<String, List<String>> findPrice(String product) {
                return name -> Shop.getShops().stream()
                        .map(shop -> shop.getPriceDiscount(product))
                        // 在Quote对象中对shop返回的字符串进行转换
                        .map(Quote::parse)
                        // 联系Discount服务，为每个Quote申请折扣
                        .map(Discount::applyDiscount)
                        .collect(Collectors.toList());
            }

            public static void main(String[] args) {
                //[BestPrice price is 166.4, LetsSaveBig price is 164.34, MyFavoriteShop price is 127.67, BuyItAll price is 160.25, BuyItAll price is 145.88, BuyItAll price is 156.44, BuyItAll price is 102.99, BuyItAll price is 144.33, CostCo price is 172.49]
                //Done in 18237 msecs
                Shop.findShopPerformance("myPhone27S", findPrice("myPhone27S"));

                //书里面是五个
                //毫无意外，这次执行耗时10秒，因为顺序查询5个商店耗时大约5秒，现在又加上了Discount
                //服务为5个商店返回的价格申请折扣所消耗的5秒钟。你已经知道，把流转换为并行流的方式，非
                //常容易提升该程序的性能。不过，通过11.3节的介绍，你也知道这一方案在商店的数目增加时，
                //扩展性不好，因为Stream底层依赖的是线程数量固定的通用线程池。相反，你也知道，如果自
                //定义CompletableFutures调度任务执行的执行器能够更充分地利用CPU资源
            }

        }

        /**
         * 11.4.3 构造同步和异步操作
         * 再次使用CompletableFuture提供的特性，以异步方式重新实现findPrices方法
         */
        public static class Jia1143 {

            public static Function<String, List<String>> findPrice(String product) {
                return name -> {
                    List<CompletableFuture<String>> priceFutures = Shop.getShops().stream()
                            // 以异步方式取得每个shop中指定产品的原始价格
                            .map(shop -> CompletableFuture.supplyAsync(
                                    () -> shop.getPriceDiscount(product), executor
                            ))
                            // Quote对象存在时，对其返回的值进行转换
                            .map(future -> future.thenApply(Quote::parse))
                            // 使用另一个异步任务构造期望的Future，申请折扣
                            .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(
                                    () -> Discount.applyDiscount(quote), executor
                            )))
                            .collect(Collectors.toList());

                    return priceFutures.stream()
                            // 等待流中的所有Future执行完毕，并提取各自的返回值
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                };
            }

            public static void main(String[] args) {

                // 1. 获取价格
                //将Lambda表达式作为参数传递给supplyAsync工厂方法就可以以异步方式对shop进行查询。
                //第一个转换的结果是一个Stream<CompletableFuture<String>>，
                //一旦运行结束，每个CompletableFuture对象中都会包含对应shop返回的字符串。
                //注意，你对CompletableFuture进行了设置，用代码清单11-12中的方法向其传递了一个订制的执行器Executor

                // 2.解析报价
                //现在你需要进行第二次转换将字符串转变为订单。由于一般情况下解析操作不涉及任何远程
                //服务，也不会进行任何I/O操作，它几乎可以在第一时间进行，所以能够采用同步操作，不会带
                //来太多的延迟。由于这个原因，你可以对第一步中生成的CompletableFuture对象调用它的
                //thenApply，将一个由字符串转换Quote的方法作为参数传递给它。
                //注意到了吗？直到你调用的CompletableFuture执行结束，使用的thenApply方法都不会
                //阻塞你代码的执行。这意味着CompletableFuture最终结束运行时，你希望传递Lambda表达式
                //给thenApply方法，将Stream中的每个CompletableFuture<String>对象转换为对应的
                //CompletableFuture<Quote>对象。你可以把这看成是为处理CompletableFuture的结果建立了一个菜单，

                // 3. 为计算折扣价格构造Future
                //第三个map操作涉及联系远程的Discount服务，为从商店中得到的原始价格申请折扣率。
                //这一转换与前一个转换又不大一样，因为这一转换需要远程执行（或者，就这个例子而言，它需
                //要模拟远程调用带来的延迟），出于这一原因，你也希望它能够异步执行。
                //为了实现这一目标，你像第一个调用传递getPrice给supplyAsync那样，将这一操作以
                //Lambda表达式的方式传递给了supplyAsync工厂方法，该方法最终会返回另一个CompletableFuture对象。到目前为止，你已经进行了两次异步操作，用了两个不同的CompletableFutures
                //对象进行建模，你希望能把它们以级联的方式串接起来进行工作。
                // 从shop对象中获取价格，接着把价格转换为Quote。
                // 拿到返回的Quote对象，将其作为参数传递给Discount服务，取得最终的折扣价格。

                //Java 8的 CompletableFuture API提供了名为thenCompose的方法，它就是专门为这一目
                //的而设计的，thenCompose方法允许你对两个异步操作进行流水线，第一个操作完成时，将其
                //结果作为参数传递给第二个操作。换句话说，你可以创建两个CompletableFutures对象，对
                //第一个CompletableFuture对象调用 thenCompose，并向其传递一个函数。当第一个
                //CompletableFuture执行完毕后，它的结果将作为该函数的参数，这个函数的返回值是以第一
                //个CompletableFuture的返回做输入计算出的第二个CompletableFuture对象。使用这种方
                //式，即使Future在向不同的商店收集报价，主线程还是能继续执行其他重要的操作，比如响应
                //UI事件。
                //将这三次map操作的返回的Stream元素收集到一个列表，你就得到了一个List<CompletableFuture<String>>，等这些CompletableFuture对象最终执行完毕，你就可以像代码清
                //单11-11中那样利用join取得它们的返回值

                Shop.findShopPerformance("myPhone27S", findPrice("myPhone27S"));
                //在代码清单11-16中使用的thenCompose方法像CompletableFuture类中的其他方法一
                //样，也提供了一个以Async后缀结尾的版本thenComposeAsync。通常而言，名称中不带Async
                //的方法和它的前一个任务一样，在同一个线程中运行；而名称以Async结尾的方法会将后续的任
                //务提交到一个线程池，所以每个任务是由不同的线程处理的。就这个例子而言，第二个
                //CompletableFuture对象的结果取决于第一个CompletableFuture，所以无论你使用哪个版
                //本的方法来处理CompletableFuture对象，对于最终的结果，或者大致的时间而言都没有多少
                //差别。我们选择thenCompose方法的原因是因为它更高效一些，因为少了很多线程切换的开销。
            }
        }

        /**
         * 11.4.4 将两个 CompletableFuture 对象整合起来，无论它们是否存在依赖
         * 另一种比较常见的情况是，你需要将两个完
         * 全不相干的CompletableFuture对象的结果整合起来，而且你也不希望等到第一个任务完全结
         * 束才开始第二项任务。
         *
         * 这种情况，你应该使用thenCombine方法，它接收名为BiFunction的第二参数，这个参数
         * 定义了当两个CompletableFuture对象完成计算后，结果如何合并。同thenCompose方法一样，
         * thenCombine方法也提供有一个Async的版本。这里，如果使用thenCombineAsync会导致
         * BiFunction中定义的合并操作被提交到线程池中，由另一个任务以异步的方式执行。
         * 回到我们正在运行的这个例子，你知道，有一家商店提供的价格是以欧元（EUR）计价的，
         * 但是你希望以美元的方式提供给你的客户。你可以用异步的方式向商店查询指定商品的价格，同
         * 时从远程的汇率服务那里查到欧元和美元之间的汇率。当二者都结束时，再将这两个结果结合起
         * 来，用返回的商品价格乘以当时的汇率，得到以美元计价的商品价格。用这种方式，你需要使用
         * 第三个 CompletableFuture 对象，当前两个 CompletableFuture 计算出结果，并由
         * BiFunction方法完成合并后，由它来最终结束这一任务，代码清单如下所示。
         */
        public static class Jia1144 {

            enum Money {
                EUR,
                USD
            }

            private static Double getRate(Money money1, Money money2) {
                return null;
            }

            // 合并两个独立的CompletableFuture对象
            public static Function<String, List<String>> findPrice(String product) {
                return name -> {
                    List<CompletableFuture<String>> priceFutures = Shop.getShops().stream()
                            // 以异步方式取得每个shop中指定产品的原始价格
                            .map(shop -> CompletableFuture.supplyAsync(
                                    () -> shop.getPriceDiscount(product), executor
                                //这里整合的操作只是简单的乘法操作，用另一个单独的任务对其进行操作有些浪费资源，所
                                //以你只要使用thenCombine方法，无需特别求助于异步版本的thenCombineAsync方法
                                ).thenCombine(
                                        CompletableFuture.supplyAsync(
                                                () -> getRate(Money.EUR, Money.USD)
                                        ),
                                        // (price, rate) -> price * rate
                                        (price, rate) -> price + rate
                                    )
                            )
                            .collect(Collectors.toList());

                    return priceFutures.stream()
                            // 等待流中的所有Future执行完毕，并提取各自的返回值
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                };
            }
        }

        /**
         * 11.4.5 对 Future 和 CompletableFuture 的回顾
         * 前文非常清晰地呈现了相对于采用Java 8之前提供的Future实现，
         * CompletableFuture版本实现所具备的巨大优势。
         * CompletableFuture利用Lambda表达式以声明式的API提供了一种机制，能够用最有效的方式，
         * 非常容易地将多个以同步或异步方式执行复杂操作的任务结合到一起。为了更直观地感受一下使
         * 用CompletableFuture在代码可读性上带来的巨大提升
         */
        public static class Jia1145 {

            public static void main(String[] args) {
                // 利用Java 7的方法合并两个Future对象
                ExecutorService executor = Executors.newCachedThreadPool();
                // 创建一个ExecutorService将任务提交到线程池
                Future<Double> futureRate = executor.submit(new Callable<Double>() {

                    @Override
                    public Double call() {
                        // 创建一个查询欧元到美元转换汇率的Future
                        return Jia1144.getRate(Jia1144.Money.EUR, Jia1144.Money.USD);
                    }
                });

                Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
                    public Double call() throws ExecutionException, InterruptedException {
                        // 在第二个 Future中查询指定商店中特定商品的价格
                        // 在查找价格操作的同一个Future中， 特定商品的价格将价格和汇率做乘法计算出汇后价格
                        double priceInEUR = Shop.getPrice("");// product
                        return priceInEUR * futureRate.get();
                    }
                });

                //你通过向执行器提交一个Callable对象的方式创建了第一个Future
                //对象，向外部服务查询欧元和美元之间的转换汇率。紧接着，你创建了第二个Future对象，查
                //询指定商店中特定商品的欧元价格。最终，用与代码清单11-17一样的方式，你在同一个Future
                //中通过查询商店得到的欧元商品价格乘以汇率得到了最终的价格。注意，代码清单11-17中如果
                //使用thenCombineAsync，不使用thenCombine，像代码清单11-18中那样，采用第三个Future
                //单独进行商品价格和汇率的乘法运算，效果是几乎相同的。这两种实现看起来没太大区别，原因
                //是你只对两个Future进行了合并。通过代码清单11-19和代码清单11-20，我们能看到创建流水线
                //对同步和异步操作进行混合操作有多么简单，随着处理任务和需要合并结果数目的增加，这种声
                //明式程序设计的优势也愈发明显。

                //你的“最佳价格查询器”应用基本已经完成，不过还缺失了一些元素。你会希望尽快将不同
                //商店中的商品价格呈现给你的用户（这是车辆保险或者机票比价网站的典型需求），而不是像你之
                //前那样，等所有的数据都完备之后再呈现。接下来的一节，你会了解如何通过响应CompletableFuture的completion事件实现这一功能（与此相反，调用get或者join方法只会造成阻塞，直
                //到CompletableFuture完成才能继续往下运行）。
            }
        }
    }

    /**
     * 11.5 响应 CompletableFuture 的 completion 事件
     * 本章你看到的所有示例代码都是通过在响应之前添加1秒钟的等待延迟模拟方法的远程调
     * 用。毫无疑问，现实世界中，你的应用访问各个远程服务时很可能遭遇无法预知的延迟，触发的
     * 原因多种多样，从服务器的负荷到网络的延迟，有些甚至是源于远程服务如何评估你应用的商业
     * 价值，即可能相对于其他的应用，你的应用每次查询的消耗时间更长。
     */
    public static class Jia115 {

        private static final Random random = new Random();

        //由于这些原因，你希望购买的商品在某些商店的查询速度要比另一些商店更快。为了说明本
        //章的内容，我们以下面的代码清单为例，使用randomDelay方法取代原来的固定延迟。
        // 一个模拟生成0.5秒至2.5秒随机延迟的方法
        private static void randomDelay() {
            int delay = 500 + random.nextInt(2000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        /**
         * 11.5.1 对最佳价格查询器应用的优化
         * 目前为止，你实现的findPrices方法只有在取得所有商店的返回值时才显示商品的价格。
         * 而你希望的效果是，只要有商店返回商品价格就在第一时间显示返回值，不再等待那些还未返回
         * 的商店（有些甚至会发生超时）
         */
        public static class Jia1151 {

            // 避免的首要问题是，等待创建一个包含了所有价格的List创建完成。你应该做的是直
            //接处理CompletableFuture流，这样每个CompletableFuture都在为某个商店执行必要的操
            //作。为了实现这一目标，在下面的代码清单中，你会对代码清单11-12中代码实现的第一部分进
            //行重构，实现findPricesStream方法来生成一个由CompletableFuture构成的流
            // 重构findPrices方法返回一个由Future构成的流
            public static Stream<CompletableFuture<String>> findPricesStream(String product) {
                return Shop.getShops().stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getPriceDiscount(product), executor
                        ))
                        .map(future -> future.thenApply(Quote::parse))
                        .map(future -> future.thenCompose(quote ->
                                CompletableFuture.supplyAsync(
                                        () -> Discount.applyDiscount(quote)
                                        ,executor
                                )
                        ));
            }

            public static void main(String[] args) {
                //为findPricesStream方法返回的Stream添加了第四个map操作，在此之前，你
                //已经在该方法内部调用了三次 map 。这个新添加的操作其实很简单，只是在每个
                //CompletableFuture上注册一个操作，该操作会在CompletableFuture完成执行后使用它的
                //返回值。Java 8的CompletableFuture通 过thenAccept方法提供了这一功能，它接收
                //CompletableFuture执行完毕后的返回值做参数。在这里的例子中，该值是由Discount服务
                //返回的字符串值，它包含了提供请求商品的商店名称及折扣价格
                // TODO 这里没有输出值
                findPricesStream("myPhone")
                        .map(f -> f.thenAccept(System.out::println));

                //注意，和你之前看到的thenCompose和thenCombine方法一样，thenAccept方法也提供
                //了一个异步版本，名为thenAcceptAsync。异步版本的方法会对处理结果的消费者进行调度，
                //从线程池中选择一个新的线程继续执行，不再由同一个线程完成CompletableFuture的所有任
                //务。因为你想要避免不必要的上下文切换，更重要的是你希望避免在等待线程上浪费时间，尽快
                //响应CompletableFuture的completion事件，所以这里没有采用异步版本。
                //由 于thenAccept方法已经定义了如何处理CompletableFuture返回的结果，一旦
                //CompletableFuture计算得到结果，它就返回一个CompletableFuture<Void>。所以，map
                //操作返回的是一个Stream<CompletableFuture<Void>>。对这个<CompletableFuture-
                //<Void>>对象，你能做的事非常有限，只能等待其运行结束，不过这也是你所期望的。你还希望
                //能给最慢的商店一些机会，让它有机会打印输出返回的价格。为了实现这一目的，你可以把构成
                //Stream的所有CompletableFuture<Void>对象放到一个数组中，等待所有的任务执行完成，
                //代码如下所示。
                long start = System.nanoTime();
                CompletableFuture[] futures = findPricesStream("myPhone")
                        .map(f -> f.thenAccept(System.out::println))
                        .toArray(size -> new CompletableFuture[size]);

                CompletableFuture.allOf(futures).join();
                System.out.println("All shops have now responded in "
                        + ((System.nanoTime() - start) / 1_000_000) + " msecs");

                //allOf工厂方法接收一个由CompletableFuture构成的数组，数组中的所有CompletableFuture对象执行完成之后，它返回一个CompletableFuture<Void>对象。这意味着，如果你需
                //要等待最初Stream中的所有 CompletableFuture对象执行完毕，对 allOf方法返回的
                //CompletableFuture执行join操作是个不错的主意。这个方法对“最佳价格查询器”应用也是
                //有用的，因为你的用户可能会困惑是否后面还有一些价格没有返回，使用这个方法，你可以在执
                //行完毕之后打印输出一条消息“All shops returned results or timed out”。
                //然而在另一些场景中，你可能希望只要CompletableFuture对象数组中有任何一个执行完
                //毕就不再等待，比如，你正在查询两个汇率服务器，任何一个返回了结果都能满足你的需求。在
                //这种情况下，你可以使用一个类似的工厂方法anyOf。该方法接收一个CompletableFuture对象
                //构成的数组，返回由第一个执行完毕的CompletableFuture对象的返回值构成的CompletableFuture<Object>。
            }

        }

        /**
         * 11.5.2 付诸实践
         */
        public static class Jia1152 {

            public static void main(String[] args) {
                //以通过代码清单11-19中的randomDelay方法模拟
                //远程方法调用，产生一个介于0.5秒到2.5秒的随机延迟，不再使用恒定1秒的延迟值。代码清单
                //11-21应用了这一改变，执行这段代码你会看到不同商店的价格不再像之前那样总是在一个时刻
                //返回，而是随着商店折扣价格返回的顺序逐一地打印输出。为了让这一改变的效果更加明显，我
                //们对代码进行了微调，在输出中打印每个价格计算所消耗的时间：
                long start = System.nanoTime();
                CompletableFuture[] futures = Jia1151.findPricesStream("myPhone27S")
                        .map(f -> f.thenAccept(
                                s -> System.out.println(s + " (done in " +
                                        ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                        .toArray(size -> new CompletableFuture[size]);
                CompletableFuture.allOf(futures).join();
                System.out.println("All shops have now responded in "
                        + ((System.nanoTime() - start) / 1_000_000) + " msecs");
            }
        }

    }

}
