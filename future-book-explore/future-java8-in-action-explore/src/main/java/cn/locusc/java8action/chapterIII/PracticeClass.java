package cn.locusc.java8action.chapterIII;

import cn.locusc.java8action.domain.Apple;

import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Jay
 * 第三章联系
 * 小结:
 *  Lambda表达式可以理解为一种匿名函数：它没有名称，但有参数列表、函数主体、返回
 * 类型，可能还有一个可以抛出的异常的列表。
 *  Lambda表达式让你可以简洁地传递代码。
 *  函数式接口就是仅仅声明了一个抽象方法的接口。
 *  只有在接受函数式接口的地方才可以使用Lambda表达式。
 *  Lambda表达式允许你直接内联，为函数式接口的抽象方法提供实现，并且将整个表达式
 * 作为函数式接口的一个实例。
 *  Java 8自带一些常用的函数式接口，放在java.util.function包里，包括Predicate
 * <T>、Function<T,R>、Supplier<T>、Consumer<T>和BinaryOperator<T>，如表
 * 3-2所述。
 *  为了避免装箱操作，对Predicate<T>和Function<T, R>等通用函数式接口的原始类型
 * 特化：IntPredicate、IntToLongFunction等。
 * 错误的 Java
 * 代码！（函数
 * 的写法不能
 * 像数学里那
 * 样。）
 *  环绕执行模式（即在方法所必需的代码中间，你需要执行点儿什么操作，比如资源分配
 * 和清理）可以配合Lambda提高灵活性和可重用性。
 *  Lambda表达式所需要代表的类型称为目标类型。
 *  方法引用让你重复使用现有的方法实现并直接传递它们。
 *  Comparator、Predicate和Function等函数式接口都有几个可以用来结合Lambda表达
 * 式的默认方法。
 * 2021/11/18
 */
public class PracticeClass {

    @FunctionalInterface
    public interface TriFunction<A, B, C, D> {
        D apply(A a, B b, C c);
    }

    public static class AppleComparator implements Comparator<Apple> {
        @Override
        public int compare(Apple o1, Apple o2) {
            return o1.getWeight().compareTo(o2.getWeight());
        }
    }

    public static void main(String[] args) {
        TriFunction<String, Integer, String, Apple> runnable = Apple::new;

        List<Apple> apples = Apple.getApples();
        // 传递代码 对象方式传递
        apples.sort(new AppleComparator());

        // 使用匿名类
        apples.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });

        // 使用lambda表达式
        apples.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));

        Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());
        apples.sort(c);

        // 使用方法引用
        apples.sort(Comparator.comparing(Apple::getWeight));
    }

    // 3.8复合 Lambda 表达式的有用方法
    public static class AccordWithLambdaMethod {

        public static void main(String[] args) {
            List<Apple> apples = Apple.getApples();
            // 3.8.1 比较器符合
            // 逆序
            apples.sort(Comparator.comparing(Apple::getWeight).reversed());
            // 比较器链
            apples.sort(Comparator.comparing(Apple::getWeight)
                    .reversed()
                    // 在根据一个条件排序 链式调用
                    .thenComparing(Apple::getCountry)
            );

            // 3.8.2谓词复合
            Predicate<Apple> applePredicate = (Apple a) -> a.getColor().equals("red");
            // 产生现有Predicate对象redApple的非
            Predicate<Apple> notRedApple = applePredicate.negate();
            // 链接Predicate的方法来构造更复杂Predicate对象
            // and和or方法是按照在表达式链中的位置，从左向右确定优先级的。因此，a.or(b).and(c)可以看作(a || b) && c。
            Predicate<Apple> redAndHeavyAppleOrGreen = applePredicate.and(a -> a.getWeight() > 150)
                    .or(a -> "green".equals(a.getColor()));


            // 3.8.3 函数复合
            // 把Function接口所代表的Lambda表达式复合起来
            Function<Integer, Integer> f = (Integer x) -> x + 1;
            Function<Integer, Integer> g = (Integer x) -> x * 2;
            Function<Integer, Integer> h = f.andThen(g);
            System.out.println(h.apply(1));

            // 比方说你有一系列工具方法，对用String表示的一封信做文本转换
            // 以通过复合这些工具方法来创建各种转型流水线了，比如创建一个流水线：先加上抬头，然后进行拼写检查，最后加上一个落款
            Function<String, String> addHeader = Letter::addHeader;
            Function<String, String>  transformationPipeline = addHeader
                    .andThen(Letter::checkSpelling)
                    .andThen(Letter::addFooter);
            System.out.println(transformationPipeline.apply("bless you"));
            // 第二个流水线可能只加抬头、落款，而不做拼写检查
            Function<String, String>  transformationPipeline1 = addHeader.andThen(Letter::addFooter);

            // 数学中的类似思想
            // 3.9.1 积分
            // 3.9.2 与 Java 8 的 Lambda 联系起来
            // f(x) = x + 10
            DoubleFunction<Double> f1 = (double x) -> x + 10;

            integrate(f1, 3, 7);
            // f(x) = x + 10
            // 等价于 integrate(f(x), 3, 7);
            integrate((double x) -> x + 10, 3, 7);
            // 等价于
            integrate(f2(), 3, 7);
            // 等价于
            integrate(AccordWithLambdaMethod::f2, 3, 7);
        }

        // 占位符fx 返回x + 10
        private static Double f2(double v) {
            return v + 10;
        }

        // 直接返回x -> x + 10
        public static DoubleFunction<Double> f2() {
            return x -> x + 10;
        }

        public static Double integrate(DoubleFunction<Double> f, Integer a, Integer b) {
            return (f.apply(a) + f.apply(b)) * (b-a) / 2.0;
        }

        public static class Letter{
            public static String addHeader(String text){
                return "From Raoul, Mario and Alan: " + text;
            }
            public static String addFooter(String text){
                return text + " Kind regards";
            }
            public static String checkSpelling(String text){
                return text.replaceAll("labda", "lambda");
            }
        }

    }

}
