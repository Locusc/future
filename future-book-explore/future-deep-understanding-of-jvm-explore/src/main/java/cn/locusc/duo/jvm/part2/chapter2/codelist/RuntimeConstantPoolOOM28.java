package cn.locusc.duo.jvm.part2.chapter2.codelist;

/**
 * @author Jay
 * String.intern()返回引用的测试
 *
 * 关于这个字符串常量池的实现在哪里出现问题，还可以引申出一些更有意思的影响，具体见代码
 * 清单2-8所示。
 *
 * 这段代码在JDK 6中运行，会得到两个false，而在JDK 7中运行，会得到一个true和一个false。产
 * 生差异的原因是，在JDK 6中，intern()方法会把首次遇到的字符串实例复制到永久代的字符串常量池
 * 中存储，返回的也是永久代里面这个字符串实例的引用，而由StringBuilder创建的字符串对象实例在
 * Java堆上，所以必然不可能是同一个引用，结果将返回false。
 * 而JDK 7（以及部分其他虚拟机，例如JRockit）的intern()方法实现就不需要再拷贝字符串的实例
 * 到永久代了，既然字符串常量池已经移到Java堆中，那只需要在常量池里记录一下首次出现的实例引
 * 用即可，因此intern()返回的引用和由StringBuilder创建的那个字符串实例就是同一个。而对str2比较返
 * 回false，这是因为“java”
 * [2]这个字符串在执行String-Builder.toString()之前就已经出现过了，字符串常量
 * 池中已经有它的引用，不符合intern()方法要求“首次遇到”的原则，“计算机软件”这个字符串则是首次
 * 出现的，因此结果返回true。
 *
 * 它是在加载sun.misc.Version这个类的时候进入常量池的。本书第2版并未解释java这个字符串此前是
 * 哪里出现的，所以被批评“挖坑不填了”（无奈地摊手）。如读者感兴趣是如何找出来的，
 * 可参考RednaxelaFX的知乎回答（https://www.zhihu.com/question/51102308/answer/124441115）。
 *
 * 2022/7/29
 */
public class RuntimeConstantPoolOOM28 {

    public static void main(String[] args) {
        String str1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);
    }

}
