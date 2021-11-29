package cn.locusc.java8action.chapter9DefaultMethod;

import java.io.Serializable;
import java.util.*;

/**
 * @author Jay
 * 默认方法 （让类可以自动地继承接口的一个默认实现）
 *  什么是默认方法
 *  如何以一种兼容的方式改进API
 *  默认方法的使用模式
 *  解析规则
 *
 * 小结
 *  Java 8中的接口可以通过默认方法和静态方法提供方法的代码实现。
 *  默认方法的开头以关键字default修饰，方法体与常规的类方法相同。
 *  向发布的接口添加抽象方法不是源码兼容的。
 *  默认方法的出现能帮助库的设计者以后向兼容的方式演进API。
 *  默认方法可以用于创建可选方法和行为的多继承。
 *  我们有办法解决由于一个类从多个接口中继承了拥有相同函数签名的方法而导致的冲突。
 *  类或者父类中声明的方法的优先级高于任何默认方法。如果前一条无法解决冲突，那就
 * 选择同函数签名的方法中实现得最具体的那个接口的方法。
 *  两个默认方法都同样具体时，你需要在类中覆盖该方法，显式地选择使用哪个接口中提
 * 供的默认方法。
 * 2021/11/26
 */
public class ActionClass {

    interface Jia9 {

        // List接口中的sort，以及Collection接口中的stream是两个典型的默认方法
        // default void sort(Comparator<? super E> c){
        //     Collections.sort(this, c);
        // }

        // default Stream<E> stream() {
        //    return StreamSupport.stream(spliterator(), false);
        // }
    }

    public static void main(String[] args) {
        // 接口中的default关键字可以判断一个方法是否为默认方法
        List<Integer> numbers = Arrays.asList(3, 5, 1, 2, 6);
        // 直接调用List接口中的sort默认方法对元素进行排序
        // 并按自然序列对其中的元素进行排序（即标准的字母数字方式排序）
        numbers.sort(Comparator.naturalOrder());


        // default Stream<E> stream() {
        //    return StreamSupport.stream(spliterator(), false);
        // }
        // stream 方法中调用了SteamSupport.stream方法来返回一个流。
        // stream方法的主体调用spliterator方法 它也是Collection接口的一个默认方法。
    }

    /**
     * 9.1 不断演进的 API
     * 如果对现有的接口增加新的API 那么实现这个接口的用户必须要实现这个新的API
     * 但是使用默认方法就在接口定义API的实现 用户也就不一定要实现该API
     * 同时一个类可以继承多个默认方法
     */
    static class Jia91 {

        /**
         * 9.1.1 初始版本的 API
         */
        static class Jia911 {

            public static interface Drawable {

            }

            public interface Resizable extends Drawable {
                int draw();
                int getWidth();
                int getHeight();
                void setWidth(int width);
                void setHeight(int height);
                void setAbsoluteSize(int width, int height);
            }

            // 用户实现
            // 用户根据自身的需求实现了Resizable接口，创建了Ellipse类：
            public static class Ellipse implements Resizable {
                @Override
                public int draw() { return 0; }
                @Override
                public int getWidth() { return 0; }
                @Override
                public int getHeight() { return 0; }
                @Override
                public void setWidth(int width) { }
                @Override
                public void setHeight(int height) { }
                @Override
                public void setAbsoluteSize(int width, int height) { }
            }
            public static class Square implements Resizable {
                @Override
                public int draw() { return 0; }
                @Override
                public int getWidth() { return 0; }
                @Override
                public int getHeight() { return 0; }
                @Override
                public void setWidth(int width) { }
                @Override
                public void setHeight(int height) { }
                @Override
                public void setAbsoluteSize(int width, int height) { }
            }
            public static class Rectangle implements Resizable {
                @Override
                public int draw() { return 0; }
                @Override
                public int getWidth() { return 0; }
                @Override
                public int getHeight() { return 0; }
                @Override
                public void setWidth(int width) { }
                @Override
                public void setHeight(int height) { }
                @Override
                public void setAbsoluteSize(int width, int height) { }
            }

            public static class Utils {
                public static void paint(List<Resizable> l) {
                    l.forEach(r -> {
                        // 调用每个形状自己的setAbsoluteSize方法
                        r.setAbsoluteSize(42, 42);
                        r.draw();
                    });
                }
            }

            // 实现了一个处理各种Resizable形状（包括Ellipse）的游戏
            public static class Game {

                public static void main(String[] args) {
                    // 可以调整大小的形状列表
                    List<Resizable> resizableShapes = Arrays.asList(
                        new Square(), new Rectangle(), new Ellipse()
                    );
                    Utils.paint(resizableShapes);
                }

            }
        }

        /**
         * 9.1.2 第二版 API
         * 要求你更新Resizable的实现
         * 让Square、Rectangle以及其他的形状都能支持setRelativeSize方法
         * 满足这些新的需求，发布了第二版API
         *
         * 默认方法试图解决的问题
         * 让类库的设计者放心地改进应用程序接口
         * 无需担忧对遗留代码的影响，这是因为实现更新接口的类现在会自动继承一个默认的方法实现
         */
        static class Jia912 {

            public interface Resizable {
                int getWidth();
                int getHeight();
                void setWidth(int width);
                void setHeight(int height);
                void setAbsoluteSize(int width, int height);
                // 第二版API添加了一个新方法
                void setRelativeSize(int wFactor, int hFactor);
            }

            public static void main(String[] args) {
                // 用户面临的窘境 更新已发布API会导致后向兼容性问题
                //向接口添加新方法是二进制兼容的，这意味着如果不重新编译该类，即使不实现新的方法，
                //现有类的实现依旧可以运行。不过，用户可能修改他的游戏，在他的Utils.paint方法中调用
                //setRelativeSize方法，因为paint方法接受一个Resizable对象列表作为参数。如果传递的
                //是一个Ellipse对象，程序就会抛出一个运行时错误，因为它并未实现setRelativeSize方法：
                //Exception in thread "main" java.lang.AbstractMethodError:
                // lambdasinaction.chap9.Ellipse.setRelativeSize(II)V
                //其次，如果用户试图重新编译整个应用（包括Ellipse类），他会遭遇下面的编译错误：
                //lambdasinaction/chap9/Ellipse.java:6: error: Ellipse is not abstract and does
                // not override abstract method setRelativeSize(int,int) in Resizable

                //不同类型的兼容性：二进制、源代码和函数行为
                //变更对Java程序的影响大体可以分成三种类型的兼容性，分别是：二进制级的兼容、源代
                //码级的兼容，以及函数行为的兼容。①刚才我们看到，向接口添加新方法是二进制级的兼容，
                //但最终编译实现接口的类时却会发生编译错误。了解不同类型兼容性的特性是非常有益的，下
                //面我们会深入介绍这部分的内容。
                //二进制级的兼容性表示现有的二进制执行文件能无缝持续链接（包括验证、准备和解析）
                //和运行。比如，为接口添加一个方法就是二进制级的兼容，这种方式下，如果新添加的方法不
                //被调用，接口已经实现的方法可以继续运行，不会出现错误。
                //简单地说，源代码级的兼容性表示引入变化之后，现有的程序依然能成功编译通过。比如，
                //向接口添加新的方法就不是源码级的兼容，因为遗留代码并没有实现新引入的方法，所以它们
                //无法顺利通过编译。
                //最后，函数行为的兼容性表示变更发生之后，程序接受同样的输入能得到同样的结果。比
                //如，为接口添加新的方法就是函数行为兼容的，因为新添加的方法在程序中并未被调用（抑或
                //该接口在实现中被覆盖了）。
            }

        }
        
    }


    /**
     * 9.2 概述默认方法
     * 缺失的方法实现会作为接口的一部分由实现类继承
     * （所以命名为默认实现），而无需由实现类提供。
     *
     * 默认方法由default修饰符修饰，
     * 并像类中声明的其他方法一样包含方法体
     */
    public static class Jia92 {

        // 集合库中定义一个名为Sized的接口，在其中定义一个抽象方法size，以及一个默认方法isEmpty
        public interface Sized {
            int size();

            // 实现了Sized接口的类都会自动继承isEmpty的实现
            default boolean isEmpty() {
                return size() == 0;
            }

        }

        public interface Resizable extends Jia91.Jia911.Drawable {
            int draw();
            int getWidth();
            int getHeight();
            void setWidth(int width);
            void setHeight(int height);
            void setAbsoluteSize(int width, int height);

            // 以兼容的方式改进这个库（即使用该库的用户不需要修改他们实现了Resizable的类），
            // 可以使用默认提供setRelativeSize的默认实现
            default void setRelativeSize(int wFactor, int hFactor) {
                setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
            }
        }

        public static void main(String[] args) {
            // 思考
            // 是否这意味着Java已经在某种程度上实现了多继承
            // 如果实现类也实现了同样的方法，这时会发生什么情况？默认方法会被覆盖吗？
            // Java 8中已经定义了一些规则和机制来处理这些问题

            // 函数式接口只包含一个抽象方法，默认方法是种非抽象方法

            //Java 8中的抽象类和抽象接口
            //那么抽象类和抽象接口之间的区别是什么呢？它们不都能包含抽象方法和包含方法体的实现吗？
            //首先，一个类只能继承一个抽象类，但是一个类可以实现多个接口。
            //其次，一个抽象类可以通过实例变量（字段）保存一个通用状态，而接口是不能有实例变量的。

            // Collection接口的一个默认方法 removeIf方法是一个典型的默认方法
        }
    }

    /**
     * 9.3 默认方法的使用模式
     * 使用默认方法的两种用例：可选方法和行为的多继承
     */
    public static class Jia93 {

        /**
         * 9.3.1 可选方法
         */
        public static class Jia931 {

            // 实现Iterator接口的类
            // 通常会为remove方法放置一个空的实现，这些都是些毫无用处的模板代码。
            // 采用默认方法之后，你可以为这种类型的方法提供一个默认的实现，
            // 这样实体类就无需在自己的实现中显式地提供一个空方法
            interface Iterator<T> {
                boolean hasNext();
                T next();

                // 实现Iterator接口的每一个类都不需要再
                // 声明一个空的remove方法了，因为它现在已经有一个默认的实现
                default void remove() {
                    throw new UnsupportedOperationException();
                }
            }

        }

        /**
         * 9.3.2 行为的多继承
         * 默认方法让之前无法想象的事儿以一种优雅的方式得以实现，即行为的多继承
         * 这是一种让类从多个来源重用代码的能力
         */
        public static class Jia932 {

            // 1.
            // Java的类只能继承单一的类，但是一个类可以实现多接口
            // 继承唯一一个类 但是实现了六个接口
            public static class ArrayList<E> extends AbstractList<E>
                implements List<E>, RandomAccess, Cloneable,
                Serializable, Iterable<E>, Collection<E> {

                @Override
                public E get(int index) {
                    return null;
                }

                @Override
                public int size() {
                    return 0;
                }
            }

            // 2.
            // 这种方式和模板设计模式有些相似，都是以其他方法需要实现的方法定义好框架算法。'
            // 实现接口的用户同时它们也会天然地继承rotateBy的默认实现
            public interface Rotatable {
                void setRotationAngle(int angleInDegrees);
                int getRotationAngle();
                // rotateBy方法的一个默认实现
                default void rotateBy(int angleInDegrees){
                    setRotationAngle((getRotationAngle () + angleInDegrees) % 360);
                }
            }

            // 3.


            public interface MoveAble {
                int getX();
                int getY();
                void setX(int x);
                void setY(int y);
                default void moveHorizontally(int distance){
                    setX(getX() + distance);
                }
                default void moveVertically(int distance){
                    setY(getY() + distance);
                }
            }

            public interface Resizable {
                int getWidth();
                int getHeight();
                void setWidth(int width);
                void setHeight(int height);
                void setAbsoluteSize(int width, int height);
                default void setRelativeSize(int wFactor, int hFactor){
                    setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
                }
            }

            // 3.组合接口
            // 需要给出所有的抽象方法的实现 但是无需重复实现的默认方法
            public static class Monster implements Rotatable, MoveAble, Resizable {
                @Override
                public void setRotationAngle(int angleInDegrees) { }
                @Override
                public int getRotationAngle() { return 0; }
                @Override
                public int getX() { return 0; }
                @Override
                public int getY() { return 0; }
                @Override
                public void setX(int x) { }
                @Override
                public void setY(int y) { }
                @Override
                public int getWidth() { return 0; }
                @Override
                public int getHeight() { return 0; }
                @Override
                public void setWidth(int width) { }
                @Override
                public void setHeight(int height) { }
                @Override
                public void setAbsoluteSize(int width, int height) { }
            }


            public static void main(String[] args) {
                // 1. 类型的多继承
                // Java 8中接口方法可以包含实现，类可以从多个接口中继承它们的行为（即实现的代码）。
                // 保持接口的精致性和正交性能帮助你在现有的代码基上最大程度地实现代码复用和行为组合。

                // 2. 利用正交方法的精简接口
                // 假设你需要为你正在创建的游戏定义多个具有不同特质的形状。有的形状需要调整大小，但
                // 是不需要有旋转的功能；有的需要能旋转和移动，但是不需要调整大小。

                // 3. 组合接口
                // 通过组合这些接口，你现在可以为你的游戏创建不同的实体类
                // Monster可以移动、旋转和缩放。
                // Monster继承了rotateBy、moveHorizontally、moveVertically和setRelativeSize的实现
                // 构造函数会设置Monster的坐标、高度、宽度及默认仰角
                Monster monster = new Monster();
                // 调用由Rotatable中继承而来的rotateBy
                monster.rotateBy(180);
                // 调用由MoveAble中继承而来的moveVertically
                monster.moveVertically(10);
                // 假设你现在需要声明另一个类，它要能移动和旋转，但是不能缩放，比如说Sun。这时也无
                // 需复制粘贴代码，你可以像下面这样复用MoveAble和Rotatable接口的默认实现

                //假设你需要修改moveVertically的实现，让它更高效地运行。你可以在MoveAble接口内直接修改它的实现，
                //所有实现该接口的类会自动继承新的代码（这里我们假设用户并未定义自己的方法实现）

                //关于继承的一些错误观点
                //继承不应该成为你一谈到代码复用就试图倚靠的万精油。比如，从一个拥有100个方法及
                //字段的类进行继承就不是个好主意，因为这其实会引入不必要的复杂性。你完全可以使用代理
                //有效地规避这种窘境，即创建一个方法通过该类的成员变量直接调用该类的方法。这就是为什
                //么有的时候我们发现有些类被刻意地声明为final类型：声明为final的类不能被其他的类继
                //承，避免发生这样的反模式，防止核心代码的功能被污染。注意，有的时候声明为final的类
                //都会有其不同的原因，比如，String类被声明为final，因为我们不希望有人对这样的核心
                //功能产生干扰。
                //这种思想同样也适用于使用默认方法的接口。通过精简的接口，你能获得最有效的组合，
                //因为你可以只选择你需要的实现。
            }
        }
    }

    /**
     * 9.4 解决冲突的规则
     * 随着默认方法在Java 8中引入，有可能出现一个类继承了多个方法而它们使用的却是同样的函数签名。
     */
    public static class Jia94 {

        public interface A {
            default void hello() {
                System.out.println("Hello from A");
            }
        }
        public interface B extends A {
            @Override
            default void hello() {
                System.out.println("Hello from B");
            }
        }
        public static class C extends D implements B, A {
            public static void main(String... args) {
                new C().hello();
            }
        }

        public static class D implements A{ }

        /**
         * 9.4.1 解决问题的三条规则
         */
        public static class Jia941 {
            //如果一个类使用相同的函数签名从多个地方（比如另一个类或接口）继承了方法，通过三条
            //规则可以进行判断。
            //(1) 类中的方法优先级最高。类或父类中声明的方法的优先级高于任何声明为默认方法的优
            //先级。
            //(2) 如果无法依据第一条进行判断，那么子接口的优先级更高：函数签名相同时，优先选择
            //拥有最具体实现的默认方法的接口，即如果B继承了A，那么B就比A更加具体。
            //(3) 最后，如果还是无法判断，继承了多个接口的类必须通过显式覆盖和调用期望的方法
            //显式地选择使用哪一个默认方法的实现。
        }

        /**
         * 9.4.2 选择提供了最具体实现的默认方法的接口
         */
        public static class Jia942 {
            // 例子中C类同时实现了B接口和A接口，而这两个接口
            // 恰巧又都定义了名为hello的默认方法。另外，B继承自A。
            // 按照规则(2)，应该选择的是提供了最具体实现的默认方法的接口。
            // 由于B比A更具体，所以应该选择B的hello方法

            // 依据规则(1)，类中声明的方法具有更高的优先级。D并未覆盖hello方法，可是它实现了接
            //口A。所以它就拥有了接口A的默认方法。规则(2)说如果类或者父类没有对应的方法，那么就应
            //该选择提供了最具体实现的接口中的方法。因此，编译器会在接口A和接口B的hello方法之间做
            //选择。由于B更加具体，所以选择B

            // 我们在这个测验中继续复用之前的例子，唯一的不同在于D现在显式地覆盖了从A接口中
            //继承的hello方法。你认为现在的输出会是什么呢？
            //public class D implements A{
            // void hello(){
            // System.out.println("Hello from D");
            // }
            //}
            //public class C extends D implements B, A {
            // public static void main(String... args) {
            // new C().hello();
            // }
            //}
            //答案：由于依据规则(1)，父类中声明的方法具有更高的优先级，所以程序会打印输出“Hello
            //from D”。
            //注意，D的声明如下：
            //public abstract class D implements A {
            // public abstract void hello();
            //}
            //这样的结果是，虽然在结构上，其他的地方已经声明了默认方法的实现，C还是必须提供
            //自己的hello方法。
        }

        /**
         * 9.4.3  冲突及如何显式地消除歧义
         */
        public static class Jia943 {

            public interface A {
                default void hello() {
                    System.out.println("Hello from A");
                }
            }
            public interface B {
                default void hello() {
                    System.out.println("Hello from B");
                }
            }

            // 只能显式地决定你希望在C中使用哪一个方法
            // 为了达到这个目的，你可以覆盖类C中的hello方法，在它的方法体内显式地
            // 调用你希望调用的方法。Java 8中引入了一种新的语法X.super.m(…)，
            // 其中X是你希望调用的m方法所在的父接口
            public class C implements B, A {
                @Override
                public void hello() {
                    B.super.hello();
                }
            }

            public static void main(String[] args) {
                // 到目前为止，你看到的这些例子都能够应用前两条判断规则解决
                // “Error: class C inherits unrelated defaults for hello()from types B and A.”

                // 这个测试中，我们假设接口A和B的声明如下所示：
                //public interface A{
                // default Number getNumber(){
                // return 10;
                // }
                //}
                //public interface B{
                // default Integer getNumber(){
                // return 42;
                // }
                //}
                //类C的声明如下：
                //public class C implements B, A {
                // public static void main(String... args) {
                // System.out.println(new C().getNumber());
                // }
                //}
                //这个程序的会打印输出什么呢？
                //答案：类C无法判断A或者B到底哪一个更加具体。这就是类C无法通过编译的原因。
            }
        }

        /**
         * 9.4.4 菱形继承问题
         */
        public static class Jia944 {

            public interface A{
                default void hello(){
                    System.out.println("Hello from A");
                }
            }
            public interface B extends A { }
            public interface C extends A { }
            public static class D implements B, C {
                // 只有A声明了一个默认方法。由于这
                // 个接口是D的父接口，代码会打印输出“Hello from A”。
                public static void main(String... args) {
                    new D().hello();
                }
            }

            public static void main(String[] args) {

                // 如果B中也提供了一个默认的hello方法，并且函数签名跟A
                //中的方法也完全一致，这时会发生什么情况呢？根据规则(2)，编译器会选择提供了更具体实现的
                //接口中的方法。由于B比A更加具体，所以编译器会选择B中声明的默认方法。如果B和C都使用相
                //同的函数签名声明了hello方法，就会出现冲突，正如我们之前所介绍的，你需要显式地指定使
                //用哪个方法

                //顺便提一句，如果你在C接口中添加一个抽象的hello方法（这次添加的不是一个默认方法），
                //会发生什么情况呢？你可能也想知道答案。
                //public interface C extends A {
                // void hello();
                //}
                //这个新添加到C接口中的抽象方法hello比由接口A继承而来的hello方法拥有更高的优先级，
                //因为C接口更加具体。因此，类D现在需要为hello显式地添加实现，否则该程序无法通过编译。
            }
        }

    }
}
