package cn.locusc.java8action.chapter8Efficient;

import cn.locusc.java8action.domain.Apple;
import cn.locusc.java8action.domain.Dish;
import cn.locusc.java8action.domain.Point;
import cn.locusc.java8action.domain.Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static cn.locusc.java8action.chapter2.PracticeClass.filter;
import static java.util.stream.Collectors.toList;

/**
 * @author Jay
 * 本章内容
 * 如何使用Lambda表达式重构代码
 *  Lambda表达式对面向对象的设计模式的影响
 *  Lambda表达式的测试
 *  如何调试使用Lambda表达式和Stream API的代码
 *
 * 小结
 * 下面回顾一下这一章的主要内容。
 *  Lambda表达式能提升代码的可读性和灵活性。
 *  如果你的代码中使用了匿名类，尽量用Lambda表达式替换它们，但是要注意二者间语义
 * 的微妙差别，比如关键字this，以及变量隐藏。
 *  跟Lambda表达式比起来，方法引用的可读性更好 。
 *  尽量使用Stream API替换迭代式的集合处理。
 *  Lambda表达式有助于避免使用面向对象设计模式时容易出现的僵化的模板代码，典型的
 * 比如策略模式、模板方法、观察者模式、责任链模式，以及工厂模式。
 *  即使采用了Lambda表达式，也同样可以进行单元测试，但是通常你应该关注使用了
 * Lambda表达式的方法的行为。
 *  尽量将复杂的Lambda表达式抽象到普通方法中。
 *  Lambda表达式会让栈跟踪的分析变得更为复杂。
 *  流提供的peek方法在分析Stream流水线时，能将中间变量的值输出到日志中，是非常有
 * 用的工具。
 * 2021/11/25
 */
public class ActionClass {

    /**
     * 8.1 为改善可读性和灵活性重构代码
     * 使用lambda表达式 代码会变得更加灵活
     * 采用行为参数化的方式可以应对需求的变化 一句传入的参数动态选择何执行相应的行为
     */
    public static class Jia81 {

        /**
         * 8.1.1 改善代码的可读性
         * Java 8的新特性也可以帮助提升代码的可读性
         *  使用Java 8，你可以减少冗长的代码，让代码更易于理解
         *  通过方法引用和Stream API，你的代码会变得更直观
         *
         * 利用Lambda表达式、方法引用以及Stream改善程序代码的可读性
         *  重构代码，用Lambda表达式取代匿名类
         *  用方法引用重构Lambda表达式
         *  用Stream API重构命令式的数据处理
         */
        public static class Jia811 {

        }

        /**
         * 8.1.2 从匿名类到 Lambda 表达式的转换
         */
        public static class Jia812 {

            interface Task {
                public void execute();
            }

            public static void doSomething(Runnable r) {
                r.run();
            }

            public static void doSomething(Task a) {
                a.execute();
            }

            public static void main(String[] args) {
                // 传统的方式使用匿名类
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Hello");
                    }
                };

                // 新的方式 使用lambda表达式
                Runnable r2 = () -> System.out.println("Hello");

                // 匿名类转换为Lambda表达式可能是一个比较复杂的过程
                // 首先匿名类何lambda表达式中的this和super的含义是不同的
                // 在匿名类中 this代表的是类自身
                // 在lambda中 代表是的包含类
                // 并且匿名类可以屏蔽包含类的变量而lambda表达式不能(编译错误)
                int a = 10;
                Runnable r3 = () -> {
                    // int a = 2; 编译报错
                    System.out.println(a);
                };

                Runnable r4 = new Runnable() {
                    @Override
                    public void run() {
                        int a = 2;
                        System.out.println(a);
                    }
                };

                // 在涉及重载的上下文里 将匿名类转换为Lambda表达式可能导致最终的代码更加晦涩，
                // 实际上，匿名类的类型是在初始化时确定的，而Lambda的类型取决于它的上下文
                // 假设你用与Runnable同样的签名声明了一个函数接口，我们称之为Task
                doSomething(new Task() {
                    @Override
                    public void execute() {
                        System.out.println("Danger danger!!");
                    }
                });

                // 但是这种匿名类转换为lambda表达式时，导致了一种晦涩的方法调用
                // 因为Runnable和Task都是合法的目标类型
                // doSomething(Runnable) 和doSomething(Task)都匹配该类型
                // doSomething(() -> System.out.println("Danger danger!!"));

                // 使用显式的类型转换来解决这种模棱两可的情况
                doSomething((Task) () -> System.out.println("Danger danger!!"));

                // NetBeans和IntelliJ都支持这种重构，它们能自动地帮你检查，避免发生这些问题
            }
        }

        /**
         * 8.1.3 从 Lambda 表达式到方法引用的转换
         * lambda表达式式非常适用于需要传递代码片段的场景
         * 为了改善代码的可读性，尽量使用方法引用。因为方法名往往能更直观地表达代码的意图
         */
        public static class Jia813 {

            public static void main(String[] args) {
                List<Dish> menu = Dish.getOfficial();
                Map<Dish.CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream()
                    .collect(
                        Collectors.groupingBy(dish -> {
                            if (dish.getCalories() <= 400) return Dish.CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return Dish.CaloricLevel.NORMAL;
                            else return Dish.CaloricLevel.FAT;
                        })
                    );

                // 将lambda表达式的内容抽取到一个单独的方法中
                // 将其作为参数传递给groupingBy方法
                // 变换之后，代码变得更加简洁，程序的意图也更加清晰
                Map<Dish.CaloricLevel, List<Dish>> dishesByCaloricLevelFunc = menu
                    .stream()
                    .collect(Collectors.groupingBy(Dish::getCaloricLevel));

                // 还应该尽量考虑使用静态辅助方法 比如comparing, maxBy
                // 这些方法设计之初考虑了会结合方法引用一起使用
                List<Apple> inventory = Apple.getApples();
                // 这里需要考虑如何实现比较算法
                inventory.stream()
                    .sorted((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
                // 优化过的代码更清晰地表达了它的设计意图 来就像问题描述，非常清晰
                inventory.stream()
                    .sorted(Comparator.comparingInt(Apple::getWeight));


                // 很多通用的归约操作比如sum、maximum，都有内建的辅助方法可以和方法引用结合使用
                // 使用Collectors接口可以轻松得到和或者最大值 与采用Lambada表达式和底层的归约操作比起来直观得多
                menu.stream().map(Dish::getCalories)
                    .reduce(0, (c1, c2) -> c1 + c2);
                // 使用内置集合类 更清晰地表达问题陈述是什么
                menu.stream().collect(Collectors.summingInt(Dish::getCalories));
            }

        }

        /**
         * 8.1.4 从命令式的数据处理切换到 Stream
         * 建议将所有使用迭代器这种数据处理模式处理集合的代码都转换成Stream API的方式
         * Stream API能更清晰地表达数据处理管道的意图
         * 通过短路和延迟载入以及利用第7章介绍的现代计算机的多核架构，我们可以对Stream进行优化
         */
        public static class Jia814 {

            public static void main(String[] args) {
                // 下面的命令式代码使用了两种模式：筛选和抽取这两种模式被混在了一起
                // 迫使程序员必须彻底搞清楚程序的每个细节才能理解代码的功能
                // 实现需要并行运行的程序所面对的困难也多得多
                List<Dish> menu = Dish.getOfficial();
                List<String> dishNames = new ArrayList<>();
                for (Dish dish : menu) {
                    if(dish.getCalories() > 300) {
                        dishNames.add(dish.getName());
                    }
                }

                // 替代方案使用Stream API，这种方式编写的代码读起来更像是问题陈述，并行化也非常容易
                menu.parallelStream()
                    .filter(d -> d.getCalories() > 300)
                    .map(Dish::getName)
                    .collect(toList());

                // 将命令式的代码结构转换为Stream API的形式是个困难的任务
                // 你需要考虑控制流语句，比如break、continue、return，并选择使用恰当的流操作
            }
        }

        /**
         * 8.1.5 增加代码的灵活性
         * Lambda表达式有利于行为参数化
         * 可以使用不同的Lambda表示不同的行为 并将它们作为参数传递给函数去处理执行
         * 这种方式可以帮助我们淡定从容地面对需求的变化
         * 可以用多种方式为Predicate创建筛选条件 或者使用Comparator对多种对象进行比较
         */
        public static class Jia815 {

            private static String generateDiagnostic() {
                return null;
            }

            // 使用Lambda表达式的函数接口，该接口能够抛出一个IOException
            public interface BufferedReaderProcessor {
                String process(BufferedReader b) throws IOException;
            }

            // 将BufferedReaderProcessor作为执行参数传入
            public static String processFile(BufferedReaderProcessor p) throws IOException {
                try(BufferedReader br = new BufferedReader(new FileReader("java8inaction/chap8/data.txt"))) {
                    return p.process(br);
                }
            }

            public static void main(String[] args) throws IOException {
                // 1 采用函数接口
                // 可以依照这两种模式重构代码 延迟执行和环绕执行

                // 2. 有条件的延迟执行
                // 控制语句被混杂在业务逻辑代码之中
                // 日志器的状态（它支持哪些日志等级）通过isLoggable方法暴露给了客户端代码。
                // 为什么要在每次输出一条日志之前都去查询日志器对象的状态？这只能搞砸你的代码。
                Logger logger = Logger.getLogger("cn.locusc.java8action.chapter8Efficient.ActionClass");
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("Problem: " + generateDiagnostic());
                }
                // 更好的方案是使用log方法，该方法在输出日志消息之前，会在内部检查日志对象是否已经设置为恰当的日志等级
                // 但是日志消息的输出与否每次都需要判断 即使已经传递了参数，不开启日志
                logger.log(Level.FINER, "Problem: " + generateDiagnostic());
                // 延迟消息构造 日志就只会在某些特定的情况下才开启
                logger.log(Level.FINER, () -> "Problem: " + generateDiagnostic());

                // 3. 环绕执行
                // 业务代码千差万别，但是它们拥有同样的准备和清理阶段
                // 好处是可以重用准备和清理阶段的逻辑，减少重复冗余的代码
                String oneLine =
                    // 传入一个Lambda表达式
                    processFile((BufferedReader b) -> b.readLine());

                String twoLines =
                    // 传入另一个 Lambda表达式
                    processFile((BufferedReader b) -> b.readLine() + b.readLine());
            }
        }
    }

    /**
     * 8.2 使用 Lambda 重构面向对象的设计模式
     * 使用Lambda表达式后，很多现存的略显臃肿的面向对象设计模式能够用更精简的方式实现了
     *  策略模式
     *  模板方法
     *  观察者模式
     *  责任链模式
     *  工厂模式
     */
    public static class Jia82 {

        /**
         * 8.2.1 策略模式
         * 策略模式代表了解决一类算法的通用解决方案，你可以在运行时选择使用哪种方案
         * 可以应用在使用不同的标准来验证输入的有效性，使用不同的方式来分析或者格式化输入
         *
         * 策略模式包含三部分内容，如图8-1所示。
         *  一个代表某个算法的接口（它是策略模式的接口）。
         *  一个或多个该接口的具体实现，它们代表了算法的多种实现（比如，实体类ConcreteStrategyA或者ConcreteStrategyB）。
         *  一个或多个使用策略对象的客户。
         */
        public static class Jia821 {

            public interface ValidationStrategy {
                boolean execute(String s);
            }

            public static class IsAllLowerCase implements ValidationStrategy {
                @Override
                public boolean execute(String s) {
                    return s.matches("[a-z]+");
                }
            }

            public static class IsNumeric implements ValidationStrategy {
                @Override
                public boolean execute(String s){
                    return s.matches("\\d+");
                }
            }

            public static void main(String[] args) {
                // 假设你希望验证输入的内容是否根据标准进行了恰当的格式化（比如只包含小写字母或数字）。
                // 验证策略
                Validator numericValidator = new Validator(new IsNumeric());
                boolean b1 = numericValidator.validate("aaa");

                Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
                boolean b2 = lowerCaseValidator.validate("bbbb");


                // 使用lambda表达式
                // Lambda表达式避免了采用策略设计模式时僵化的模板代码
                // ValidationStrategy还与Predicate<String>具有同样的函数描述
                Validator numericValidatorLambda =
                    new Validator((String s) -> s.matches(("[a-z]+")));
                boolean b3 = numericValidatorLambda.validate("aaaa");

                Validator lowerCaseValidatorLambda =
                    new Validator((String s) -> s.matches("\\d+"));
            }
        }

        /**
         * 8.2.2 模板方法
         * 如果需要采用某个算法的框架，同时又希望有一定的灵活度，能对它的某些部分进行改进
         * 采用模板方法设计模式是比较通用的方案
         */
        public static class Jia822 {

            public static class Customer { }

            public static class Database {
                public static Customer getCustomerWithId(int id) {
                    return new Customer();
                }
            }

            abstract static class OnlineBanking {

                public void processCustomer(int id) {
                    Customer c = Database.getCustomerWithId(id);
                    makeCustomerHappy(c);
                }

                abstract void makeCustomerHappy(Customer c);

            }

            static class OnlineBankingLambda {

                public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
                    Customer c = Database.getCustomerWithId(id);
                    makeCustomerHappy.accept(c);
                }

            }

            public static void main(String[] args) {
                //假设你需要编写一个简单的在线银行
                //应用。通常，用户需要输入一个用户账户，之后应用才能从银行的数据库中得到用户的详细信息，
                //最终完成一些让用户满意的操作。不同分行的在线银行应用让客户满意的方式可能还略有不同，
                //比如给客户的账户发放红利，或者仅仅是少发送一些推广文件。

                //processCustomer方法搭建了在线银行算法的框架：获取客户提供的ID，然后提供服务让
                //用户满意。不同的支行可以通过继承OnlineBanking类，对该方法提供差异化的实现。

                // 使用lambda表达式
                new OnlineBankingLambda().processCustomer(1337, System.out::println);
            }
        }

        /**
         * 8.2.3 观察者模式
         * 某些事件发生时（比如状态转变），如果一个对象（通常我们称之为主题）
         * 需要自动地通知其他多个对象（称为观察者），就会采用该方案。
         *
         * 假如你需要为Twitter这样的应用设计并
         * 实现一个定制化的通知系统。想法很简单：好几家报纸机构，比如《纽约时报》《卫报》以及《世
         * 界报》都订阅了新闻，他们希望当接收的新闻中包含他们感兴趣的关键字时，能得到特别通知
         */
        public static class Jia823 {

            // 需要一个观察者接口，它将不同的观察者聚合在一起。
            // 仅有一个名为notify的方法，一旦接收到一条新的新闻，该方法就会被调用
            interface Observer {
                void notify(String tweet);
            }

            interface Subject{
                // 使用registerObserver方法可以注册一个新的观察者
                void registerObserver(Observer o);

                void notifyObservers(String tweet);
            }

            /**
             * 声明不同的观察者（比如，这里是三家不同的报纸机构），
             * 依据新闻中不同的关键字分别定义不同的行为
             */
            static class NYTimes implements Observer{
                public void notify(String tweet) {
                    if(tweet != null && tweet.contains("money")){
                        System.out.println("Breaking news in NY! " + tweet);
                    }
                }
            }
            static class Guardian implements Observer{
                public void notify(String tweet) {
                    if(tweet != null && tweet.contains("queen")){
                        System.out.println("Yet another news in London... " + tweet);
                    }
                }
            }
            static class LeMonde implements Observer{
                public void notify(String tweet) {
                    if(tweet != null && tweet.contains("wine")){
                        System.out.println("Today cheese, wine and news! " + tweet);
                    }
                }
            }

            /**
             * Subject使用registerObserver方法可以注册一个新的观察者，使用notifyObservers
             * 方法通知它的观察者一个新闻的到来。
             */
            static class Feed implements Subject {

                private final List<Observer> observers = new ArrayList<>();

                @Override
                public void registerObserver(Observer o) {
                    this.observers.add(o);
                }

                @Override
                public void notifyObservers(String tweet) {
                    observers.forEach(o -> o.notify(tweet));
                }
            }

            public static void main(String[] args) {
                Feed f = new Feed();
                f.registerObserver(new NYTimes());
                f.registerObserver(new Guardian());
                f.registerObserver(new LeMonde());
                f.notifyObservers("The queen said her favourite book is Java 8 in Action!");

                // 使用lambda表达式
                //那么，是否我们随时随地都可以使用Lambda表达式呢？答案是否定的！我们前文介绍的例
                //子中，Lambda适配得很好，那是因为需要执行的动作都很简单，因此才能很方便地消除僵化代
                //码。但是，观察者的逻辑有可能十分复杂，它们可能还持有状态，抑或定义了多个方法，诸如此
                //类。在这些情形下，你还是应该继续使用类的方式。
                f.registerObserver((String tweet) -> {
                    if(tweet != null && tweet.contains("money")){
                        System.out.println("Breaking news in NY! " + tweet);
                    }
                });
                f.registerObserver((String tweet) -> {
                    if(tweet != null && tweet.contains("queen")){
                        System.out.println("Yet another news in London... " + tweet);
                    }
                });
            }
        }

        /**
         * 8.2.4 责任链模式
         * 一种创建处理对象序列（比如操作序列）的通用方案
         * 一个处理对象可能需要在完成一些工作之后，将结果传递给另一个对象，
         * 这个对象接着做一些工作，再转交给下一个处理对象，以此类推。
         */
        public static class Jia824 {

            public abstract static class ProcessingObject<T> {
                protected ProcessingObject<T> successor;

                public ProcessingObject<T> setSuccessor(ProcessingObject<T> successor) {
                    this.successor = successor;
                    return this;
                }

                public T handle(T input){
                    T r = handleWork(input);
                    if(successor != null){
                        return successor.handle(r);
                    }
                    return r;
                }
                abstract protected T handleWork(T input);
            }

            public static class HeaderTextProcessing extends ProcessingObject<String> {
                public String handleWork(String text){
                    return "From Raoul, Mario and Alan: " + text;
                }
            }

            public static class SpellCheckerProcessing extends ProcessingObject<String> {
                public String handleWork(String text){
                    return text.replaceAll("labda", "lambda");
                }
            }

            public static class JayProcessing extends ProcessingObject<String> {
                public String handleWork(String text){
                    return text + "jay";
                }
            }

            public static void main(String[] args) {
                ProcessingObject<String> p1 = new HeaderTextProcessing();
                ProcessingObject<String> p2 = new SpellCheckerProcessing();
                ProcessingObject<String> p3 = new JayProcessing();
                p1.setSuccessor(p2.setSuccessor(p3));

                String result = p1.handle("Aren't labdas really sexy?!!");
                System.out.println(result);

                // 使用Lambda表达式
                UnaryOperator<String> headerProcessing =
                    (String text) -> "From Raoul, Mario and Alan: " + text;

                UnaryOperator<String> spellCheckerProcessing =
                    (String text) -> text.replaceAll("labda", "lambda");

                UnaryOperator<String> jayCheckerProcessing =
                    (String text) -> text + "   JAY";

                Function<String, String> pipeline = headerProcessing
                    .andThen(spellCheckerProcessing)
                    .andThen(jayCheckerProcessing);

                // 将方法结合起来，结果就是一个操作链
                String apply = pipeline.apply("Aren't labdas really sexy?!!");

                System.out.println(apply);
            }
        }

        /**
         * 8.2.5 工厂模式
         * 无需向客户暴露实例化的逻辑就能完成对象的创建
         */
        public static class Jia825 {

            public static class Product { }

            public static class Loan extends Product{ }

            public static class Stock extends Product{ }

            public static class Bond extends Product{ }
            
            /**
             * 假定你为一家银行工作
             * 需要一种方式创建不同的金融产品：贷款、期权、股票，等等
             */
            public static class ProductFactory {

                /**
                 * 这里贷款（Loan）、股票（Stock）和债券（Bond）都是产品（Product）的子类。
                 * createProduct方法可以通过附加的逻辑来设置每个创建的产品。但是带来的好处也显而易
                 * 见，你在创建对象时不用再担心会将构造函数或者配置暴露给客户
                 */
                public static Product createProduct(String name){
                    switch(name){
                        case "loan": return new Loan();
                        case "stock": return new Stock();
                        case "bond": return new Bond();
                        default: throw new RuntimeException("No such product " + name);
                    }
                }

            }

            final static HashMap<String, Supplier<Product>> map = new HashMap<>();

            static {
                map.put("loan", Loan::new);
                map.put("stock", Stock::new);
                map.put("bond", Bond::new);
            }

            public static Product createProduct(String name){
                Supplier<Product> p = map.get(name);
                if(p != null) return p.get();
                throw new IllegalArgumentException("No such product " + name);
            }

            //我们假设你希望保存具有三个参数（两个参数为Integer类型，一个参数为String
            //类型）的构造函数；为了完成这个任务，你需要创建一个特殊的函数接口TriFunction。最终
            //的结果是Map变得更加复杂
            public interface TriFunction<T, U, V, R>{
                R apply(T t, U u, V v);
            }

            static Map<String, TriFunction<Integer, Integer, String, Product>> map3
                = new HashMap<>();

            public static void main(String[] args) {
                Product p = ProductFactory.createProduct("loan");
                // 使用Lambda表达式
                // 引用方法一样引用构造函数。下面就是一个引用贷款（Loan）构造函数的示例
                Supplier<Product> loanSupplier = Loan::new;
                Loan loan = (Loan) loanSupplier.get();

                // 重构之前的代码，创建一个Map，将产品名映射到对应的构造函数
                // 现在，你可以像之前使用工厂设计模式那样，利用这个Map来实例化不同的产品
                Product product = createProduct("loan");

                // 使用Java 8中的新特性达到了传统工厂模式同样的效果
                // 如果工厂方法createProduct需要接收多个传递给产品构造方法的参数，这种方式的扩展性不是很
                //好。你不得不提供不同的函数接口，无法采用之前统一使用一个简单接口的方式
            }

        }
    }

    /**
     * 8.3 测试 Lambda 表达式
     * 大多数时候，我们受雇进行的程序开发工作的要求并不是编写优美的代码，而是编写正确的代码。
     */
    public static class Jia83 {

        // 单元测试会检查moveRightBy方法的行为是否与预期一致
        // @Test
        public void testMoveRightBy() throws Exception {
            Point p1 = new Point(5, 5);
            Point p2 = p1.moveRightBy(10);
            // assertEquals(15, p2.getX());
            // assertEquals(5, p2.getY());
        }

        /**
         * 8.3.1 测试可见 Lambda 函数的行为
         * 可以借助某个字段访问Lambda函数，这种情况，你可以利用这些字段，通过
         * 它们对封装在Lambda函数内的逻辑进行测试
         */
        public static class Jia831 {


            // Lambda表达式会生成函数接口的一个实例
            // 你可以测试该实例的行为
            // @Test
            public static void testComparingTwoPoints() {
                Point p1 = new Point(10, 15);
                Point p2 = new Point(10, 20);
                int result = Point.compareByXAndThenY.compare(p1, p2);
                // assertEquals(-1, result);
            }

            public static void main(String[] args) {
                testComparingTwoPoints();
            }

        }

        /**
         * 8.3.2 测试使用 Lambda 的方法的行为
         * Lambda的初衷是将一部分逻辑封装起来给另一个方法使用
         * 不应该将Lambda表达式声明为public 它们仅是具体的实现细节
         * 相反 我们需要对使用Lambda表达式的方法进行测试
         */
        public static class Jia832 {

            // 没必要对Lambda表达式p -> new Point(p.getX() + x,p.getY())进行测试
            // 它只是moveAllPointsRightBy内部的实现细节
            // 更应该关注的是方法moveAllPointsRightBy的行为

            // 单元测试中，Point类恰当地实现equals方法非常重要，否则该测试的结果
            // 就取决于Object类的默认实现
            // @Test
            public static void testMoveAllPointsRightBy() throws Exception{
                List<Point> points =
                    Arrays.asList(new Point(5, 5), new Point(10, 5));
                List<Point> expectedPoints =
                    Arrays.asList(new Point(15, 5), new Point(20, 5));
                List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
                // assertEquals(expectedPoints, newPoints);
            }
        }

        /**
         * 8.3.3 将复杂的 Lambda 表达式分到不同的方法
         * 可能你会碰到非常复杂的Lambda表达式，包含大量的业务逻辑，比如需要处理复杂情况的
         * 定价算法。你无法在测试程序中引用Lambda表达式，这种情况该如何处理呢？一种策略是将
         * Lambda表达式转换为方法引用（这时你往往需要声明一个新的常规方法），我们在8.1.3节详细讨
         * 论过这种情况。
         */
        public static class Jia833 { }

        /**
         * 8.3.4 高阶函数的测试
         * 接受函数作为参数的方法或者返回一个函数的方法（所谓的“高阶函数”，higher-order
         * function，我们在第14章会深入展开介绍）更难测试。如果一个方法接受Lambda表达式作为参数，
         * 你可以采用的一个方案是使用不同的Lambda表达式对它进行测试。比如，你可以使用不同的谓
         * 词对第2章中创建的filter方法进行测试
         */
        public static class Jia834 {

            // @Test
            //如果被测试方法的返回值是另一个方法，可以仿照我们之前处理
            //Comparator的方法，把它当成一个函数接口，对它的功能进行测试。
            public void testFilter() throws Exception{
                List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
                List<Integer> even = filter(numbers, i -> i % 2 == 0);
                List<Integer> smallerThanThree = filter(numbers, i -> i < 3);
                // assertEquals(Arrays.asList(2, 4), even);
                // assertEquals(Arrays.asList(1, 2), smallerThanThree);
            }

        }


    }

    /**
     * 8.4 高阶函数的测试
     *  查看栈跟踪
     *  输出日志
     */
    public static class Jia84 {

        /**
         * 8.4.1 查看栈跟踪
         * 程序突然停止运行（比如突然抛出一个异常），这时你首先要调查程序在什么地方发生
         * 了异常以及为什么会发生该异常。这时栈帧就非常有用。程序的每次方法调用都会产生相应的调
         * 用信息，包括程序中方法调用的位置、该方法调用使用的参数、被调用方法的本地变量。这些信
         * 息被保存在栈帧上。
         *
         * 程序失败时，你会得到它的栈跟踪，通过一个又一个栈帧，你可以了解程序失败时的概略信
         * 息。换句话说，通过这些你能得到程序失败时的方法调用列表
         */
        public static class Jia841 {

            public static int divideByZero(int n){
                return n / 0;
            }

            public static void main(String[] args) {
                //Lambda表达式和栈跟踪
                //不幸的是，由于Lambda表达式没有名字，它的栈跟踪可能很难分析。
                //在下面这段简单的代码中，我们刻意地引入了一些错误

                // at Debugging.lambda$main$0(Debugging.java:6)
                // at Debugging$$Lambda$5/284720968.apply(Unknown Source)
                // 由于Lambda表达式没有名字，所以编译器只能为
                // 如果你使用了大量的类，其中又包含多个Lambda表达式，这就成了一个非常头痛的问题
                List<Point> points = Arrays.asList(new Point(12, 2), null);
                // points.stream().map(p -> p.getX()).forEach(System.out::println);

                // points.stream().map(Point::getX).forEach(System.out::println);

                // 如果方法引用指向的是同一个类中声明的方法，那么它的名称是可以在栈跟踪中显示
                // Exception in thread "main" java.lang.ArithmeticException: / by zero
                //	at cn.locusc.java8action.chapter8Efficient.ActionClass$Jia84$Jia841.divideByZero(ActionClass.java:736)
                List<Integer> numbers = Arrays.asList(1, 2, 3);
                numbers.stream().map(Jia841::divideByZero).forEach(System
                    .out::println);
            }

        }

        /**
         * 8.4.2 使用日志调试
         * 假设你试图对流操作中的流水线进行调试
         * 使用forEach将流操作的结果日志输出到屏幕上或者记录到日志文件中
         */
        public static class Jia842 {

            public static void main(String[] args) {
                List<Integer> numbers = Arrays.asList(2, 3, 4, 5);

                //一旦调用forEach，整个流就会恢复运行。到底哪种方式能更有效地帮助我们理
                //解Stream流水线中的每个操作（比如map、filter、limit）产生的输出
                numbers.stream()
                    .map(x -> x + 17)
                    .filter(x -> x % 2 == 0)
                    .limit(3)
                    .forEach(System.out::println);

                // peek的设计初衷就是在流的每个元素恢复运行之前，插入执行一个动作
                // 它不像forEach那样恢复整个流的运行
                // 而是在一个元素上完成操作之后，它只会将操作顺承到流水线中的下一个操作
                List<Integer> result =
                    numbers.stream()
                        // 输出来自数据源的当前元素值
                        .peek(x ->  System.out.println("from stream: " + x))
                        .map(x -> x + 17)
                        // 输 出 map 操作的结果
                        .peek(x -> System.out.println("after map: " + x))
                        .filter(x -> x % 2 == 0)
                        // 输出经过filter操作之后，剩下的元素个数
                        .peek(x -> System.out.println("after filter: " + x))
                        .limit(3)
                        // 输出经过limit操作之后，剩下的元素个数
                        .peek(x -> System.out.println("after limit: " + x))
                        .collect(toList());
            }

        }

    }


}
