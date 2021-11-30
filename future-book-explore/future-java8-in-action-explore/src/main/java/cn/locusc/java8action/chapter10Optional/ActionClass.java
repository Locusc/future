package cn.locusc.java8action.chapter10Optional;

import lombok.Data;

import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Jay
 * 本章内容
 *  null引用引发的问题，以及为什么要避免null引用
 *  从null到Optional：以null安全的方式重写你的域模型
 *  让Optional发光发热： 去除代码中对null的检查
 *  读取Optional中可能值的几种方法
 *  对可能缺失值的再思考
 *
 * 小结
 *  null引用在历史上被引入到程序设计语言中，目的是为了表示变量值的缺失。
 *  Java 8中引入了一个新的类java.util.Optional<T>，对存在或缺失的变量值进行
 * 建模。
 *  你可以使用静态工厂方法Optional.empty、Optional.of以及Optional.ofNullable创建Optional对象。
 *  Optional类支持多种方法，比如map、flatMap、filter，它们在概念上与Stream类
 * 中对应的方法十分相似。
 *  使用Optional会迫使你更积极地解引用Optional对象，以应对变量值缺失的问题，最
 * 终，你能更有效地防止代码中出现不期而至的空指针异常。
 *  使用Optional能帮助你设计更好的API，用户只需要阅读方法签名，就能了解该方法是
 * 否接受一个Optional类型的值。
 * 2021/11/29
 */
public class ActionClass {
    // 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

    /**
     * 10.1 如何为缺失的值建模
     * 假设你需要处理下面这样的嵌套对象，这是一个拥有汽车及汽车保险的客户。
     */
    public static class Jia101 {

        public class Person {
            private Car car;
            public Car getCar() { return car; }
        }
        public class Car {
            private Insurance insurance;
            public Insurance getInsurance() { return insurance; }
        }
        public class Insurance {
            private String name;
            public String getName() { return name; }
        }

        // 如果返回的person值为null会怎样？
        // 如果getInsurance的返回值也是null，结果又会怎样？
        public String getCarInsuranceName(Person person) {
            return person.getCar().getInsurance().getName();
        }

        /**
         * 10.1.1 采用防御式检查减少 NullPointerException
         */
        public static class Jia1011 {

            // 深层质疑
            //原因是它不断重复着一种模式：每次你不确定一
            //个变量是否为null时，都需要添加一个进一步嵌套的if块，也增加了代码缩进的层数。很明显，
            //这种方式不具备扩展性，同时还牺牲了代码的可读性
            public String getCarInsuranceName(Person person) {
                // 每个null检查都会增加调用链上剩余代码的嵌套层数
                if (person != null) {
                    Car car = person.getCar();
                    if (car != null) {
                        Insurance insurance = car.getInsurance();
                        if (insurance != null) {
                            return insurance.getName();
                        }
                    }
                }
                return "Unknown";
            }

            // 使得代码的维护异常艰难 这种流程是极易出错
            // 如果你忘记检查了那个可能为null的属性会怎样
            public String getCarInsuranceName2(Person person) {
                // 每个null检查都会添加新的退出点
                if (person == null) {
                    return "Unknown";
                }
                Car car = person.getCar();
                if (car == null) {
                    return "Unknown";
                }
                Insurance insurance = car.getInsurance();
                if (insurance == null) {
                    return "Unknown";
                }
                return insurance.getName();
            }
        }

        /**
         * 10.1.2 null 带来的种种问题
         *  它是错误之源。
         * NullPointerException是目前Java程序开发中最典型的异常。
         *  它会使你的代码膨胀。
         * 它让你的代码充斥着深度嵌套的null检查，代码的可读性糟糕透顶。
         *  它自身是毫无意义的。
         * null自身没有任何的语义，尤其是，它代表的是在静态类型语言中以一种错误的方式对
         * 缺失变量值的建模。
         *  它破坏了Java的哲学。
         * Java一直试图避免让程序员意识到指针的存在，唯一的例外是：null指针。
         *  它在Java的类型系统上开了个口子。
         * null并不属于任何类型，这意味着它可以被赋值给任意引用类型的变量。这会导致问题，
         * 原因是当这个变量被传递到系统中的另一个部分后，你将无法获知这个null变量最初的
         * 赋值到底是什么类型。
         */
        public static class Jia1012 {

        }

        /**
         * 10.1.3 其他语言中 null 的替代品
         * Groovy，通过引入安全导航操作符（Safe Navigation Operator，标记为?）可以安全访问可能为null的变量
         * Haskell中包含了一个Maybe类型，它本质上是对optional值的封装
         * Scala有类似的数据结构，名字叫Option[T]，它既可以包含类型为T的变量，也可以不包含该变量，
         */
        public static class Jia1013 {

        }

    }

    /**
     * 10.2 Optional 类入门
     * 汲取Haskell和Scala的灵感，Java 8中引入了一个新的类java.util.Optional<T>。这
     * 是一个封装Optional值的类
     * 举例来说，使用新的类意味着，如果你知道一个人可能有也可能
     * 没有车，那么Person类内部的car变量就不应该声明为Car，遭遇某人没有车时把null引用赋值
     * 给它，而是应该像图10-1那样直接将其声明为Optional<Car>类型。
     */
    public static class Jia102 {
        //变量存在时，Optional类只是对类简单封装。变量不存在时，缺失的值会被建模成一个“空”
        //的Optional对象，由方法Optional.empty()返回。Optional.empty()方法是一个静态工厂
        //方法，它返回Optional类的特定单一实例
        public class Person {
            // 人可能有车，也可能没有车，因此将这个字段声明为Optional
            private Optional<Car> car;
            public Optional<Car> getCar() { return car; }
        }
        public class Car {
            // 车可能进行了保险，也可能没有保险，所以将这个字段声明为Optional
            private Optional<Insurance> insurance;
            public Optional<Insurance> getInsurance() { return insurance; }
        }
        public class Insurance {
            // 保险公司必须有名字
            // 业务上可以判断出不为null值 不需要使用optional
            // 如果出现null引用 需要直接排查问题所在
            private String name;
            public String getName() { return name; }
        }
        public static void main(String[] args) {
            //始终如一地使用Optional，能非常清晰地界定出变量值的缺失是结构上的问
            //题，还是你算法上的缺陷，抑或是你数据中的问题。另外，我们还想特别强调，引入Optional
            //类的意图并非要消除每一个null引用。与此相反，它的目标是帮助你更好地设计出普适的API，
            //让程序员看到方法签名，就能了解它是否接受一个Optional的值。这种强制会让你更积极地将
            //变量从Optional中解包出来，直面缺失的变量值。
        }
    }

    /**
     * 10.3 应用 Optional 的几种模式
     * 已经知道了如何使用Optional类型来声明你的域模型，也
     * 了解了这种方式与直接使用null引用表示变量值的缺失的优劣。但是，我们该如何使用呢？用
     * 这种方式能做什么，或者怎样使用Optional封装的值呢
     */
    public static class Jia103 {
        @Data
        public static class Person {
            private Optional<Car> car;
            private int age;
            public Optional<Car> getCar() { return car; }
        }
        @Data
        public static class Car {
            private Optional<Insurance> insurance;
            public Optional<Insurance> getInsurance() { return insurance; }
        }
        public static class Insurance {
            private String name;
            public String getName() { return name; }
        }

        /**
         * 10.3.1 创建 Optional 对象
         */
        public static class Jia1031 {

            public static void main(String[] args) {
                // 你首先需要学习的是如何创建Optional对象

                // 1. 声明一个空的Optional
                // 你可以通过静态工厂方法Optional.empty，创建一个空的Optional对象
                Optional<Object> optCar = Optional.empty();

                // 2. 依据一个非空值创建Optional
                // 你还可以使用静态工厂方法Optional.of，依据一个非空值创建一个Optional对象：
                // 如果car是一个null，这段代码会立即抛出一个NullPointerException，而不是等到你
                // 试图访问car的属性值时才返回一个错误
                Car car = new Car();
                Optional<Car> optCar1 = Optional.of(car);

                // 3. 可接受null的Optional
                // 使用静态工厂方法Optional.ofNullable，你可以创建一个允许null值的Optional对象
                Optional<Car> car1 = Optional.ofNullable(car);
                //如果car是null，那么得到的Optional对象就是个空对象。
                //你可能已经猜到，我们还需要继续研究“如何获取Optional变量中的值”。尤其是，Optional
                //提供了一个get方法，它能非常精准地完成这项工作，我们在后面会详细介绍这部分内容。不过
                //get方法在遭遇到空的Optional对象时也会抛出异常，所以不按照约定的方式使用它，又会让
                //我们再度陷入由null引起的代码维护的梦魇。因此，我们首先从无需显式检查的Optional值的
                //使用入手，这些方法与Stream中的某些操作极其相似
            }
        }

        /**
         * 10.3.2 使用 map 从 Optional 对象中提取和转换值
         */
        public static class Jia1032 {

            public static void main(String[] args) {
                // 从对象中提取信息是一种比较常见的模式。比如，你可能想要从insurance公司对象中提取
                // 公司的名称。提取名称之前，你需要检查insurance对象是否为null
                Insurance insurance = new Insurance();
                String name = null;
                if(insurance != null) {
                    name = insurance.getName();
                }

                // 为了支持这种模式，Optional提供了一个map方法。它的工作方式如下
                Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
                //与第4章和第5章中看到的流的map方法相差无几。map操作会将提供的
                //函数应用于流的每个元素。你可以把Optional对象看成一种特殊的集合数据，它至多包含一个
                //元素。如果Optional包含一个值，那函数就将该值作为参数传递给map，对该值进行转换。如
                //果Optional为空，就什么也不做
                Optional<String> optInsuranceName = optInsurance.map(Insurance::getName);
            }
        }

        /**
         * 10.3.3 使用 flatMap 链接 Optional 对象
         * public String getCarInsuranceName(Person person) {
         *     return person.getCar().getInsurance().getName();
         * }
         */
        public static class Jia1033 {

            public String getCarInsuranceName(Optional<Person> person) {
                return person.flatMap(Person::getCar)
                        .flatMap(Car::getInsurance)
                        .map(Insurance::getName)
                        // 如果Optional的结果值为空，设置默认值
                        .orElse("Unknown");
            }

            public static void main(String[] args) {
                Person person = new Person();
                Optional<Person> optPerson = Optional.of(person);

                //不幸的是，这段代码无法通过编译。为什么呢？optPerson是Optional<Person>类型的
                //变量， 调用map方法应该没有问题。但getCar返回的是一个Optional<Car>类型的对象（如代
                //码清单10-4所示），这意味着map操作的结果是一个Optional<Optional<Car>>类型的对象。因
                //此，它对getInsurance的调用是非法的，因为最外层的optional对象包含了另一个optional
                //对象的值，而它当然不会支持getInsurance方法
                // Optional<String> name = optPerson.map(Person::getCar)
                        // .map(Car::getInsurance)
                        // .map(Insurance::getName);

                // 图10-4 Stream和Optional的flagMap方法对比
                // 传递给流的flatMap方法会将每个正方形转换为另一个流中的两个三角形。那
                //么，map操作的结果就包含有三个新的流，每一个流包含两个三角形，但flatMap方法会将这种
                //两层的流合并为一个包含六个三角形的单一流。类似地，传递给optional的flatMap方法的函
                //数会将原始包含正方形的optional对象转换为包含三角形的optional对象。如果将该方法传递
                //给map方法，结果会是一个Optional对象，而这个Optional对象中包含了三角形；但flatMap
                //方法会将这种两层的Optional对象转换为包含三角形的单一Optional对象

                // 1. 使用Optional获取car的保险公司名称
                // 使用Optional具有明显的优势。这一次，你可以用非常容易却又普适的方法实现之前你期望的
                //效果——不再需要使用那么多的条件分支，也不会增加代码的复杂性
                // ，它通过类型系统让你的域模型中隐藏的知识显式地体现在
                //你的代码中，换句话说，你永远都不应该忘记语言的首要功能就是沟通，即使对程序设计语言而
                //言也没有什么不同。声明方法接受一个Optional参数，或者将结果作为Optional类型返回，让
                //你的同事或者未来你方法的使用者，很清楚地知道它可以接受空值，或者它可能返回一个空值

                // 2. 使用Optional解引用串接的Person/Car/Insurance对象
                //返回的Optional可能是两种情况：如果调用链上的任何一个方法返回一个
                //空的Optional，那么结果就为空，否则返回的值就是你期望的保险公司的名称。那么，你如何
                //读出这个值呢？毕竟你最后得到的这个对象还是个Optional<String>，它可能包含保险公司的
                //名称，也可能为空。代码清单10-5中，我们使用了一个名为orElse的方法，当Optional的值为
                //空时，它会为其设定一个默认值。除此之外，还有很多其他的方法可以为Optional设定默认值，
                //或者解析出Optional代表的值

                // 在域模型中使用Optional，以及为什么它们无法序列化
                //在代码清单10-4中，我们展示了如何在你的域模型中使用Optional，将允许缺失或者暂
                //无定义的变量值用特殊的形式标记出来。然而，Optional类设计者的初衷并非如此，他们构
                //思时怀揣的是另一个用例。这一点，Java语言的架构师Brian Goetz曾经非常明确地陈述过，
                //Optional的设计初衷仅仅是要支持能返回Optional对象的语法。
                //由于Optional类设计时就没特别考虑将其作为类的字段使用，所以它也并未实现
                //Serializable接口。由于这个原因，如果你的应用使用了某些要求序列化的库或者框架，在
                //域模型中使用Optional，有可能引发应用程序故障。然而，我们相信，通过前面的介绍，你
                //已经看到用Optional声明域模型中的某些类型是个不错的主意，尤其是你需要遍历有可能全
                //部或部分为空，或者可能不存在的对象时。如果你一定要实现序列化的域模型，作为替代方案，
                //我们建议你像下面这个例子那样，提供一个能访问声明为Optional、变量值可能缺失的接口，
                //代码清单如下：
                //public class Person {
                // private Car car;
                // public Optional<Car> getCarAsOptional() {
                // return Optional.ofNullable(car);
                // }
                //}
            }
        }

        /**
         * 10.3.4 默认行为及解引用 Optional 对象
         * 我们决定采用orElse方法读取这个变量的值，使用这种方式你还可以定义一个默认值，遭
         * 遇空的Optional变量时，默认值会作为该方法的调用返回值。Optional类提供了多种方法读取
         * Optional实例中的变量值
         *  get()是这些方法中最简单但又最不安全的方法。如果变量存在，它直接返回封装的变量
         * 值，否则就抛出一个NoSuchElementException异常。所以，除非你非常确定Optional
         * 变量一定包含值，否则使用这个方法是个相当糟糕的主意。此外，这种方式即便相对于
         * 嵌套式的null检查，也并未体现出多大的改进。
         *  orElse(T other)是我们在代码清单10-5中使用的方法，正如之前提到的，它允许你在
         * Optional对象不包含值时提供一个默认值。
         *  orElseGet(Supplier<? extends T> other)是orElse方法的延迟调用版，Supplier
         * 方法只有在Optional对象不含值时才执行调用。如果创建默认值是件耗时费力的工作，
         * 你应该考虑采用这种方式（借此提升程序的性能），或者你需要非常确定某个方法仅在
         * Optional为空时才进行调用，也可以考虑该方式（这种情况有严格的限制条件）。
         *  orElseThrow(Supplier<? extends X> exceptionSupplier)和get方法非常类似，
         * 它们遭遇Optional对象为空时都会抛出一个异常，但是使用orElseThrow你可以定制希
         * 望抛出的异常类型。
         * ifPresent(Consumer<? super T>)让你能在变量值存在时执行一个作为参数传入的
         * 方法，否则就不进行任何操作。
         */
        public static class Jia1034 {

        }

        /**
         * 10.3.5 两个 Optional 对象的组合
         */
        public static class Jia1035 {

            // 我们假设你有这样一个方法，它接受一个Person和一个Car对象，并以此为条件对外
            //部提供的服务进行查询，通过一些复杂的业务逻辑，试图找到满足该组合的最便宜的保险公司
            public Insurance findCheapestInsurance(Person person, Car car) {
                // 不同的保险公司提供的查询服务
                // 对比所有数据
                return null;
            }

            // 我们还假设你想要该方法的一个null-安全的版本，它接受两个Optional对象作为参数，
            //返回值是一个Optional<Insurance>对象，如果传入的任何一个参数值为空，它的返回值亦为
            //空。Optional类还提供了一个isPresent方法，如果Optional对象包含值，该方法就返回true，
            //所以你的第一想法可能是通过下面这种方式实现该方法：
            public Optional<Insurance> nullSafeFindCheapestInsurance(
                    Optional<Person> person, Optional<Car> car) {
                if (person.isPresent() && car.isPresent()) {
                    return Optional.of(findCheapestInsurance(person.get(), car.get()));
                } else {
                    return Optional.empty();
                }
            }

            // 这个方法具有明显的优势，我们从它的签名就能非常清楚地知道无论是person还是car，它
            //的值都有可能为空，出现这种情况时，方法的返回值也不会包含任何值。不幸的是，该方法的具
            //体实现和你之前曾经实现的null检查太相似了：方法接受一个Person和一个Car对象作为参数，
            //而二者都有可能为null。利用Optional类提供的特性，有没有更好或更地道的方式来实现这个
            //方法呢?

            // 测验10.1：以不解包的方式组合两个Optional对象'
            // 结合本节中介绍的map和flatMap方法，用一行语句重新实现之前出现的nullSafeFindCheapestInsurance()方法
            // 你可以像使用三元操作符那样，无需任何条件判断的结构，以一行语句实现该方法，代码如下
            //这段代码中，你对第一个Optional对象调用flatMap方法，如果它是个空值，传递给它
            //的Lambda表达式不会执行，这次调用会直接返回一个空的Optional对象。反之，如果person
            //对象存在，这次调用就会将其作为函数Function的输入，并按照与flatMap方法的约定返回
            //一个Optional<Insurance>对象。这个函数的函数体会对第二个Optional对象执行map操
            //作，如果第二个对象不包含car，函数Function就返回一个空的Optional对象，整个
            //nullSafeFindCheapestInsuranc方法的返回值也是一个空的Optional对象。最后，如果
            //person和car对象都存在，作为参数传递给map方法的Lambda表达式能够使用这两个值安全
            //地调用原始的findCheapestInsurance方法，完成期望的操作。
            public Optional<Insurance> nullSafeFindCheapestInsuranceImproves (Optional<Person> person, Optional<Car> car) {
                return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
            }
        }

        /**
         * 10.3.6 使用 filter 剔除特定的值
         */
        public static class Jia1036 {

            //假设在我们的Person/Car/Insurance 模型中，Person还提供了一个方法可以取得
            //Person对象的年龄，请使用下面的签名改写代码清单10-5中的getCarInsuranceName方法
            //找出年龄大于或者等于minAge参数的Person所对应的保险公司列表
            public String getCarInsuranceName(Optional<Person> person, int minAge){
                return person.filter(p -> p.getAge() >= minAge)
                        .flatMap(Person::getCar)
                        .flatMap(Car::getInsurance)
                        .map(Insurance::getName)
                        .orElse("Unknown");
            }

            public static void main(String[] args) {
                // 你经常需要调用某个对象的方法，查看它的某些属性。比如，你可能需要检查保险公司的名
                //称是否为“Cambridge-Insurance”。为了以一种安全的方式进行这些操作，你首先需要确定引用指
                //向的Insurance对象是否为null，之后再调用它的getName方法，如下所示：
                Insurance insurance = new Insurance();
                if(insurance != null && "CambridgeInsurance".equals(insurance.getName())){
                    System.out.println("ok");
                }

                // 使用Optional对象的filter方法，这段代码可以重构如下：
                //filter方法接受一个谓词作为参数。如果Optional对象的值存在，并且它符合谓词的条件，
                //filter方法就返回其值；否则它就返回一个空的Optional对象。如果你还记得我们可以将
                //Optional看成最多包含一个元素的Stream对象，这个方法的行为就非常清晰了。如果Optional
                //对象为空，它不做任何操作，反之，它就对Optional对象中包含的值施加谓词操作。如果该操
                //作的结果为true，它不做任何改变，直接返回该Optional对象，否则就将该值过滤掉，将
                //Optional的值置空
                Optional<Insurance> optInsurance = Optional.empty();
                optInsurance.filter(insurance1 -> "CambridgeInsurance".equals(insurance.getName()))
                        .ifPresent(x -> System.out.println("ok"));
            }
        }
    }

    /**
     * 10.4 使用 Optional 的实战示例
     */
    public static class Jia104 {

        /**
         * 10.4.1 用 Optional 封装可能为 null 的值
         */
        public static class Jia1041 {

            public static void main(String[] args) {

                //现存Java API几乎都是通过返回一个null的方式来表示需要值的缺失，或者由于某些原因计
                //算无法得到该值。比如，如果Map中不含指定的键对应的值，它的get方法会返回一个null。但
                //是，正如我们之前介绍的，大多数情况下，你可能希望这些方法能返回一个Optional对象。你
                //无法修改这些方法的签名，但是你很容易用Optional对这些方法的返回值进行封装。我们接着
                //用Map做例子，假设你有一个Map<String, Object>方法，访问由key索引的值时，如果map
                //中没有与key关联的值，该次调用就会返回一个null。
                HashMap<String, Object> map = new HashMap<>();
                Object key = map.get("key");

                // 使用Optional封装map的返回值，你可以对这段代码进行优化。要达到这个目的有两种方式：
                // 你可以使用笨拙的if-then-else判断语句，毫无疑问这种方式会增加代码的复杂度；或者
                // 你可以采用我们前文介绍的Optional.ofNullable方法：

                Optional<Object> value = Optional.ofNullable(map.get("key"));
            }
        }

        /**
         * 10.4.2 异常与 Optional 的对比
         */
        public static class Jia1042 {

            //由于某种原因，函数无法返回某个值，这时除了返回null，Java API比较常见的替代做法是
            //抛出一个异常。这种情况比较典型的例子是使用静态方法Integer.parseInt(String)，将
            //String转换为int。在这个例子中，如果String无法解析到对应的整型，该方法就抛出一个
            //NumberFormatException。最后的效果是，发生String无法转换为int时，代码发出一个遭遇
            //非法参数的信号，唯一的不同是，这次你需要使用try/catch 语句，而不是使用if条件判断来
            //控制一个变量的值是否非空。
            //你也可以用空的Optional对象，对遭遇无法转换的String时返回的非法值进行建模，这时
            //你期望parseInt的返回值是一个optional。我们无法修改最初的Java方法，但是这无碍我们进
            //行需要的改进，你可以实现一个工具方法，将这部分逻辑封装于其中，最终返回一个我们希望的
            //Optional对象，代码如下所示
            public static Optional<Integer> stringToInt(String s) {
                try {
                    // 如果String能转换为对应的Integer，将其封装在Optioal对象中返回
                    return Optional.of(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    // 否则返回一个空的Optional对象
                    return Optional.empty();
                }
            }
            //你可以将多个类似的方法封装到一个工具类中，让我们称之为OptionalUtility。通过这种方式，你以后就能直接调用OptionalUtility.stringToInt方法，将
            //String转换为一个Optional<Integer>对象，而不再需要记得你在其中封装了笨拙的
            //try/catch的逻辑了

            // 基础类型的Optional对象，以及为什么应该避免使用它们
            //与 Stream对象一样，Optional也提供了类似的基础类
            //型——OptionalInt、OptionalLong以及OptionalDouble——所以代码清单10-6中的方法可
            //以不返回Optional<Integer>，而是直接返回一个OptionalInt类型的对象。第5章中，我们
            //讨论过使用基础类型Stream的场景，尤其是如果Stream对象包含了大量元素，出于性能的考量，
            //使用基础类型是不错的选择，但对Optional对象而言，这个理由就不成立了，因为Optional
            //对象最多只包含一个值。
            //我们不推荐大家使用基础类型的Optional，因为基础类型的Optional不支持map、
            //flatMap以及filter方法，而这些却是Optional类最有用的方法（正如我们在10.2节所看到的
            //那样）。此外，与Stream一样，Optional对象无法由基础类型的Optional组合构成，所以，举
            //例而言，如果代码清单10-6中返回的是OptionalInt类型的对象，你就不能将其作为方法引用传
            //递给另一个Optional对象的flatMap方法。
        }
    }

    /**
     * 10.4.3 把所有内容整合起来
     */
    public static class Jia1042 {

        //我们假设你的程序需要从这些属性中读取一个值，该值是以秒为单位计量的一段时间。
        //由于一段时间必须是正数，你想要该方法符合下面的签名
        public static int readDuration(Properties props, String name) {
            return 0;
        }

        public int readDurationImproves(Properties props, String name) {
            String value = props.getProperty(name);
            // 确保名称对应的属性存在
            if (value != null) {
                try {
                    int i = Integer.parseInt(value);
                    // 检查返回的数字是否为正数
                    if (i > 0) {
                        return i;
                    }
                } catch (NumberFormatException nfe) { }
            }
            // 如果前述的条件都不满足，返回0
            return 0;
        }

        public static void main(String[] args) {
            Properties param = new Properties();
            param.setProperty("a", "5");
            param.setProperty("b", "true");
            param.setProperty("c", "-3");

            //即，如果给定属性对应的值是一个代表正整数的字符串，就返回该整数值，任何其他的情况都返
            //回0。为了明确这些需求，你可以采用JUnit的断言，将它们形式化：
//            assertEquals(5, readDuration(param, "a"));
//            assertEquals(0, readDuration(param, "b"));
//            assertEquals(0, readDuration(param, "c"));
//            assertEquals(0, readDuration(param, "d"));
            // 这些断言反映了初始的需求：如果属性是a，readDuration方法返回5，因为该属性对应的
            //字符串能映射到一个正数；对于属性b，方法的返回值是0，因为它对应的值不是一个数字；对于
            //c，方法的返回值是0，因为虽然它对应的值是个数字，不过它是个负数；对于d，方法的返回值
            //是0，因为并不存在该名称对应的属性。让我们以命令式编程的方式实现满足这些需求的方法，
            //代码清单如下所示

            // 以命令式编程的方式从属性中读取duration值
            //测验10.3：使用Optional从属性中读取duration
            //请尝试使用Optional类提供的特性及代码清单10-6中提供的工具方法，通过一条精炼的
            //语句重构代码清单10-7中的方法。
            //答案：如果需要访问的属性值不存在，Properties.getProperty(String)方法的返回
            //值就是一个null，使用ofNullable工厂方法非常轻易地就能把该值转换为Optional对象。接
            //着，你可以向它的flatMap方法传递代码清单10-6中实现的OptionalUtility.stringToInt
            //方法的引用，将Optional<String>转换为Optional<Integer>。最后，你非常轻易地就可
            //以过滤掉负数。这种方式下，如果任何一个操作返回一个空的Optional对象，该方法都会返
            //回orElse方法设置的默认值0；否则就返回封装在Optional对象中的正整数。下面就是这段
            //简化的实现：
            //public int readDuration(Properties props, String name) {
            // return Optional.ofNullable(props.getProperty(name))
            // .flatMap(OptionalUtility::stringToInt)
            // .filter(i -> i > 0)
            // .orElse(0);
            //}
            // 它们都是对数据库查询过程的反思，查询时，多种操作会被串接在一起执行
        }
    }
}
