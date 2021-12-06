package cn.locusc.java8action.chapter14FuncSkills;

import cn.locusc.java8action.chapter3.PracticeClass;
import cn.locusc.java8action.domain.Apple;
import cn.locusc.java8action.domain.TrainJourney;
import cn.locusc.java8action.domain.Tree;
import com.alibaba.fastjson.JSON;
import org.w3c.dom.ranges.Range;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jay
 * 函数式编程的技巧
 * 本章内容:
 *  一等成员、高阶方法、科里化以及局部应用
 *  持久化数据结构
 *  生成Java Stream时的延迟计算和延迟列表
 *  模式匹配以及如何在Java中应用
 *  引用透明性和缓存
 *
 * 小结
 * 下面是本章中你应该掌握的重要概念。
 *  一等函数是可以作为参数传递，可以作为结果返回，同时还能存储在数据结构中的函数。
 *  高阶函数接受至少一个或者多个函数作为输入参数，或者返回另一个函数的函数。Java
 * 中典型的高阶函数包括comparing、andThen和compose。
 *  科里化是一种帮助你模块化函数和重用代码的技术。
 *  持久化数据结构在其被修改之前会对自身前一个版本的内容进行备份。因此，使用该技
 * 术能避免不必要的防御式复制。
 *  Java语言中的Stream不是自定义的。
 *  延迟列表是Java语言中让Stream更具表现力的一个特性。延迟列表让你可以通过辅助方法
 * （supplier）即时地创建列表中的元素，辅助方法能帮忙创建更多的数据结构。
 *  模式匹配是一种函数式的特性，它能帮助你解包数据类型。它可以看成Java语言中switch
 * 语句的一种泛化。
 *  遵守“引用透明性”原则的函数，其计算结构可以进行缓存。
 *  结合器是一种函数式的思想，它指的是将两个或多个函数或者数据结构进行合并。
 * 2021/12/3
 */
public class ActionClass {

    /**
     * 14.1 无处不在的函数
     * 使用术语“函数式编程”意指函数或者方法的行为应该像“数学函数”一样——
     * 没有任何副作用
     * 函数可以像任何其他值一样随意使用
     * 可以作为参数传递，可以作为返回值，还能存储在数据结构中。
     * 能够像普通变量一样使用的函数称为一等函数（first-class function）
     */
    public static class Jia141 {

        public static void main(String[] args) {
            // Java 8中使用下面这样的方法引用将一个方法引用保存到一个变量是合理合法的：
            Function<String, Integer> strToInt = Integer::parseInt;
        }

        /**
         * 14.1.1 高阶函数
         * 目前为止，我们使用函数值属于一等这个事实只是为了将它们传递给Java 8的流处理操作
         * 达到行为参数化的效果，类似我们在第1章和第2章中将
         * Apple::isGreenApple作为参数值传递给filterApples方法那样
         * 但这仅仅是个开始。另一个有趣的例子是静态方法Comparator.comparing的使用，
         * 它接受一个函数作为参数同时返回另一个函数（一个比较器）
         *
         * 函数式编程的世界里，如果函数，比如Comparator.comparing，能满足下面任一要求就
         * 可以被称为高阶函数（higher-order function）：
         *  接受至少一个函数作为参数
         *  返回的结果是一个函数
         */
        public static class Jia1411 {

            public static void main(String[] args) {
                // comparing方法接受一个函数作为参数，同时返回另一个函数
                Comparator<Apple> c = Comparator.comparing(Apple::getWeight);
                // 第3章我们构造函数创建流水线时，做了一些类似的事
                Function<String, String> addFooter = PracticeClass.AccordWithLambdaMethod.Letter::addFooter;
                Function<String, String> transformationPipeline = addFooter.andThen(PracticeClass.AccordWithLambdaMethod.Letter::checkSpelling)
                        .andThen(addFooter);

                //因为Java 8中，函数不仅可以作为参数传递，还可以作为结果返回，
                //能赋值给本地变量，也可以插入到某个数据结构

                // 比如，一个计算口袋的程序可能有这样的一个Map<String, Function<Double, Double>>
                // 它将字符串sin映射到方法Function<Double,Double>
                // 实现对Math::sin的方法引用。我们在第8章介绍工厂方法时进行了类似的操作。

                // 对于喜欢第3章结尾的那个微积分示例的读者，由于它接受一个函数作为参数（比如，
                // (Double x) -> x \* x），又返回一个函数作为结果（这个例子中返回值是(Double x) -> 2
                // * x），你可以用不同的方式实现类型定义，如下所示：
                // Function<Function<Double,Double>, Function<Double,Double>>

                // 我们把它定义成Function类型（最左边的Function），目的是想显式地向你确认可以将这
                // 个函数传递给另一个函数。但是，最好使用差异化的类型定义，函数签名如下：
                // Function<Double,Double> differentiate(Function<Double,Double> func)
                // 其实二者说的是同一件事。

                // 副作用和高阶函数
                //第7章中我们了解到传递给流操作的函数应该是无副作用的，否则会发生各种各样的问题
                //（比如错误的结果，有时由于竞争条件甚至会产生我们无法预期的结果）。这一原则在你使用高
                //阶函数时也同样适用。编写高阶函数或者方法时，你无法预知会接收什么样的参数——一旦传
                //入的参数有某些副作用，我们将会一筹莫展！如果作为参数传入的函数可能对你程序的状态产
                //生某些无法预期的改变，一旦发生问题，你将很难理解程序中发生了什么；它们甚至会用某种
                //难于调试的方式调用你的代码。因此，将所有你愿意接收的作为参数的函数可能带来的副作用
                //以文档的方式记录下来是一个不错的设计原则，最理想的情况下你接收的函数参数应该没有任
                //何副作用！
            }
        }

        /**
         * 14.1.2 科里化
         * 科里化的理论定义
         * 科里化①是一种将具备2个参数（比如，x和y）的函数f转化为使用一个参数的函数g，并
         * 且这个函数的返回值也是一个函数，它会作为新函数的一个参数。后者的返回值和初始函数的
         * 返回值相同，即f(x,y) = (g(x))(y)。
         * 当然，我们可以由此推出：你可以将一个使用了6个参数的函数科里化成一个接受第2、4、
         * 6号参数，并返回一个接受5号参数的函数，这个函数又返回一个接受剩下的第1号和第3号参数
         * 的函数。
         * 一个函数使用所有参数仅有部分被传递时，通常我们说这个函数是部分应用的（partially
         * applied）。
         */
        public static class Jia1412 {

            // 这里x是你希望转换的数量，f是转换因子，b是基线值。但是这个方法有些过于宽泛了。通
            //常，你还需要在同一类单位之间进行转换，比如公里和英里。当然，你也可以在每次调用
            //converter方法时都使用3个参数，但是每次都提供转换因子和基准比较繁琐，并且你还极有可
            //能输入错误。
            public static double converter(double x, double f, double b) {
                return x * f + b;
            }

            // 当然，你也可以为每一个应用编写一个新方法，不过这样就无法对底层的逻辑进行复用。
            //这里我们提供一种简单的解法，它既能充分利用已有的逻辑，又能让converter针对每个应
            //用进行定制。你可以定义一个“工厂”方法，它生产带一个参数的转换方法，我们希望借此来说
            //明科里化。
            private static DoubleUnaryOperator curriedConverter(double f, double b) {
                return (double x) -> x * f + b;
            }


            public static void main(String[] args) {
                // 应用程序通常都会有国际化的需求，将一套单位转换到另一套单位是经常碰到的问题。
                // 单位转换通常都会涉及转换因子以及基线调整因子的问题。
                // 比如，将摄氏度转换到华氏度的公式是CtoF(x) = x*9/5 + 32。
                // 所有的单位转换几乎都遵守下面这种模式：
                //(1) 乘以转换因子
                //(2) 如果需要，进行基线调整、

                //可以使用下面这段通用代码表达这一模式
                // converter()
                // curriedConverter()

                //现在，你要做的只是向它传递转换因子和基准值（f和b），它会不辞辛劳地按照你的要求返
                //回一个方法（使用参数x）。比如，你现在可以按照你的需求使用工厂方法产生你需要的任何
                //converter：
                DoubleUnaryOperator convertCtoF = curriedConverter(9.0 / 5, 32);
                DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
                DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214, 0);

                // 由于DoubleUnaryOperator定义了方法applyAsDouble，你可以像下面这样使用你的converter：
                double v = convertUSDtoGBP.applyAsDouble(1000);
                // 这样一来，你的代码就更加灵活了，同时它又复用了现有的转换逻辑！让我们一起回顾下你
                //都做了哪些工作。你并没有一次性地向converter方法传递所有的参数x、f和b，相反，你只是
                //使用了参数f和b并返回了另一个方法，这个方法会接收参数x，最终返回你期望的值x * f + b。
                //通过这种方式，你复用了现有的转换逻辑，同时又为不同的转换因子创建了不同的转换方法
            }
        }
    }

    /**
     * 14.2 持久化数据结构
     * 这一节中，我们会探讨函数式编程中如何使用数据结构。这一主题有各种名称，比如函数式
     * 数据结构、不可变数据结构，不过最常见的可能还要算持久化数据结构（不幸的是，这一术语和
     * 数据库中的持久化概念有一定的冲突，数据库中它代表的是“生命周期比程序的执行周期更长的
     * 数据”）。
     * 我们应该注意的第一件事是，函数式方法不允许修改任何全局数据结构或者任何作为参数
     * 传入的参数。为什么呢？因为一旦对这些数据进行修改，两次相同的调用就很可能产生不同的
     * 结构——这违背了引用透明性原则，我们也就无法将方法简单地看作由参数到结果的映射。
     */
    public static class Jia142 {

        /**
         * 14.2.1 破坏式更新和函数式更新的比较
         * 让我们看看不这么做会导致怎样的结果。假设你需要使用一个可变类TrainJourney（利用一
         * 个简单的单向链接列表实现）表示从A地到B地的火车旅行，你使用了一个整型字段对旅程的一些
         * 细节进行建模，比如当前路途段的价格。旅途中你需要换乘火车，所以需要使用几个由onward字
         * 段串联在一起的TrainJourney对象；直达火车或者旅途最后一段对象的onward字段为null：
         */
        public static class Jia1421 {

            // 假设你有几个相互分隔的TrainJourney对象分别代表从X到Y和从Y到Z的旅行。你希望创
            // 建一段新的旅行，它能将两个TrainJourney对象串接起来（即从X到Y再到Z）。
            // 一种方式是采用简单的传统命令式的方法将这些火车旅行对象链接起来，

            // 这个方法是这样工作的，它找到TrainJourney对象a的下一站，将其由表示a列表结束的
            //null替换为列表b（如果a不包含任何元素，你需要进行特殊处理）。
            //这就出现了一个问题：假设变量firstJourney包含了从X地到Y地的线路，另一个变量
            //secondJourney包含了从Y地到Z地的线路。如果你调用link(firstJourney, secondJourney)
            //方法，这段代码会破坏性地更新 firstJourney ，结果 secondJourney 也会加被入到
            //firstJourney，最终请求从X地到Z地的用户会如其所愿地看到整合之后的旅程，不过从X地到Y
            //地的旅程也被破坏性地更新了。这之后，变量firstJourney就不再代表从X到Y的旅程，而是一
            //个新的从X到Z的旅程了！这一改动会导致依赖原先的firstJourney代码失效！假设
            //firstJourney表示的是清晨从伦敦到布鲁塞尔的火车，这趟车上后一段的乘客本来打算要去布
            //鲁塞尔，可是发生这样的改动之后他们莫名地多走了一站，最终可能跑到了科隆。现在你大致了
            //解了数据结构修改的可见性会导致怎样的问题了，作为程序员，我们一直在与这种缺陷作斗争。
            public static TrainJourney link(TrainJourney a, TrainJourney b) {
                if(a == null) return b;
                TrainJourney t = a;
                while (t.onward != null) {
                    t = t.onward;
                }
                t.onward = b;
                return a;
            }

            // 函数式编程解决这一问题的方法是禁止使用带有副作用的方法。如果你需要使用表示计算结
            //果的数据结果，那么请创建它的一个副本而不要直接修改现存的数据结构。这一最佳实践也适用
            //于标准的面向对象程序设计。不过，对这一原则，也存在着一些异议，比较常见的是认为这样做
            //会导致过度的对象复制，有些程序员会说“我会记住那些有副作用的方法”或者“我会将这些写
            //入文档”。但这些都不能解决问题，这些坑都留给了接受代码维护工作的程序员。采用函数式编
            //程方案的代码如下：

            //很明显，这段代码是函数式的（它没有做任何修改，即使是本地的修改），它没有改动任何
            //现存的数据结构。不过，也请特别注意，这段代码有一个特别的地方，它并未创建整个新
            //TrainJourney对象的副本——如果a是n个元素的序列，b是m个元素的序列，那么调用这个函
            //数后，它返回的是一个由n+m个元素组成的序列，这个序列的前n个元素是新创建的，而后m个元
            //素和TrainJourney对象b是共享的。另外，也请注意，用户需要确保不对append操作的结果进
            //行修改，因为一旦这样做了，作为参数传入的TrainJourney对象序列b就可能被破坏。图14-2
            //和图14-3解释说明了破坏式append和函数式append之间的区别。
            static TrainJourney append(TrainJourney a, TrainJourney b) {
                return a == null ? b : new TrainJourney(a.price, append(a.onward, b));
            }

            public static void main(String[] args) {
                TrainJourney trainJourney = new TrainJourney(1, new TrainJourney(3, null));
                TrainJourney trainJourney1 = new TrainJourney(2, null);
                System.out.println(append(trainJourney, trainJourney1).toString());

                System.out.println(link(trainJourney, trainJourney1));
            }
        }

        /**
         * 14.2.2 另一个使用 Tree 的例子
         * 转入新主题之前，让我们再看一个使用其他数据结构的例子——我们想讨论的对象是二叉查
         * 找树，它也是HashMap实现类似接口的方式。我们的设计中Tree包含了String类型的键，以及
         * int类型的键值，它可能是名字或者年龄：
         */
        public static class Jia1422 {

            static class TreeProcessor {

                public  int lookup(String k, int defaultVal, Tree t) {
                    if(t == null) {
                        return defaultVal;
                    }
                    if(k.equals(t.key)) {
                        return t.val;
                    }
                    return lookup(k, defaultVal,
                            k.compareTo(t.key) < 0 ? t.left : t.right);
                }

                //通过二叉查找树找到String值对应的整型数。现在，我们想想你该如何更新与某个
                //键对应的值（简化起见，我们假设键已经存在于这个树中了）：
                public static void update(String k, int newval, Tree t) {
                    if(t == null) {
                       // 应增加一个新的节点
                    } else if (k.equals(t.key)) {
                        t.val = newval;
                    } else {
                        update(k, newval, k.compareTo(t.key) < 0 ?
                                t.left : t.right);
                    }
                }
                //对这个例子，增加一个新的节点会复杂很多；最简单的方法是让update直接返回它刚遍历
                //的树（除非你需要加入一个新的节点，否则返回的树结构是不变的）。现在，这段代码看起来已
                //经有些臃肿了（因为update试图对树进行原地更新，它返回的是跟传入的参数同样的树，但是
                //如果最初的树为空，那么新的节点会作为结果返回）。
                public static Tree updateImproved(String k, int newval, Tree t) {
                    if (t == null)
                        new Tree(k, newval, null, null);
                    else if (k.equals(t.key))
                        t.val = newval;
                    else if (k.compareTo(t.key) < 0)
                        t.left = updateImproved(k, newval, t.left);
                    else
                        t.right = updateImproved(k, newval, t.right);
                    return t;
                }

                // 注意，这两个版本的update都会对现有的树进行修改，这意味着使用树存放映射关系的所
                // 有用户都会感知到这些修改
            }
        }

        /**
         * 14.2.3 采用函数式的方法
         * 那么这一问题如何通过函数式的方法解决呢？你需要为新的键-值对创建一个新的节点，除
         * 此之外你还需要创建从树的根节点到新节点的路径上的所有节点。通常而言，这种操作的代价并
         * 不太大，如果树的深度为d，并且保持一定的平衡性，那么这棵树的节点总数是2d，这样你就只
         * 需要重新创建树的一小部分节点了。
         */
        public static class Jia1423 {

            // 这段代码中，我们通过一行语句进行的条件判断，没有采用if-then-else这种方式，目的
            //是希望强调一个思想，那就是该函数体仅包含一条语句，没有任何副作用。不过你也可以按照自
            //己的习惯，使用if-then-else这种方式，在每一个判断结束处使用return返回。
            public static Tree fupdate(String k, int newval, Tree t) {
                return (t == null) ?
                        new Tree(k, newval, null, null) :
                        k.equals(t.key) ?
                                new Tree(k, newval, t.left, t.right) :
                                k.compareTo(t.key) < 0 ?
                                        new Tree(t.key, t.val, fupdate(k,newval, t.left), t.right) :
                                        new Tree(t.key, t.val, t.left, fupdate(k,newval, t.right));
            }

            // 那么，update 和fupdate之间的区别到底是什么呢？我们注意到，前文中方法update有这
            //样一种假设，即每一个update的用户都希望共享同一份数据结构，也希望能了解程序任何部分
            //所做的更新。因此，无论任何时候，只要你使用非函数式代码向树中添加某种形式的数据结构，
            //请立刻创建它的一份副本，因为谁也不知道将来的某一天，某个人会突然对它进行修改，这一点
            //非常重要（不过也经常被忽视）。与之相反，fupdate是纯函数式的。它会创建一个新的树，并
            //将其作为结果返回，通过参数的方式实现共享。图14-4对这一思想进行了阐释。你使用了一个树
            //结构，树的每个节点包含了person对象的姓名和年龄。调用fupdate不会修改现存的树，它会
            //在原有树的一侧创建新的节点，同时保证不损坏现有的数据结构。

            // 这种函数式数据结构通常被称为持久化的——数据结构的值始终保持一致，不受其他部分变
            //化的影响——这样，作为程序员的你才能确保fupdate不会对作为参数传入的数据结构进行修
            //改。不过要达到这一效果还有一个附加条件：这个约定的另一面是，所有使用持久化数据结构的
            //用户都必须遵守这一“不修改”原则。如果不这样，忽视这一原则的程序员很有可能修改fupdate
            //的结果（比如，修改Emily的年纪为20岁）。这会成为一个例外（也是我们不期望发生的）事件，
            //为所有使用该结构的方法感知，并在之后修改作为参数传递给fupdate的数据结构。
            //通过这些介绍，我们了解到fupdate可能有更加高效的方式：基于“不对现存结构进行修改”
            //规则，对仅有细微差别的数据结构（比如，用户A看到的树结构与用户B看到的就相差不多），我
            //们可以考虑对这些通用数据结构使用共享存储。你可以凭借编译器，将Tree类的字段key、val、
            //left以及right声明为final执行，“禁止对现存数据结构的修改”这一规则；不过我们也需要
            //注意final只能应用于类的字段，无法应用于它指向的对象，如果你想要对对象进行保护，你需
            //要将其中的字段声明为final，以此类推。
            //噢，你可能会说：“我希望对树结构的更新对某些用户可见（当然，这句话的潜台词是其他
            //人看不到这些更新）。”那么，要实现这一目标，你可以通过两种方式：第一种是典型的Java解决
            //方案（对对象进行更新时，你需要特别小心，慎重地考虑是否需要在改动之前保存对象的一份副
            //本）。另一种是函数式的解决方案：逻辑上，你在做任何改动之前都会创建一份新的数据结构（这
            //样一来就不会有任何的对象发生变更），只要确保按照用户的需求传递给他正确版本的数据结构
            //就好了。这一想法甚至还可以通过API直接强制实施。如果数据结构的某些用户需要进行可见性
            //的改动，它们应该调用API，返回最新版的数据结构。对于另一些客户应用，它们不希望发生任
            //何可见的改动（比如，需要长时间运行的统计分析程序），就直接使用它们保存的备份，因为它
            //知道这些数据不会被其他程序修改。
            //有些人可能会说这个过程很像更新刻录光盘上的文件，刻录光盘时，一个文件只能被激光写
            //入一次，该文件的各个版本分别被存储在光盘的各个位置（智能光盘编辑软件甚至会共享多个不
            //同版本之间的相同部分），你可以通过传递文件起始位置对应的块地址（或者名字中编码了版本
            //信息的文件名）选择你希望使用哪个版本的文件。Java中，情况甚至比刻录光盘还好很多，不再
            //使用的老旧数据结构会被Java虚拟机自动垃圾回收掉。
        }
    }

    /**
     * 14.3 Stream 的延迟计算
     * 通过前一章的介绍，你已经了解Stream是处理数据集合的利器。不过，由于各种各样的原因，
     * 包括实现时的效率考量，Java 8的设计者们在将Stream引入时采取了比较特殊的方式。其中一个
     * 比较显著的局限是，你无法声明一个递归的Stream，因为Stream仅能使用一次。在接下来的一节，
     * 我们会详细展开介绍这一局限会带来的问题。
     */
    public static class Jia143 {

        /**
         * 14.3.1 自定义的 Stream
         * 让我们一起回顾下第6章中生成质数的例子，这个例子有助于我们理解递归式Stream的思想。
         * 你大概已经看到，作为MyMathUtils类的一部分，你可以用下面这种方式计算得出由质数构成
         * 的Stream：
         */
        public static class Jia1431 {

            public static Stream<Integer> primes(int n) {
                return Stream.iterate(2, i -> i + 1)
                        .filter(Jia1431::isPrime)
                        .limit(n);
            }

            //不过这一方案看起来有些笨拙：你每次都需要遍历每个数字，查看它能否被候选数字整除（实
            //际上，你只需要测试那些已经被判定为质数的数字）。
            public static boolean isPrime(int candidate) {
                int candidateRoot = (int) Math.sqrt((double) candidate);
                return IntStream.rangeClosed(2, candidateRoot)
                        .noneMatch(i -> candidate % i == 0);
            }

            //理想情况下，Stream应该实时地筛选掉那些能被质数整除的数字。这听起来有些异想天开，
            //不过我们一起看看怎样才能达到这样的效果。


            //过我们一起看看怎样才能达到这样的效果。
            //(1) 你需要一个由数字构成的Stream，你会在其中选择质数。
            //(2) 你会从该Stream中取出第一个数字（即Stream的首元素），它是一个质数（初始时，这个
            //值是2）。
            //(3) 紧接着你会从Stream的尾部开始，筛选掉所有能被该数字整除的元素。
            //(4) 最后剩下的结果就是新的Stream，你会继续用它进行质数的查找。本质上，你还会回到
            //第一步，继续进行后续的操作，所以这个算法是递归的。

            //注意，这个算法不是很好(这里有一个math question)，原因是多方面的①。不过，就说明如何使用Stream展开工作这个
            //目的而言，它还是非常合适的，因为算法简单，容易说明。让我们试着用Stream API对这个算法
            //进行实现。

            //1. 第一步： 构造由数字组成的Stream
            //你可以使用方法IntStream.iterate构造由数字组成的Stream，它由2开始，可以上达无限，
            //就像我们在第5章中介绍的那样，代码如下：
            static IntStream numbers() {
                return IntStream.iterate(2, n -> n + 1);
            }

            //2. 第二步： 取得首元素
            //IntStream类提供了方法findFirst，可以返回Stream的第一个元素：
            static int head(IntStream numbers) {
                return numbers.findFirst().getAsInt();
            }

            //3. 第三步： 对尾部元素进行筛选
            //定义一个方法取得Stream的尾部元素：
            static IntStream tail(IntStream numbers) {
                return numbers.skip(1);
            }

            //拿到Stream的头元素，你可以像下面这段代码那样对数字进行筛选：
            public static void main(String[] args) {
                IntStream numbers = numbers().limit(10);
                int head = head(numbers);
                IntStream filtered = tail(numbers).filter(n -> n % head != 0);
                filtered.forEach(System.out::println);
            }

            // 4. 第四步：递归地创建由质数组成的Stream
            //现在到了最复杂的部分。你可能试图将筛选返回的Stream作为参数再次传递给该方法，这样
            //你可以接着取得它的头元素，继续筛选掉更多的数字，如下所示：
            static IntStream primes(IntStream numbers) {
                int head = head(numbers);
                return IntStream.concat(
                        IntStream.of(head),
                        primes(tail(numbers).filter(n -> n % head != 0))
                );
            }

            //5. 坏消息
            //不幸的是，如果执行步骤四中的代码，你会遭遇如下这个错误：“java.lang.IllegalStateException:
            //stream has already been operated upon or closed.”实际上，你正试图使用两个终端操作：findFirst
            //和skip将Stream切分成头尾两部分。还记得我们在第4章中介绍的内容吗？一旦你对Stream执行
            //一次终端操作调用，它就永久地终止了！

            //6. 延迟计算
            //除此之外，该操作还附带着一个更为严重的问题： 静态方法IntStream.concat接受两个
            //Stream实例作参数。但是，由于第二个参数是primes方法的直接递归调用，最终会导致出现无
            //限递归的状况。然而，对大多数的Java应用而言，Java 8在Stream上的这一限制，即“不允许递归
            //定义”是完全没有影响的，使用Stream后，数据库的查询更加直观了，程序还具备了并发的能力。
            //所以，Java 8的设计者们进行了很好的平衡，选择了这一皆大欢喜的方案。不过，Scala和Haskell
            //这样的函数式语言中Stream所具备的通用特性和模型仍然是你编程武器库中非常有益的补充。你
            //需要一种方法推迟primes中对concat的第二个参数计算。如果用更加技术性的程序设计术语来
            //描述，我们称之为延迟计算、非限制式计算或者名调用。只在你需要处理质数的那个时刻（比如，
            //要调用方法limit了）才对Stream进行计算。Scala（我们会在下一章介绍）提供了对这种算法的
            //支持。在Scala中，你可以用下面的方式重写前面的代码，操作符#::实现了延迟连接的功能（只
            //有在你实际需要使用Stream时才对其进行计算）：
            //def numbers(n: Int): Stream[Int] = n #:: numbers(n+1)
            //def primes(numbers: Stream[Int]): Stream[Int] = {
            //numbers.head #:: primes(numbers.tail filter (n -> n % numbers.head != 0))
            //}

            //看不懂这段代码？完全没关系。我们展示这段代码的目的只是希望能让你了解Java和其他的
            //函数式编程语言的区别。让我们一起回顾一下刚刚介绍的参数是如何计算的，这对我们后面的内
            //容很有裨益。在Java语言中，你执行一次方法调用时，传递的所有参数在第一时间会被立即计算
            //出来。但是，在Scala中，通过#::操作符，连接操作会立刻返回，而元素的计算会推迟到实际计
            //算需要的时候才开始。现在，让我们看看如何通过Java实现延迟列表的思想。
        }


        /**
         * 14.3.2 创建你自己的延迟列表
         * Java 8的Stream以其延迟性而著称。它们被刻意设计成这样，即延迟操作，有其独特的原因：
         * Stream就像是一个黑盒，它接收请求生成结果。当你向一个 Stream发起一系列的操作请求时，这
         * 些请求只是被一一保存起来。只有当你向Stream发起一个终端操作时，才会实际地进行计算。这
         * 种设计具有显著的优点，特别是你需要对Stream进行多个操作时（你有可能先要进行filter操
         * 作，紧接着做一个map，最后进行一次终端操作reduce）；这种方式下Stream只需要遍历一次，
         * 不需要为每个操作遍历一次所有的元素。
         * 这一节，我们讨论的主题是延迟列表，它是一种更加通用的Stream形式（延迟列表构造了一
         * 个跟Stream非常类似的概念）。延迟列表同时还提供了一种极好的方式去理解高阶函数；你可以
         * 将一个函数作为值放置到某个数据结构中，大多数时候它就静静地待在那里，一旦对其进行调用
         * （即根据需要），它能够创建更多的数据结构。图【LinkedList和LazyList】解释了这一思想。
         *
         * LinkedList的元素存在于（并不断延展）内存中。而LazyList的
         * 元素由函数在需要使用时动态创建，你可以将它们看成实时延展的
         */
        public static class Jia1432 {
            //我们谈论得已经很多，现在让我们一起看看它是如何工作的。你想要利用我们前面介绍的算
            //法，生成一个由质数构成的无限列表。

            public static class One {
                //1. 一个基本的链接列表
                //还记得吗，你可以通过下面这种方式，用Java语言实现一个简单的名为MyLinkedList的链
                //接-列表-式的类（这里我们只考虑最精简的MyList接口）：
                interface MyList<T> {
                    T head();
                    MyList<T> tail();
                    default MyList<T> filter(Predicate<T> p) {
                        return this;
                    };
                    default boolean isEmpty() {
                        return true;
                    }

                    static <T> void printAll(MyList<T> list){
                        while (!list.isEmpty()){
                            System.out.println(list.head());
                            list = list.tail();
                        }
                    }
                }

                static class MyLinkedList<T> implements MyList<T> {

                    private final T head;
                    private final MyList<T> tail;

                    public MyLinkedList(T head, MyList<T> tail) {
                        this.head = head;
                        this.tail = tail;
                    }

                    @Override
                    public T head() {
                        return head;
                    }

                    @Override
                    public MyList<T> tail() {
                        return tail;
                    }

                    public boolean isEmpty() {
                        return false;
                    }
                }

                static class Empty<T> implements MyList<T> {
                    public T head() {
                        throw new UnsupportedOperationException();
                    }
                    public MyList<T> tail() {
                        throw new UnsupportedOperationException();
                    }
                }

                public static void main(String[] args) {
                    // 你现在可以构造一个示例的MyLinkedList值，如下所示：
                    MyLinkedList<Integer> l =
                            new MyLinkedList<>(5, new MyLinkedList<>(10, new Empty<>()));
                }
            }

            // 2. 一个基础的延迟列表
            //对这个类进行改造，使其符合延迟列表的思想，最简单的方法是避免让tail立刻出现在内
            //存中，而是像第3章那样，提供一个Supplier<T>方法（你也可以将其看成一个使用函数描述符
            //void -> T的工厂方法），它会产生列表的下一个节点。使用这种方式的代码如下：
            public static class Two {

                static class LazyList<T> implements One.MyList<T> {
                    final T head;
                    final Supplier<One.MyList<T>> tail;

                    public LazyList(T head, Supplier<One.MyList<T>> tail) {
                        this.head = head;
                        this.tail = tail;
                    }


                    @Override
                    public T head() {
                        return head;
                    }

                    // 注意，与前面的head不同，这
                    // 里tail使用了一个Supplier
                    // 方法提供了延迟性
                    @Override
                    public One.MyList<T> tail() {
                        return tail.get();
                    }

                    public boolean isEmpty() {
                        return false;
                    }

                    public One.MyList<T> filter(Predicate<T> p) {
                        return isEmpty() ?
                                this :
                                p.test(head) ?
                                        new LazyList<>(head(), () -> tail().filter(p)) :
                                        tail().filter(p);
                    }
                }
            }

            //调用Supplier的get方法会触发延迟列表（LazyList）的节点创建，就像工厂会创建新的
            //对象一样。
            //现在，你可以像下面那样传递一个Supplier作为LazyList的构造器的tail参数，创建由
            //数字构成的无限延迟列表了，该方法会创建一系列数字中的下一个元素：
            public static Two.LazyList<Integer> from(int n) {
                return new Two.LazyList<>(n, () -> from(n + 1));
            }

            //如果尝试执行下面的代码，你会发现，下面的代码执行会打印输出“2 3 4”。这些数字真真
            //实实都是实时计算得出的。你可以在恰当的位置插入System.out.println进行查看，如果
            //from(2)执行得很早，它试图计算从2!开始的所有数字，它会永远运行下去，这时你不需要做任
            //何事情。
//            public static void main(String[] args) {
//                Two.LazyList<Integer> numbers = from(2);
//                int two = numbers.head();
//                int three = numbers.tail().head();
//                int four = numbers.tail().tail().head();
//                System.out.println(two + " " + three + " " + four);
//            }

            //3. 回到生成质数
            //看看你能否利用我们目前已经做的去生成一个自定义的质数延迟列表（有些时候，你会遭遇
            //无法使用Stream API的情况）。如果你将之前使用Stream API的代码转换成使用我们新版的
            //LazyList，它看起来会像下面这段代码：
            public static One.MyList<Integer> primes(One.MyList<Integer> numbers) {
                return new Two.LazyList<>(
                        numbers.head(),
                        () -> primes(
                                numbers.tail().filter(n -> n % numbers.head() != 0)
                        )
                );
            }

            //4. 实现一个延迟筛选器
            //不过，这个LazyList（更确切地说是List接口）并未定义filter方法，所以前面的这段
            //代码是无法编译通过的。让我们添加该方法的一个定义，修复这个问题：
//            public One.MyList<T> filter(Predicate<T> p) {
//                return isEmpty() ?
//                        this :
//                        p.test(head) ?
//                                new Two.LazyList<>(head(), () -> tail().filter(p)) :
//                                tail().filter(p);
//            }

            //你的代码现在可以通过编译，准备使用了。通过链接对tail和head的调用，你可以计算出
            //头三个质数：
            public static void main(String[] args) {
                Two.LazyList<Integer> numbers = from(2);
                int two = primes(numbers).head();
                int three = primes(numbers).tail().head();
                int five = primes(numbers).tail().tail().head();
                System.out.println(two + " " + three + " " + five);

                // One.MyList.printAll(primes(from(2)));
            }

            //这段代码的输出是“2 3 5”，这是头三个质数的值。现在，你可以把玩这段程序了，比如，
            //你可以打印输出所有的质数（printAll方法会递归地打印输出列表的头尾元素，这个程序会永
            //久地运行下去）：
            // One.MyList.printAll(primes(from(2)));

            //本章的主题是函数式编程，我们应该在更早的时候就让你知道其实有更加简洁地方式完成这
            //一递归操作
//            static <T> void printAll(MyList<T> list){
//                if (list.isEmpty())
//                    return;
//                System.out.println(list.head());
//                printAll(list.tail());
//            }
            //但是，这个程序不会永久地运行下去；它最终会由于栈溢出而失效，因为Java不支持尾部调
            //用消除（tail call elimination），这一点我们曾经在第13章介绍过。

            //5. 何时使用
            //到目前为止，你已经构建了大量技术，包括延迟列表和函数，使用它们却只定义了一个包含
            //质数的数据结构。为什么呢？哪些实际的场景可以使用这些技术呢？好吧，你已经了解了如何向
            //数据结构中插入函数（因为Java 8允许你这么做），这些函数可以用于按需创建数据结构的一部分，
            //现在你不需要在创建数据结构时就一次性地定义所有的部分。如果你在编写游戏程序，比如棋牌
            //类游戏，你可以定义一个数据结构，它在形式上涵盖了由所有可能移动构成的一个树（这些步骤
            //要在早期完成计算工作量太大），具体的内容可以在运行时创建。最终的结果是一个延迟树，而
            //不是一个延迟列表。我们本章关注延迟列表，原因是它可以和Java 8的另一个新特性Stream串接
            //起来，我们能够针对性地讨论Stream和延迟列表各自的优缺点。
            //还有一个问题就是性能。我们很容易得出结论，延迟操作的性能会比提前操作要好——仅在
            //程序需要时才计算值和数据结构当然比传统方式下一次性地创建所有的值（有时甚至比实际需求
            //更多的值）要好。不过，实际情况并非如此简单。完成延迟操作的开销，比如 LazyList中每个
            //元素之间执行额外Suppliers调用的开销，有可能超过你猜测会带来的好处，除非你仅仅只访问
            //整个数据结构的10%，甚至更少。最后，还有一种微妙的方式会导致你的LazyList并非真正的
            //延迟计算。如果你遍历LazyList中的值，比如from(2)，可能直到第10个元素，这种方式下，
            //它会创建每个节点两次，最终创建20个节点，而不是10个。这几乎不能被称为延迟计算。问题在
            //于每次实时访问LazyList的元素时，tail中的Supplier都会被重复调用；你可以设定tail中
            //的Supplier方法仅在第一次实时访问时才执行调用，从而修复这一问题——计算的结果会缓存
            //起来——效果上对列表进行了增强。要实现这一目标，你可以在LazyList的定义中添加一个私
            //有的Optional<LazyList<T>>类型字段alreadyComputed，tail方法会依据情况查询及更新
            //该字段的值。纯函数式语言Haskell就是以这种方式确保它所有的数据结构都恰当地进行了延迟。
            //如果你对这方面的细节感兴趣，可以查看相关文章。①
            //我们推荐的原则是将延迟数据结构作为你编程兵器库中的强力武器。如果它们能让程序设计
            //更简单，就尽量使用它们。如果它们会带来无法接受的性能损失，就尝试以更加传统的方式重新
            //实现它们。
            //现在，让我们转向几乎所有函数式编程语言中都提供的一个特性，不过Java语言中暂时并未
            //提供这一特性，它就是模式匹配。
        }
    }

    /**
     * 14.4 模式匹配
     * 函数式编程中还有另一个重要的方面，那就是（结构式）模式匹配。不要将这个概念和正则
     * 表达式中的模式匹配相混
     * 淆。还记得吗，第1章结束时，我们了解到数学公式可以通过下面的方
     * 式进行定义：
     * f(0) = 1
     * f(n) = n*f(n-1) otherwise
     * 不过在Java语言中，你只能通过if-then-else语句或者switch语句实现。随着数据类型变
     * 得愈加复杂，需要处理的代码（以及代码块）的数量也在迅速攀升。使用模式匹配能有效地减少
     * 这种混乱的情况。
     */
    public static class Jia144 {

        //为了说明，我们先看一个树结构，你希望能够遍历这一整棵树。我们假设使用一种简单的数
        //学语言，它包含数字和二进制操作符：
        static class Expr {
        }

        static class Number extends Expr {
            int val;
            public Number(int val) {
                this.val = val;
            }

            @Override
            public String toString() {
                return "" + val;
            }
        }

        static class BinOp extends Expr {
            String opname;
            Expr left, right;
            public BinOp(String opname, Expr left, Expr right) {
                this.opname = opname;
                this.left = left;
                this.right = right;
            }

            @Override
            public String toString() {
                return "(" + left + " " + opname + " " + right + ")";
            }
        }

        public class SimplifyExprVisitor {
            public Expr visit(BinOp e){
                if("+".equals(e.opname) && e.right instanceof Number){
                    return e.left;
                }
                return e;
            }
        }

        //假设你需要编写方法简化一些表达式。比如，5 + 0可以简化为5。使用我们的域语言，new
        //BinOp("+", new Number(5), new Number(0))可以简化为Number(5)。你可以像下面这样
        //遍历Expr结构：
//        Expr simplifyExpression(Expr expr) {
//            if (expr instanceof BinOp
//                    && ((BinOp)expr).opname.equals("+"))
//                    && ((BinOp)expr).right instanceof Number
//                    && ... // 你可以预期这种方式下代码会迅速地变得异常丑陋，难于维护
//                    && ... ) {
//                return (Binop)expr.left;
//            }
//            ...
//        }

        /**
         * 14.4.1 访问者设计模式
         * Java语言中还有另一种方式可以解包数据类型，那就是使用访问者（Visitor）设计模式。本
         * 质上，使用这种方法你需要创建一个单独的类，这个类封装了一个算法，可以“访问”某种数据
         * 类型。
         * 它是如何工作的呢？访问者类接受某种数据类型的实例作为输入。它可以访问该实例的所有
         * 成员。下面是一个例子，通过这个例子我们能了解这一方法是如何工作的。首先，你需要向BinOp
         * 添加一个accept方法，它接受一个SimplifyExprVisitor作为参数，并将自身传递给它（你
         * 还需要为Number添加一个类似的方法）：
         */
        public static class Jia1441 {

        }

        /**
         * 14.4.2 用模式匹配力挽狂澜
         * 通过一个名为模式匹配的特性，我们能以更简单的方案解决问题。这种特性目前在Java语言
         * 中暂时还不提供，所以我们会以Scala程序设计语言的一个小例子来展示模式匹配的强大威力。通
         * 过这些介绍你能够了解一旦Java语言支持模式匹配，我们能做哪些事情。
         * 假设数据类型Expr代表的是某种数学表达式，在Scala程序设计语言中（我们采用Scala的原
         * 因是它的语法与Java非常接近），你可以利用下面的这段代码解析表达式：
         * def simplifyExpression(expr: Expr): Expr = expr match {
         *  case BinOp("+", e, Number(0)) => e // 加0
         *  case BinOp("*", e, Number(1)) => e // 乘以1
         *  case BinOp("/", e, Number(1)) => e // 除以1
         *  case _ => expr // 不能简化expr
         * }
         * 模式匹配为操纵类树型数据结构提供了一个极其详细又极富表现力的方式。构建编译器或者
         * 处理商务规则的引擎时，这一工具尤其有用。注意，Scala的语法
         * Expression match { case Pattern => Expression ... }
         * 和Java的语法非常相似：
         * switch (Expression) { case Constant : Statement ... }
         * Scala的通配符判断和Java中的default:扮演这同样的角色。这二者之间主要的语法区别在
         * 于Scala是面向表达式的，而Java则更多地面向语句，不过，对程序员而言，它们主要的区别是Java
         * 中模式的判断标签被限制在了某些基础类型、枚举类型、封装基础类型的类以及String类型。
         * 使用支持模式匹配的语言实践中能带来的最大的好处在于，你可以避免出现大量嵌套的switch
         * 或者if-then-else语句和字段选择操作相互交织的情况。
         * 非常明显，Scala的模式匹配在表达的难易程度上比Java更胜一筹，你只能期待未来版本的
         * Java能支持更具表达性的switch语句。我们会在第16章给出更加详细的介绍。
         * 与此同时，让我们看看如何凭借Java 8的Lambda以另一种方式在Java中实现类模式匹配。我
         * 们在这里介绍这一技巧的目的仅仅是想让你了解Lambda另一个有趣的应用。
         */
        public static class Jia1442 {
            //Java中的伪模式匹配
            //首先，让我们看看Scala的模式匹配特性提供的匹配表达式有多么丰富。比如下面这个例子：

            // def simplifyExpression(expr: Expr): Expr = expr match {
            // case BinOp("+", e, Number(0)) => e
            // ...
            //它表达的意思是：“检查expr是否为BinOp，抽取它的三个组成部分（opname、left、
            //right），紧接着对这些组成部分分别进行模式匹配——第一个部分匹配String+，第二个部分
            //匹配变量e（它总是匹配），第三个部分匹配模式Number(0)。”换句话说，Scala（以及很多其他
            //的函数式语言）中的模式匹配是多层次的。我们使用Java 8的Lambda表达式进行的模式匹配模拟
            //只会提供一层的模式匹配；以前面的这个例子而言，这意味着它只能覆盖BinOp(op, l, r)或
            //者Number(n)这种用例，无法顾及BinOp("+", e, Number(0))。

            //首先，我们做一些稍微让人惊讶的观察。由于你选择使用Lambda，原则上你的代码里不应
            //该使用if-then-else。你可以使用方法调用
            //myIf(condition, () -> e1, () -> e2);

            //在某些地方，比如库文件中，你可能有这样的定义（使用了通用类型T）:
            static <T> T myIf(boolean b,  Supplier<T> truecase, Supplier<T> falsecase) {
                return b ? truecase.get() : falsecase.get();
            }
            //类型T扮演了条件表达式中结果类型的角色。原则上，你可以用if-then-else完成类似的事儿
            //当然，正常情况下用这种方式会增加代码的复杂度，让它变得愈加晦涩难懂，因为用
            //if-then-else就已经能非常顺畅地完成这一任务，这么做似乎有些杀鸡用牛刀的嫌疑。不过，
            //我们也注意到，Java的switch和if-then-else无法完全实现模式匹配的思想，而Lambda表达
            //式能以简单的方式实现单层的模式匹配——对照使用if-then-else链的解决方案，这种方式要
            //简洁得多。

            //回来继续讨论类Expr的模式匹配值，Expr类有两个子类，分别为BinOp和Number，你可以
            //定义一个方法patternMatchExpr（同样，我们在这里会使用泛型T，用它表示模式匹配的结果
            //类型）：
            interface TriFunction<S, T, U, R>{
                R apply(S s, T t, U u);
            }

            static <T> T patternMatchExpr(
                    Expr e,
                    TriFunction<String, Expr, Expr, T> binopcase,
                    Function<Integer, T> numcase,
                    Supplier<T> defaultcase) {

                return (e instanceof BinOp) ?
                        binopcase.apply(((BinOp)e).opname, ((BinOp)e).left,
                                ((BinOp)e).right) :
                        (e instanceof Number) ?
                                numcase.apply(((Number)e).val) :
                                defaultcase.get();

            }

//            public static void main(String[] args) {
                // 最终的结果是，方法调用
//                patternMatchExpr(e, (op, l, r) -> binopcode,
//                        (n) -> numcode,
//                        () -> defaultcode);

                //会判断e是否为BinOp类型（如果是，会执行binopcode方法，它能够通过标识符op、l和r访问
                //BinOp的字段），是否为Number类型（如果是，会执行numcode方法，它可以访问n的值）。这个
                //方法还可以返回defaultcode，如果有人在将来某个时刻创建了一个树节点，它既不是BinOp
                //类型，也不是Number类型，那就会执行这部分代码。
//            }

            // 下面这段代码通过简化的加法和乘法表达式展示了如何使用patternMatchExpr。
            public static Expr simplify(Expr e) {
                // 处理BinOp表达式
                TriFunction<String, Expr, Expr, Expr> binopcase =
                        (opname, left, right) -> {
                            // 处理加法
                            if ("+".equals(opname)) {
                                if (left instanceof Number && ((Number) left).val == 0) {
                                    return right;
                                }
                                if (right instanceof Number && ((Number) right).val == 0) {
                                    return left;
                                }
                            }
                            // 处理乘法
                            if ("*".equals(opname)) {
                                if (left instanceof Number && ((Number) left).val == 1) {
                                    return right;
                                }
                                if (right instanceof Number && ((Number) right).val == 1) {
                                    return left;
                                }
                            }
                            return new BinOp(opname, left, right);
                        };
                // 处理Number对象
                Function<Integer, Expr> numcase = val -> new Number(val);
                //如果用户提供的Expr无法识别时进行的默认处理机制
                Supplier<Expr> defaultcase = () -> new Number(0);
                return patternMatchExpr(e, binopcase, numcase, defaultcase);
            }

            private static Integer evaluate(Expr e) {
                Function<Integer, Integer> numcase = val -> val;
                Supplier<Integer> defaultcase = () -> 0;
                TriFunction<String, Expr, Expr, Integer> binopcase =
                        (opname, left, right) -> {
                            if ("+".equals(opname)) {
                                if (left instanceof Number && right instanceof Number) {
                                    return ((Number) left).val + ((Number) right).val;
                                }
                                if (right instanceof Number && left instanceof BinOp) {
                                    return ((Number) right).val + evaluate((BinOp) left);
                                }
                                if (left instanceof Number && right instanceof BinOp) {
                                    return ((Number) left).val + evaluate((BinOp) right);
                                }
                                if (left instanceof BinOp && right instanceof BinOp) {
                                    return evaluate((BinOp) left) + evaluate((BinOp) right);
                                }
                            }
                            if ("*".equals(opname)) {
                                if (left instanceof Number && right instanceof Number) {
                                    return ((Number) left).val * ((Number) right).val;
                                }
                                if (right instanceof Number && left instanceof BinOp) {
                                    return ((Number) right).val * evaluate((BinOp) left);
                                }
                                if (left instanceof Number && right instanceof BinOp) {
                                    return ((Number) left).val * evaluate((BinOp) right);
                                }
                                if (left instanceof BinOp && right instanceof BinOp) {
                                    return evaluate((BinOp) left) * evaluate((BinOp) right);
                                }
                            }
                            return defaultcase.get();
                        };

                return patternMatchExpr(e, binopcase, numcase, defaultcase);
            }

            //目前为止，你已经学习了很多内容，包括高阶函数、科里化、持久化数据结构、延迟列表以
            //及模式匹配。现在我们看一些更加微妙的技术，为了避免将前面的内容弄得过于复杂，我们刻意
            //地将这部分内容推迟到了后面
            public static void main(String[] args) {
                Expr e = new BinOp("+", new Number(5), new BinOp("*", new Number(5), new Number(2)));

                Expr e1 = new Number(6);
                Integer match = evaluate(e);
                System.out.println(match);
            }
        }
    }

    /**
     * 14.5 杂项
     * 这一节里我们会一起探讨两个关于函数式和引用透明性的比较复杂的问题，一个是效率，另
     * 一个关乎返回一致的结果。这些都是非常有趣的问题，我们直到现在才讨论它们的原因是它们通
     * 常都由副作用引起，并非我们要介绍的核心概念。我们还会探究结合器（Combinator）的思想——
     * 即接受两个或多个方法（函数）做参数且返回结果是另一个函数的方法；这一思想直接影响了新
     * 增到Java 8中的许多API。
     */
    public static class Jia145 {

        /**
         * 14.5.1 缓存或记忆表
         * 假设你有一个无副作用的方法omputeNumberOfNodes(Range)，它会计算一个树形网络中
         * 给定区间内的节点数目。让我们假设，该网络不会发生变化，即该结构是不可变的，然而调用
         * computeNumberOfNodes方法的代价是非常昂贵的，因为该结构需要执行递归遍历。不过，你可
         * 能需要多次地计算该结果。如果你能保证引用透明性，那么有一种聪明的方法可以避免这种冗余
         * 的开销。解决这一问题的一种比较标准的解决方案是使用记忆表（memoization）——为方法添加
         * 一个封装器，在其中加入一块缓存（比如，利用一个HashMap）——封装器被调用时，首先查看
         * 缓存，看请求的“（参数，结果）对”是否已经存在于缓存，如果已经存在，那么方法直接返回缓
         * 存的结果；否则，你会执行computeNumberOfNodes调用，不过从封装器返回之前，你会将新计
         * 算出的“（参数，结果）对”保存到缓存中。严格地说，这种方式并非纯粹的函数式解决方案，因
         * 为它会修改由多个调用者共享的数据结构，不过这段代码的封装版本的确是引用透明的。
         * 实际操作上，这段代码的工作如下：
         */
        public static class Jia1451 {

            Integer computeNumberOfNodes(Range range) {
                return null;
            }

            final Map<Range,Integer> numberOfNodes = new HashMap<>();
            Integer computeNumberOfNodesUsingCache(Range range) {
                Integer result = numberOfNodes.get(range);
                if (result != null){
                    return result;
                }
                result = computeNumberOfNodes(range);
                numberOfNodes.put(range, result);
                return result;
            }

            //注意 Java 8改进了Map接口，提供了一个名为computeIfAbsent的方法处理这样的情况。我们
            //会在附录B介绍这一方法。但是，我们在这里也提供一些参考，你可以用下面的方式调用
            //computeIfAbsent方法，帮助你编写结构更加清晰的代码：
            Integer computeNumberOfNodesUsingCacheImproved(Range range) {
                return numberOfNodes.computeIfAbsent(range,
                        this::computeNumberOfNodes);
            }
        }

        /**
         * 14.5.2 “返回同样的对象”意味着什么
         * 让我们在次回顾一下14.2.3节中二叉树的例子。图14-4中，变量t指向了一棵现存的树，依
         * 据该图，调用fupdate(fupdate("Will",26, t)会生成一个新的树，这里我们假设该树会
         * 被赋值给变量t2。通过该图，我们非常清楚地知道变量t，以及所有它涉及的数据结构都是不
         * 会变化的。现在，假设你在新增的赋值操作中执行一次字面上和上一操作完全相同的调用，如
         * 下所示：
         * t3 = fupdate("Will", 26, t);
         * 这时t会指向第三个新创建的节点，该节点包含了和t2一样的数据。好，问题来了：fupdate
         * 是否符合引用透明性原则呢？引用透明性原则意味着“使用相同的参数（即这个例子的情况）产
         * 生同样的结果”。问题是t2和t3属于不同的对象引用，所以(t2==t3)这一结论并不成立，这样
         * 说起来你只能得出一个结论：fupdate并不符合引用透明性原则。虽然如此，使用不会改动的持
         * 久化数据结构时，t2和t3在逻辑上并没有差别。 对于这一点我们已经辩论了很长时间，不过最
         * 简单的概括可能是函数式编程通常不使用==（引用相等），而是使用equal对数据结构值进行比
         * 较，由于数据没有发生变更，所以这种模式下fupdate是引用透明的。
         */
        public static class Jia1452 {

            public static void main(String[] args) {
                // Jia142.Jia1423.fupdate(Jia142.Jia1423.fupdate("Will",26, t);
                Tree t3 = Jia142.Jia1423.fupdate("Will", 26, new Tree("jay", 23, null, null));
                System.out.println(JSON.toJSONString(t3));
            }
        }

        /**
         * 14.5.3 结合器
         * 函数式编程时编写高阶函数是非常普通而且非常自然的事。高阶函数接受两个或多个函数，
         * 并返回另一个函数，实现的效果在某种程度上类似于将这些函数进行了结合。术语结合器通常用
         * 于描述这一思想。Java 8中的很多API都受益于这一思想，比如CompletableFuture类中的
         * thenCombine方法。该方法接受两个CompletableFuture方法和一个BiFunction方法，返回
         * 另一个CompletableFuture方法
         */
        public static class Jia1453 {

            //虽然深入探讨函数式编程中结合器的特性已经超出了本书的范畴，了解结合器使用的一些特
            //例还是非常有价值的，它能让我们切身体验函数式编程中构造接受和返回函数的操作是多么普通
            //和自然。下面这个方法就体现了函数组合（function composition）的思想：
            static <A,B,C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
                return x -> g.apply(f.apply(x));
            }

            //它接受函数f和g作为参数，并返回一个函数，实现的效果是先做f，接着做g。你可以接着
            //用这种方式定义一个操作，通过结合器完成内部迭代的效果。让我们看这样一个例子，你希望接
            //受一个参数，并使用函数f连续地对它进行操作（比如n次），类似循环的效果。我们将你的操作
            //命名为repeat，它接受一个参数f，f代表了一次迭代中进行的操作，它返回的也是一个函数，
            //返回的函数会在n次迭代中执行。像下面这样一个方法调用
            //repeat(3, (Integer x) -> 2*x);

            //形成的效果是x ->(2*(2*(2*x)))或者x -> 8*x。
            //你可以通过下面这段代码进行测试：
            //System.out.println(repeat(3, (Integer x) -> 2*x).apply(10));
            //输出的结果是80。
            //你可以按照下面的方式编写repeat方法（请特别留意0次循环的特殊情况）：
            static <A> Function<A, A> repeat(int n, Function<A, A> f) {
                return n == 0 ? x -> x : compose(f, repeat(n-1, f));
            }

            // 这个想法稍作变更可以对迭代概念的更丰富外延进行建模，甚至包括对在迭代之间传递可变
            //状态的函数式模型

            public static void main(String[] args) {
                Function<Integer, Integer> repeat = repeat(3, (Integer x) -> 2 * x);
                Integer apply = repeat.apply(10);
                System.out.println(apply);
            }

        }
    }

}
